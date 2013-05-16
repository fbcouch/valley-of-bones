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

import com.ahsgaming.valleyofbones.TextureManager;
import com.ahsgaming.valleyofbones.Utils;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;

/**
 * @author jami
 *
 */
public class TileSet {
	int firstgid = 1;
	String atlas;
	String name;
	Array<String> images;

	Array<TextureRegion> tiles;
	
	public TileSet() { }
	
	@SuppressWarnings("unchecked")
	public TileSet(ObjectMap<String, Object> set) {
		if (set.containsKey("firstgid"))
			this.firstgid = (int)Float.parseFloat(set.get("firstgid").toString());

		if (set.containsKey("name"))
			this.name = set.get("name").toString();

        if (set.containsKey("atlas"))
            atlas = set.get("atlas").toString();

        if (set.containsKey("tiles")) {
            Array<Object> objectArray = (Array<Object>)set.get("tiles");
            images = new Array<String>();
            tiles = new Array<TextureRegion>();

            for (Object o: objectArray) {
                images.add(o.toString());
                if (!atlas.equals("")) tiles.add(TextureManager.getSpriteFromAtlas(atlas, o.toString()));
            }
        }
	}

	public TextureRegion getTile(int gid) {
		if (gid - firstgid >= tiles.size) return null;
		return tiles.get(gid - firstgid);
	}
	
	@Override
	public String toString() {
		String json = "{";
		
		json += Utils.toJsonProperty("firstgid", this.firstgid);
		json += Utils.toJsonProperty("name", this.name);
		json += Utils.toJsonProperty("atlas", this.atlas);
		json += Utils.toJsonProperty("tiles", this.images);
		
		return json + "}";
	}

	/**
	 * @return the firstgid
	 */
	public int getFirstgid() {
		return firstgid;
	}

    public int getLastgid() {
        return firstgid + tiles.size - 1;
    }

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return the tiles
	 */
	public Array<TextureRegion> getTiles() {
		return tiles;
	}
	
	
}
