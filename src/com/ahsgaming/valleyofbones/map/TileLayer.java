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
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;

/**
 * @author jami
 *
 */
public class TileLayer {
	int[] data = new int[0];
	int x = 0, y = 0, width = 0, height = 0;
	String name = "";
	String type = "tilelayer";
	boolean visible = true;
	float opacity = 1;
	
	final Room room;
	
	Group layerGroup = new Group();
	
	public TileLayer(Room map) {
		this.room = map;
		
	}
	
	public TileLayer(Room map, String name) {
		this(map);
		this.name = name;
	}
	
	public TileLayer(Room map, String name, int x, int y, int width, int height) {
		this(map, name);
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		
		this.data = new int[width * height];
	}
	
	@SuppressWarnings("unchecked")
	public TileLayer(Room map, ObjectMap<String, Object> layer) {
		this(map);
		if (layer.containsKey("data")) {
			Array<Object> arr = (Array<Object>)layer.get("data");
			data = new int[arr.size];
			for (int i=0; i<arr.size; i++) {
				data[i] = (int)Float.parseFloat(arr.get(i).toString());
			}
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
	
	public Group getGroup() {
		return layerGroup;
	}
	
	public void init() {
		layerGroup.remove();
		layerGroup.setBounds(x, y, width, height);
		
		for (int i=0; i < data.length; i++) {
			if (data[i] == 0) continue;
			Image img = new Image(room.getTile(data[i]));
			img.setPosition(i % room.getWidth() * room.getTilewidth(), (int)(i / room.getWidth()) * room.getTileheight());
			layerGroup.addActor(img);
		}
	}
	
	@Override
	public String toString() {
		String json = "{";
		
		Array<Object> data = new Array<Object>();
		for (int i=0;i<this.data.length; i++) {
			data.add(this.data[i]);
		}
		
		json += Utils.toJsonProperty("data", data);
		json += Utils.toJsonProperty("name", this.name);
		json += Utils.toJsonProperty("x", this.x);
		json += Utils.toJsonProperty("y", this.y);
		json += Utils.toJsonProperty("width", this.width);
		json += Utils.toJsonProperty("height", this.height);
		json += Utils.toJsonProperty("opacity", this.opacity);
		json += Utils.toJsonProperty("visible", this.visible);
		json += Utils.toJsonProperty("type", this.type);
		
		return json + "}";
	}

	/**
	 * @return the data
	 */
	public int[] getData() {
		return data;
	}
	
	public void setTile(int x, int y, int gid) {
		data[x + y * this.width] = gid; 
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
	 * @return the map
	 */
	public Room getMap() {
		return room;
	}

	/**
	 * @return the layerGroup
	 */
	public Group getLayerGroup() {
		return layerGroup;
	}
}
