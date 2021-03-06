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
package com.ahsgaming.valleyofbones;

import com.ahsgaming.valleyofbones.map.HexMap;
import com.ahsgaming.valleyofbones.map.MapData;
import com.ahsgaming.valleyofbones.network.*;
import com.ahsgaming.valleyofbones.screens.GameSetupConfig;
import com.ahsgaming.valleyofbones.units.*;
import com.ahsgaming.valleyofbones.units.Prototypes.JsonProto;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

/**
 * @author jami
 *
 */
public class GameController {
	public static final String DEFAULT_MAP = "valley"; // TODO get default map from map list?
	public static final String MAP_DIRECTORY = "maps";
	
	public String LOG = "GameController";
	
	UnitManager unitManager;
	AbstractUnit selectedObject;

	Array<Player> players;
	
	String mapName;
	HexMap map;
	Vector2[] spawnPoints;
	
	GameStates state;
	
	Array<Command> commandHistory;
	Array<Command> commandQueue;
	Array<Command> cmdsToAdd;
	
	Player currentPlayer;
    int gameTurn = 0;
	float baseTimer = 60;
    float actionBonusTime = 0;
    float unitBonusTime = 0;
    int maxPauses = 0;

	float turnTimer = 0;
	boolean nextTurn = false;
	
	int nextObjectId = 0;
	
	GameResult gameResult = null;

    GameSetupConfig.SpawnTypes spawnType = GameSetupConfig.SpawnTypes.SPAWN_NORMAL;

    /**
	 * Constructors
	 */
	
	public GameController(GameSetupConfig config, Array<Player> players) {
		this.mapName = config.mapName;
        this.maxPauses = config.maxPauses;
        this.spawnType = GameSetupConfig.SpawnTypes.values()[config.spawnType];

        this.baseTimer = config.baseTimer;
        this.actionBonusTime = config.actionBonusTime;
        this.unitBonusTime = config.unitBonusTime;

        // TODO - set ruleset here

        unitManager = new UnitManager(this);

		commandHistory = new Array<Command>();
		commandQueue = new Array<Command>();
		cmdsToAdd = new Array<Command>();
		
		this.players = players;
		
		this.loadMap();
		this.loadMapObjects();

		state = GameStates.PAUSED;
	}
	
	/**
	 * Methods
	 */
	
	private HexMap loadMap() {
		// loads the map based on the value in mapName
		if (mapName == null || mapName.length() == 0) mapName = DEFAULT_MAP;
		// TODO implement loading of maps
		//map = new HexMap(this, 19, 13, 2, 4);
		map = new HexMap(this, Gdx.files.internal("maps/" + mapName + ".json"));
		return map;
	}
	
	private void loadMapObjects() {
        Array<Player> playersToSpawn = new Array<Player>();
        switch(spawnType) {
            case SPAWN_NORMAL:
                playersToSpawn.addAll(players);
                break;
            case SPAWN_INVERTED:
                for (int i = players.size - 1; i >= 0; i--) {
                    playersToSpawn.add(players.get(i));
                }
                break;
            case SPAWN_RANDOM:
                Array<Player> p = new Array<Player>(players);
                while (p.size > 0) {
                    playersToSpawn.add(p.removeIndex((int) Math.floor(Math.random() * p.size)));
                }
                break;
        }

		for (MapData.MapObject spawn : map.getPlayerSpawns()) {
			AbstractUnit unit;
			if (spawn.player >= 0 && spawn.player < players.size) {
                unit = Unit.createUnit(getNextObjectId(), "castle-base", playersToSpawn.get(spawn.player));
				playersToSpawn.get(spawn.player).setBaseUnit(unit);
			} else {
                unit = Unit.createUnit(getNextObjectId(), "castle-base", null);
                Gdx.app.log(VOBGame.LOG, "Map Error: player spawn index out of range");
			}
			Vector2 pos = map.boardToMapCoords(spawn.x, spawn.y);
			unit.getView().setPosition(pos.x, pos.y);
			unit.getView().setBoardPosition(spawn.x, spawn.y);
			unitManager.addUnit(unit);
			
			if (spawn.player >= 0 && spawn.player < players.size) {
				addSpawnPoint(playersToSpawn.get(spawn.player).getPlayerId(), new Vector2(unit.getView().getX() + unit.getView().getWidth() * 0.5f, unit.getView().getY() + unit.getView().getHeight() * 0.5f));
			}
		}

        for (MapData.MapObject obj : map.getControlPoints()) {
            AbstractUnit unit;
            unit = Unit.createUnit(getNextObjectId(), obj.proto, obj.player == -1 ? null : players.get(obj.player));
            Vector2 pos = map.boardToMapCoords(obj.x, obj.y);
            unit.getView().setPosition(pos.x, pos.y);
            unit.getView().setBoardPosition(new Vector2(obj.x, obj.y));
            unitManager.addUnit(unit);
        }

        if (VOBGame.DEBUG_ATTACK) {
            AbstractUnit unit = Unit.createUnit(getNextObjectId(), "marine-base", players.get(0));
            unit.getView().setBoardPosition(9, 0);
            unit.getView().setPosition(getMap().boardToMapCoords(9, 0));
            unitManager.addUnit(unit);

            unit = Unit.createUnit(getNextObjectId(), "marine-base", players.get(1));
            unit.getView().setBoardPosition(10, 0);
            unit.getView().setPosition(getMap().boardToMapCoords(10, 0));
            unitManager.addUnit(unit);
        }
	}
	
	public void update(float delta) {
		
		switch(state) {
            case RUNNING:
                doCommands();

                unitManager.update(delta);
                map.update();

                for (int p = 0; p < players.size; p++) {
                    players.get(p).update(this, delta);
                }

                turnTimer -= delta;

                checkResult();

                if (gameResult != null) {
                    this.state = GameStates.GAMEOVER;
                }

                break;
            case PAUSED:
                for (Command c: cmdsToAdd) {
                    if (c instanceof Unpause) {
                        executeCommand(c);
                        map.invalidateViews();
                    } else {
                        commandQueue.add(c);
                    }
                }
                break;
        }

	}
	
	public void doTurn() {
//		Gdx.app.log(LOG, "doTurn");

        if (nextTurn) {
            unitManager.endTurn(currentPlayer);
        }

		gameTurn += 1;
        turnTimer = baseTimer;
        nextTurn = false;

        if (players.size > 0 && (gameTurn > 1 || currentPlayer == null)) {    // this way when the NetController sets currentPlayer, that player goes first (doTurn is called at the beginning of the game)
            int i = players.indexOf(currentPlayer, true);
            i = (i + 1) % players.size;
            currentPlayer = players.get(i);
        }

        if (unitBonusTime > 0) {
            turnTimer += getUnitsByPlayerId(currentPlayer.getPlayerId()).size * unitBonusTime;
        }

        unitManager.startTurn(currentPlayer);
        currentPlayer.startTurn(this);
        map.invalidateViews();
	}
	
	public void checkResult() {
		int alive = 0;
		Player playerAlive = null;
		for (Player p: players) {
			if (p.isAlive()) {
				alive += 1;
				playerAlive = p;
			}
		}
		
		if (alive <= 1) {
			GameResult result = new GameResult();
			if (playerAlive != null) {
				result.winner = playerAlive.getPlayerId();
			}
			Array<Integer> losingPlayers = new Array<Integer>();
			for (Player p : players) {
				if (!p.isAlive()) {
					losingPlayers.add(p.getPlayerId());
				}
			}
			result.losers = new int[losingPlayers.size];
			for (int i = 0; i < losingPlayers.size; i++) {
				result.losers[i] = losingPlayers.get(i);
			}
			
			//Gdx.app.log(LOG, String.format("Game Over // Winner: %d (%d); Losers: (%d)", result.winningTeam, result.winners.length, result.losers.length));
			this.gameResult = result;
		}
	}

    public void declareWinner(Player p) {
        GameResult result = new GameResult();
        result.winner = p.getPlayerId();
        result.losers = new int[players.size - 1];
        int i = 0;
        for (Player player: players) {
            if (player != p) {
                result.losers[i] = player.getPlayerId();
                i++;
            }
        }
        this.gameResult = result;
    }

	public void doCommands() {
		// process commands

        commandQueue.addAll(cmdsToAdd);
        cmdsToAdd.clear();

		Array<Command> toKeep = new Array<Command>();
		commandQueue.reverse();
		Command command;
		while (commandQueue.size > 0) {
            command = commandQueue.pop();
//			Gdx.app.log(LOG, "Command: " + Integer.toString(command.turn));
//            if (command instanceof Unpause) Gdx.app.log(LOG, "UNPAUSE");
//            if (command instanceof Pause) Gdx.app.log(LOG, "PAUSE");
//            Gdx.app.log(LOG, "Current turn: " + gameTurn);
			if (command.turn < gameTurn) {
				// remove commands in the past without executing
			} else if (command.turn == gameTurn) {
				// execute current commands and remove
				if (state == GameStates.RUNNING || command instanceof Unpause) { 
					executeCommand(command);
				}

			} else {
				// future commands are left alone
                cmdsToAdd.add(command);
			}
		}
		
		
	}
	
	public void executeCommand(Command cmd) {
		if (cmd.validate(this)) {
            cmd.execute(this);
            commandHistory.add(cmd);
            map.invalidateViews();
        }
	}

    public void actionReset() {
        if (turnTimer < actionBonusTime)
            turnTimer = actionBonusTime;
    }
	
	public boolean playerCanSee(Player player, AbstractUnit target) {
        return unitManager.canPlayerSee(player, target);
    }

    public boolean playerCanDetect(Player player, AbstractUnit target) {
        return unitManager.canPlayerDetect(player, target);
    }

	public void executePause(Pause cmd) {
		state = GameStates.PAUSED;
        if (cmd.owner != -1 && !cmd.isAuto) {
            getPlayerById(cmd.owner).addPause();
        }
	}
	
	public void executeUnpause(Unpause cmd) {
		state = GameStates.RUNNING;
	}

    /**
	 * Getters/Setters
	 */

    public int getMaxPauses() {
        return maxPauses;
    }

    public void setMaxPauses(int maxPauses) {
        this.maxPauses = maxPauses;
    }

	public Array<Player> getPlayers() {
		return players;
	}
	
	public Player getPlayerById(int id) {
		for (Player player: players) {
			if (player.getPlayerId() == id) return player;
		}
		return null;
	}

    public Array<AbstractUnit> getUnits() {
        return unitManager.getUnits();
    }
	
	public Array<AbstractUnit> getUnitsByPlayerId(int id) {
		return unitManager.getUnits(id);
	}

    public boolean canPlayerRefundUnit(Player player, AbstractUnit unit) {
        return false; // TODO re-implement refund
//        return player != null && unit != null
//                && player == currentPlayer && unit.getOwner() == player
//                && !unit.getData().getType().equals("building")
//                && units.contains(unit, true)
//                && !unit.isRemove();
    }

    public void refundUnit(Player player, AbstractUnit unit) {
//        unit.setRemove(true);
//        player.setBankMoney(player.getBankMoney() + unit.getRefund());
//        removeGameUnit(unit);
    }

    public Array<AbstractUnit> getUnitsInArea(Vector2 boardPos, int radius) {
        return unitManager.getUnitsInArea(boardPos, radius);
    }

	public AbstractUnit getUnitAtBoardPos(int x, int y) {
        return unitManager.getUnit(new Vector2(x, y));
	}
	
	public AbstractUnit getUnitAtBoardPos(float x, float y) {
		return getUnitAtBoardPos((int) x, (int) y);
	}
	
	public AbstractUnit getUnitAtBoardPos(Vector2 boardPos) {
		return getUnitAtBoardPos((int) boardPos.x, (int) boardPos.y);
	}
	
	public boolean isBoardPosEmpty(int x, int y) {
        if (!map.getMapData().isBoardPositionTraversible(x, y)) return false;
        return (unitManager.getUnit(new Vector2(x, y)) == null);
	}
	
	public boolean isBoardPosEmpty(float x, float y) {
		return isBoardPosEmpty((int)x, (int)y);
	}
	
	public boolean isBoardPosEmpty(Vector2 boardPos) {
		return isBoardPosEmpty((int)boardPos.x, (int)boardPos.y);
	}

    public UnitManager getUnitManager() {
        return unitManager;
    }
	
	public HexMap getMap() {
		return map;
	}
	
	public int getGameTurn() {
		return gameTurn;
	}
	
	public float getTurnTimer() {
		return turnTimer;
	}

    public float getBaseTimer() {
        return baseTimer;
    }

    public void setTurnTimer(float timer) {
        turnTimer = timer;
    }

    public Player getCurrentPlayer() {
        return currentPlayer;
    }

    public void setCurrentPlayer(Player p) {
        currentPlayer = p;
    }

    public void setCurrentPlayer(int pid) {
        currentPlayer = getPlayerById(pid);
    }
	
	public Array<Command> getCommandHistory() {
		return this.commandHistory;
	}
	
	public Array<Command> getCommandQueue() {
		return this.commandQueue;
	}

    public void setCommandQueue(Command[] commands) {
        this.commandQueue.clear();
        if (commands != null)
            commandQueue.addAll(commands);
    }
	
	public void queueCommand(Command cmd) {
//		Gdx.app.log(LOG, state.toString());
		if (state == GameStates.RUNNING || cmd instanceof Unpause) {
//            if (cmd instanceof Move) Gdx.app.log(LOG, "queue MOVE");
            cmdsToAdd.add(cmd);
		}
	}
	
	public int getNextObjectId() {
		int id = this.nextObjectId;
		this.nextObjectId += 1;
		return id;
	}
	
	public GameStates getState() {
		return state;
	}
	
	public void setState(GameStates state) {
		this.state = state;
	}
	
	public void addSpawnPoint(int id, Vector2 point) {
		if (id < 0) return;
		
		if (spawnPoints == null) {
			spawnPoints = new Vector2[id + 1];
		} else if (spawnPoints.length <= id) {
			Vector2[] tmp = new Vector2[id + 1];
			for (int i=0;i<spawnPoints.length;i++) tmp[i] = spawnPoints[i];
			spawnPoints = tmp;
		}
		spawnPoints[id] = point;
	}
	
	public Vector2 getSpawnPoint(int id) {
		if (id >= 0 && id < spawnPoints.length){
			return spawnPoints[id];
		}
		return null;
	}
	
	public GameResult getGameResult() {
		return gameResult;
	}

    public boolean isNextTurn() {
        return nextTurn;
    }

    public void setNextTurn(boolean nextTurn) {
        this.nextTurn = nextTurn;
    }

    public String getMapName() {
        return mapName;
    }

    /**
	 * static methods
	 */

}
