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
import com.ahsgaming.valleyofbones.network.NetController;
import com.ahsgaming.valleyofbones.units.Prototypes;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;

import java.util.HashMap;

/**
 * @author jami
 *
 */
public class MPGameSetupScreen extends AbstractScreen {
	public String LOG = "MPGameSetupScreen";
	GameSetupConfig config;
    NetController client;
	
	Array<Player> pList;
    HashMap<Integer, String> sList;

    boolean isHost = false;

    SelectBox mapSelect, spawnSelect, ruleSelect, moveSelect, baseTime, actionTime, unitTime;
    Label lblMap, lblSpawn, lblRule, lblMove, lblBaseTime, lblActionTime, lblUnitTime;
    CheckBox chkSpectators;

    boolean needsUpdate = false;

    String[] spawnTypes = new String[]{ "Normal", "Inverted", "Random" };
    String[] firstMoves = new String[]{ "Random", "P1", "P2" };

    Table chatTable;
    Array<String> chatHistory;
    InputListener chatListener;
    ScrollPane chatScroll;

    /**
	 * @param game
	 */
	public MPGameSetupScreen(VOBGame game, GameSetupConfig cfg) {
		super(game);
		config = cfg;
		client = game.createGame(cfg);
	}
	
	public void setupScreen() {

		Label gameTypeLbl = new Label("Multiplayer", getSkin(), "medium");

		Table table = new Table(getSkin());
        table.setFillParent(true);
        stage.addActor(table);

        table.add(gameTypeLbl).colspan(2).center();
        table.row().minWidth(600);

        Table playerTable = new Table(getSkin());
        playerTable.setBackground(getSkin().getDrawable("default-pane"));

        playerTable.add().padBottom(10).width(30);
        playerTable.add().padBottom(10).width(30);
        playerTable.add(new Label("Name", getSkin(), "small-grey")).padBottom(10);
        playerTable.add(new Label("Race", getSkin(), "small-grey")).padBottom(10);
        playerTable.add(new Label("Color", getSkin(), "small-grey")).padBottom(10);
        playerTable.add(new Label("Ready", getSkin(), "small-grey")).padBottom(10).row();

        pList = new Array<Player>();
        pList.addAll(game.getPlayers());

        for (final Player p: pList) {
            playerTable.add("P" + ((pList.indexOf(p, true) + 1))).padLeft(10);
            if (pList.indexOf(p, true) == 0) {
                Image host = new Image(game.getTextureManager().getSpriteFromAtlas("assets", "king-small"));
                playerTable.add(host).size(host.getWidth() / VOBGame.SCALE, host.getHeight() / VOBGame.SCALE).padLeft(10).padRight(10);
            } else {
                if (config.isHost) {
                    Image btn = getRemovePlayerButton(p.getPlayerId());
                    playerTable.add(btn).size(btn.getWidth() / VOBGame.SCALE, btn.getHeight() / VOBGame.SCALE).padLeft(10).padRight(10);
                } else {
                    playerTable.add().padLeft(10).padRight(10);
                }
            }

            playerTable.add(new Label(String.format("%s (%d)", p.getPlayerName(), p.getPlayerId()), getSkin())).left().expandX();

            if (p == client.getPlayer() || (config.isHost && p.isAI())) {

                SelectBox raceSelect = new SelectBox(Prototypes.getRaces().toArray(), getSkin());
                playerTable.add(raceSelect).expandX();
                raceSelect.setSelection(p.getRace());

                raceSelect.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        KryoCommon.UpdatePlayer update = new KryoCommon.UpdatePlayer();
                        update.id = p.getPlayerId();
                        update.race = ((SelectBox)actor).getSelection();
                        update.color = 0;
                        while (update.color < Player.AUTOCOLORS.length && !p.getPlayerColor().equals(Player.AUTOCOLORS[update.color])) {
                            update.color++;
                        }
                        update.ready = p.isReady();
                        if (client instanceof MPGameClient)
                            ((MPGameClient)client).sendPlayerUpdate(update);
                    }
                });

            } else {
                playerTable.add(p.getRace());
            }

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
                        update.race = p.getRace();
                        update.color = i;
                        update.ready = p.isReady();
                        if (client instanceof MPGameClient)
                            ((MPGameClient)client).sendPlayerUpdate(update);
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
                    update.race = p.getRace();
                    update.color = 0;
                    while (update.color < Player.AUTOCOLORS.length && !p.getPlayerColor().equals(Player.AUTOCOLORS[update.color])) {
                        update.color++;
                    }
                    update.ready = !p.isReady();
                    if (client instanceof MPGameClient)
                        ((MPGameClient)client).sendPlayerUpdate(update);
                }
            });


            playerTable.row().padBottom(5).padTop(5);
        }

        if (pList.size < 2 && config.isHost) {
            TextButton addAI = new TextButton(" Add AI Player ", getSkin(), "small");
            addAI.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    super.clicked(event, x, y);

                    game.addAIPlayer();
                }
            });
            playerTable.add();
            playerTable.add();
            playerTable.add(addAI).colspan(5).left();
        }

        table.add(playerTable).fillX().colspan(2);

        table.row();

        sList = game.getSpectators();
        if (sList.keySet().size() > 0) {
            table.add("Spectators").colspan(2).left();
            table.row();

            Table spectatorTable = new Table(getSkin());
            spectatorTable.setBackground(getSkin().getDrawable("default-pane"));

            for (Integer id: sList.keySet()) {
                if (config.isHost) {
                    Image btn = getRemovePlayerButton(id);
                    spectatorTable.add(btn).size(btn.getWidth() / VOBGame.SCALE, btn.getHeight() / VOBGame.SCALE).padLeft(10).padRight(10);
                }
                spectatorTable.add(sList.get(id)).expandX().left();
                spectatorTable.row().expandX().padBottom(5).padTop(5);
            }

            table.add(spectatorTable).fillX().colspan(2);
            table.row();
        }

        ChangeListener updateListener = new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                needsUpdate = true;
            }
        };

        Table setupTable = new Table(getSkin());

        chkSpectators = new CheckBox(" Allow Spectators?", getSkin());
        chkSpectators.setChecked(config.allowSpectate);
        chkSpectators.setDisabled(!config.isHost);
        chkSpectators.addListener(updateListener);
        setupTable.add(chkSpectators).colspan(2).left().padTop(4).row();

        Label mapLbl = new Label("Map:", getSkin());
        setupTable.add(mapLbl).left();

        if (config.isHost) {
            needsUpdate = true;  // always send an update on setup, just so we're all on the same page

            JsonReader reader = new JsonReader();
            JsonValue val = reader.parse(Gdx.files.internal("maps/maps.json").readString());

            Array<String> maps = new Array<String>();
            for (JsonValue v: val) {
                maps.add(v.asString());
            }

            mapSelect = new SelectBox(maps.toArray(), getSkin());
            mapSelect.setSelection(config.mapName);

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

        setupTable.add("Starting Locations:").left();
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

        setupTable.add("Timing Rules:").colspan(2).left().row();

        Table timingTable = new Table(getSkin());
        setupTable.add(timingTable).colspan(2).fillX();

        timingTable.add("Base:").left().expandX();
        if (config.isHost) {
            baseTime = new SelectBox(new String[]{"30", "60", "90"}, getSkin());
            timingTable.add(baseTime).expandX().padLeft(4);
            baseTime.setSelection(Integer.toString(config.baseTimer));
            baseTime.addListener(updateListener);
        } else {
            lblBaseTime = new Label(Integer.toString(config.baseTimer), getSkin());
            timingTable.add(lblBaseTime).expandX().padLeft(4);
        }

        timingTable.add("Action:").left().expandX().padLeft(4);
        if (config.isHost) {
            actionTime = new SelectBox(new String[]{"0", "15", "30"}, getSkin());
            timingTable.add(actionTime).expandX().padLeft(4);
            actionTime.setSelection(Integer.toString(config.actionBonusTime));
            actionTime.addListener(updateListener);
        } else {
            lblActionTime = new Label(Integer.toString(config.actionBonusTime), getSkin());
            timingTable.add(lblActionTime).expandX().padLeft(4);
        }

        timingTable.add("Unit:").left().expandX().padLeft(4);
        if (config.isHost) {
            unitTime = new SelectBox(new String[]{"0", "3", "5"}, getSkin());
            timingTable.add(unitTime).expandX().padLeft(4);
            unitTime.setSelection(Integer.toString(config.unitBonusTime));
            unitTime.addListener(updateListener);
        } else {
            lblUnitTime = new Label(Integer.toString(config.unitBonusTime), getSkin());
            timingTable.add(lblUnitTime).expandX().padLeft(4);
        }

        table.add(setupTable).fillX();

        Table controlTable = new Table(getSkin());

        Sprite mapThumb = game.getTextureManager().getSpriteFromAtlas("assets", config.mapName);
        if (mapThumb != null) {
            controlTable.add(new Image(mapThumb)).colspan(2).row();
        }

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
            controlTable.add().expand();
            controlTable.add(start).padTop(4).right();

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

        controlTable.add();
        controlTable.add(cancel).fillX().padTop(4).right();

        table.add(controlTable).fillX().row();

        if (chatTable == null) {
            chatTable = new Table(getSkin());
            chatScroll = new ScrollPane(chatTable, getSkin());
            chatScroll.setFadeScrollBars(false);
            chatTable.padLeft(20);
        }
        table.add(chatScroll).colspan(2).fillX().height(100);

        table.row();

        Table chatBox = new Table(getSkin());
        final TextField chatMsgText = new TextField("", getSkin());
        if (chatListener != null) {
            stage.removeListener(chatListener);
        }
        stage.addListener(new InputListener() {
            @Override
            public boolean keyUp(InputEvent event, int keycode) {
                if (stage.getKeyboardFocus() == chatMsgText && keycode == Input.Keys.ENTER && chatMsgText.getText().length() > 0) {
                    Gdx.app.log(LOG, "Chat: " + chatMsgText.getText());
                    client.sendChat(chatMsgText.getText());
                    chatMsgText.setText("");
                    return false;
                }
                return super.keyUp(event, keycode);
            }
        });

        chatBox.add(chatMsgText).expandX().fillX();
        TextButton chatMsgSend = new TextButton("Send", getSkin(), "small");
        chatMsgSend.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);    //To change body of overridden methods use File | Settings | File Templates.

                if (chatMsgText.getText().length() > 0) {
                    Gdx.app.log(LOG, "Chat: " + chatMsgText.getText());
                    client.sendChat(chatMsgText.getText());
                    chatMsgText.setText("");
                }
            }
        });
        chatBox.add(chatMsgSend).fillY().fillX();
        table.add(chatBox).colspan(2).fillX();
        chatHistory = new Array<String>();
	}
	
	private Image getRemovePlayerButton(final int pid) {
		Image remove = new Image(VOBGame.instance.getTextureManager().getSpriteFromAtlas("assets", "walking-boot-small"));

		remove.addListener(new ClickListener() {

			/* (non-Javadoc)
			 * @see com.badlogic.gdx.scenes.scene2d.utils.ClickListener#touchUp(com.badlogic.gdx.scenes.scene2d.InputEvent, float, float, int, int)
			 */
			@Override
			public void touchUp(InputEvent event, float x, float y,
					int pointer, int button) {
				Gdx.app.log(LOG, String.format("remove (%d)", pid));
				game.removePlayer(pid);
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

        if (!client.isConnected() && !client.isConnecting()) {
            game.setScreen(game.getGameJoinScreen());
        }

        if (!isHost && config.isHost) {
            isHost = true;
            stage.clear();
            setupScreen();
            return;
        }

        if (config.isHost && needsUpdate) {
            needsUpdate = false;
            KryoCommon.GameDetails gameDetails = new KryoCommon.GameDetails();
            gameDetails.map = mapSelect.getSelection();
            gameDetails.rules = ruleSelect.getSelectionIndex();
            gameDetails.firstMove = moveSelect.getSelectionIndex();
            gameDetails.spawn = spawnSelect.getSelectionIndex();
            gameDetails.baseTimer = Integer.parseInt(baseTime.getSelection());
            gameDetails.actionBonusTime = Integer.parseInt(actionTime.getSelection());
            gameDetails.unitBonusTime = Integer.parseInt(unitTime.getSelection());
            gameDetails.allowSpectate = chkSpectators.isChecked();
            if (!config.mapName.equals(mapSelect.getSelection())) {
                stage.clear();
                config.setDetails(gameDetails);
                setupScreen();
            } else {
                config.setDetails(gameDetails);
            }
            if (client instanceof MPGameClient)
                ((MPGameClient)client).sendGameDetails(gameDetails);
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

        synchronized(client.getChatLog()) {
            if (!chatHistory.equals(client.getChatLog())) {
                chatHistory.clear();
                chatHistory.addAll(client.getChatLog());
                chatTable.clearChildren();
                for (String msg: chatHistory) {
                    Label message = new Label(msg, getSkin());
                    message.setWrap(true);
                    chatTable.add(message).fillX().expandX().left().row();
                }
                chatScroll.layout();
                chatScroll.setScrollPercentY(100);
            }
        }

        if (!config.isMulti || config.isHost) {
            if (!mapSelect.getSelection().equals(config.mapName)) mapSelect.setSelection(config.mapName);
            if (moveSelect.getSelectionIndex() != config.firstMove) moveSelect.setSelection(config.firstMove);
            if (spawnSelect.getSelectionIndex() != config.spawnType) moveSelect.setSelection(config.spawnType);
            if (ruleSelect.getSelectionIndex() != config.ruleSet) moveSelect.setSelection(config.ruleSet);

            baseTime.setSelection(Integer.toString(config.baseTimer));
            actionTime.setSelection(Integer.toString(config.actionBonusTime));
            unitTime.setSelection(Integer.toString(config.unitBonusTime));

        } else {
            if (!lblMap.getText().toString().equals(config.mapName)) {
                stage.clear();
                setupScreen();
                chatScroll.layout();
                chatScroll.setScrollPercentY(100);
                return;
            }
            if (!lblSpawn.getText().toString().equals(spawnTypes[config.spawnType])) lblSpawn.setText(spawnTypes[config.spawnType]);
            if (!lblMove.getText().toString().equals(firstMoves[config.firstMove])) lblMove.setText(firstMoves[config.firstMove]);
            if (!lblRule.getText().toString().equals(game.getRuleSets().get(config.ruleSet).name)) lblRule.setText(game.getRuleSets().get(config.ruleSet).name);

            lblBaseTime.setText(Integer.toString(config.baseTimer));
            lblActionTime.setText(Integer.toString(config.actionBonusTime));
            lblUnitTime.setText(Integer.toString(config.unitBonusTime));
        }
	}


}
