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
import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;

/**
 * @author jami
 *
 */
public class GameClient {
	public String LOG = "GameClient";
	
	Client client;
	String host;
	Connection clientConn;

	int playerId = -1;
	Player player = null;
	
	GameController controller;
	int sinceLastNetTick = 0;
	int sinceLastGameTick = 0;
	long lastTimeMillis = 0;
	
	ArrayList<Player> players = new ArrayList<Player>();
	
	SpaceTacticsGame game;
	
	/**
	 * 
	 */
	public GameClient(SpaceTacticsGame g, final String playerName) {
		this.game = g;
		
		client = new Client();
		client.start();
		
		KryoCommon.register(client);
		
		client.addListener(new Listener() {
			
			public void connected (Connection c) {
				RegisterPlayer rp = new RegisterPlayer();
				rp.name = playerName;
				client.sendTCP(rp);
			}
			
			public void received (Connection c, Object obj) {
				if (controller != null) {
					if (obj instanceof Command) {
						Command cmd = (Command)obj;
						if (cmd instanceof Unpause) System.out.println("Unpause " + Integer.toString(cmd.tick));
						controller.queueCommand(cmd);
					}
				}
				
				if (obj instanceof RegisteredPlayer) {
					RegisteredPlayer reg = (RegisteredPlayer)obj;
					playerId = reg.id;
					Gdx.app.log(LOG, "RegisteredPlayer rec'd");
				}
				
				if (obj instanceof RegisteredPlayer[]) {
					Gdx.app.log(LOG, "Playerlist rec'd");
					RegisteredPlayer[] plist = (RegisteredPlayer[])obj;
					players.clear();
					for (int p=0;p<plist.length;p++) {
						Player pl = new Player(plist[p].id, plist[p].name, plist[p].color);
						players.add(pl);
						if (pl.getPlayerId() == playerId) player = pl;
					}
				}
			}
			
			public void disconnected (Connection c) {
				
			}
		});
		
		host = "localhost";
		try {
			client.connect(5000, host, KryoCommon.tcpPort);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			Gdx.app.log(LOG, "Client connection failed: " + e.getMessage());
			e.printStackTrace();
		}
	}
	
	public void startGame() {
		controller = new GameController("", players);
		controller.LOG = controller.LOG + "#Client";
		
		lastTimeMillis = System.currentTimeMillis();
		
		Unpause cmd = new Unpause();
		cmd.owner = playerId;
		cmd.tick = 0;
		sendCommand(cmd);
	}
	
	public boolean update() {
		if (controller == null) return true;
		
		long time = System.currentTimeMillis();
		int delta = (int)(time - lastTimeMillis);
		lastTimeMillis = time;
		
		//Gdx.app.log("Client#Update", String.format("time: %d, lastTime: %d, delta: %d, sinceLastGameTick: %d", time, lastTimeMillis, delta, sinceLastGameTick));
		if (delta < 0) return true;
		
		sinceLastNetTick += delta;
		sinceLastGameTick += delta;
		
		
		while (sinceLastNetTick >= KryoCommon.NET_TICK_LENGTH) {
			// TODO net tick
			sinceLastNetTick -= KryoCommon.NET_TICK_LENGTH;
		}
		
		while (sinceLastGameTick >= KryoCommon.GAME_TICK_LENGTH) {
			controller.update(KryoCommon.GAME_TICK_LENGTH * 0.001f);
			sinceLastGameTick -= KryoCommon.GAME_TICK_LENGTH;
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
	
	public void sendCommand(Command cmd) {
		client.sendTCP(cmd);
	}
	
	public GameController getController() {
		return controller;
	}
	
	public ArrayList<Player> getPlayers() {
		return players;
	}
}
