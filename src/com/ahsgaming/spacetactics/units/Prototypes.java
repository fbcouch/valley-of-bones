package com.ahsgaming.spacetactics.units;

import java.util.ArrayList;

import com.ahsgaming.spacetactics.SpaceTacticsGame;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.ObjectMap;


public class Prototypes {

	public static class JsonProto {
		static final String TYPE = "none";
		
		static JsonProto createFromMap(String id, ObjectMap<String, Object> map) {
			return null;
		}
	}
	
	/**
	 * Defines a unit
	 * @author jami
	 *
	 */
	public static class JsonUnit extends JsonProto {
		static final String TYPE = "unit";
		String id, name, image;
		int health, shield, armor;
		
		ArrayList<String> depends; // list of id's that are required for this to be built 
		
		public static JsonUnit createFromMap(String id, ObjectMap<String, Object> map) {
			JsonUnit retUnit = new JsonUnit();
			retUnit.id = id;
			for (String key : map.keys()) {
				if (key.equals("name")) {
					retUnit.name = (String) map.get(key);
				} else if (key.equals("image")) {
					retUnit.image = (String) map.get(key);
				} else if (key.equals("health")) {
					retUnit.health = (int)Float.parseFloat(map.get(key).toString());
				} else if (key.equals("armor")) {
					retUnit.armor = (int)Float.parseFloat(map.get(key).toString());
				} else if (key.equals("shield")) {
					retUnit.shield = (int)Float.parseFloat(map.get(key).toString());
				} else {
					Gdx.app.log(SpaceTacticsGame.LOG, JsonUnit.class.getSimpleName() + "#createFromMap: Unknown key");
				}
			}
			
			return retUnit;
		}
	}

	public static class JsonStation extends JsonShip {
		static final String TYPE = "station";
		
		public static JsonStation createFromMap(String id, ObjectMap<String, Object> map) {
			
			for (String key : map.keys()) {
				if (key.equals("hardpoints")) {
					
				}
			}
			return null;
		}
	}
	
	public static class JsonShip extends JsonUnit {
		static final String TYPE = "ship";
		
		ArrayList<JsonHardpoint> hardpoints;
		
		public static JsonShip createFromMap(String id, ObjectMap<String, Object> map) {
			return null;
		}
	}
	
	public static class JsonHardpoint extends JsonUnit {
		static final String TYPE = "hardpoint";
		
		Vector2 offset;
		
		String weapon;
		
		public static JsonHardpoint createFromMap(String id, ObjectMap<String, Object> map) {
			return null;
		}
	}
	
	public static class JsonSquadron extends JsonUnit {
		static final String TYPE = "squadron";
		
		ArrayList<String> weapons;
		
		public static JsonSquadron createFromMap(String id, ObjectMap<String, Object> map) {
			return null;
		}
	}
	
	public static class JsonWeapon extends JsonProto {
		static final String TYPE = "weapon";
		
		String id, name;
		
		public static JsonWeapon createFromMap(String id, ObjectMap<String, Object> map) {
			return null;
		}
	}
	
	public static class JsonUpgrade extends JsonProto {
		static final String TYPE = "upgrade";
		
		String id, name, fromId, toId;
		int cost;
		
		public static JsonUpgrade createFromMap(String id, ObjectMap<String, Object> map) {
			return null;
		}
	}
	
	
	public static ArrayList<JsonProto> loadUnits(String file) {
		ArrayList<JsonProto> unitList = new ArrayList<JsonProto>();
		
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
					unitList.add(JsonStation.createFromMap(key, (ObjectMap)mapObjs.get(key)));
				} else if (type.equals("ship")) {
					unitList.add(JsonShip.createFromMap(key, (ObjectMap)mapObjs.get(key)));
				} else if (type.equals("squadron")) {
					unitList.add(JsonSquadron.createFromMap(key, (ObjectMap)mapObjs.get(key)));
				} else if (type.equals("weapon")) {
					unitList.add(JsonWeapon.createFromMap(key, (ObjectMap)mapObjs.get(key)));
				} else if (type.equals("upgrade")) {
					unitList.add(JsonUpgrade.createFromMap(key, (ObjectMap)mapObjs.get(key)));
				} else {
					Gdx.app.log(SpaceTacticsGame.LOG, "loadUnits: Unknown type: " + type);
				}
			} else {
				unitList.add(JsonUnit.createFromMap(key, (ObjectMap)mapObjs.get(key)));
			}
		}
		
		return unitList;
	}
	
}
