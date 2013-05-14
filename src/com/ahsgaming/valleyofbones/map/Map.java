/**
 * Legend of Rogue
 * An AHS Gaming Production
 * (c) 2013 Jami Couch
 * fbcouch 'at' gmail 'dot' com
 * Licensed under Apache 2.0
 * See www.ahsgaming.com for more info
 * 
 * LibGDX
 * (c) 2011 see LibGDX authors file
 * Licensed under Apache 2.0
 * 
 * Pixelated Fonts by Kenney, Inc. Licensed as CC-SA.
 * See http://kenney.nl for more info.
 * 
 * All other art assets (c) 2013 Jami Couch, licensed CC-BY-SA
 */
package com.ahsgaming.valleyofbones.map;

import com.ahsgaming.roguelike.RogueLike;
import com.ahsgaming.roguelike.Utils;
import com.ahsgaming.roguelike.entity.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.ObjectMap;

/**
 * @author jami
 *
 */
public class Map {
	public static final String LOG = "Map";
	
	int height = 0, width = 0;
	int tileheight = 0, tilewidth = 0;
	
	Array<TileSet> tilesets;
	ObjectMap<String, Room> rooms;
	
	String currentRoomId;
	Room currentRoom;
	
	int version = 1;
	
	ObjectMap<String, Object> properties;
	
	Group mapGroup = new Group();
	Group entityGroup = new Group();
	
	World world;
	Box2DDebugRenderer debugRenderer;
	
	public Map() {
		tilesets = new Array<TileSet>();
		rooms = new ObjectMap<String, Room>();
		properties = new ObjectMap<String, Object>();
	}
	
	public void init(Vector2 characterPos) {
		this.world = new World(new Vector2(0, 0), true);
		
		debugRenderer = new Box2DDebugRenderer();
		
		for (TileSet s: tilesets) {
			s.init();
		}
		
		for (Room r: rooms.values()) {
			r.init();
		}
		
		this.switchToRoomByPosition(characterPos.x, characterPos.y);
	}
	
	public void update(float delta, Matrix4 projMatrix, Vector2 characterPos) {
		if (RogueLike.DEBUG_RENDER) debugRenderer.render(world, projMatrix);
		
		world.step(RogueLike.BOX_STEP, RogueLike.BOX_VELOCITY_ITERATIONS, RogueLike.BOX_POSITION_ITERATIONS);
		
		switchToRoomByPosition(characterPos.x, characterPos.y);
		
		getCurrentRoom().update(delta, projMatrix);
	}
	
	public void updateMap() {
		for (Room r: rooms.values()) {
			r.updateRoom();
		}
	}
	
	public void switchToRoomByPosition(float x, float y) {
		for (Room r: rooms.values()) {
			if (x >= r.getX() && x <= r.getX() + r.getWidth() * r.getTilewidth() &&
					y >= r.getY() && y <= r.getY() + r.getHeight() * r.getTileheight()) {
				if (!r.equals(currentRoom)) setCurrentRoom(r);
				return;
			}
		}
	}

	public Map loadFromFile(FileHandle jsonFile) {
		JsonReader jsonReader = new JsonReader();
		Object json = jsonReader.parse(jsonFile);
		
		return loadFromJson(json);
	}
	
	public Map loadFromString(String jsonString) {
		JsonReader jsonReader = new JsonReader();
		Object json = jsonReader.parse(jsonString);
		
		return loadFromJson(json);
	}

	@SuppressWarnings("unchecked")
	private Map loadFromJson(Object json) {
		tilesets = new Array<TileSet>();
		rooms = new ObjectMap<String, Room>();
		if (properties == null) properties = new ObjectMap<String, Object>();
		
		ObjectMap<String, Object> jsonObjects = (ObjectMap<String, Object>)json;
		
		if (jsonObjects.containsKey("width"))
			this.width = (int)Float.parseFloat(jsonObjects.get("width").toString());
		
		if (jsonObjects.containsKey("height"))
			this.height = (int)Float.parseFloat(jsonObjects.get("height").toString());
		
		if (jsonObjects.containsKey("tilewidth"))
			this.tilewidth = (int)Float.parseFloat(jsonObjects.get("tilewidth").toString());
		
		if (jsonObjects.containsKey("tileheight"))
			this.tileheight = (int)Float.parseFloat(jsonObjects.get("tileheight").toString());
		
		if (jsonObjects.containsKey("version"))
			this.version = (int)Float.parseFloat(jsonObjects.get("version").toString());
		
		
		if (jsonObjects.containsKey("properties")) {
			this.properties = (ObjectMap<String, Object>)jsonObjects.get("properties");
		}
		
		if (jsonObjects.containsKey("tilesets")) {
			Array<ObjectMap<String, Object>> tilesets = (Array<ObjectMap<String, Object>>)jsonObjects.get("tilesets");
			for (ObjectMap<String, Object> set: tilesets) {
				this.tilesets.add(new TileSet(set));
			}
		}
		
		if (jsonObjects.containsKey("rooms")) {
			ObjectMap<String, Object> rooms = (ObjectMap<String, Object>)jsonObjects.get("rooms");
			for (String rId: rooms.keys()) {
				Room room = new Room(this);
				room.loadFromJson(rooms.get(rId));
				this.rooms.put(rId, room);
			}
		}
		
		
		return this;
	}
	
	public Room getCurrentRoom() {
		if (currentRoom == null) {
			if (currentRoomId == null || !rooms.containsKey(currentRoomId)) {
				currentRoom = rooms.values().next();
				currentRoomId = rooms.findKey(currentRoom, true);
			} else {
				currentRoom = rooms.get(currentRoomId);
			}
		}
		return currentRoom;
	}
	
	public void setCurrentRoom(Room r) {
		for (Entity e: getEntities())
			e.setPhysicsActive(false);
		
		currentRoom = r;
		currentRoomId = rooms.findKey(currentRoom, true);
		
		world.setContactFilter(currentRoom);
		world.setContactListener(currentRoom);
		this.mapGroup.clear();
		mapGroup.addActor(currentRoom.getMapGroup());
		
		this.entityGroup.clear();
		entityGroup.addActor(currentRoom.getEntityGroup());
		
		for (Entity e: getEntities())
			e.setPhysicsActive(true);
	}
	
	public Array<Entity> getEntities() {
		return getCurrentRoom().getEntities();
	}
	
	public void addEntity(Entity e) {
		getCurrentRoom().addEntity(e);
	}
	
	public void removeEntity(Entity e) {
		getCurrentRoom().removeEntity(e);
	}
	
	public Group getEntityGroup() {
		return entityGroup;
	}
	
	public void addRoom(Room room) {
		// assign a new room id
		String id = Utils.getRandomId();
		while (rooms.containsKey(id)) {
			id = Utils.getRandomId();
		}
		rooms.put(id, room);
	}
	
	/**
	 * @return the mapGroup
	 */
	public Group getMapGroup() {
		return mapGroup;
	}
	
	public TextureRegion getTile(int gid) {
		if (gid == 0 || tilesets.size == 0) return null;
		
		TileSet set = null;
		for (TileSet s: tilesets) {
			if (gid >= s.firstgid) {
				set = s;
			}
		}
		if (set == null) return null;
		
		return set.getTile(gid - set.firstgid);
	}
	
	/**
	 * @return the tileheight
	 */
	public int getTileheight() {
		return tileheight;
	}

	/**
	 * @return the tilewidth
	 */
	public int getTilewidth() {
		return tilewidth;
	}
	
	/**
	 * @return the tilesets
	 */
	public Array<TileSet> getTilesets() {
		return tilesets;
	}
	
	public void setTilesets(Array<TileSet> tilesets) {
		this.tilesets.clear();
		this.tilesets.addAll(tilesets);
		if (tilesets.size > 0) {
			TileSet one = tilesets.get(0);
			tilewidth = one.tilewidth;
			tileheight = one.tileheight;
		}
	}
	
	/**
	 * @return the version
	 */
	public int getVersion() {
		return version;
	}
	
	public World getWorld() {
		return world;
	}
	
	@Override
	public String toString() {
		
		String json = "{";
		
		json += Utils.toJsonProperty("width", this.width);
		json += Utils.toJsonProperty("height", this.height);
		json += Utils.toJsonProperty("tilewidth", this.tilewidth);
		json += Utils.toJsonProperty("tileheight", this.tileheight);
		json += Utils.toJsonProperty("properties", this.properties);
		json += Utils.toJsonProperty("tilesets", this.tilesets);
		
		json += Utils.toJsonProperty("rooms", this.rooms);
		json += Utils.toJsonProperty("version", this.version);
		
		return json + "}";
	}
}
