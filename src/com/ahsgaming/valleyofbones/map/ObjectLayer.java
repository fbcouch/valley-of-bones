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

import com.ahsgaming.roguelike.Utils;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;

/**
 * @author jami
 *
 */
public class ObjectLayer {
	public static String LOG = "ObjectLayer";
	
	int x = 0, y = 0, width = 0, height = 0;
	String name = "";
	String type = "objectgroup";
	boolean visible = true;
	float opacity = 1;
	
	Array<Object> objects = new Array<Object>();
	
	final Room map;
	
	/**
	 * 
	 */
	public ObjectLayer(Room map) {
		this.map = map;
	}
	
	public ObjectLayer(Room map, String name) {
		this(map);
		this.name = name;
	}
	
	public ObjectLayer(Room map, String name, int x, int y, int width, int height) {
		this(map, name);
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
	}
	
	@SuppressWarnings("unchecked")
	public ObjectLayer(Room map, ObjectMap<String, Object> layer) {
		this(map);
		
		if (layer.containsKey("objects")) {
			this.objects = (Array<Object>)layer.get("objects");
		}
		
		if (layer.containsKey("x"))
			this.x = (int)Float.parseFloat(layer.get("x").toString());
		
		if (layer.containsKey("y"))
			this.y = (int)Float.parseFloat(layer.get("y").toString());
		
		if (layer.containsKey("width"))
			this.width = (int)Float.parseFloat(layer.get("width").toString());
		
		if (layer.containsKey("height"))
			this.height = (int)Float.parseFloat(layer.get("height").toString());
		
		if (layer.containsKey("name"))
			this.name = layer.get("name").toString();
		
		if (layer.containsKey("opacity"))
			this.opacity = Float.parseFloat(layer.get("opacity").toString());
		
		if (layer.containsKey("visible"))
			this.visible = Boolean.parseBoolean(layer.get("visible").toString());
		
		if (layer.containsKey("type"))
			this.type = layer.get("type").toString();
		
	}
	
	@Override
	public String toString() {
		String json = "{";
		
		json += Utils.toJsonProperty("name", this.name);
		json += Utils.toJsonProperty("x", this.x);
		json += Utils.toJsonProperty("y", this.y);
		json += Utils.toJsonProperty("width", this.width);
		json += Utils.toJsonProperty("height", this.height);
		json += Utils.toJsonProperty("opacity", this.opacity);
		json += Utils.toJsonProperty("visible", this.visible);
		json += Utils.toJsonProperty("type", this.type);
		json += "\"objects\":[";
		
		for (Object o: objects) {
			json += "{";
			
			ObjectMap<String, Object> om = (ObjectMap<String, Object>)o;
			for (String key: om.keys()) {
				json += Utils.toJsonProperty(key, om.get(key));
			}
			
			json += "},";
		}
		
		json += "],";
		
		return json + "}";
	}

	/**
	 * @return the lOG
	 */
	public static String getLOG() {
		return LOG;
	}

	/**
	 * @return the x
	 */
	public int getX() {
		return x;
	}
	
	public void setX(int x) {
		this.x = x;
	}

	/**
	 * @return the y
	 */
	public int getY() {
		return y;
	}
	
	public void setY(int y) {
		this.y = y;
	}

	/**
	 * @return the width
	 */
	public int getWidth() {
		return width;
	}
	
	public void setWidth(int w) {
		this.width = w;
	}

	/**
	 * @return the height
	 */
	public int getHeight() {
		return height;
	}
	
	public void setHeight(int h) {
		this.height = h;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return the type
	 */
	public String getType() {
		return type;
	}

	/**
	 * @return the visible
	 */
	public boolean isVisible() {
		return visible;
	}

	/**
	 * @return the opacity
	 */
	public float getOpacity() {
		return opacity;
	}

	/**
	 * @return the objects
	 */
	public Array<Object> getObjects() {
		return objects;
	}

	/**
	 * @return the map
	 */
	public Room getMap() {
		return map;
	}

	
	
}
