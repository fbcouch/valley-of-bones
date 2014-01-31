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
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;

/**
 * @author jami
 *
 */
public class SPGameSetupScreen extends AbstractScreen {
	public String LOG = "SPGameSetupScreen";
	GameSetupConfig config;

	Array<Player> pList;

    boolean isHost = false;

    SelectBox mapSelect;
    Label mapSelection;

	/**
	 * @param game
	 */
	public SPGameSetupScreen(VOBGame game, GameSetupConfig cfg) {
		super(game);
		config = cfg;
        cfg.maxPauses = 0;
		game.createGame(cfg);
        game.addAIPlayer(1);
	}
	
	public void setupScreen() {
		
		Label gameTypeLbl = new Label("Single Player", getSkin(), "medium");

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
                playerTable.add();
            }

            playerTable.add(new Label(String.format("%s (%d)", p.getPlayerName(), p.getPlayerId()), getSkin())).left();
            playerTable.add("Terran");
//            Image[] colors = new Image[Player.AUTOCOLORS.length];
//
//            for (int c = 0; c < colors.length; c++) {
//                colors[c] = new Image(getSkin().getDrawable("white-hex"));
//                colors[c].setColor(Player.AUTOCOLORS[c]);
//            }
//
//            ImageSelectBox color = new ImageSelectBox(colors, new ImageSelectBox.ImageSelectBoxStyle(
//                    getSkin().getDrawable("default-select"),
//                    getSkin().get("default", ScrollPane.ScrollPaneStyle.class),
//                    new ImageList.ImageListStyle(getSkin().getDrawable("default-rect-pad"))
//            ));
//            ImageList color = new ImageList(colors, new ImageList.ImageListStyle(getSkin().getDrawable("default-rect-pad")));
            Image color = new Image(getSkin().getDrawable("white-hex"));
            color.setColor(Player.AUTOCOLORS[pList.indexOf(p, true)]);
            playerTable.add(color);

            color.addListener(new ClickListener() {
                @Override
                public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                    int i = 0;
                    while (i < Player.AUTOCOLORS.length && !Player.AUTOCOLORS[i].equals(event.getTarget().getColor())) {
                        i++;
                    }
                    i = (event.getButton() == 0 ? i + 1 : i - 1);
                    i = Math.max(0, i);
                    i = Math.min(i, Player.AUTOCOLORS.length - 1);
                    event.getTarget().setColor(Player.AUTOCOLORS[i]);
                    return super.touchDown(event, x, y, pointer, button);
                }
            });
//            playerTable.add(colors[0]).size(colors[0].getWidth() / VOBGame.SCALE, colors[0].getHeight() / VOBGame.SCALE);
//            if (pList.indexOf(p, true) == 0) {
//                color.setSelectedIndex(0);
//            } else {
//                color.setSelectedIndex(1);
//            }
            playerTable.row().expandX().padBottom(5).padTop(5);
        }

        table.add(playerTable).fillX().colspan(2);

		table.row();

        Table setupTable = new Table(getSkin());

        Label mapLbl = new Label("Map:", getSkin());
        setupTable.add(mapLbl).left();

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

        setupTable.row().expandX().expandY().top().left();

        table.add(setupTable).fill();

        Table controlTable = new Table(getSkin());

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
		controlTable.add(start).padTop(4).colspan(2);

		controlTable.row();

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
		
		controlTable.add(cancel).fillX().padTop(4).colspan(2);
		
		table.add(controlTable).fillX();
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
	}
}
