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
import com.ahsgaming.valleyofbones.network.KryoCommon;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
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
    boolean needsUpdate = false;

    SelectBox mapSelect;
    Image mapThumb;

	/**
	 * @param game
	 */
	public SPGameSetupScreen(VOBGame game, GameSetupConfig cfg) {
		super(game);
		config = cfg;
        cfg.maxPauses = 0;
		game.createGame(cfg);
        game.addAIPlayer();
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

        playerTable.add().padBottom(10);
        playerTable.add().padBottom(10);
        playerTable.add(new Label("Name", getSkin(), "small-grey")).padBottom(10);
        playerTable.add(new Label("Race", getSkin(), "small-grey")).padBottom(10);
        playerTable.add(new Label("Color", getSkin(), "small-grey")).padBottom(10).row();

        pList = new Array<Player>();
		pList.addAll(game.getPlayers());

		for (final Player p: pList) {
            playerTable.add("P" + ((pList.indexOf(p, true) + 1))).padLeft(10);
            if (pList.indexOf(p, true) == 0) {
                Image host = new Image(game.getTextureManager().getSpriteFromAtlas("assets", "king-small"));
                playerTable.add(host).size(host.getWidth() / VOBGame.SCALE, host.getHeight() / VOBGame.SCALE).padLeft(10);
            } else {
                playerTable.add().padLeft(10);
            }

            playerTable.add(new Label(String.format("%s (%d)", p.getPlayerName(), p.getPlayerId()), getSkin())).expandX().left();
            playerTable.add("Terran").expandX();

            Image color = new Image(getSkin().getDrawable("white-hex"));
            color.setColor(Player.AUTOCOLORS[pList.indexOf(p, true)]);
            playerTable.add(color).expandX();

            color.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    super.clicked(event, x, y);
                    int i = 0;
                    while (i < Player.AUTOCOLORS.length && !Player.AUTOCOLORS[i].equals(event.getTarget().getColor())) {
                        i++;
                    }
                    i++;

                    i %= Player.AUTOCOLORS.length;
                    event.getTarget().setColor(Player.AUTOCOLORS[i]);
                    p.setPlayerColor(Player.AUTOCOLORS[i]);
                }
            });

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
        config.mapName = mapSelect.getSelection();

        mapSelect.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                config.mapName = ((SelectBox)actor).getSelection();
                needsUpdate = true;
            }
        });

        mapThumb = new Image(game.getTextureManager().getSpriteFromAtlas("assets", config.mapName));

        setupTable.add(mapSelect).left().padBottom(4).padTop(4).fillX();
        setupTable.row();

        setupTable.add("Rules:").left();
        SelectBox ruleSelect = new SelectBox(new String[]{ "Classic" }, getSkin());

        ruleSelect.setSelection(config.ruleSet);
        ruleSelect.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                config.ruleSet = ((SelectBox)actor).getSelectionIndex();
            }
        });

        setupTable.add(ruleSelect).left().padBottom(4).fillX();
        setupTable.row();

        setupTable.add("Spawns:").left();
        SelectBox spawnSelect = new SelectBox(new String[]{ "Normal", "Inverted", "Random" }, getSkin());

        spawnSelect.setSelection(config.spawnType);
        spawnSelect.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                config.spawnType = ((SelectBox)actor).getSelectionIndex();
            }
        });

        setupTable.add(spawnSelect).left().padBottom(4).fillX();
        setupTable.row();

        setupTable.add("First Move:").left();
        SelectBox moveSelect = new SelectBox(new String[]{ "Random", "P1", "P2" }, getSkin());

        moveSelect.setSelection(config.firstMove);
        moveSelect.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                config.firstMove = ((SelectBox)actor).getSelectionIndex();
            }
        });

        setupTable.add(moveSelect).left().padBottom(4).fillX();
        setupTable.row();

        setupTable.add("Timing Rules:").colspan(2).left().row();

        Table timingTable = new Table(getSkin());
        setupTable.add(timingTable).colspan(2).fillX();

        timingTable.add("Base:").left().expandX();
        SelectBox baseTime = new SelectBox(new String[]{"30", "60", "90"}, getSkin());
        timingTable.add(baseTime).expandX();
        baseTime.setSelection(Integer.toString(config.baseTimer));
        baseTime.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                config.baseTimer = Integer.parseInt(((SelectBox)actor).getSelection());
            }
        });

        timingTable.add("Action:").left().expandX();
        SelectBox actionTime = new SelectBox(new String[]{"0", "15", "30"}, getSkin());
        timingTable.add(actionTime).expandX();
        actionTime.setSelection(Integer.toString(config.actionBonusTime));
        actionTime.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                config.actionBonusTime = Integer.parseInt(((SelectBox)actor).getSelection());
            }
        });

        timingTable.add("Unit:").left().expandX();
        SelectBox unitTime = new SelectBox(new String[]{"0", "3", "5"}, getSkin());
        timingTable.add(unitTime).expandX();
        unitTime.setSelection(Integer.toString(config.unitBonusTime));
        unitTime.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                config.unitBonusTime = Integer.parseInt(((SelectBox)actor).getSelection());
            }
        });

        setupTable.row().expandX().expandY().top().left();

        table.add(setupTable).fill();

        Table controlTable = new Table(getSkin());

        controlTable.add(mapThumb).colspan(2).row();

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
        controlTable.add().expand();
		controlTable.add(start).padTop(4).right();

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

        controlTable.add();
		controlTable.add(cancel).fillX().padTop(4);
		
		table.add(controlTable).fillX();

        table.row();
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
				needsUpdate = true;

			}
		}

        if (needsUpdate) {
            stage.clear();
            setupScreen();
            needsUpdate = false;
        }
	}
}
