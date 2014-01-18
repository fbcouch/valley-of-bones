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
import com.ahsgaming.valleyofbones.VOBGame;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.JsonValue;
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

	Array<TextureRegion> tiles, depths;
	
	public TileSet() { }

	public TileSet(JsonValue json) {

        firstgid = json.getInt("firstgid");

        name = json.getString("name", "");

        atlas = json.getString("atlas");

        images = new Array<String>();
        tiles = new Array<TextureRegion>();
        depths = new Array<TextureRegion>();

        for (JsonValue v: json.get("tiles")) {
            images.add(v.asString());
            tiles.add(VOBGame.instance.getTextureManager().getSpriteFromAtlas(atlas, v.asString()));
            depths.add(VOBGame.instance.getTextureManager().getSpriteFromAtlas(atlas, v.asString() + "-depth"));
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
