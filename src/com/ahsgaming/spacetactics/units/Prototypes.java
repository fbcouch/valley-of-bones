package com.ahsgaming.spacetactics.units;

import java.util.ArrayList;

import com.ahsgaming.spacetactics.SpaceTacticsGame;
import com.ahsgaming.spacetactics.TextureManager;
import com.badlogic.gdx.Gdx;
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
		static final String TYPE = "none";
		String id = "", name = "", image = "";
		int cost = 0;
		
		ArrayList<String> depends;
		
		static JsonProto createFromMap(String id, ObjectMap<String, Object> map) {
			JsonProto retProto = new JsonProto();

			retProto.id = id;
			for (String key: map.keys()) {
				if (key.equals("name")) {
					retProto.name = map.get(key).toString();
				} else if (key.equals("image")) {
					retProto.image = map.get(key).toString();
				} else if (key.equals("cost")) {
					retProto.cost = (int)Float.parseFloat(map.get(key).toString());
				} else if (key.equals("depends")) {
					retProto.depends = new ArrayList<String>();
					Array<String> deps = (Array<String>)map.get(key);
					for (String dep: deps) {
						retProto.depends.add(dep);
					}
				}
			}
			if (retProto.image != null && !retProto.image.equals("")) TextureManager.loadTexture(retProto.image);
			
			return retProto;
		}
	}
	
	/**
	 * Defines a unit
	 * @author jami
	 *
	 */
	public static class JsonUnit extends JsonProto {
		static final String TYPE = "unit";
		int health = 0, shield = 0, armor = 0;
		float speed = 0;
		float accel = 0;
		float turn = 0;
		Array<String> weapons;
		
		public static JsonUnit createFromMap(String id, ObjectMap<String, Object> map) {
			JsonUnit retUnit = new JsonUnit();
			
			JsonProto proto = JsonProto.createFromMap(id, map);
			retUnit.name = proto.name;
			retUnit.image = proto.image;
			retUnit.cost = proto.cost;
			retUnit.depends = proto.depends;
			
			retUnit.id = id;
			for (String key : map.keys()) {
				if (key.equals("health")) {
					retUnit.health = (int)Float.parseFloat(map.get(key).toString());
				} else if (key.equals("armor")) {
					retUnit.armor = (int)Float.parseFloat(map.get(key).toString());
				} else if (key.equals("shield")) {
					retUnit.shield = (int)Float.parseFloat(map.get(key).toString());
				} else if (key.equals("speed")) {
					retUnit.speed = Float.parseFloat(map.get(key).toString());
				} else if (key.equals("accel")) {
					retUnit.accel = Float.parseFloat(map.get(key).toString());
				} else if (key.equals("turn")) {
					retUnit.turn = Float.parseFloat(map.get(key).toString());
				} else if (key.equals("weapons")) {
					retUnit.weapons = (Array<String>)map.get(key);
				} else if (key.equals("weapon")) {
					retUnit.weapons = new Array<String>();
					retUnit.weapons.add(map.get(key).toString());
				} else {
					Gdx.app.log(SpaceTacticsGame.LOG, JsonUnit.class.getSimpleName() + "#createFromMap: Unknown key");
				}
			}
			return retUnit;
		}
	}
	
	public static class JsonHardpoint extends JsonUnit {
		static final String TYPE = "hardpoint";
		
		Vector2 offset;
		
		String weapon;
		
		public static JsonHardpoint createFromMap(String id, ObjectMap<String, Object> map) {
			JsonHardpoint retPoint = new JsonHardpoint();
			retPoint.id = id;
			
			JsonUnit unit = JsonUnit.createFromMap(id, map);
			retPoint.name = unit.name;
			retPoint.image = unit.image;
			retPoint.depends = unit.depends;
			retPoint.cost = unit.cost;
			retPoint.health = unit.health;
			retPoint.shield = unit.shield;
			retPoint.armor = unit.armor;
			
			for (String key: map.keys()) {
				if (key.equals("offset")) {
					Array<Float> offsetArray = (Array<Float>)map.get(key);
					retPoint.offset = new Vector2(offsetArray.get(0), offsetArray.get(1));
				} else if (key.equals("weapon")) {
					retPoint.weapon = map.get(key).toString();
				}
			}
			
			return retPoint;
		}
	}
	
	public static class JsonShip extends JsonUnit {
		static final String TYPE = "ship";
		
		ArrayList<JsonHardpoint> hardpoints;
		ArrayList<String> weapons;
		
		public static JsonShip createFromMap(String id, ObjectMap<String, Object> map) {
			JsonShip retShip = new JsonShip();
			retShip.id = id;
			
			JsonUnit unit = JsonUnit.createFromMap(id, map);
			retShip.name = unit.name;
			retShip.image = unit.image;
			retShip.depends = unit.depends;
			retShip.cost = unit.cost;
			retShip.health = unit.health;
			retShip.shield = unit.shield;
			retShip.armor = unit.armor;
			
			for (String key: map.keys()) {
				if (key.equals("weapons")) {
					Array<String> wepArray = (Array<String>)map.get(key);
					retShip.weapons = new ArrayList<String>();
					for (String weapon: wepArray) {
						retShip.weapons.add(weapon);
					}
				} else if (key.equals("hardpoints")) {
					ObjectMap<String, Object> hpMap = (ObjectMap)map.get(key);
					ArrayList<JsonHardpoint> hardPts = new ArrayList<JsonHardpoint>();
					for (String hpid: hpMap.keys()) {
						hardPts.add(JsonHardpoint.createFromMap(hpid, (ObjectMap)hpMap.get(key)));
					}
					retShip.hardpoints = hardPts;
				}
			}
			
			return retShip;
		}
	}
	
	public static class JsonStation extends JsonShip {
		static final String TYPE = "station";
		
		public static JsonStation createFromMap(String id, ObjectMap<String, Object> map) {
			JsonStation retStation = new JsonStation();
			retStation.id = id;
			
			JsonUnit unit = JsonUnit.createFromMap(id, map);
			retStation.name = unit.name;
			retStation.image = unit.image;
			retStation.depends = unit.depends;
			retStation.cost = unit.cost;
			retStation.health = unit.health;
			retStation.shield = unit.shield;
			retStation.armor = unit.armor;
			
			for (String key : map.keys()) {
				if (key.equals("hardpoints")) {
					ObjectMap<String, Object> hpMap = (ObjectMap)map.get(key);
					ArrayList<JsonHardpoint> hardPts = new ArrayList<JsonHardpoint>();
					for (String hpid: hpMap.keys()) {
						
						hardPts.add(JsonHardpoint.createFromMap(hpid, (ObjectMap)hpMap.get(hpid)));
					}
					retStation.hardpoints = hardPts;
				}
			}
			return retStation;
		}
	}
	
	public static class JsonSquadron extends JsonUnit {
		static final String TYPE = "squadron";
		
		public static JsonSquadron createFromMap(String id, ObjectMap<String, Object> map) {
			JsonSquadron retSquad = new JsonSquadron();
			retSquad.id = id;
			
			JsonUnit unit = JsonUnit.createFromMap(id, map);
			retSquad.name = unit.name;
			retSquad.image = unit.image;
			retSquad.depends = unit.depends;
			retSquad.cost = unit.cost;
			retSquad.health = unit.health;
			retSquad.shield = unit.shield;
			retSquad.armor = unit.armor;
			retSquad.speed = unit.speed;
			retSquad.accel = unit.accel;
			retSquad.turn = unit.turn;
			retSquad.weapons = unit.weapons;
			/*
			for (String key: map.keys()) {
				if (key.equals("weapons")) {
					Array<String> wepArray = (Array<String>)map.get(key);
					retSquad.weapons = new ArrayList<String>();
					for (String weapon: wepArray) {
						retSquad.weapons.add(weapon);
					}
				}
			}*/
			
			return retSquad;
		}
	}
	
	public static class JsonWeapon extends JsonProto {
		static final String TYPE = "weapon";
		
		public float damage = 0, lifetime = 0, speed = 0, accel = 0, turn = 0, fireRate = 0;
		
		public static JsonWeapon createFromMap(String id, ObjectMap<String, Object> map) {
			JsonWeapon retWep = new JsonWeapon();
			
			JsonProto proto = JsonProto.createFromMap(id, map);
			retWep.name = proto.name;
			retWep.image = proto.image;
			retWep.cost = proto.cost;
			retWep.depends = proto.depends;
			
			retWep.id = id;
			for (String key: map.keys()) {
				if (key.equals("damage")) {
					retWep.damage = Float.parseFloat(map.get(key).toString());
				} else if (key.equals("lifetime")) {
					retWep.lifetime = Float.parseFloat(map.get(key).toString());
				} else if (key.equals("speed")) {
					retWep.speed = Float.parseFloat(map.get(key).toString());
				} else if (key.equals("accel")) {
					retWep.accel = Float.parseFloat(map.get(key).toString());
				} else if (key.equals("turn")) {
					retWep.turn = Float.parseFloat(map.get(key).toString());
				} else if (key.equals("fire-rate")) {
					retWep.fireRate = Float.parseFloat(map.get(key).toString());
				}
			}
			
			return retWep;
		}
	}
	
	public static class JsonUpgrade extends JsonProto {
		static final String TYPE = "upgrade";
		
		String id = "", name = "", fromId = "", toId = "";
		int cost = 0;
		
		public static JsonUpgrade createFromMap(String id, ObjectMap<String, Object> map) {
			JsonUpgrade retUp = new JsonUpgrade();
			
			JsonProto proto = JsonProto.createFromMap(id, map);
			retUp.name = proto.name;
			retUp.image = proto.image;
			retUp.cost = proto.cost;
			retUp.depends = proto.depends;
			
			retUp.id = id;
			for (String key: map.keys()) {
				if (key.equals("from")) {
					retUp.fromId = map.get(key).toString();
				} else if (key.equals("to")) {
					retUp.toId = map.get(key).toString();
				}
			}
			
			return retUp;
		}
	}
	
	
	public static ObjectMap<String, JsonProto> loadUnits(String file) {
		ObjectMap<String, JsonProto> unitList = new ObjectMap<String, JsonProto>();
		
		Json json = new Json();
		//json.fromJson(JsonUnit.class, Gdx.files.internal(file));
		JsonReader jsonReader = new JsonReader();
		Object rObj = jsonReader.parse(Gdx.files.internal(file));
		ObjectMap<String, Object> mapObjs = (ObjectMap)rObj;
		for (String key : mapObjs.keys()) {
			ObjectMap<String, Object> createObj = (ObjectMap)mapObjs.get(key);
			if (createObj.containsKey("type")) {
				String type = (String)createObj.get("type");
				if (type.equals("station")) {
					unitList.put(key, JsonStation.createFromMap(key, (ObjectMap)mapObjs.get(key)));
				} else if (type.equals("ship")) {
					unitList.put(key, JsonShip.createFromMap(key, (ObjectMap)mapObjs.get(key)));
				} else if (type.equals("squadron")) {
					unitList.put(key, JsonSquadron.createFromMap(key, (ObjectMap)mapObjs.get(key)));
				} else if (type.equals("weapon")) {
					unitList.put(key, JsonWeapon.createFromMap(key, (ObjectMap)mapObjs.get(key)));
				} else if (type.equals("upgrade")) {
					unitList.put(key, JsonUpgrade.createFromMap(key, (ObjectMap)mapObjs.get(key)));
				} else {
					Gdx.app.log(SpaceTacticsGame.LOG, "loadUnits: Unknown type: " + type);
				}
			} else {
				unitList.put(key, JsonUnit.createFromMap(key, (ObjectMap)mapObjs.get(key)));
			}
		}
		
		return unitList;
	}
	
}
