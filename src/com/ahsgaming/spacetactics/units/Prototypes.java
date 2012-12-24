package com.ahsgaming.spacetactics.units;

import java.util.ArrayList;

import com.ahsgaming.spacetactics.SpaceTacticsGame;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.ObjectMap;


public class Prototypes {

	/**
	 * Defines a unit
	 * @author jami
	 *
	 */
	public static class JsonUnit {
		static final String TYPE = "unit";
		String id, name, image;
		int health, shield, armor;
		
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

	public static class JsonStation extends JsonUnit {
		static final String TYPE = "station";
		
		public static JsonStation createFromMap(String id, ObjectMap<String, Object> map) {
			return null;
		}
	}
	
	public static class JsonShip extends JsonUnit {
		static final String TYPE = "ship";
		
		public static JsonShip createFromMap(String id, ObjectMap<String, Object> map) {
			return null;
		}
	}
	
	public static class JsonSquadron extends JsonUnit {
		static final String TYPE = "squadron";
		
		public static JsonSquadron createFromMap(String id, ObjectMap<String, Object> map) {
			return null;
		}
	}
	
	public static class JsonWeapon {
		static final String TYPE = "weapon";
		
		String id, name;
		
		public static JsonWeapon createFromMap(String id, ObjectMap<String, Object> map) {
			return null;
		}
	}
	
	public static class JsonUpgrade {
		static final String TYPE = "upgrade";
		
		String id, name, fromId, toId;
		
		public static JsonUpgrade createFromMap(String id, ObjectMap<String, Object> map) {
			return null;
		}
	}
	
	
	public static ArrayList<JsonUnit> loadUnits(String file) {
		ArrayList<JsonUnit> unitList = new ArrayList<JsonUnit>();
		
		Json json = new Json();
		//json.fromJson(JsonUnit.class, Gdx.files.internal(file));
		JsonReader jsonReader = new JsonReader();
		Object rObj = jsonReader.parse(Gdx.files.internal(file));
		ObjectMap<String, Object> mapObjs = (ObjectMap)rObj;
		for (String key : mapObjs.keys()) {
			ObjectMap createObj = (ObjectMap)mapObjs.get(key);
			if (createObj.containsKey("type")) {
				String type = (String)createObj.get("type");
				if (type.equals("building")) {
					
				} else if (type.equals("ship")) {
					
				} else if (type.equals("squadron")) {
					
				} else {
					Gdx.app.log(SpaceTacticsGame.LOG, "loadUnits: Unknown type: " + type);
				}
			} else {
				JsonUnit.createFromMap(key, (ObjectMap)mapObjs.get(key));
			}
		}
		
		return unitList;
	}
	
}
