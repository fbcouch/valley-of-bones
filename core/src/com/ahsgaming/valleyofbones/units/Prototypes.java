package com.ahsgaming.valleyofbones.units;

import java.util.ArrayList;
import java.util.HashMap;

import com.ahsgaming.valleyofbones.*;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.*;


public class Prototypes {
	static final String UNIT_FILE = "rules/classic.json"; // TODO pass this in
    static HashMap<String, Array<JsonProto>> protos = null;
	
	public static JsonProto getProto(String race, String id) {
		if (protos == null) loadUnits(UNIT_FILE);
		if (!protos.containsKey(race)) return null;

        JsonProto proto = null;
        for (JsonProto p: protos.get(race)) {
            if (p.id.equals(id)) proto = p;
        }

		return proto;
	}

    public static Array<JsonProto> getPlayerCanBuild(String race) {
        if (protos == null) loadUnits(UNIT_FILE);
        if (!protos.containsKey(race)) return null;

        Array<JsonProto> returnVal = new Array<JsonProto>();
        for (JsonProto jp: protos.get(race)) {
            int cost = 0;
            if (jp.hasProperty("cost"))
                cost = jp.getProperty("cost").asInt();
            if ((jp.type.equals("building") || jp.type.equals("unit")) && cost >= 0) returnVal.add(jp);
        }

        for (int i=0; i<returnVal.size; i++) {
            int max = i;
            int maxcost = 0;
            if (returnVal.get(max).hasProperty("cost"))
                maxcost = returnVal.get(max).getProperty("cost").asInt();

            for (int j=i+1; j<returnVal.size; j++) {
                int jcost = 0;
                if (returnVal.get(j).hasProperty("cost"))
                    jcost = returnVal.get(j).getProperty("cost").asInt();

                if (jcost > maxcost) {
                    max = j;
                    maxcost = jcost;
                }
            }

            if (max != i) {
                returnVal.insert(i, returnVal.removeIndex(max));
            }
        }

        return returnVal;
    }

    public static Array<JsonProto> getAll(String race) {
        if (protos == null) loadUnits(UNIT_FILE);
        if (!protos.containsKey(race)) return null;

        Array<JsonProto> array = new Array<JsonProto>();
        for(JsonProto proto: protos.get(race)) {
            if (proto.type.equals("building") || proto.type.equals("unit"))
                array.add(proto);
        }
        return array;
    }

    public static Array<String> getRaces() {
        if (protos == null) loadUnits(UNIT_FILE);
        Array<String> races = new Array<String>();
        for (String race: protos.keySet()) {
            races.add(race);
        }
        return races;
    }
	
	public static class JsonProto {
		public String id = "";
		public String type = "";
		public String image = "";
		public String title = "";
		public String desc = "";
        public String attackSound = "";

        public int cost = 0;
        public int food = 0;
		
		public JsonValue properties = new JsonValue(0);
		
		public JsonProto() {}

		public JsonProto(JsonValue json) {
			
//			if (json.containsKey("id"))
            id = json.get("id").asString();
			
//			if (json.containsKey("type"))
            type = json.get("type").asString();
			
//			if (json.containsKey("image"))
            image = json.get("image").asString();
			

            properties = json.get("properties");
			
//			if (json.containsKey("title"))
				title = json.get("title").asString();
			
//			if (json.containsKey("desc"))
				desc = json.get("desc").asString();

            if (hasProperty("cost"))
                cost = getProperty("cost").asInt();

            if (hasProperty("food") && getProperty("food").isNumber())
                food = getProperty("food").asInt();

            attackSound = json.getString("attacksound", null);
		}
		
		@Override
		public String toString() {
			String json = "{";
			json += Utils.toJsonProperty("id", id);
			json += Utils.toJsonProperty("type", type);
			json += Utils.toJsonProperty("image", image);
			json += Utils.toJsonProperty("title", title);
			json += Utils.toJsonProperty("desc", desc);
			json += Utils.toJsonProperty("properties", properties);
			json += "}";
			return json;
		}
		
		public boolean hasProperty(String name) {
			return properties.get(name) != null;
		}
		
		public JsonValue getProperty(String name) {
			return properties.get(name);
		}
	}
	
	public static void loadUnits(String file) {
		if (protos == null) protos = new HashMap<String, Array<JsonProto>>();
		
		JsonReader reader = new JsonReader();
		JsonValue json = reader.parse(Gdx.files.internal("assets/" + file));

        for (JsonValue race: json) {
            Array<JsonProto> raceProtos = new Array<JsonProto>();

            for (JsonValue unit: race) {
                raceProtos.add(new JsonProto(unit));
            }

            protos.put(race.name, raceProtos);
        }
		
//		if (json.get("entities") != null) {
//			JsonValue jsonArray = json.get("entities");
//			for (JsonValue child: jsonArray) {
//				JsonProto jp = new JsonProto(child);
//				protos.put(jp.id, jp);
//			}
//		}
	}
}
