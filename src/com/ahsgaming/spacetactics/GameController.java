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

import java.io.File;
import java.util.ArrayList;

import com.ahsgaming.spacetactics.network.Attack;
import com.ahsgaming.spacetactics.network.Build;
import com.ahsgaming.spacetactics.network.Command;
import com.ahsgaming.spacetactics.network.Move;
import com.ahsgaming.spacetactics.network.Pause;
import com.ahsgaming.spacetactics.network.Unpause;
import com.ahsgaming.spacetactics.network.Upgrade;
import com.ahsgaming.spacetactics.units.Bullet;
import com.ahsgaming.spacetactics.units.Prototypes;
import com.ahsgaming.spacetactics.units.Prototypes.JsonUnit;
import com.ahsgaming.spacetactics.units.Unit;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.tiled.TiledLoader;
import com.badlogic.gdx.graphics.g2d.tiled.TiledMap;
import com.badlogic.gdx.graphics.g2d.tiled.TiledObject;
import com.badlogic.gdx.graphics.g2d.tiled.TiledObjectGroup;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Group;

/**
 * @author jami
 *
 */
public class GameController {
	public static final String DEFAULT_MAP = "blank.tmx";
	public static final String MAP_DIRECTORY = "maps";
	
	public String LOG = "GameController";
	
	ArrayList<GameObject> gameObjects, selectedObjects, objsToAdd, objsToRemove;
	Group grpRoot, grpMap, grpUnits;
	
	ArrayList<Player> players;
	
	String mapName;
	TiledMap map;
	Vector2[] spawnPoints;
	
	GameStates state;
	
	ArrayList<Command> commandHistory;
	ArrayList<Command> commandQueue;
	ArrayList<Command> cmdsToAdd;
	int gameTick = 0;
	
	int nextObjectId = 0;
	
	
	/**
	 * Constructors
	 */
	
	public GameController(String mapName, ArrayList<Player> players) {
		// TODO load map
		this.mapName = mapName;
		grpRoot = new Group();
		grpMap = new Group();
		grpUnits = new Group();
		grpRoot.addActor(grpMap);
		grpRoot.addActor(grpUnits);
		
		gameObjects = new ArrayList<GameObject>();
		selectedObjects = new ArrayList<GameObject>();
		objsToAdd = new ArrayList<GameObject>();
		objsToRemove = new ArrayList<GameObject>();
		
		commandHistory = new ArrayList<Command>();
		commandQueue = new ArrayList<Command>();
		cmdsToAdd = new ArrayList<Command>();
		
		this.players = players;
		
		this.loadMap();
		this.loadMapObjects();
		
		grpRoot.setSize(map.width * map.tileWidth, map.height * map.tileHeight);
		
		// TODO start paused
		state = GameStates.PAUSED;
	}
	
	/**
	 * Methods
	 */
	
	private TiledMap loadMap() {
		// loads the map based on the value in mapName
		if (mapName == null || mapName.length() == 0) mapName = DEFAULT_MAP;
		map = TiledLoader.createMap(Gdx.files.internal(MAP_DIRECTORY + File.separator + mapName));
		
		return map;
	}
	
	private Group loadMapObjects() {
		Texture tex = new Texture(Gdx.files.internal("base-fighter1.png"));
		tex.setFilter(TextureFilter.Linear, TextureFilter.Linear);
		
		for (TiledObjectGroup group : map.objectGroups) {
			for (TiledObject obj : group.objects) {
				if (obj.type.contains("team_start")) {
					Vector2 objPos = mapToLevelCoords(new Vector2(obj.x - obj.width * 0.5f, obj.y - obj.height * 0.5f));
					Unit unit;
					int owner = -1;
					try {
						owner = Integer.parseInt(Character.toString(obj.type.charAt(obj.type.length() - 1))) - 1;
						if (owner >= 0 && owner < players.size()) {
							unit = new Unit(getNextObjectId(), getPlayerById(owner), (JsonUnit)Prototypes.getProto("space-station-base"));
						} else {
							unit = new Unit(getNextObjectId(), null, (JsonUnit)Prototypes.getProto("space-station-base"));
							Gdx.app.log(SpaceTacticsGame.LOG, "Map Error: player spawn index out of range");
						}
					} catch (NumberFormatException e) {
						owner = -1;
						unit = new Unit(getNextObjectId(), null, new TextureRegion(tex));
					}
					unit.setPosition(objPos.x, objPos.y);
					addGameUnit(unit);
					
					if (owner > -1) {
						addSpawnPoint(owner, new Vector2(unit.getX() + unit.getWidth() * 0.5f, unit.getY() + unit.getHeight() * 0.5f));
					}
					
					// TODO remove this
					unit = new Unit(getNextObjectId(), getPlayerById(owner), (JsonUnit)Prototypes.getProto("fighters-base"));
					unit.setPosition(objPos.x + 100,  objPos.y + 100);
					addGameUnit(unit);
					
					unit = new Unit(getNextObjectId(), getPlayerById(owner + 1), (JsonUnit)Prototypes.getProto("fighters-base"));
					unit.setPosition(objPos.x + 100, objPos.y + 200);
					addGameUnit(unit);
				}
			}
		}
		
		return grpUnits;
	}
	
	public void update(float delta) {
		//Gdx.app.log(LOG, "update...");
		// TODO process commands
		ArrayList<Command> toAdd = cmdsToAdd;
		cmdsToAdd = new ArrayList<Command>();
		commandQueue.addAll(toAdd);
		
		ArrayList<Command> toRemove = new ArrayList<Command>();
		for (Command command: commandQueue) {
			Gdx.app.log(LOG, "Command: " + Integer.toString(command.tick));
			if (command.tick < gameTick) {
				// remove commands in the past without executing
				toRemove.add(command);
			} else if (command.tick == gameTick) {
				// execute current commands and remove
				// TODO execute the command
				//Gdx.app.log(SpaceTacticsGame.LOG, "Executing command on tick " + Integer.toString(command.tick) + "==" + Integer.toString(gameTick));
				if (state == GameStates.RUNNING || command instanceof Unpause) { 
					executeCommand(command);
				}
				toRemove.add(command);
				commandHistory.add(command);
			} // future commands are left alone
		}
		commandQueue.removeAll(toRemove);
		
		if (state == GameStates.RUNNING) {
			gameTick += 1;
			//Gdx.app.log(LOG, "Game Tick: " + Integer.toString(gameTick));
			
			// update collection
			gameObjects.removeAll(objsToRemove);
			for (GameObject obj: objsToRemove) {
				grpUnits.removeActor(obj);
			}
			objsToRemove.clear();
			
			for (GameObject obj: objsToAdd) {
				addGameUnitNow(obj);
			}
			objsToAdd.clear();
			
			for (GameObject obj : gameObjects) {
				// update physics
				
				if (obj.getAccel().len() > obj.getMaxAccel()) {
					// clamp acceleration to max
					float angle = obj.getAccel().angle();
					obj.getAccel().set(obj.getMaxAccel(), 0);
					obj.getAccel().rotate(angle);
				}
				
				obj.getVelocity().add(obj.getAccel().mul(delta));
				if (obj.getVelocity().len() > obj.getMaxSpeed()) {
					// clamp velocity to max
					float angle = obj.getVelocity().angle();
					obj.getVelocity().set(obj.getMaxSpeed(), 0);
					obj.getVelocity().rotate(angle);
				}
				
				obj.setPosition(obj.getX() + obj.getVelocity().x * delta, obj.getY() + obj.getVelocity().y * delta);
				
				obj.update(this, delta);
				
				if (obj.isRemove()) objsToRemove.add(obj);
			}
		}
	}

	
	public void executeCommand(Command cmd) {
		if (cmd instanceof Attack) {
			executeAttack((Attack)cmd);
		} else if (cmd instanceof Build) {
			executeBuild((Build)cmd);
		} else if (cmd instanceof Move) {
			executeMove((Move)cmd);
		} else if (cmd instanceof Pause) {
			executePause((Pause)cmd);
		} else if (cmd instanceof Unpause) {
			executeUnpause((Unpause)cmd);
		} else if (cmd instanceof Upgrade) {
			executeUpgrade((Upgrade)cmd);
		} else {
			Gdx.app.log(LOG, "Unknown command");
		}
	}
	
	public void executeAttack(Attack cmd) {
		Gdx.app.log(LOG, String.format("Attack: unit(%d) --> unit(%d)", cmd.unit, cmd.target));
		GameObject obj = getObjById(cmd.unit);
		GameObject tar = getObjById(cmd.target);
		if (obj == null || tar == null) {
			if (obj == null) Gdx.app.log(LOG, String.format("Attack: cannot find unit(%d)", cmd.unit));
			if (tar == null) Gdx.app.log(LOG, String.format("Attack: cannot find target(%d)", cmd.target));
		} else if (obj.owner == null || obj.owner.getPlayerId() != cmd.owner) {
			Gdx.app.log(LOG,  "Error: object owner does not match command owner");
		} else {
			if (obj instanceof Unit && tar instanceof Unit) {
				((Unit)obj).doCommand(cmd, cmd.isAdd);// TODO implement shift-click to add to queue
			} else {
				if (!(obj instanceof Unit)) {
					Gdx.app.log(LOG, "Error: unit is not a Unit");
				}
				
				if (!(tar instanceof Unit)) {
					Gdx.app.log(LOG, "Error: target is not a Unit");
				}
			}
		}
	}
	
	public void executeBuild(Build cmd) {
		// TODO place builder
		// for now, just add the unit
		Unit unit = new Unit(getNextObjectId(), this.getPlayerById(cmd.owner), (JsonUnit)Prototypes.getProto(cmd.building));
		unit.setPosition(cmd.location, "center");
		objsToAdd.add(unit);
	}
	
	public void executeMove(Move cmd) {
		Gdx.app.log(LOG, "Move unit: " + Integer.toString(cmd.unit)+ " to: " + cmd.toLocation.toString());
		GameObject obj = getObjById(cmd.unit);
		if (obj == null) {
			Gdx.app.log(this.getClass().getSimpleName(), "Error: unknown unit id");
		} else if (obj.owner == null || obj.owner.getPlayerId() != cmd.owner) {
			Gdx.app.log(LOG, "Error: object owner does not match command owner");
		} else {
			if (obj instanceof Unit) {
				((Unit)obj).doCommand(cmd, cmd.isAdd);
			} else {
				obj.moveTo(cmd.toLocation, cmd.isAdd);
			}
		}
	}
	
	public void executePause(Pause cmd) {
		state = GameStates.PAUSED;
	}
	
	public void executeUnpause(Unpause cmd) {
		state = GameStates.RUNNING;
	}
	
	public void executeUpgrade(Upgrade cmd) {
		
	}
	
	/**
	 * Getters/Setters
	 */
	
	public Group getGroup() {
		return grpRoot;
	}
	
	public ArrayList<Player> getPlayers() {
		return players;
	}
	
	public Player getPlayerById(int id) {
		for (Player player: players) {
			if (player.getPlayerId() == id) return player;
		}
		return null;
	}
	
	private void addGameUnitNow(GameObject obj) {
		if (!gameObjects.contains(obj)) gameObjects.add(obj);
		if (!obj.hasParent() || !obj.getParent().equals(grpUnits)) grpUnits.addActor(obj);
	}
	
	public void addGameUnit(GameObject obj) {
		if (!objsToAdd.contains(obj)) objsToAdd.add(obj);
	}
	
	public void removeGameUnit(GameObject obj) {
		gameObjects.remove(obj);
		grpUnits.removeActor(obj);
	}
	
	public ArrayList<GameObject> getGameObjects() {
		return gameObjects;
	}
	
	public GameObject getObjById(int id) {
		for (GameObject obj: getGameObjects()) {
			if (obj.getObjId() == id) {
				return obj;
			}
		}
		
		return null;
	}
	
	public ArrayList<GameObject> getSelectedObjects() {
		return selectedObjects;
	}
	
	public void selectObjectsInArea(Rectangle box, Player owner, boolean addToSelection) {
		if (!addToSelection) {
			selectedObjects.clear();
		}
		// TODO check owner
		boolean hasOwnerObjs = false;
		for (GameObject obj: selectedObjects) {
			if (obj.getOwner() == owner) hasOwnerObjs = true;
		}
		
		GameObject firstNewObj = null;
		for (GameObject obj: gameObjects) {
			if (obj.isColliding(box) && !selectedObjects.contains(obj)) {
				if (obj.getOwner() == owner) hasOwnerObjs = true;
				
				if (obj.getOwner() == owner || !hasOwnerObjs) {
					selectedObjects.add(obj);
				}
				
				if (firstNewObj == null) firstNewObj = obj;
			}
		}
		
		
		if (hasOwnerObjs) {
			// only select owner objs
			ArrayList<GameObject> toRemove = new ArrayList<GameObject>();
			for (GameObject obj: selectedObjects) {
				if (obj.getOwner() != owner) toRemove.add(obj);
			}
			selectedObjects.removeAll(toRemove);
		} else if (firstNewObj != null){
			// only select one
			selectedObjects.clear();
			selectedObjects.add(firstNewObj);
		}
	}
	
	public ArrayList<GameObject> getObjsAtPosition(Vector2 mapCoords) {
		ArrayList<GameObject> returnVal = new ArrayList<GameObject>();
		
		for (GameObject obj: gameObjects) {
			if (obj.isColliding(mapCoords)) {
				returnVal.add(obj);
			}
		}
		
		return returnVal;
	}
	
	public TiledMap getMap() {
		return map;
	}
	
	/**
	 * @return the gameTick
	 */
	public int getGameTick() {
		return gameTick;
	}

	/**
	 * @param gameTick the gameTick to set
	 */
	public void setGameTick(int gameTick) {
		this.gameTick = gameTick;
	}
	
	public ArrayList<Command> getCommandHistory() {
		return this.commandHistory;
	}
	
	public ArrayList<Command> getCommandQueue() {
		return this.commandQueue;
	}
	
	public void queueCommand(Command cmd) {
		if (state == GameStates.RUNNING || cmd instanceof Unpause) {
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
	
	/**
	 * static methods
	 */

	/**
	 * converts TiledMap coordinates to LibGDX coordinates
	 * @param mapCoords - Vector2 coordinates in a TiledMap reference frame (0,0 is top-left)
	 * @return Vector2 coordinates in Level/GDX reference frame (0,0 is bottom-left)
	 */
	public Vector2 mapToLevelCoords(Vector2 mapCoords) {
		return new Vector2(mapCoords.x, (map.height * map.tileHeight) - mapCoords.y);
	}
}
