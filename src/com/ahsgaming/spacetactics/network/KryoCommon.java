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

import com.ahsgaming.spacetactics.Player;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.EndPoint;

/**
 * @author jami
 *
 */
public class KryoCommon {
	public static final int tcpPort = 54556;
	public static final int udpPort = 54557;
	
	public static final int NET_TICK_LENGTH = 100; // ms
	public static final int GAME_TICK_LENGTH = 20; // ms
	
	public static void register (EndPoint endPoint) {
		Kryo kryo = endPoint.getKryo();
		kryo.register(String.class);
		kryo.register(Vector2.class);
		kryo.register(Attack.class);
		kryo.register(Build.class);
		kryo.register(Move.class);
		kryo.register(Upgrade.class);
		kryo.register(Pause.class);
		kryo.register(Unpause.class);
		kryo.register(Color.class);
		kryo.register(RegisterPlayer.class);
		kryo.register(RegisteredPlayer.class);
		kryo.register(RegisteredPlayer[].class);
		
	}
	
	public static class RegisterPlayer {
		public String name;
	}
	
	public static class RegisteredPlayer {
		public int id;
		public String name;
		public Color color;
	}
}
