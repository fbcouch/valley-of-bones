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

import com.ahsgaming.valleyofbones.GameResult;
import com.ahsgaming.valleyofbones.VOBGame;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.EndPoint;

import java.util.HashMap;

/**
 * @author jami
 *
 */
public class KryoCommon {
	public static final int tcpPort = 54556;
	public static final int udpPort = 54549;
	
	public static void register (EndPoint endPoint) {
		Kryo kryo = endPoint.getKryo();
		kryo.register(String.class);
		kryo.register(Vector2.class);
		kryo.register(Attack.class);
		kryo.register(Build.class);
        kryo.register(Refund.class);
		kryo.register(Move.class);
		kryo.register(Upgrade.class);
		kryo.register(Pause.class);
		kryo.register(Unpause.class);
		kryo.register(EndTurn.class);
        kryo.register(ActivateAbility.class);
        kryo.register(Surrender.class);
        kryo.register(Command[].class);
		kryo.register(StartTurn.class);
		kryo.register(Color.class);
		kryo.register(RegisterPlayer.class);
		kryo.register(RegisteredPlayer.class);
		kryo.register(RegisteredPlayer[].class);
		kryo.register(AddAIPlayer.class);
		kryo.register(RemovePlayer.class);
		kryo.register(StartGame.class);
		kryo.register(GameDetails.class);
		kryo.register(int[].class);
		kryo.register(GameResult.class);
        kryo.register(VersionError.class);
        kryo.register(GameFullError.class);
        kryo.register(Spectator.class);
        kryo.register(Spectator[].class);
        kryo.register(GameUpdate.class);
        kryo.register(UpdatePlayer.class);
        kryo.register(ChatMessage.class);
	}
	
	public static class RegisterPlayer {
		public String name;
        public String version = VOBGame.VERSION;
        public boolean spectator = false;
        public int prefColor = 0;
        public String key;
	}

    public static class UpdatePlayer {
        public int id;
        public int color;
        public boolean ready;
    }
	
	public static class RegisteredPlayer {
		public int id;
		public String name;
		public int color;
        public boolean host;
        public boolean spectator;
        public boolean ready;
        public boolean isAI;
	}

    public static class AddAIPlayer {
	}
	
	public static class RemovePlayer {
		public int id;
	}
	
	public static class StartGame {
        public int currentPlayer;
        public int spawnType;
    }
	
	public static class GameDetails {
        public int hostId;
        public String map;
        public int rules;
        public int firstMove = 0;
        public int spawn;
        public int baseTimer;
        public int actionBonusTime;
        public int unitBonusTime;
        public boolean allowSpectate = true;
	}

    public static interface Error {}

    public static class VersionError implements Error {
        public String version = VOBGame.VERSION;
    }

    public static class GameFullError implements Error {}

    public static class Spectator {
        public String name;
        public String version = VOBGame.VERSION;
    }

    public static class GameUpdate {
        public int currentPlayer;
        public int turn;
        public float timer;
    }

    public static class ChatMessage {
        public String name;
        public String message;
    }
}
