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

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.ahsgaming.valleyofbones.Utils;
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
	Vector2 size = new Vector2();
    boolean collidable = true;
	boolean visible = true;
	float opacity = 1;
	
	final HexMap map;
	
	Group layerGroup = new Group();
    Image[] tiles;

	@SuppressWarnings("unchecked")
	public TileLayer(HexMap map, ObjectMap<String, Object> layer) {
		this.map = map;

        if (layer.containsKey("data")) {
			Array<Object> arr = (Array<Object>)layer.get("data");
			data = new int[arr.size];
			for (int i=0; i<arr.size; i++) {
				data[i] = (int)Float.parseFloat(arr.get(i).toString());
			}
		}
		
		if (layer.containsKey("opacity"))
			opacity = Float.parseFloat(layer.get("opacity").toString());
		
		if (layer.containsKey("visible"))
			visible = Boolean.parseBoolean(layer.get("visible").toString());

        if (layer.containsKey("collidable"))
            collidable = Boolean.parseBoolean(layer.get("collidable").toString());

        size.set(map.getWidth(), map.getHeight());

        init();
	}
	
	public Group getGroup() {
		return layerGroup;
	}
	
	public void init() {
		layerGroup.remove();
		layerGroup.setBounds(0, 0, size.x, size.y);
		tiles = new Image[(int)(size.x * size.y)];
		for (int i=0; i < data.length; i++) {
			if (data[i] == 0) continue;
			Image img = new Image(map.getTile(data[i]));
			img.setPosition(i % map.getWidth() * map.getTileWidth() + ((i / map.getWidth()) % 2) * map.getTileWidth() * 0.5f, (int)(i / map.getWidth()) * map.getTileHeight() * 0.75f);
			layerGroup.addActor(img);
            tiles[i] = img;
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
		json += Utils.toJsonProperty("opacity", this.opacity);
		json += Utils.toJsonProperty("visible", this.visible);
        json += Utils.toJsonProperty("collidable", this.collidable);
		
		return json + "}";
	}

	/**
	 * @return the data
	 */
	public int[] getData() {
		return data;
	}
	
	public void setTile(int x, int y, int gid) {
		data[x + y * (int)size.x] = gid;
	}

    public void setTileStatus(int x, int y, Color status) {
        if (tiles[x + y * (int)size.x] != null) tiles[x + y * (int)size.x].setColor(status);
    }

    public Color getTileStatus(int x, int y) {
        return (tiles[x + y * (int)size.x] == null ? null : tiles[x + y * (int)size.x].getColor());
    }

	/**
	 * @return the visible
	 */
	public boolean isVisible() {
		return visible;
	}

    public boolean isCollidable() {
        return collidable;
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
	public HexMap getMap() {
		return map;
	}

	/**
	 * @return the layerGroup
	 */
	public Group getLayerGroup() {
		return layerGroup;
	}
}
