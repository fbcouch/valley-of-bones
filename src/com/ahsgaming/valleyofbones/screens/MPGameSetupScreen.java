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
import com.ahsgaming.valleyofbones.network.MPGameClient;
import com.badlogic.gdx.Gdx;
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
public class MPGameSetupScreen extends AbstractScreen {
	public String LOG = "MPGameSetupScreen";
	GameSetupConfig config;
    MPGameClient client;
	
	Array<Player> pList;
    Array<String> sList;

    boolean isHost = false;

    SelectBox mapSelect, spawnSelect, ruleSelect, moveSelect;
    Label lblMap, lblSpawn, lblRule, lblMove;

    boolean needsUpdate = false;

    String[] spawnTypes = new String[]{ "Normal", "Inverted", "Random" };
    String[] firstMoves = new String[]{ "Random", "P1", "P2" };

    /**
	 * @param game
	 */
	public MPGameSetupScreen(VOBGame game, GameSetupConfig cfg) {
		super(game);
		config = cfg;
		client = (MPGameClient)game.createGame(cfg);
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

        for (final Player p: pList) {
            if (pList.indexOf(p, true) == 0) {
                Image host = new Image(game.getTextureManager().getSpriteFromAtlas("assets", "king-small"));
                playerTable.add(host).size(host.getWidth() / VOBGame.SCALE, host.getHeight() / VOBGame.SCALE).expandX();
            } else {
                if (config.isHost) {
                    Image btn = getRemovePlayerButton(p);
                    playerTable.add(btn).size(btn.getWidth() / VOBGame.SCALE, btn.getHeight() / VOBGame.SCALE).expandX();
                } else {
                    playerTable.add().expandX();
                }
            }

            playerTable.add(new Label(String.format("%s (%d)", p.getPlayerName(), p.getPlayerId()), getSkin())).left().expandX();
            playerTable.add("Terran").expandX();

            Image color = new Image(getSkin().getDrawable("white-hex"));
            color.setColor(p.getPlayerColor());
            playerTable.add(color).expandX().width(color.getWidth());

            if (p == client.getPlayer() || (config.isHost && p.isAI())) {
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

                        KryoCommon.UpdatePlayer update = new KryoCommon.UpdatePlayer();
                        update.id = p.getPlayerId();
                        update.color = i;
                        update.ready = p.isReady();
                        client.sendPlayerUpdate(update);
                    }
                });
            }

            CheckBox chkReady = new CheckBox("", getSkin());
            playerTable.add(chkReady).expandX();

            chkReady.setChecked(p.isReady());
            if (p != client.getPlayer()) chkReady.setDisabled(true);

            chkReady.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    KryoCommon.UpdatePlayer update = new KryoCommon.UpdatePlayer();
                    update.id = p.getPlayerId();
                    update.color = 0;
                    while (update.color < Player.AUTOCOLORS.length && !p.getPlayerColor().equals(Player.AUTOCOLORS[update.color])) {
                        update.color++;
                    }
                    update.ready = !p.isReady();
                    client.sendPlayerUpdate(update);
                }
            });


            playerTable.row().expandX().padBottom(5).padTop(5);
        }

        if (pList.size < 2 && config.isHost) {
            TextButton addAI = new TextButton("Add AI Player", getSkin());
            addAI.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    super.clicked(event, x, y);

                    game.addAIPlayer();
                }
            });

            playerTable.add(addAI).colspan(5).left();
        }

        table.add(playerTable).fillX().colspan(2);

        table.row();

        sList = new Array<String>(game.getSpectators());
        if (sList.size > 0) {
            table.add("Spectators").colspan(2).left();
            table.row();

            Table spectatorTable = new Table(getSkin());
            spectatorTable.setBackground(getSkin().getDrawable("default-pane"));

            for (String name: sList) {
                spectatorTable.add(name).expandX().left();
                spectatorTable.row().expandX().padBottom(5).padTop(5);
            }

            table.add(spectatorTable).fillX().colspan(2);
            table.row();
        }

        Table setupTable = new Table(getSkin());

        Label mapLbl = new Label("Map:", getSkin());
        setupTable.add(mapLbl).left();

        ChangeListener updateListener = new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                needsUpdate = true;
            }
        };

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

            mapSelect.addListener(updateListener);

            setupTable.add(mapSelect).left().padTop(4).padBottom(4).fillX();
        } else {
            lblMap = new Label(config.mapName, getSkin());
            setupTable.add(lblMap).left();
        }
        setupTable.row();

        setupTable.add("Rules:").left();
        if (config.isHost) {
            Array<VOBGame.RuleSet> ruleSets = game.getRuleSets();
            String[] items = new String[ruleSets.size];
            for (int i = 0; i < ruleSets.size; i++) {
                items[i] = ruleSets.get(i).name;
            }
            ruleSelect = new SelectBox(items, getSkin());
            setupTable.add(ruleSelect).left().padBottom(4).fillX();
            ruleSelect.setSelection(config.ruleSet);

            ruleSelect.addListener(updateListener);
        } else {
            lblRule = new Label(game.getRuleSets().get(config.ruleSet).name, getSkin());
            setupTable.add(lblRule).left();
        }
        setupTable.row();

        setupTable.add("Spawns:").left();
        if (config.isHost) {
            spawnSelect = new SelectBox(spawnTypes, getSkin());
            setupTable.add(spawnSelect).left().padBottom(4).fillX();
            spawnSelect.setSelection(config.spawnType);

            spawnSelect.addListener(updateListener);
        } else {
            lblSpawn = new Label(spawnTypes[config.spawnType], getSkin());
            setupTable.add(lblSpawn).left();
        }
        setupTable.row();

        setupTable.add("First Move:").left();
        if (config.isHost) {
            moveSelect = new SelectBox(firstMoves, getSkin());
            setupTable.add(moveSelect).left().padBottom(4).fillX();
            moveSelect.setSelection(config.firstMove);

            moveSelect.addListener(updateListener);
        } else {
            lblMove = new Label(firstMoves[config.firstMove], getSkin());
            setupTable.add(lblMove).left();
        }
        setupTable.row();

        setupTable.row().expandX().expandY().top().left();

        table.add(setupTable).fill();


        Table controlTable = new Table(getSkin());

        if (config.isHost) {
            isHost = true;
            TextButton start = new TextButton("Start Game",getSkin());
            start.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    super.clicked(event, x, y);
                    if (game.getPlayers().size >= 2)
                        game.sendStartGame();
                }
            });
            controlTable.add(start).padTop(4);

            controlTable.row();
        }

        TextButton cancel = new TextButton("Cancel", getSkin(), "cancel");
        cancel.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                game.setScreen(game.getMainMenuScreen());
                game.closeGame();
            }
        });

        controlTable.add(cancel).fillX().padTop(4);

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

        if (config.isHost && needsUpdate) {
            needsUpdate = false;
            KryoCommon.GameDetails gameDetails = new KryoCommon.GameDetails();
            gameDetails.map = mapSelect.getSelection();
            gameDetails.rules = ruleSelect.getSelectionIndex();
            gameDetails.firstMove = moveSelect.getSelectionIndex();
            gameDetails.spawn = spawnSelect.getSelectionIndex();
            client.sendGameDetails(gameDetails);
        }
		
		//Gdx.app.log(LOG, Integer.toString(game.getPlayers().size()));
		Array<Player> players = game.getPlayers();
		
		synchronized (players) {
			if (!pList.equals(players) || isHost != config.isHost) {
				stage.clear();
				setupScreen();
                return;
			}
		}

        synchronized (game.getSpectators()) {
            if (!sList.equals(game.getSpectators())) {
                stage.clear();
                setupScreen();
                return;
            }
        }

        if (!config.isMulti || config.isHost) {
            if (!mapSelect.getSelection().equals(config.mapName)) mapSelect.setSelection(config.mapName);
            if (moveSelect.getSelectionIndex() != config.firstMove) moveSelect.setSelection(config.firstMove);
            if (spawnSelect.getSelectionIndex() != config.spawnType) moveSelect.setSelection(config.spawnType);
            if (ruleSelect.getSelectionIndex() != config.ruleSet) moveSelect.setSelection(config.ruleSet);

        } else {
            if (!lblMap.getText().equals(config.mapName)) lblMap.setText(config.mapName);
            if (!lblSpawn.getText().equals(spawnTypes[config.spawnType])) lblSpawn.setText(spawnTypes[config.spawnType]);
            if (!lblMove.getText().equals(firstMoves[config.firstMove])) lblMove.setText(firstMoves[config.firstMove]);
            if (!lblRule.getText().equals(game.getRuleSets().get(config.spawnType).name)) lblRule.setText(game.getRuleSets().get(config.ruleSet).name);

        }
	}


}
