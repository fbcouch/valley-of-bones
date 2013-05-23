package com.ahsgaming.valleyofbones.units;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;

import com.ahsgaming.valleyofbones.*;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.ObjectMap;


public class Prototypes {
    public static final String LOG = "Prototypes";
	public static final String UNIT_FILE = "units.json";
	static ObjectMap<String, JsonProto> protos = null; 
	
	public static JsonProto getProto(String id) {
		if (protos == null) loadUnits(UNIT_FILE);
		
		return protos.get(id);
	}

    public static ObjectMap<String, JsonProto> getProtos() {
        if (protos == null) loadUnits(UNIT_FILE);
        ObjectMap<String, JsonProto> pmap = new ObjectMap<String, JsonProto>();
        pmap.putAll(protos);
        return pmap;
    }

    public static void setProto(String id, JsonProto proto) {
        if (protos == null) loadUnits(UNIT_FILE);

        protos.put(id, proto);
    }

    public static void setProtos(ObjectMap<String, JsonProto> protos) {
        if (protos == null) loadUnits(UNIT_FILE);

        protos.putAll(protos);
    }

    public static Array<JsonProto> getPlayerCanBuild(Player p, GameController gc) {
        Array<JsonProto> returnVal = new Array<JsonProto>();
        for (JsonProto jp: protos.values()) {
            int cost = 0;
            if (jp.hasProperty("cost"))
                cost = (int)Float.parseFloat(jp.getProperty("cost").toString());
            if ((jp.type.equals("building") || jp.type.equals("unit")) && cost >= 0) returnVal.add(jp);
        }

        for (int i=0; i<returnVal.size; i++) {
            int max = i;
            int maxcost = 0;
            if (returnVal.get(max).hasProperty("cost"))
                maxcost = (int)Float.parseFloat(returnVal.get(max).getProperty("cost").toString());

            for (int j=i+1; j<returnVal.size; j++) {
                int jcost = 0;
                if (returnVal.get(j).hasProperty("cost"))
                    jcost = (int)Float.parseFloat(returnVal.get(j).getProperty("cost").toString());

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
	
	public static class JsonProto {
		public String id = "";
		public String type = "";
		public String image = "";
		public String title = "";
		public String desc = "";
		
		public ObjectMap<String, Object> properties = new ObjectMap<String, Object>();
		
		public JsonProto() {}

		public JsonProto(ObjectMap<String, Object> json) {
			
			if (json.containsKey("id"))
				id = json.get("id").toString();
			
			if (json.containsKey("type"))
				type = json.get("type").toString();
			
			if (json.containsKey("image"))
				image = json.get("image").toString();
			
			if (json.containsKey("properties"))
				properties = (ObjectMap<String, Object>)json.get("properties");
			
			if (json.containsKey("title"))
				title = json.get("title").toString();
			
			if (json.containsKey("desc"))
				desc = json.get("desc").toString();
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
			return properties.containsKey(name);
		}
		
		public Object getProperty(String name) {
			return properties.get(name);
		}

        public void setProperty(String name, String value) {
            properties.put(name, value);
        }
	}
	
	public static void loadUnits(String file) {
		if (protos == null) protos = new ObjectMap<String, JsonProto>();
		
		JsonReader reader = new JsonReader();
		ObjectMap<String, Object> json = (ObjectMap<String, Object>)reader.parse(Gdx.files.internal(file));
		
		if (json.containsKey("entities") && json.get("entities") instanceof Array) {
			Array<Object> jsonArray = (Array<Object>)json.get("entities");
			for (Object o: jsonArray) {
				JsonProto jp = new JsonProto((ObjectMap<String, Object>)o);
				protos.put(jp.id, jp);
			}
		}
	}

    public static void saveUnits(String file) {
        Writer writer = Gdx.files.local("assets/" + file).writer(false);

        Array<JsonProto> protoArray = new Array<JsonProto>();
        int i = 0;

        for (JsonProto p: protos.values()) {
            protoArray.add(p);
            i++;
        }
        String json = "{";
        json += Utils.toJsonProperty("entities", protoArray);
        json += "}";

        PrettyJsonWriter pjw = new PrettyJsonWriter(writer);
        try {
            pjw.write(json);
            pjw.close();
        } catch (IOException e) {
            e.printStackTrace();
            Gdx.app.log(LOG, "Failed to write " + "assets/" + file);
        }


    }
}
