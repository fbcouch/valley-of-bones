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
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.ObjectMap;

/**
 * @author jami
 *
 */
public class TileLayer {
    public static final String LOG = "TileLayer";
	int[] data = new int[0];
	Vector2 size = new Vector2();
    boolean traversible = false;
    boolean collidable = false;
	boolean visible = true;
	float opacity = 1;
	
	final HexMap map;
	
	Group layerGroup = new Group();
    Image[] tiles;

	@SuppressWarnings("unchecked")
	public TileLayer(HexMap map, JsonValue layer) {
		this.map = map;

        collidable = layer.getBoolean("collidable", true);
        opacity = layer.getFloat("opacity", 1);
        visible = layer.getBoolean("visible", true);
        traversible = layer.getBoolean("traversible", true);

        data = new int[layer.get("data").size];
        int i = 0;
        for (JsonValue v: layer.get("data")) {
            data[i] = v.asInt();
            i++;
        }

        size.set(map.getWidth(), map.getHeight());

        init();
	}
	
	public Group getGroup() {
		return layerGroup;
	}
	
	public void init() {
		layerGroup.remove();
        layerGroup.setTransform(false);
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
        json += Utils.toJsonProperty("collidable", this.collidable);
        json += Utils.toJsonProperty("opacity", this.opacity);
		json += Utils.toJsonProperty("visible", this.visible);
        json += Utils.toJsonProperty("traversible", this.traversible);
		
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

    public int getTileData(int x, int y) {
        return data[x + y * (int)size.x];
    }

    public void setTileStatus(int x, int y, Color status) {
        if (tiles[x + y * (int)size.x] != null) tiles[x + y * (int)size.x].setColor(status);
    }

    public Color getTileStatus(int x, int y) {
        return (tiles[x + y * (int)size.x] == null ? null : tiles[x + y * (int)size.x].getColor());
    }

    public boolean isCollidable() {
        return collidable;
    }

	/**
	 * @return the visible
	 */
	public boolean isVisible() {
		return visible;
	}

    public boolean isTraversible() {
        return traversible;
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
