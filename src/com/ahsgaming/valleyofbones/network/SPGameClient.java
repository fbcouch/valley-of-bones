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

import com.ahsgaming.valleyofbones.*;
import com.ahsgaming.valleyofbones.ai.FSMAIPlayer;
import com.ahsgaming.valleyofbones.screens.GameSetupConfig;
import com.badlogic.gdx.utils.Array;

/**
 * @author jami
 *
 */
public class SPGameClient implements NetController {
	public String LOG = "SPGameClient";

	int playerId = -1;
	Player player = null;

	GameController controller;

	GameSetupConfig gameConfig;

	Array<Player> players = new Array<Player>();
    int nextPlayerId = 0;

	VOBGame game;

	boolean stopClient = false;

	/**
	 *
	 */
	public SPGameClient(VOBGame g, final GameSetupConfig cfg) {
		this.game = g;
		gameConfig = cfg;

        player = new Player(getNextPlayerId(), cfg.playerName, Player.getUnusedColor(players));
        players.add(player);
	}
	
	public void startGame() {
		// OK, this should be called within an opengl context, so we can create everything
		controller = new GameController(gameConfig, players);
		controller.LOG = controller.LOG + "#SPClient";
        controller.setCurrentPlayer(player); // in SP, player always goes first
        Unpause up = new Unpause();
        up.owner = -1;
        controller.queueCommand(up);
	}
	
	public void sendStartGame() {
		game.setLoadGame();
	}
	
	public void endGame() {
		controller.setState(GameStates.GAMEOVER);
		game.setGameResult(controller.getGameResult());
	}
	
	public void stop() {
		stopClient = true;
	}
	
	public boolean update(float delta) {

		if (controller == null) return true;

        controller.update(delta);


        if (controller.isNextTurn() || controller.getTurnTimer() <= 0) {
            controller.setNextTurn(true);
            controller.doTurn();
        }

        if (controller.getGameResult() != null) {
            endGame();
            return false;
        }

		return true;
	}
	
	public void addAIPlayer() {
		if (players.size < 4)
            players.add(new FSMAIPlayer(this, getNextPlayerId(), "AI Player", Player.getUnusedColor(players)));
	}
	
	public void removePlayer(int playerId) {
		Array<Player> remove = new Array<Player>();
        for (Player p: players) {
            if (p.getPlayerId() == playerId)
                remove.add(p);
        }
        players.removeAll(remove, true);
	}

    protected int getNextPlayerId() {
        int id = nextPlayerId;
        nextPlayerId += 1;
        return id;
    }
	
	public void sendCommand(Command cmd) {
        // only queue if its your turn!
        cmd.turn = controller.getGameTurn();
		if (cmd.owner == controller.getCurrentPlayer().getPlayerId()) controller.queueCommand(cmd);
	}

    public void sendAICommand(Command cmd) {
        sendCommand(cmd);
    }
	
	public Array<Player> getPlayers() {
		return players;
	}
	
	public Player getPlayer() {
		return player;
	}
	
	public boolean isConnected() {
		return true;
	}
	
	public boolean isConnecting() {
		return false;
	}

	@Override
	public void setGameController(GameController controller) {
		this.controller = controller;
	}

	@Override
	public GameController getGameController() {
		return controller;
	}

    @Override
    public void setMap(String map) {
        gameConfig.mapName = map;
    }

    @Override
    public Array<String> getSpectators() {
        return new Array<String>();
    }
}
