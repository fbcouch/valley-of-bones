/**
 * Copyright 2012 Jami Couch
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 * This project uses:
 * 
 * LibGDX
 * Copyright 2011 see LibGDX AUTHORS file
 * Licensed under Apache License, Version 2.0 (see above).
 * 
 */
package com.ahsgaming.valleyofbones.screens;

import com.ahsgaming.valleyofbones.Player;
import com.ahsgaming.valleyofbones.VOBGame;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;

/**
 * @author jami
 *
 */
public class MPGameSetupScreen extends AbstractScreen {
	public String LOG = "MPGameSetupScreen";
	GameSetupConfig config;
	
	Array<Player> pList;

    boolean isHost = false;

    SelectBox mapSelect;
    Label mapSelection;
	
	/**
	 * @param game
	 */
	public MPGameSetupScreen(VOBGame game, GameSetupConfig cfg) {
		super(game);
		config = cfg;
		game.createGame(cfg);
	}
	
	public void setupScreen() {

		Label gameTypeLbl = new Label("Multiplayer", getSkin(), "medium");

		Table table = new Table(getSkin());
        table.setFillParent(true);
        stage.addActor(table);

        table.add(gameTypeLbl).colspan(2).center();
        table.row().minWidth(600);

        table.add("Players").colspan(2).left();
        table.row();

        Table playerTable = new Table(getSkin());
        playerTable.setBackground(getSkin().getDrawable("default-pane"));

        pList = new Array<Player>();
        pList.addAll(game.getPlayers());

        for (Player p: pList) {
            if (pList.indexOf(p, true) == 0) {
                Image host = new Image(game.getTextureManager().getSpriteFromAtlas("assets", "king-small"));
                playerTable.add(host).size(host.getWidth() / VOBGame.SCALE, host.getHeight() / VOBGame.SCALE);
            } else {
                if (config.isHost) {
                    Image btn = getRemovePlayerButton(p);
                    playerTable.add(btn).size(btn.getWidth() / VOBGame.SCALE, btn.getHeight() / VOBGame.SCALE);
                } else {
                    playerTable.add();
                }
            }

            playerTable.add(new Label(String.format("%s (%d)", p.getPlayerName(), p.getPlayerId()), getSkin())).left();
            playerTable.add("Terran");
            if (pList.indexOf(p, true) == 0) {
                playerTable.add(new Label("Red", getSkin(), "default-font", Player.COLOR_RED));
            } else {
                playerTable.add(new Label("Blue", getSkin(), "default-font", Player.COLOR_BLUE));
            }
            playerTable.row().expandX().padBottom(5).padTop(5);
        }

        if (pList.size < 2) {
            TextButton addAI = new TextButton("Add AI Player", getSkin());
            addAI.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    super.clicked(event, x, y);

                    game.addAIPlayer(1);
                }
            });

            playerTable.add(addAI).colspan(4).left();
        }

        table.add(playerTable).fillX().colspan(2);

        table.row();

        Table setupTable = new Table(getSkin());

        Label mapLbl = new Label("Map:", getSkin());
        setupTable.add(mapLbl).left();

        if (config.isHost) {
            JsonReader reader = new JsonReader();
            JsonValue val = reader.parse(Gdx.files.internal("maps/maps.json").readString());

            Array<String> maps = new Array<String>();
            for (JsonValue v: val) {
                maps.add(v.asString());
            }

            mapSelect = new SelectBox(maps.toArray(), getSkin());
            mapSelect.setSelection(config.mapName);
            game.setMap(mapSelect.getSelection());

            mapSelect.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    game.setMap(((SelectBox)actor).getSelection());
                }
            });

            setupTable.add(mapSelect).left();
        } else {
            mapSelection = new Label(config.mapName, getSkin());
            setupTable.add(mapSelection).left();
        }

        setupTable.row().expandX().expandY().top().left();

        table.add(setupTable).fill();


        Table controlTable = new Table(getSkin());

        if (config.isHost) {
            isHost = true;
            TextButton start = new TextButton("Start Game",getSkin());
            start.addListener(new ClickListener() {

                /* (non-Javadoc)
                 * @see com.badlogic.gdx.scenes.scene2d.utils.ClickListener#touchUp(com.badlogic.gdx.scenes.scene2d.InputEvent, float, float, int, int)
                 */
                @Override
                public void touchUp(InputEvent event, float x, float y,
                                    int pointer, int button) {
                    super.touchUp(event, x, y, pointer, button);
                    if (game.getPlayers().size >= 2)
                        game.sendStartGame();
                }

            });
            controlTable.add(start).size(150, 50).pad(4).right().bottom().colspan(2);

            controlTable.row();
        }

        TextButton cancel = new TextButton("Cancel", getSkin(), "cancel");
        cancel.addListener(new ClickListener() {

            /* (non-Javadoc)
             * @see com.badlogic.gdx.scenes.scene2d.utils.ClickListener#touchUp(com.badlogic.gdx.scenes.scene2d.InputEvent, float, float, int, int)
             */
            @Override
            public void touchUp(InputEvent event, float x, float y,
                                int pointer, int button) {
                super.touchUp(event, x, y, pointer, button);

                game.setScreen(game.getMainMenuScreen());
                game.closeGame();

            }

        });

        controlTable.add(cancel).size(150, 50).pad(4).right().bottom().colspan(2);

        table.add(controlTable).fillX();
	}
	
	private Image getRemovePlayerButton(final Player p) {
		Image remove = new Image(VOBGame.instance.getTextureManager().getSpriteFromAtlas("assets", "walking-boot-small"));

		remove.addListener(new ClickListener() {

			/* (non-Javadoc)
			 * @see com.badlogic.gdx.scenes.scene2d.utils.ClickListener#touchUp(com.badlogic.gdx.scenes.scene2d.InputEvent, float, float, int, int)
			 */
			@Override
			public void touchUp(InputEvent event, float x, float y,
					int pointer, int button) {
				Gdx.app.log(LOG, String.format("remove (%d)", p.getPlayerId()));
				game.removePlayer(p.getPlayerId());
			}
			
		});
		
		return remove;
	}

	/**
	 * Implemented methods
	 */
	
	@Override
	public void show() {
		super.show();
	}
	
	@Override
	public void resize(int width, int height) {
		super.resize(width, height);
		
		setupScreen();
	}
	
	/* (non-Javadoc)
	 * @see com.ahsgaming.spacetactics.screens.AbstractScreen#render(float)
	 */
	@Override
	public void render(float delta) {
		super.render(delta);
		
		//Gdx.app.log(LOG, Integer.toString(game.getPlayers().size()));
		Array<Player> players = game.getPlayers();
		
		synchronized (players) {
			if (!pList.equals(players) || isHost != config.isHost) {
				stage.clear();
				setupScreen();
			}
		}

        if (!config.isMulti || config.isHost) {
            if (!mapSelect.getSelection().equals(config.mapName)) {
                mapSelect.setSelection(config.mapName);
            }
        } else {
            if (!mapSelection.getText().equals(config.mapName)) {
                mapSelection.setText(config.mapName);
            }
        }
	}


}
