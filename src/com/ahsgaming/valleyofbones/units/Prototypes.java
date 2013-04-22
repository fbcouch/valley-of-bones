package com.ahsgaming.valleyofbones.units;

import java.util.ArrayList;

import com.ahsgaming.valleyofbones.Utils;
import com.ahsgaming.valleyofbones.VOBGame;
import com.ahsgaming.valleyofbones.TextureManager;
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
		if (protos == null) loadUnits(UNIT_FILE);
		
		return protos.get(id);
	}
	
	public static class JsonProto {
		public String id = "", type = "", image = "";
		public String title = "", desc = "";
		
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
}
