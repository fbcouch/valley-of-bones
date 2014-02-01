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
import java.util.HashMap;

import com.ahsgaming.valleyofbones.*;
import com.ahsgaming.valleyofbones.ai.AIPlayer;
import com.ahsgaming.valleyofbones.ai.FSMAIPlayer;
import com.ahsgaming.valleyofbones.network.KryoCommon.AddAIPlayer;
import com.ahsgaming.valleyofbones.network.KryoCommon.RegisterPlayer;
import com.ahsgaming.valleyofbones.network.KryoCommon.RegisteredPlayer;
import com.ahsgaming.valleyofbones.network.KryoCommon.RemovePlayer;
import com.ahsgaming.valleyofbones.network.KryoCommon.StartGame;
import com.ahsgaming.valleyofbones.screens.GameSetupConfig;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Net;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.net.HttpParametersUtils;
import com.badlogic.gdx.utils.*;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;

/**
 * @author jami
 *
 */
public class GameServer implements NetController {
	public String LOG = "GameServer";
    String globalServerUrl = (VOBGame.DEBUG_GLOBAL_SERVER ? "http://localhost:4730" : "http://secure-caverns-9874.herokuapp.com");

    final VOBGame game;

	Server server, broadcastServer;
	GameController controller;
	
	GameSetupConfig gameConfig;

    Array<RegisteredPlayer> registeredPlayers = new Array<RegisteredPlayer>();
    Array<RegisteredPlayer> registeredSpectators = new Array<RegisteredPlayer>();

    ObjectMap<Connection, Integer> connMap = new ObjectMap<Connection, Integer>();

	Array<Player> players = new Array<Player>();
    Array<Command> recdCommands = new Array<Command>();
//    HashMap<Integer, KryoCommon.Spectator> spectators = new HashMap<Integer, KryoCommon.Spectator>();
//	ObjectMap<Connection, NetPlayer> connMap = new ObjectMap<Connection, NetPlayer>();


	int nextPlayerId = 0;
	int hostId = -1;

    boolean loadGame = false;
	
	boolean stopServer = false;
	
	boolean gameStarted = false;

    GameResult gameResult;

    boolean endTurnRecd;

    int publicServerId = -1;

    float serverPing = 1800;
    float serverPingTimeout = 0;

    float awaitReconnectCountdown = 0;
    float awaitReconnectTime = 5;
    boolean awaitReconnect = false;

    int maxPlayers = 2;
	
	/**
	 * 
	 */
	public GameServer(final VOBGame game, GameSetupConfig cfg) {
		this.game = game;
		gameConfig = cfg;

        init();
    }

    public void reset() {
        if (server != null) server.close();
        if (broadcastServer != null) broadcastServer.close();
        controller = null;
        loadGame = false;
        gameStarted = false;
        nextPlayerId = 0;
        players = new Array<Player>();
        recdCommands = new Array<Command>();
//        spectators = new HashMap<Integer, KryoCommon.Spectator>();
//        connMap = new ObjectMap<Connection, NetPlayer>();
        registeredPlayers = new Array<RegisteredPlayer>();
        registeredSpectators = new Array<RegisteredPlayer>();
        connMap = new ObjectMap<Connection, Integer>();
        hostId = -1;
        stopServer = false;
        gameResult = null;
        awaitReconnectCountdown = 0;
        awaitReconnectTime = 0;
        awaitReconnect = false;

        removePublicServer();

        init();
    }

    public void init() {
		// setup the KryoNet server
		server = new Server();
		KryoCommon.register(server);
		
		broadcastServer = new Server();
		
		try {
			server.bind(gameConfig.hostPort);
			server.start();
		} catch (Exception ex) {
            Gdx.app.log(LOG, "Server failed to start");
			Gdx.app.log(LOG, ex.getMessage());
			stopServer = true;
            return;
		}

        try {
            broadcastServer.bind(0, KryoCommon.udpPort);
            broadcastServer.start();
        } catch (IOException ex) {
            Gdx.app.log(LOG, ex.getMessage());
        }

        // ai vs ai
//        addAIPlayer(0);
//        addAIPlayer(1);
//        loadGame = true;

		
		
		server.addListener(new Listener() {

			public void received(Connection c, Object obj) {
				if (obj instanceof RegisterPlayer) {
					RegisterPlayer rp = (RegisterPlayer)obj;

                    if (!rp.version.equals(VOBGame.VERSION)) {
                        // wrong version!
                        server.sendToTCP(c.getID(), new KryoCommon.VersionError());
                        return;
                    }

                    if (gameStarted) {
                        for (Player p: players) {
                            Gdx.app.log(LOG, p.getPlayerName() + ": " + connMap.findKey(p.getPlayerId(), true).isConnected());
                            if (!(p instanceof AIPlayer) && p.getPlayerName().equals(rp.name) && !connMap.findKey(p.getPlayerId(), true).isConnected()) {
                                connMap.remove(connMap.findKey(p.getPlayerId(), true));
                                connMap.put(c, p.getPlayerId());
                                Unpause up = new Unpause();
                                up.owner = -1;
                                up.turn = controller.getGameTurn();
                                Gdx.app.log(LOG, "Queuing unpause");
                                controller.queueCommand(up);
                                sendCommand(up);
                                awaitReconnect = false;
                            }
                        }
                        return;
                    }

//                    if (players.size >= Player.AUTOCOLORS.length) {
//                        server.sendToTCP(c.getID(), new KryoCommon.GameFullError());
//                        return; // TODO should join as spectator?
//                    }

                    int id = getNextPlayerId();
					Color use = new Color(1, 1, 1, 1);
					RegisteredPlayer reg = new RegisteredPlayer();
                    reg.id = id;
                    reg.name = rp.name;
                    if (players.size < maxPlayers && !rp.spectator) {
                        reg.spectator = false;
                        boolean found = false;
                        boolean[] colorsUsed = new boolean[Player.AUTOCOLORS.length];
                        for (RegisteredPlayer p: registeredPlayers) {
                            colorsUsed[p.color] = true;
                        }
                        if (rp.prefColor < Player.AUTOCOLORS.length && rp.prefColor >= 0 && !colorsUsed[rp.prefColor]) {
                            reg.color = rp.prefColor;
                        } else {
                            int i = 0;
                            while (i < Player.AUTOCOLORS.length && colorsUsed[i]) { i++; }
                            i = Math.min(i, Player.AUTOCOLORS.length - 1);
                            reg.color = i;
                        }
                    } else {
                        reg.spectator = true;
                    }

                    if (hostId == -1)
                        hostId = reg.id;

                    reg.host = (reg.id == hostId);

                    if (reg.spectator) {
                        registeredSpectators.add(reg);
                    } else {
                        registeredPlayers.add(reg);
                    }

                    connMap.put(c, reg.id);
                    server.sendToTCP(c.getID(), reg);

                    sendPlayerList();
					sendSetupInfo();
				}

                if (obj instanceof KryoCommon.Spectator) {
                    Gdx.app.log(LOG, "Spectator rec'd");
                    KryoCommon.Spectator sp = (KryoCommon.Spectator)obj;

                    if (!sp.version.equals(VOBGame.VERSION)) {
                        server.sendToTCP(c.getID(), new KryoCommon.VersionError());
                        return;
                    }
                    Gdx.app.log(LOG, "Spectator joined");


                    RegisteredPlayer reg = new RegisteredPlayer();
                    reg.id = getNextPlayerId();
                    reg.name = sp.name;
                    reg.color = 0;

                    if (hostId == -1)
                        hostId = reg.id;

                    reg.host = (reg.id == hostId);
                    reg.spectator = true;

                    registeredSpectators.add(reg);
                    server.sendToTCP(c.getID(), reg);

                    sendPlayerList();
                    sendSetupInfo();

                    if (gameStarted) {
                        server.sendToTCP(c.getID(), new StartGame());
                        Command[] cmds = new Command[controller.getCommandHistory().size];
                        for (int i = 0; i < cmds.length; i++) cmds[i] = controller.getCommandHistory().get(i);
                        server.sendToTCP(c.getID(), cmds);

                        KryoCommon.GameUpdate gameUpdate = new KryoCommon.GameUpdate();
                        gameUpdate.currentPlayer = controller.getCurrentPlayer().getPlayerId();
                        gameUpdate.turn = controller.getGameTurn();
                        gameUpdate.timer = controller.getTurnTimer();
                        server.sendToTCP(c.getID(), gameUpdate);
                    }
                }

                if (obj instanceof KryoCommon.UpdatePlayer) {
                    KryoCommon.UpdatePlayer update = (KryoCommon.UpdatePlayer)obj;
                    if (connMap.get(c) != update.id) return;

                    RegisteredPlayer player = null;
                    for (RegisteredPlayer rp: registeredPlayers) {
                        if (rp.id == update.id) {
                            player = rp;
                        }
                    }

                    if (player != null) {
                        player.color = update.color;
                        player.ready = update.ready;
                    }

                    sendPlayerList();
                }
				
				if (obj instanceof AddAIPlayer) {
					// TODO implement teams/player limits more robustly
					// make sure this is from the host
					if (connMap.get(c) != hostId) return;
					
					addAIPlayer();
					
					sendPlayerList();
				}
				
				if (obj instanceof RemovePlayer) {
					// make sure this is from the host
                    if (connMap.get(c) != hostId) return;
					
					removePlayer(((RemovePlayer)obj).id);
					
					sendPlayerList();
				}
				
				if (obj instanceof KryoCommon.GameDetails) {
                    if (connMap.get(c) != hostId) return;
					
					if (controller == null) {
						gameConfig.mapName = ((KryoCommon.GameDetails)obj).map;
						sendSetupInfo();
					}
				}
				
				// TODO need to check this for validity
				if (controller != null) {
					// the player represented by this connection has finished loading
					if (obj instanceof StartGame) {
                        int id = connMap.get(c);
                        Player player = null;
                        for (Player p: players) {
                            if (p.getPlayerId() == id)
                                player = p;
                        }
                        player.setLoaded(true);

					} else if (obj instanceof Command) {
						Command cmd = (Command)obj;
						if (cmd.owner != connMap.get(c)) cmd.owner = connMap.get(c);

                        recdCommands.add((Command)obj);
					}
				} else {
                    if (obj instanceof StartGame) {
                        if (connMap.get(c) != hostId) return;
                        for (RegisteredPlayer rp: registeredPlayers) {
                            if (!(rp.ready || rp.isAI)) {
                                return;
                            }
                        }
                        loadGame = true;
                    }
                }
			}
			
			public void connected (Connection c) {
			}
			
			public void disconnected (Connection c) {
                if (gameStarted) {
                    Player player = findPlayerById(connMap.get(c));

                    if (player == null) return;
                    // wait for reconnect
                    Pause p = new Pause();
                    p.isAuto = true;
                    p.owner = -1;
                    p.turn = controller.getGameTurn();
                    controller.queueCommand(p);
                    sendCommand(p);
                    awaitReconnectCountdown = awaitReconnectTime;
                    awaitReconnect = true;
                } else {
                    int id = connMap.get(c);

                    RegisteredPlayer player = null;
                    for (RegisteredPlayer rp: registeredPlayers) {
                        if (rp.id == id)
                            player = rp;
                    }
                    if (player != null) {
                        registeredPlayers.removeValue(player, true);
                    } else {
                        for (RegisteredPlayer rp: registeredSpectators) {
                            if (rp.id == id) {
                                player = rp;
                            }
                        }

                        if (player != null) {
                            registeredSpectators.removeValue(player, true);
                        }
                    }

                    connMap.remove(c);

                    if (hostId == id) {
                        // find a new host
                        hostId = -1;

                        for (RegisteredPlayer rp: registeredPlayers) {
                            if (!rp.isAI) {
                                player = rp;
                                hostId = rp.id;
                                break;
                            }
                        }

                        if (hostId == -1) {
                            for (RegisteredPlayer rp: registeredSpectators) {
                                if (!rp.isAI) {
                                    player = rp;
                                    hostId = rp.id;
                                    break;
                                }
                            }
                        }

                        if (hostId == -1) {
                            reset();
                            return;
                        }

                        player.host = true;
                        server.sendToTCP(connMap.findKey(player.id, true).getID(), player);
                    }

                    if (controller == null || controller.getGameResult() == null) sendPlayerList();
                }
			}
		});

        if (gameConfig.isPublic) {
            registerPublicServer();
            serverPingTimeout = serverPing;
        }
	}

    public Player findPlayerById(int id) {
        for (Player p: players) {
            if (p.getPlayerId() == id)
                return p;
        }
        return null;
    }
	
	public void startGame() {
		// the host clicked startGame --> send out the StartGame message so that the clients load and report
        for (RegisteredPlayer rp: registeredPlayers) {
            if (rp.isAI) {
                players.add(new FSMAIPlayer(this, rp.id, rp.name, Player.AUTOCOLORS[rp.color]));
            } else {
                players.add(new Player(rp.id, rp.name, Player.AUTOCOLORS[rp.color]));
            }
        }

		controller = new GameController(gameConfig, players);
		controller.LOG = controller.LOG + "#Server";

        // TODO allow configuration of who goes first?
        controller.setCurrentPlayer(players.get((MathUtils.random(players.size - 1))));
        loadGame = false;

		// don't need to broadcast on UDP anymore - thats just confusing
		broadcastServer.close();
		
		sendStartGame();
	}
	
	public void endGame() {
		// the controller has a game result --> broadcast it to everybody and close the server
		GameResult result = controller.getGameResult();
		controller.setState(GameStates.GAMEOVER);
//        game.setGameResult(result);
        gameResult = result;

		Gdx.app.log(LOG, String.format("GameResult: winner: %d; Losers: (%d)", result.winner, result.losers.length));
		
		server.sendToAllTCP(result);
		server.close();

        if (gameConfig.isPublic) {
            sendPublicServerResult();
        }
	}

	public void stop() {
		server.close();

        removePublicServer();
	}

	public boolean update(float delta) {

        if (!gameStarted && gameConfig.isPublic) {
            serverPingTimeout -= delta;
            if (serverPingTimeout <= 0) {
                serverPingTimeout = serverPing;
                sendPublicServerUpdate();
            }
        }
		
		if ((controller == null)) return true;
		
		// once the controller exists, we need to check to make sure all players are ready
		if (!gameStarted) {
			boolean allReady = true;
			for (int p=0;p<players.size;p++) {
				if (!players.get(p).isLoaded()) {
					allReady = false;
				}
			}
			
			// once all are ready, we need to unpause the game
			if (allReady) {
				Unpause up = new Unpause();
				up.owner = -1;
				up.turn = controller.getGameTurn();
				// important to both send the command out and add to the local queue!
				server.sendToAllTCP(up);
				controller.queueCommand(up);
				gameStarted = true;
                endTurnRecd = false;
                if (gameConfig.isPublic) sendPublicServerUpdate();
			}
		}

		if (gameStarted) {

            // moved this logic here to avoid "nested" iterator problem, which I think occurred because of the call to validate on another thread
            synchronized (recdCommands) {
                for (Command cmd: recdCommands) {
                    // discard past, future, invalid, or duplicated commands
                    if (cmd.turn == controller.getGameTurn() && !controller.getCommandQueue().contains(cmd, false) && controller.validate(cmd)) {
                        if (cmd instanceof EndTurn) {
                            endTurnRecd = true;
                        } else {
                            controller.queueCommand(cmd);
                            server.sendToAllTCP(cmd);
                        }
                    }
                }
                recdCommands.clear();
            }

			controller.update(delta);

            if (awaitReconnect) {
                awaitReconnectCountdown -= delta;
                if (awaitReconnectCountdown < 0) {
                    for (int p = 0; p < players.size; p++) {
                        if (players.get(p) instanceof AIPlayer || connMap.findKey(players.get(p).getPlayerId(), true).isConnected()) {
                            controller.declareWinner(players.get(p));
                            awaitReconnect = false;
                        }
                    }
                }
            }

            if (isEndTurn())
            {
                EndTurn endTurn = new EndTurn(controller.getCommandQueue());
                endTurn.turn = controller.getGameTurn();
                endTurn.owner = controller.getCurrentPlayer().getPlayerId();
                server.sendToAllTCP(endTurn);
                controller.setNextTurn(true);
                controller.doTurn();
                endTurnRecd = false;
            }
		}
		

        if (controller.getGameResult() != null) {
            endGame();
            return false;
        }

		return true;
	}
	
	public void sendPlayerList() {
        RegisteredPlayer[] list = null;
        if (!gameStarted) {
            list = new RegisteredPlayer[registeredPlayers.size + registeredSpectators.size];
            for (int p = 0; p < list.length; p++) {
                if (p < registeredPlayers.size) {
                    list[p] = registeredPlayers.get(p);
                } else {
                    list[p] = registeredSpectators.get(p - registeredPlayers.size);
                }
            }

        } else {
            list = new RegisteredPlayer[players.size];
            for (int p = 0; p < players.size; p++) {
                RegisteredPlayer rp = new RegisteredPlayer();
                Player pl = players.get(p);
                rp.id = pl.getPlayerId();
                rp.name = pl.getPlayerName();
                int c = 0;
                while (!pl.getPlayerColor().equals(Player.AUTOCOLORS[c]) && c < Player.AUTOCOLORS.length - 1)
                    c++;
                rp.color = c;
                list[p] = rp;
            }
        }
		if (gameConfig.isPublic) sendPublicServerUpdate();
		server.sendToAllTCP(list);
	}
	
	public void sendSetupInfo() {
		KryoCommon.GameDetails si = new KryoCommon.GameDetails();
		si.map = gameConfig.mapName;
        si.firstMove = -1;
        si.hostId = hostId;
        si.rules = 0;
		server.sendToAllTCP(si);
	}
	
	protected int getNextPlayerId() {
		int id = nextPlayerId;
		nextPlayerId += 1;
		return id;
	}

    public boolean isEndTurn() {
        if (controller != null) {
            if (controller.getTurnTimer() <= 0) return true;
        }

        return endTurnRecd;
    }

	@Override
	public void setGameController(GameController controller) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public GameController getGameController() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void sendStartGame() {
		StartGame startGame = new StartGame();
        startGame.currentPlayer = controller.getCurrentPlayer().getPlayerId();
        server.sendToAllTCP(startGame);
	}

	@Override
	public void addAIPlayer() {
		if (registeredPlayers.size < maxPlayers) {
            RegisteredPlayer reg = new RegisteredPlayer();
            reg.id = getNextPlayerId();
            reg.name = "AI Player";
            reg.color = Player.getUnusedColorId(registeredPlayers);
            reg.isAI = true;
            reg.host = false;
            reg.ready = true;
            reg.spectator = false;
            registeredPlayers.add(reg);
            sendPlayerList();
        }
	}

	@Override
	public void removePlayer(int playerId) {
		
		Array<Player> remove = new Array<Player>();
		for (Player p: players) {
			if (p.getPlayerId() == playerId) {
				remove.add(p);
			}
		}
		
		players.removeAll(remove, true);
	}

	@Override
	public Array<Player> getPlayers() {
		return this.players;
	}

    @Override
    public Player getPlayer() {
        return null;
    }

	@Override
	public void sendCommand(Command cmd) {
		server.sendToAllTCP(cmd);
	}

    @Override
    public void sendAICommand(Command cmd) {
        if (!controller.getCommandQueue().contains(cmd, false) && controller.validate(cmd)) {
            if (cmd instanceof EndTurn) {
                endTurnRecd = true;
            } else {
                controller.queueCommand(cmd);
                server.sendToAllTCP(cmd);
            }
        }
    }

	@Override
	public boolean isConnected() {
		return false;
	}

	@Override
	public boolean isConnecting() {
		return false;
	}

    public boolean isLoadGame() {
        return loadGame;
    }

    public boolean isGameStarted() {
        return gameStarted;
    }

    public boolean isStopServer() {
        return stopServer;
    }

    public void setStopServer(boolean stop) {
        stopServer = stop;
    }

    public GameResult getGameResult() {
        return gameResult;
    }

    public GameSetupConfig getGameConfig() {
        return gameConfig;
    }

    public void registerPublicServer() {
        Net.HttpRequest httpGet = new Net.HttpRequest(Net.HttpMethods.POST);
        httpGet.setUrl(String.format("%s/server", globalServerUrl));

        HashMap parameters = new HashMap();
        parameters.put("name", gameConfig.hostName);
        parameters.put("port", Integer.toString(gameConfig.hostPort));
//        parameters.put("ip", Utils.getIPAddress(true));

        httpGet.setContent(HttpParametersUtils.convertHttpParameters(parameters));

        Gdx.net.sendHttpRequest(httpGet, new Net.HttpResponseListener() {
            @Override
            public void handleHttpResponse(Net.HttpResponse httpResponse) {
                String response = httpResponse.getResultAsString();
                switch(httpResponse.getStatus().getStatusCode()) {
                    case 400:
                    case 404:
                        Gdx.app.log(LOG, "Failed to register with global server");
                        Gdx.app.log(LOG, response);
                        break;
                    case 200:
                        Gdx.app.log(LOG, "Registered with global server");
                        Gdx.app.log(LOG, response);
                        JsonReader reader = new JsonReader();
                        JsonValue result = reader.parse(response);
                        if (result != null)
                            publicServerId = result.getInt("id", -1);
                        Gdx.app.log(LOG, Integer.toString(publicServerId));
                        break;
                    default:
                        Gdx.app.log(LOG, String.format("Unknown HTTP Status Code: %d", httpResponse.getStatus().getStatusCode()));
                        Gdx.app.log(LOG, response);
                }
            }

            @Override
            public void failed(Throwable t) {
                Gdx.app.log(LOG, "Failed to register with global server");
                t.printStackTrace();
            }
        });
    }

    public void removePublicServer() {
        if (publicServerId >= 0) {
            Net.HttpRequest httpDelete = new Net.HttpRequest(Net.HttpMethods.DELETE);
            httpDelete.setUrl(String.format("%s/server/%d", globalServerUrl, publicServerId));
            Gdx.net.sendHttpRequest(httpDelete, new Net.HttpResponseListener() {
                @Override
                public void handleHttpResponse(Net.HttpResponse httpResponse) {
                    Gdx.app.log(LOG, httpResponse.getResultAsString());
                }

                @Override
                public void failed(Throwable t) {
                    t.printStackTrace();
                }
            });
        }

        publicServerId = -1;
    }

    public void sendPublicServerResult() {
        if (publicServerId >= 0) {
            Net.HttpRequest httpPost = new Net.HttpRequest(Net.HttpMethods.POST);
//            httpPost.setHeader("Content-Type", "application/json");
            httpPost.setUrl(String.format("%s/game", globalServerUrl));
            HashMap parameters = new HashMap();
            String playersString = "{}";
            if (controller.getPlayers().size >= 2) {
                playersString = String.format("{ \"%d\": \"%s\", \"%d\": \"%s\" }",
                                    controller.getPlayers().get(0).getPlayerId(),
                                    controller.getPlayers().get(0).getPlayerName(),
                                    controller.getPlayers().get(1).getPlayerId(),
                                    controller.getPlayers().get(1).getPlayerName());
            }
            String historyString = "[";
            boolean first = true;
            for (Command command: controller.getCommandHistory()) {
                if (first) {
                    first = false;
                } else {
                    historyString += ", ";
                }
                historyString += command.toJson();
            }
            historyString += "]";
            parameters.put("game", "{ \"history\": " + historyString + ", \"result\": " + gameResult.winner + ", \"map\": \"" + controller.getMapName() + "\", \"players\": " + playersString + "}");
            parameters.put("version", VOBGame.VERSION);
            parameters.put("map", controller.getMapName());

            httpPost.setContent(HttpParametersUtils.convertHttpParameters(parameters));

            Gdx.net.sendHttpRequest(httpPost, new Net.HttpResponseListener() {
                @Override
                public void handleHttpResponse(Net.HttpResponse httpResponse) {
                    String response = httpResponse.getResultAsString();

                    switch(httpResponse.getStatus().getStatusCode()) {
                        case 400:
                        case 404:
                            Gdx.app.log(LOG, "Failed to upload game result");
                            Gdx.app.log(LOG, response);
                            break;
                        case 200:
                            Gdx.app.log(LOG, "Uploaded game result");
                            Gdx.app.log(LOG, response);
                            break;
                        default:
                            Gdx.app.log(LOG, "Unknown response code " + httpResponse.getStatus().getStatusCode());
                            Gdx.app.log(LOG, response);
                    }
                }

                @Override
                public void failed(Throwable t) {
                    t.printStackTrace();
                }
            });

            Gdx.app.log(LOG, httpPost.getContent());
        }
    }

    public void sendPublicServerUpdate() {
        Net.HttpRequest req = new Net.HttpRequest(Net.HttpMethods.POST);
        req.setUrl(String.format("%s/server/%d", globalServerUrl, publicServerId));
        HashMap parameters = new HashMap();
        parameters.put("players", "" + players.size);
        Gdx.app.log("GameStarted", "" + gameStarted);
        parameters.put("status", "" + (gameStarted ? 1 : 0));
        req.setContent(HttpParametersUtils.convertHttpParameters(parameters));
        Gdx.app.log("LOG", req.getContent());
        Gdx.net.sendHttpRequest(req, new Net.HttpResponseListener() {
            @Override
            public void handleHttpResponse(Net.HttpResponse httpResponse) {
                switch(httpResponse.getStatus().getStatusCode()) {
                    default:
                    case 404:
                        registerPublicServer();
                        serverPingTimeout = 60;
                        break;
                    case 200:
                        Gdx.app.log(LOG, "Update success");
                        break;
                }
                Gdx.app.log(LOG, httpResponse.getResultAsString());
            }

            @Override
            public void failed(Throwable t) {
                Gdx.app.log(LOG, "Update failed");
            }
        });
    }

    @Override
    public void setMap(String map) {
        // TODO
    }

    @Override
    public Array<String> getSpectators() {
        Array<String> list = new Array<String>();
        for (RegisteredPlayer spectator: registeredSpectators) {
            list.add(spectator.name);
        }
        return list;
    }
}
