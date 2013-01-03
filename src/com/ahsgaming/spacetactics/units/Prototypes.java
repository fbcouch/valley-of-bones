package com.ahsgaming.spacetactics.units;

import java.util.ArrayList;

import com.ahsgaming.spacetactics.SpaceTacticsGame;
import com.ahsgaming.spacetactics.TextureManager;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.ObjectMap;


public class Prototypes {
	static final String UNIT_FILE = "units.json";
	static ObjectMap<String, JsonProto> protos = null; 
	
	public static JsonProto getProto(String id) {
		if (protos == null) protos = loadUnits(UNIT_FILE);
		
		return protos.get(id);
	}
	
	public static class JsonProto {
		public static final String TYPE = "none";
		public String id = "", name = "", image = "";
		public int cost = 0;
		public int food = 0;
		
		public Array<String> depends;
		
		public JsonProto() {}
		
		public JsonProto(String id, ObjectMap<String, Object> map) {
			this.id = id;
			
			for (String key: map.keys()) {
				if (key.equals("name")) {
					name = map.get(key).toString();
				} else if (key.equals("image")) {
					image = map.get(key).toString();
				} else if (key.equals("cost")) {
					cost = (int)Float.parseFloat(map.get(key).toString());
				} else if (key.equals("food")) {
					food = (int)Float.parseFloat(map.get(key).toString());
				} else if (key.equals("depends")) {
					depends = (Array<String>)map.get(key);
				}
			}
			if (image != null && !image.equals("")) TextureManager.loadTexture(image);
			
		}
		
		static JsonProto createFromMap(String id, ObjectMap<String, Object> map) {
			return new JsonProto(id, map);
		}
	}
	
	/**
	 * Defines a unit
	 * @author jami
	 *
	 */
	public static class JsonUnit extends JsonProto {
		public static final String TYPE = "unit";
		public int health = 0, shield = 0, armor = 0;
		public float speed = 0;
		public float accel = 0;
		public float turn = 0;
		public Array<String> weapons;
		public Rectangle bounds;
		
		public JsonUnit() {	}
		
		public JsonUnit(String id, ObjectMap<String, Object> map) {
			super(id, map);
			
			for (String key : map.keys()) {
				if (key.equals("health")) {
					health = (int)Float.parseFloat(map.get(key).toString());
				} else if (key.equals("armor")) {
					armor = (int)Float.parseFloat(map.get(key).toString());
				} else if (key.equals("shield")) {
					shield = (int)Float.parseFloat(map.get(key).toString());
				} else if (key.equals("speed")) {
					speed = Float.parseFloat(map.get(key).toString());
				} else if (key.equals("accel")) {
					accel = Float.parseFloat(map.get(key).toString());
				} else if (key.equals("turn")) {
					turn = Float.parseFloat(map.get(key).toString());
				} else if (key.equals("weapons")) {
					weapons = (Array<String>)map.get(key);
				} else if (key.equals("weapon")) {
					weapons = new Array<String>();
					weapons.add(map.get(key).toString());
				} else if (key.equals("bounds")) {
					Array<Float> b = (Array<Float>)map.get(key);
					if (b.size == 4) {
						bounds = new Rectangle(b.get(0), b.get(1), b.get(2), b.get(3));
					} else {
						Gdx.app.log(JsonUnit.class.getSimpleName(), "key bounds must be an array of 4 floats");
					}
				}
			}
		}
		
		public static JsonUnit createFromMap(String id, ObjectMap<String, Object> map) {
			return new JsonUnit(id, map);
		}
	}
	
	public static class JsonHardpoint extends JsonUnit {
		public static final String TYPE = "hardpoint";
		
		public Vector2 offset;
		
		public JsonHardpoint() { }
		
		public JsonHardpoint(String id, ObjectMap<String, Object> map) {
			super(id, map);
			for (String key: map.keys()) {
				if (key.equals("offset")) {
					Array<Float> offsetArray = (Array<Float>)map.get(key);
					offset = new Vector2(offsetArray.get(0), offsetArray.get(1));
				}
			}
		}
		
		public static JsonHardpoint createFromMap(String id, ObjectMap<String, Object> map) {
			return new JsonHardpoint(id, map);
		}
	}
	
	public static class JsonShip extends JsonUnit {
		public static final String TYPE = "ship";
		
		public Array<JsonHardpoint> hardpoints;
		
		public JsonShip() { }
		
		public JsonShip(String id, ObjectMap<String, Object> map) {
			super(id, map);
			
			for (String key: map.keys()) {
				if (key.equals("hardpoints")) {
					ObjectMap<String, Object> hpMap = (ObjectMap<String, Object>)map.get(key);
					Array<JsonHardpoint> hardPts = new Array<JsonHardpoint>();
					for (String hpid: hpMap.keys()) {
						hardPts.add(JsonHardpoint.createFromMap(hpid, (ObjectMap<String, Object>)hpMap.get(hpid)));
					}
					hardpoints = hardPts;
				}
			}
		}
		
		public static JsonShip createFromMap(String id, ObjectMap<String, Object> map) {
			return new JsonShip(id, map);
		}
	}
	
	public static class JsonStation extends JsonShip {
		public static final String TYPE = "station";
		
		public JsonStation() { }
		
		public JsonStation(String id, ObjectMap<String, Object> map) {
			super(id, map);
		}
		
		public static JsonStation createFromMap(String id, ObjectMap<String, Object> map) {
			return new JsonStation(id, map);
		}
	}
	
	public static class JsonSquadron extends JsonUnit {
		public static final String TYPE = "squadron";
		
		public JsonSquadron() { }
		
		public JsonSquadron(String id, ObjectMap<String, Object> map) {
			super(id, map);
		}
		
		public static JsonSquadron createFromMap(String id, ObjectMap<String, Object> map) {
			return new JsonSquadron(id, map);
		}
	}
	
	public static class JsonWeapon extends JsonProto {
		public static final String TYPE = "weapon";
		
		public float damage = 0, lifetime = 0, speed = 0, accel = 0, turn = 0, fireRate = 0;
		
		public JsonWeapon() { }
		
		public JsonWeapon(String id, ObjectMap<String, Object> map) {
			super(id, map);
			
			for (String key: map.keys()) {
				if (key.equals("damage")) {
					damage = Float.parseFloat(map.get(key).toString());
				} else if (key.equals("lifetime")) {
					lifetime = Float.parseFloat(map.get(key).toString());
				} else if (key.equals("speed")) {
					speed = Float.parseFloat(map.get(key).toString());
				} else if (key.equals("accel")) {
					accel = Float.parseFloat(map.get(key).toString());
				} else if (key.equals("turn")) {
					turn = Float.parseFloat(map.get(key).toString());
				} else if (key.equals("fire-rate")) {
					fireRate = Float.parseFloat(map.get(key).toString());
				}
			}
		}
		
		public static JsonWeapon createFromMap(String id, ObjectMap<String, Object> map) {
			return new JsonWeapon(id, map);
		}
	}
	
	public static class JsonUpgrade extends JsonProto {
		public static final String TYPE = "upgrade";
		
		public String fromId = "", toId = "";
		
		
		public JsonUpgrade() { }
		
		public JsonUpgrade(String id, ObjectMap<String, Object> map) {
			super (id, map);
			
			for (String key: map.keys()) {
				if (key.equals("from")) {
					fromId = map.get(key).toString();
				} else if (key.equals("to")) {
					toId = map.get(key).toString();
				}
			}
		}
		
		public static JsonUpgrade createFromMap(String id, ObjectMap<String, Object> map) {
			return new JsonUpgrade(id, map);
		}
	}
	
	@SuppressWarnings("unchecked")
	public static ObjectMap<String, JsonProto> loadUnits(String file) {
		ObjectMap<String, JsonProto> unitList = new ObjectMap<String, JsonProto>();
		
		JsonReader jsonReader = new JsonReader();
		Object rObj = jsonReader.parse(Gdx.files.internal(file));
		ObjectMap<String, Object> mapObjs = (ObjectMap<String, Object>)rObj;
		for (String key : mapObjs.keys()) {
			ObjectMap<String, Object> createObj = (ObjectMap<String, Object>)mapObjs.get(key);
			if (createObj.containsKey("type")) {
				String type = (String)createObj.get("type");
				if (type.equals("station")) {
					unitList.put(key, JsonStation.createFromMap(key, (ObjectMap<String, Object>)mapObjs.get(key)));
				} else if (type.equals("ship")) {
					unitList.put(key, JsonShip.createFromMap(key, (ObjectMap<String, Object>)mapObjs.get(key)));
				} else if (type.equals("squadron")) {
					unitList.put(key, JsonSquadron.createFromMap(key, (ObjectMap<String, Object>)mapObjs.get(key)));
				} else if (type.equals("weapon")) {
					unitList.put(key, JsonWeapon.createFromMap(key, (ObjectMap<String, Object>)mapObjs.get(key)));
				} else if (type.equals("upgrade")) {
					unitList.put(key, JsonUpgrade.createFromMap(key, (ObjectMap<String, Object>)mapObjs.get(key)));
				} else {
					Gdx.app.log(SpaceTacticsGame.LOG, "loadUnits: Unknown type: " + type);
				}
			} else {
				unitList.put(key, JsonUnit.createFromMap(key, (ObjectMap<String, Object>)mapObjs.get(key)));
			}
		}
		
		return unitList;
	}
	
}
