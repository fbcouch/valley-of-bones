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
package com.ahsgaming.valleyofbones.network;

import java.io.IOException;

import com.ahsgaming.valleyofbones.GameController;
import com.ahsgaming.valleyofbones.GameResult;
import com.ahsgaming.valleyofbones.GameStates;
import com.ahsgaming.valleyofbones.Player;
import com.ahsgaming.valleyofbones.VOBGame;
import com.ahsgaming.valleyofbones.network.KryoCommon.AddAIPlayer;
import com.ahsgaming.valleyofbones.network.KryoCommon.RegisterPlayer;
import com.ahsgaming.valleyofbones.network.KryoCommon.RegisteredPlayer;
import com.ahsgaming.valleyofbones.network.KryoCommon.RemovePlayer;
import com.ahsgaming.valleyofbones.network.KryoCommon.StartGame;
import com.ahsgaming.valleyofbones.screens.GameSetupScreen.GameSetupConfig;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Array;
import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;

/**
 * @author jami
 *
 */
public class MPGameClient implements NetController {
	public String LOG = "MPGameClient";
	
	Client client;
	String host;
	Connection clientConn;

	int playerId = -1;
	Player player = null;
	
	GameController controller;
	
	GameSetupConfig gameConfig;
	
	Array<Player> players = new Array<Player>();
	
	VOBGame game;
	
	boolean stopClient = false;
	
	boolean isConnecting = false;
	
	GameResult gameResult = null;

    boolean sentEndTurn = false;
    boolean recdEndTurn = false;
	
	/**
	 * 
	 */
	public MPGameClient(VOBGame g, final GameSetupConfig cfg) {
		this.game = g;
		gameConfig = cfg;
		
		client = new Client();
		client.start();
		
		KryoCommon.register(client);
		
		client.addListener(new Listener() {
			
			public void connected (Connection c) {
				RegisterPlayer rp = new RegisterPlayer();
				rp.name = cfg.playerName;
				client.sendTCP(rp);
			}
			
			public void received (Connection c, Object obj) {
                if (obj instanceof RegisteredPlayer) {
                    RegisteredPlayer reg = (RegisteredPlayer)obj;
                    playerId = reg.id;
                    gameConfig.isHost = reg.host;
                    Gdx.app.log(LOG, String.format("RegisteredPlayer rec'd (id: %d)", playerId));
                }

                if (obj instanceof RegisteredPlayer[]) {
                    Gdx.app.log(LOG, "Playerlist rec'd");
                    RegisteredPlayer[] plist = (RegisteredPlayer[])obj;
                    players.clear();
                    for (int p=0;p<plist.length;p++) {
                        Player pl = new Player(plist[p].id, plist[p].name, plist[p].color, plist[p].team);
                        players.add(pl);
                        if (pl.getPlayerId() == playerId) player = pl;
                    }
                }

                if (obj instanceof KryoCommon.GameDetails) {

                    gameConfig.mapName = ((KryoCommon.GameDetails) obj).mapName;

                }

                if (obj instanceof StartGame) {
                    // we want to start the game, but we need to load our objects on the other thread, where we have an OpenGL context
                    game.setLoadGame();
                }

                if (controller != null) {
					if (obj instanceof Command) {
						Command cmd = (Command)obj;
						if (cmd instanceof Unpause) System.out.println("Unpause " + Integer.toString(cmd.turn));
						if (!(obj instanceof StartTurn || obj instanceof EndTurn)) {
							controller.queueCommand(cmd);
						}
					}
				}

				if (obj instanceof GameResult) {
					Gdx.app.log(LOG, "GameResult rec'd");
					gameResult = (GameResult)obj;
				}
				
				if (obj instanceof StartTurn) {
					Gdx.app.log(LOG, "StartTurn");
				}
				
				if (obj instanceof EndTurn) {
					Gdx.app.log(LOG, "EndTurn");
					if (controller != null) {
                        controller.setCommandQueue(((EndTurn)obj).commands);
                        Gdx.app.log(LOG, String.format("EndTurn.commands.length = %d", ((EndTurn)obj).commands.length));

                        sentEndTurn = false;
                        recdEndTurn = true;
                    }
				}
			}
			
			public void disconnected (Connection c) {
				
			}
		});
		
		isConnecting = true;
		host = cfg.hostName;
		new Thread() {
			public void run() {
				try {
					
					client.connect(5000, host, KryoCommon.tcpPort);
					
				} catch (IOException e) {
					// TODO Auto-generated catch block
					Gdx.app.log(LOG, "Client connection failed: " + e.getMessage());
					e.printStackTrace();
				} 
				isConnecting = false;
			}
		}.start();
	}
	
	public void startGame() {
		// OK, this should be called within an opengl context, so we can create everything
		controller = new GameController(gameConfig.mapName, players);
		controller.LOG = controller.LOG + "#MPClient";
		
		sendStartGame();
	}
	
	public void sendStartGame() {
		// report that we're ready
		client.sendTCP(new StartGame());
	}
	
	public void endGame() {
		client.stop();
		controller.setState(GameStates.GAMEOVER);
		game.setGameResult(gameResult);
	}

    public void sendEndTurn() {
        sentEndTurn = true;
        client.sendTCP(new EndTurn());
    }
	
	public void stop() {
		stopClient = true;
	}
	
	public boolean update(float delta) {
		if (stopClient) {
			client.stop();
			return false;
		}

		if (controller == null) return true;

        controller.update(delta);

        if (recdEndTurn) {
            controller.doTurn();
            recdEndTurn = false;
        }

        if (controller.isNextTurn() || controller.getTurnTimer() <= 0) {
            sendEndTurn();
        }

        if (gameResult != null) {
            endGame();
            return false;
        }

		return true;
	}
	
	public void addAIPlayer(int team) {
		AddAIPlayer add = new AddAIPlayer();
		add.team = team;
		client.sendTCP(add);
	}
	
	public void removePlayer(int playerId) {
		RemovePlayer rem = new RemovePlayer();
		rem.id = playerId;
		client.sendTCP(rem);
	}
	
	public void sendCommand(Command cmd) {
		client.sendTCP(cmd);
	}
	
	public Array<Player> getPlayers() {
		return players;
	}
	
	public Player getPlayer() {
		return player;
	}
	
	public boolean isConnected() {
		if (client == null) return false;
		
		return client.isConnected();
	}
	
	public boolean isConnecting() {
		return isConnecting;
	}

	@Override
	public void setGameController(GameController controller) {
		this.controller = controller;
	}

	@Override
	public GameController getGameController() {
		return controller;
	}
}