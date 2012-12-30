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

import com.ahsgaming.spacetactics.GameController;
import com.ahsgaming.spacetactics.Player;
import com.ahsgaming.spacetactics.SpaceTacticsGame;
import com.ahsgaming.spacetactics.network.KryoCommon.RegisterPlayer;
import com.ahsgaming.spacetactics.network.KryoCommon.RegisteredPlayer;
import com.badlogic.gdx.Gdx;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;

/**
 * @author jami
 *
 */
public class GameServer {
	
	Server server;
	GameController controller;
	int sinceLastNetTick = 0;
	int sinceLastGameTick = 0;
	long lastTimeMillis = 0;
	
	ArrayList<Player> players = new ArrayList<Player>();
	int nextPlayerId = 0;
	
	/**
	 * 
	 */
	public GameServer() {
		// setup the KryoNet server
		server = new Server();
		KryoCommon.register(server);
		
		// TODO set up a broadcast server for LAN stuff
		//players.add(new Player(0, Player.AUTOCOLORS[0]));
		//players.add(new Player(1, Player.AUTOCOLORS[1]));
		
		try {
			server.bind(KryoCommon.tcpPort);
			server.start();
		} catch (IOException ex) {
			Gdx.app.log(SpaceTacticsGame.LOG + ":GameServer", ex.getStackTrace().toString());
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
					
					// TODO need to check that this color isn't already taken
					Player p = new Player(nextPlayerId, rp.name, Player.AUTOCOLORS[nextPlayerId]);
					nextPlayerId += 1;
					
					players.add(p);
					
					RegisteredPlayer reg = new RegisteredPlayer();
					reg.id = p.getPlayerId();
					reg.name = p.getPlayerName();
					reg.color = p.getPlayerColor();
					server.sendToTCP(c.getID(), reg);
					sendPlayerList();
				}
				
				// TODO need to check this for validity
				if (obj instanceof Command) {
					Command cmd = (Command)obj;
					cmd.tick = controller.getGameTick() + (cmd instanceof Unpause ? 0 : 2);
					controller.queueCommand(cmd);
					server.sendToAllTCP(cmd);
				}
			}
			
			public void disconnected (Connection c) {
				
			}
		});
		
	}
	
	public void startGame() {
		controller = new GameController("", players);
		controller.LOG = controller.LOG + "#Server";
		
		lastTimeMillis = System.currentTimeMillis();
	}

	public boolean update() {
		
		
		if ((controller == null)) return true;
		
		
		
		long time = System.currentTimeMillis();
		int delta = (int)(time - lastTimeMillis);
		lastTimeMillis = time;
		
		//Gdx.app.log("Server#Update", String.format("time: %d, lastTime: %d, delta: %d, sinceLastGameTick: %d", time, lastTimeMillis, delta, sinceLastGameTick));
		if (delta < 0) return true;
		
		sinceLastNetTick += delta;
		sinceLastGameTick += delta;
		
		while (sinceLastNetTick >= KryoCommon.NET_TICK_LENGTH * 0.001f) {
			// TODO net tick
			sinceLastNetTick -= KryoCommon.NET_TICK_LENGTH;
			//Gdx.app.log("Server", "NET TICK");
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
			list[p] = rp;
		}
		
		server.sendToAllTCP(list);
	}
}
