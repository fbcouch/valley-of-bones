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
package com.ahsgaming.spacetactics.network;

import java.io.IOException;
import java.util.ArrayList;

import com.ahsgaming.spacetactics.AIPlayer;
import com.ahsgaming.spacetactics.GameController;
import com.ahsgaming.spacetactics.NetPlayer;
import com.ahsgaming.spacetactics.Player;
import com.ahsgaming.spacetactics.SpaceTacticsGame;
import com.ahsgaming.spacetactics.network.KryoCommon.AddAIPlayer;
import com.ahsgaming.spacetactics.network.KryoCommon.RegisterPlayer;
import com.ahsgaming.spacetactics.network.KryoCommon.RegisteredPlayer;
import com.ahsgaming.spacetactics.network.KryoCommon.RemovePlayer;
import com.ahsgaming.spacetactics.network.KryoCommon.SetupInfo;
import com.ahsgaming.spacetactics.network.KryoCommon.StartGame;
import com.ahsgaming.spacetactics.screens.GameSetupScreen.GameSetupConfig;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.utils.ObjectMap;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;

/**
 * @author jami
 *
 */
public class GameServer {
	
	Server server, broadcastServer;
	GameController controller;
	int sinceLastNetTick = 0;
	int sinceLastGameTick = 0;
	long lastTimeMillis = 0;
	
	GameSetupConfig gameConfig;
	
	ArrayList<Player> players = new ArrayList<Player>();
	ObjectMap<Connection, NetPlayer> connMap = new ObjectMap<Connection, NetPlayer>();
	int nextPlayerId = 0;
	Connection host = null;
	
	boolean stopServer = false;
	
	boolean gameStarted = false;
	
	/**
	 * 
	 */
	public GameServer(GameSetupConfig cfg) {
		gameConfig = cfg;
		// setup the KryoNet server
		server = new Server();
		KryoCommon.register(server);
		
		broadcastServer = new Server();
		
		// TODO set up a broadcast server for LAN stuff
		//players.add(new Player(0, Player.AUTOCOLORS[0]));
		//players.add(new Player(1, Player.AUTOCOLORS[1]));
		
		try {
			server.bind(KryoCommon.tcpPort);
			server.start();
			
			broadcastServer.bind(0, KryoCommon.udpPort);
			broadcastServer.start();
		} catch (IOException ex) {
			Gdx.app.log(SpaceTacticsGame.LOG + ":GameServer", ex.getMessage());
			Gdx.app.exit();
		}
		
		
		
		server.addListener(new Listener() {
			
			public void received(Connection c, Object obj) {
				
				if (obj instanceof RegisterPlayer) {
					RegisterPlayer rp = (RegisterPlayer)obj;
					
					if (players.size() >= Player.AUTOCOLORS.length) return; // TODO should join as spectator? 
					
					// is that player already registered?
					for (Player p: players) {
						if (p.getPlayerName() == rp.name) return; // TODO maybe drop the connection?
					}
					
					int id = getNextPlayerId();
					Color use = new Color(1, 1, 1, 1);
					int team = -1;
					if (players.size() <= 4) {
						use = Player.getUnusedColor(players);
						int cntTeam1 = 0, cntTeam2 = 0;
						for (Player p: players) {
							if (p.getTeam() == 0) cntTeam1++;
							if (p.getTeam() == 1) cntTeam2++;
						}
						
						if (cntTeam1 > cntTeam2) {
							team = 1;
						} else {
							team = 0;
						}
					}
					
					NetPlayer p = new NetPlayer(id, rp.name, use, team);
					
					players.add(p);
					connMap.put(c, p);
					
					RegisteredPlayer reg = new RegisteredPlayer();
					reg.id = p.getPlayerId();
					reg.name = p.getPlayerName();
					reg.color = p.getPlayerColor();
					reg.team = p.getTeam();
					server.sendToTCP(c.getID(), reg);
					sendPlayerList();
					sendSetupInfo();
				}
				
				if (obj instanceof AddAIPlayer) {
					// TODO implement teams/player limits more robustly
					// make sure this is from the host
					if (c != host) return;
					
					if (players.size() < 4) {
						int id = getNextPlayerId();
						Color use = Player.getUnusedColor(players);
						
						players.add(new AIPlayer(id, "AI Player", use, ((AddAIPlayer)obj).team));
					}
					sendPlayerList();
				}
				
				if (obj instanceof RemovePlayer) {
					// make sure this is from the host
					if (c != host) return;
					
					RemovePlayer rem = (RemovePlayer)obj;
					ArrayList<Player> remove = new ArrayList<Player>();
					for (Player p: players) {
						if (p.getPlayerId() == rem.id) {
							remove.add(p);
						}
					}
					
					players.removeAll(remove);
					sendPlayerList();
				}
				
				if (obj instanceof SetupInfo) {
					if (c != host) return;
					
					if (controller == null) {
						gameConfig.mapName = ((SetupInfo)obj).mapName;
						sendSetupInfo();
					}
				}
				
				// TODO need to check this for validity
				if (controller != null) {
					// the player represented by this connection is ready
					if (obj instanceof StartGame) {
						connMap.get(c).setReady(true);
					}
					
					if (obj instanceof Command) {
						Command cmd = (Command)obj;
						if (cmd.owner != connMap.get(c).getPlayerId()) cmd.owner = connMap.get(c).getPlayerId();
						cmd.tick = controller.getNetTick() + (cmd instanceof Unpause ? 0 : 2);
						if (controller.validate(cmd)) {
							controller.queueCommand(cmd);
							server.sendToAllTCP(cmd);
						}
					}
				}
			}
			
			public void connected (Connection c) {
				if (host == null) host = c;
				
				if (gameStarted) c.close();
			}
			
			public void disconnected (Connection c) {
				Player p = connMap.get(c);
				if (players.contains(p)) players.remove(p);
				sendPlayerList();
				
				if (host == c) {
					stopServer = true;
				}
			}
		});
		
	}
	
	public void startGame() {
		// the host clicked startGame --> send out the StartGame message so that the clients load and report
		controller = new GameController(gameConfig.mapName, players);
		controller.LOG = controller.LOG + "#Server";
		
		lastTimeMillis = System.currentTimeMillis();
		
		// don't need to broadcast on UDP anymore - thats just confusing
		broadcastServer.close();
		
		server.sendToAllTCP(new StartGame());
	}
	
	public void stop() {
		stopServer = true;
	}

	public boolean update() {
		if (stopServer) {
			server.stop();
			return false;
		}
		
		if ((controller == null)) return true;
		
		// once the controller exists, we need to check to make sure all players are ready
		if (!gameStarted) {
			boolean allReady = true;
			for (int p=0;p<players.size();p++) {
				if (players.get(p) instanceof NetPlayer && !((NetPlayer)players.get(p)).isReady()) {
					allReady = false;
				}
			}
			
			// once all are ready, we need to unpause the game
			if (allReady) {
				Unpause up = new Unpause();
				up.owner = -1;
				up.tick = controller.getNetTick();
				// important to both send the command out and add to the local queue!
				server.sendToAllTCP(up);
				controller.queueCommand(up);
				gameStarted = true;
			}
		}
		
		long time = System.currentTimeMillis();
		int delta = (int)(time - lastTimeMillis);
		lastTimeMillis = time;
		
		//Gdx.app.log("Server#Update", String.format("time: %d, lastTime: %d, delta: %d, sinceLastGameTick: %d", time, lastTimeMillis, delta, sinceLastGameTick));
		if (delta < 0) return true;
		
		sinceLastNetTick += delta;
		sinceLastGameTick += delta;
		
		while (sinceLastNetTick >= KryoCommon.NET_TICK_LENGTH) {
			// TODO net tick
			sinceLastNetTick -= KryoCommon.NET_TICK_LENGTH;
			//Gdx.app.log("Server", "NET TICK");
			for (Player p: players) {
				//if (p instanceof AIPlayer) {
				//	((AIPlayer)p).update(controller, KryoCommon.NET_TICK_LENGTH * 0.001f);
				//}
				p.update(controller, KryoCommon.NET_TICK_LENGTH * 0.001f);
			}
			
			controller.netUpdate(KryoCommon.NET_TICK_LENGTH * 0.001f);
		}
		
		while (sinceLastGameTick >= KryoCommon.GAME_TICK_LENGTH) {
			// TODO game tick
			controller.update(KryoCommon.GAME_TICK_LENGTH * 0.001f);
			sinceLastGameTick -= KryoCommon.GAME_TICK_LENGTH;
			//Gdx.app.log("Server", "GAME TICK");
		}
		
		long sleepTime = KryoCommon.GAME_TICK_LENGTH - (System.currentTimeMillis() - lastTimeMillis) - sinceLastGameTick;
		
		try {
			if (sleepTime > 0) Thread.sleep(sleepTime);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return true;
	}
	
	public void sendPlayerList() {
		RegisteredPlayer[] list = new RegisteredPlayer[players.size()];
		for (int p = 0; p < players.size(); p++) {
			RegisteredPlayer rp = new RegisteredPlayer();
			Player pl = players.get(p);
			rp.id = pl.getPlayerId();
			rp.name = pl.getPlayerName();
			rp.color = pl.getPlayerColor();
			rp.team = pl.getTeam();
			list[p] = rp;
		}
		
		server.sendToAllTCP(list);
	}
	
	public void sendSetupInfo() {
		SetupInfo si = new SetupInfo();
		si.mapName = gameConfig.mapName;
		server.sendToAllTCP(si);
	}
	
	protected int getNextPlayerId() {
		int id = nextPlayerId;
		nextPlayerId += 1;
		return id;
	}
}
