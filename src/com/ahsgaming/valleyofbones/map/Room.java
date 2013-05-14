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
import com.ahsgaming.roguelike.entity.DamageBox;
import com.ahsgaming.roguelike.entity.Door;
import com.ahsgaming.roguelike.entity.Entity;
import com.ahsgaming.roguelike.entity.EntityManager;
import com.ahsgaming.roguelike.entity.EntityProto;
import com.ahsgaming.roguelike.entity.GameObject;
import com.ahsgaming.roguelike.entity.Item;
import com.ahsgaming.roguelike.entity.Monster;
import com.ahsgaming.roguelike.entity.PlayerCharacter;
import com.ahsgaming.roguelike.entity.Portal;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactFilter;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.EdgeShape;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.ObjectMap;

/**
 * @author jami
 *
 */
public class Room implements ContactFilter, ContactListener {
	public static String LOG = "Room";
	
	int height = 0, width = 0;
	int x = 0, y = 0;
	
	Array<ObjectLayer> objLayers;
	Array<TileLayer> tileLayers;
	
	ObjectMap<String, Object> properties;
	
	Group mapGroup = new Group();
	
	final Map parent;
	
	Array<Entity> liveEntities = new Array<Entity>();
	Group liveEntityGroup = new Group();
	
	
	//-------------------------------------------------------------------------
	// Constructors
	//-------------------------------------------------------------------------
	
	/**
	 * 
	 */
	public Room(Map parent) {
		this.parent = parent;
		
		objLayers = new Array<ObjectLayer>();
		tileLayers = new Array<TileLayer>();
		
		properties = new ObjectMap<String, Object>();
	}
	
	//-------------------------------------------------------------------------
	// Methods
	//-------------------------------------------------------------------------
	
	public void init() {
		mapGroup = new Group();
		mapGroup.setBounds(0, 0, parent.getMapGroup().getWidth(), parent.getMapGroup().getHeight());
		Group grp = new Group();
		grp.setBounds(x, y, width * getTilewidth(), height * getTileheight());
		mapGroup.addActor(grp);
		for (TileLayer l: tileLayers) {
			l.init();
			grp.addActor(l.getGroup());
		}
		
		generateMapPhysics(parent.getWorld());
		generateEntities(parent.getWorld());
		
	}
	
	public void update(float delta, Matrix4 projMatrix) {
		
	}
	
	public Room loadFromJson(Object json) {
		objLayers = new Array<ObjectLayer>();
		tileLayers = new Array<TileLayer>();
		if (properties == null) properties = new ObjectMap<String, Object>();
		
		ObjectMap<String, Object> jsonObjects = (ObjectMap<String, Object>)json;
		
		if (jsonObjects.containsKey("width"))
			this.width = (int)Float.parseFloat(jsonObjects.get("width").toString());
		
		if (jsonObjects.containsKey("height"))
			this.height = (int)Float.parseFloat(jsonObjects.get("height").toString());
		
		if (jsonObjects.containsKey("x"))
			this.x = (int)Float.parseFloat(jsonObjects.get("x").toString());
		
		if (jsonObjects.containsKey("y"))
			this.y = (int)Float.parseFloat(jsonObjects.get("y").toString());		
		
		if (jsonObjects.containsKey("properties")) {
			this.properties = (ObjectMap<String, Object>)jsonObjects.get("properties");
		}
		
		if (jsonObjects.containsKey("layers")) {
			Array<ObjectMap<String, Object>> layers = (Array<ObjectMap<String, Object>>)jsonObjects.get("layers");
			for (ObjectMap<String, Object> layer: layers) {
				if (layer.containsKey("type")) {
					if (layer.get("type").toString().equals("tilelayer")) {
						this.tileLayers.add(new TileLayer(this, layer));
					} else if (layer.get("type").toString().equals("objectgroup")) {
						this.objLayers.add(new ObjectLayer(this, layer));
						Gdx.app.log(LOG, String.format("objLayer %d", objLayers.get(objLayers.size - 1).objects.size));
					}
				}
			}
		}
		
		return this;
	}
	
	public void updateRoom() {
		objLayers.clear();
		ObjectLayer objs = new ObjectLayer(this);
		JsonReader json = new JsonReader();
		for (Entity e: liveEntities) {
			ObjectMap<String, Object> o = (ObjectMap<String, Object>)json.parse(e.toString());
			objs.objects.add(o);
		}
		
		objLayers.add(objs);
		objs.type = "objectgroup";
		objs.name = "entities";
		objs.height = this.height;
		objs.width = this.width;
	}
	
	public void generateEntities(World world) {
		liveEntityGroup.clear();
		liveEntityGroup.setBounds(mapGroup.getX(), mapGroup.getY(), mapGroup.getWidth(), mapGroup.getHeight());
		
		for (ObjectLayer l: getObjLayers()) {
			for (Object o: l.getObjects()) {
				ObjectMap<String, Object> om = (ObjectMap<String, Object>)o;
				Entity en = generateEntityFromObjectMap(world, om, false);
				
				if (en != null)
					addEntity(en);
			}
		}
	}
	
	public Entity generateEntityFromObjectMap(World world, ObjectMap<String, Object> om, boolean noPhysics) {
		int x = 0, y = 0;
		String name = "", type = "";
		ObjectMap<String, Object> properties = new ObjectMap<String, Object>();
		
		if (om.containsKey("type")) 
			type = om.get("type").toString();
		
		if (om.containsKey("name")) 
			name = om.get("name").toString();
		
		if (om.containsKey("x")) 
			x = (int)Float.parseFloat(om.get("x").toString());
		
		if (om.containsKey("y")) 
			y = (int)Float.parseFloat(om.get("y").toString());
		
		if (om.containsKey("properties")) 
			properties = (ObjectMap<String, Object>)om.get("properties");
		
		EntityProto je = EntityManager.get(type);
		if (je == null) return null;
		
		Entity en = null;
		if (type.startsWith("ITEM")) {
			en = new Item(world, je, noPhysics);
		} else if (type.startsWith("DOOR")) {
			en = new Door(world, je);
		} else if (type.startsWith("MOB")) {
			en = new Monster(world, je);
		} else if (type.startsWith("PORTAL")) {
			en = new Portal(world, je);
		}
		
		if (en == null) return null;
		
		en.setPosition(x - x % getTilewidth() + en.getWidth() * 0.5f, y - y % getTileheight() + en.getHeight() * 0.5f);
		if (type.startsWith("DOOR") || type.startsWith("PORTAL")) {
			en.setPosition(x - x % getTilewidth(), y - y % getTileheight());
		}
		
		en.setName(name);
		en.setProperties(properties);
		return en;
	}
	
	protected void generateMapPhysics(World world) {
		// Boundaries
		BodyDef outerBodyDef = new BodyDef();
		outerBodyDef.position.set(new Vector2(0, 0));
		
		Body outerBody = world.createBody(outerBodyDef);
		EdgeShape outerBox = new EdgeShape();
		Vector2[] vertices = new Vector2[4];
		
		// Collidables (walls, etc)
		// TODO this will need some major optimization probably (maybe need to write some tests?)
		for (TileLayer l: getTileLayers()) {
			if (l.getName().equals("collide")) {
				for (int i=0;i<l.getData().length;i++) {
					if (l.getData()[i] == 0) continue;
					
					BodyDef collideTileDef = new BodyDef();
					collideTileDef.position.set(
							new Vector2(x + mapGroup.getX() + l.getX() + (i % l.getWidth() * getTilewidth()), 
									y + mapGroup.getY() + l.getY() + (i / l.getWidth() * getTileheight()))
									).mul(RogueLike.WORLD_TO_BOX);
					
					Body tileBody = world.createBody(collideTileDef);
					PolygonShape tileBox = new PolygonShape();
					//EdgeShape tileBox = new EdgeShape();
					vertices = new Vector2[4];
					vertices[0] = (new Vector2(0, 0).mul(RogueLike.WORLD_TO_BOX));
					vertices[1] = (new Vector2(getTilewidth(), 0).mul(RogueLike.WORLD_TO_BOX));
					vertices[2] = (new Vector2(getTilewidth(), getTileheight()).mul(RogueLike.WORLD_TO_BOX));
					vertices[3] = (new Vector2(0, getTileheight()).mul(RogueLike.WORLD_TO_BOX));
					
					tileBox.set(vertices);
					tileBody.createFixture(tileBox, 0.0f);
					
				}
			}
		}
	}
	
	public void updateBounds() {
		int left = -1, top = -1, right = -1, bottom = -1;
		for (TileLayer layer: this.tileLayers) {
			if (layer.x < left || left == -1) left = layer.x;
			
			if (layer.y < bottom || bottom == -1) bottom = layer.y;
			
			if (layer.x + layer.width > right || right == -1) right = layer.x + layer.width;
			
			if (layer.y + layer.height > top || top == -1) top = layer.y + layer.height;
		}
		
		x = left;
		y = bottom;
		width = right - left;
		height = top - bottom;
	}
	
	//-------------------------------------------------------------------------
	// Getters & Setters
	//-------------------------------------------------------------------------

	/**
	 * @return the lOG
	 */
	public static String getLOG() {
		return LOG;
	}

	/**
	 * @return the height
	 */
	public int getHeight() {
		return height;
	}
	
	/**
	 * @return the mapGroup
	 */
	public Group getMapGroup() {
		return mapGroup;
	}
	
	/**
	 * @return the objLayers
	 */
	public Array<ObjectLayer> getObjLayers() {
		return objLayers;
	}
	
	/**
	 * 
	 * @param the layer to add
	 */
	public void addObjLayer(ObjectLayer layer) {
		objLayers.add(layer);
	}

	/**
	 * @return the properties
	 */
	public ObjectMap<String, Object> getProperties() {
		return properties;
	}

	/**
	 * @param i
	 * @return
	 */
	public TextureRegion getTile(int i) {
		return parent.getTile(i);
	}
	
	/**
	 * @return the tileheight
	 */
	public int getTileheight() {
		return parent.getTileheight();
	}

	/**
	 * @return the layers
	 */
	public Array<TileLayer> getTileLayers() {
		return tileLayers;
	}
	
	/**
	 * @param the layer to add
	 */
	public void addTileLayer(TileLayer layer) {
		tileLayers.add(layer);
		updateBounds();
	}

	/**
	 * @return the tilewidth
	 */
	public int getTilewidth() {
		return parent.getTilewidth();
	}

	/**
	 * @return the width
	 */
	public int getWidth() {
		return width;
	}
	
	public int getX() {
		return x;
	}
	
	public int getY() {
		return y;
	}
	
	
	public void addEntity(Entity e) {
		if (!liveEntities.contains(e, true))
			liveEntities.add(e);
		liveEntityGroup.addActor(e);
	}
	
	public void removeEntity(Entity e) {
		liveEntities.removeValue(e, true);
		e.remove();
		e.destroy();
	}
	
	public Group getEntityGroup() {
		return liveEntityGroup;
	}
	
	public Array<Entity> getEntities() {
		return liveEntities;
	}

	public void clearEntities() {
		liveEntities.clear();
		liveEntityGroup.clear();
	}
	
	//-------------------------------------------------------------------------
	// ContactFilter / ContactListener
	//-------------------------------------------------------------------------
	
	/* (non-Javadoc)
	 * @see com.badlogic.gdx.physics.box2d.ContactListener#beginContact(com.badlogic.gdx.physics.box2d.Contact)
	 */
	@Override
	public void beginContact(Contact contact) {
		if (contact.getFixtureA().getUserData() instanceof GameObject && contact.getFixtureB().getUserData() instanceof GameObject) {
			GameObject objA = (GameObject)contact.getFixtureA().getUserData();
			GameObject objB = (GameObject)contact.getFixtureB().getUserData();
			
			if ((objA instanceof PlayerCharacter && objB instanceof Portal)
					|| (objB instanceof PlayerCharacter && objA instanceof Portal)) {
				Portal p;
				PlayerCharacter c;
				if (objA instanceof Portal) {
					p = (Portal)objA;
					c = (PlayerCharacter)objB;
				} else {
					p = (Portal)objB;
					c = (PlayerCharacter)objA;
				}
				
				// TODO figure out how to use portals!
				RogueLike.getCurrentGame().usePortal(p);
			} else {
				objA.beginContact(objB);
				objB.beginContact(objA);
			}
		}
		
		if (contact.getFixtureA().getUserData() instanceof DamageBox && contact.getFixtureB().getUserData() == null) {
			DamageBox d = (DamageBox)contact.getFixtureA().getUserData();
			d.mapCollide();
		}
		
		if (contact.getFixtureB().getUserData() instanceof DamageBox && contact.getFixtureA().getUserData() == null) {
			DamageBox d = (DamageBox)contact.getFixtureB().getUserData();
			d.mapCollide();
		}
	}

	/* (non-Javadoc)
	 * @see com.badlogic.gdx.physics.box2d.ContactListener#endContact(com.badlogic.gdx.physics.box2d.Contact)
	 */
	@Override
	public void endContact(Contact contact) {
		if (contact.getFixtureA().getUserData() instanceof GameObject && contact.getFixtureB().getUserData() instanceof GameObject) {
			GameObject objA = (GameObject)contact.getFixtureA().getUserData();
			GameObject objB = (GameObject)contact.getFixtureB().getUserData();
			
			objA.endContact(objB);
			objB.endContact(objA);
		}
	}

	/* (non-Javadoc)
	 * @see com.badlogic.gdx.physics.box2d.ContactListener#preSolve(com.badlogic.gdx.physics.box2d.Contact, com.badlogic.gdx.physics.box2d.Manifold)
	 */
	@Override
	public void preSolve(Contact contact, Manifold oldManifold) {
		if (contact.getFixtureA().getUserData() instanceof GameObject && contact.getFixtureB().getUserData() instanceof GameObject) {
			GameObject objA = (GameObject)contact.getFixtureA().getUserData();
			GameObject objB = (GameObject)contact.getFixtureB().getUserData();
			if ((objA instanceof DamageBox && ((DamageBox)objA).isOwner(objB)) ||
					(objB instanceof DamageBox && ((DamageBox)objB).isOwner(objA))) {
				contact.setEnabled(false);
			}
		}
		
	}

	/* (non-Javadoc)
	 * @see com.badlogic.gdx.physics.box2d.ContactListener#postSolve(com.badlogic.gdx.physics.box2d.Contact, com.badlogic.gdx.physics.box2d.ContactImpulse)
	 */
	@Override
	public void postSolve(Contact contact, ContactImpulse impulse) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see com.badlogic.gdx.physics.box2d.ContactFilter#shouldCollide(com.badlogic.gdx.physics.box2d.Fixture, com.badlogic.gdx.physics.box2d.Fixture)
	 */
	@Override
	public boolean shouldCollide(Fixture fixtureA, Fixture fixtureB) {
		return (!(fixtureA.getUserData() instanceof GameObject && fixtureB.getUserData() instanceof GameObject)
				|| (((GameObject)fixtureA.getUserData()).shouldCollide((GameObject)fixtureB.getUserData())
						&& (((GameObject)fixtureB.getUserData()).shouldCollide((GameObject)fixtureA.getUserData()))));
	}
	
	@Override
	public String toString() {
		
		String json = "{";
		
		json += Utils.toJsonProperty("width", this.width);
		json += Utils.toJsonProperty("height", this.height);
		json += Utils.toJsonProperty("x", this.x);
		json += Utils.toJsonProperty("y", this.y);
		json += Utils.toJsonProperty("properties", this.properties);
		Array<Object> l = new Array<Object>();
		l.addAll(this.tileLayers);
		l.addAll(this.objLayers);
		json += Utils.toJsonProperty("layers", l);
		
		return json + "}";
	}
}
