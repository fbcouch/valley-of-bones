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
package com.ahsgaming.spacetactics;

import java.io.IOException;

import com.ahsgaming.spacetactics.network.KryoCommon;
import com.badlogic.gdx.Gdx;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;

/**
 * @author jami
 *
 */
public class GameServer {
	public static final int NET_TICK_LENGTH = 100; // ms
	public static final int GAME_TICK_LENGTH = 50; // ms
	
	Server server;
	GameController controller;
	int sinceLastNetTick = 0;
	int sinceLastGameTick = 0;
	long lastTimeMillis = 0;
	
	/**
	 * 
	 */
	public GameServer() {
		// setup the KryoNet server
		server = new Server();
		KryoCommon.register(server);
		
		// TODO set up a broadcast server for LAN stuff
		
		try {
			server.bind(KryoCommon.tcpPort);
			server.start();
		} catch (IOException ex) {
			Gdx.app.log(SpaceTacticsGame.LOG + ":GameServer", ex.getStackTrace().toString());
			Gdx.app.exit();
		}
		
		server.addListener(new Listener() {
			
			public void received(Connection c, Object obj) {
				
			}
			
			public void disconnected (Connection c) {
				
			}
		});
		
		controller = new GameController("");
		
		lastTimeMillis = System.currentTimeMillis();
	}

	public boolean update() {
		//Gdx.app.log("Server", "Update");
		long time = System.currentTimeMillis();
		int delta = (int)(time - lastTimeMillis);
		lastTimeMillis = time;
		sinceLastNetTick += delta;
		sinceLastGameTick += delta;
		
		while (sinceLastNetTick >= GameServer.NET_TICK_LENGTH) {
			// TODO net tick
			sinceLastNetTick -= GameServer.NET_TICK_LENGTH;
			//Gdx.app.log("Server", "NET TICK");
		}
		
		while (sinceLastGameTick >= GameServer.GAME_TICK_LENGTH) {
			// TODO game tick
			controller.update(GameServer.GAME_TICK_LENGTH);
			sinceLastGameTick -= GameServer.GAME_TICK_LENGTH;
			//Gdx.app.log("Server", "GAME TICK");
		}
		
		try {
			Thread.sleep(GameServer.GAME_TICK_LENGTH - (System.currentTimeMillis() - lastTimeMillis) - sinceLastGameTick);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return true;
	}
}
