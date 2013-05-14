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

import com.ahsgaming.roguelike.TextureManager;
import com.ahsgaming.roguelike.Utils;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;

/**
 * @author jami
 *
 */
public class TileSet {
	int firstgid = 1;
	String image;
	int imageheight = 0, imagewidth = 0;
	int margin = 0, spacing = 0;
	String name;
	ObjectMap<String, Object> properties;
	int tileheight = 0, tilewidth = 0;
	
	TextureRegion texture;
	Array<TextureRegion> tiles;
	
	public TileSet() { }
	
	@SuppressWarnings("unchecked")
	public TileSet(ObjectMap<String, Object> set) {
		if (set.containsKey("firstgid"))
			this.firstgid = (int)Float.parseFloat(set.get("firstgid").toString());
		
		if (set.containsKey("image"))
			this.image = set.get("image").toString();
		
		if (set.containsKey("imagewidth"))
			this.imagewidth = (int)Float.parseFloat(set.get("imagewidth").toString());
		
		if (set.containsKey("imageheight"))
			this.imageheight = (int)Float.parseFloat(set.get("imageheight").toString());
		
		if (set.containsKey("margin"))
			this.margin = (int)Float.parseFloat(set.get("margin").toString());
		
		if (set.containsKey("name"))
			this.name = set.get("firstgid").toString();
		
		if (set.containsKey("properties"))
			this.properties = (ObjectMap<String, Object>)set.get("properties");
		
		if (set.containsKey("spacing"))
			this.spacing = (int)Float.parseFloat(set.get("spacing").toString());
		
		if (set.containsKey("tileheight"))
			this.tileheight = (int)Float.parseFloat(set.get("tileheight").toString());
		
		if (set.containsKey("tilewidth"))
			this.tilewidth = (int)Float.parseFloat(set.get("tilewidth").toString());
		
	}
	
	public void init() {
		this.texture = TextureManager.getTexture(this.image);
		
		tiles = new Array<TextureRegion>();
		
		for (int y = 0; y < (int)(imageheight / tileheight); y++) {
			for (int x = 0; x < (int)(imagewidth / tilewidth); x++) {
				tiles.add(new TextureRegion(texture, (int)(x * tilewidth), (int)(y * tileheight), tilewidth, tileheight));
			}
		}
		
		// TODO this should probably update the tileset properties as well
	}
	
	public TextureRegion getTile(int gid) {
		if (gid >= tiles.size) return null;
		return tiles.get(gid);
	}
	
	@Override
	public String toString() {
		String json = "{";
		
		json += Utils.toJsonProperty("firstgid", this.firstgid);
		json += Utils.toJsonProperty("image", this.image);
		json += Utils.toJsonProperty("imageheight", this.imageheight);
		json += Utils.toJsonProperty("imagewidth", this.imagewidth);
		json += Utils.toJsonProperty("margin", this.margin);
		json += Utils.toJsonProperty("name", this.name);
		json += Utils.toJsonProperty("properties", this.properties);
		json += Utils.toJsonProperty("spacing", this.spacing);
		json += Utils.toJsonProperty("tileheight", this.tileheight);
		json += Utils.toJsonProperty("tilewidth", this.tilewidth);
		
		return json + "}";
	}

	/**
	 * @return the firstgid
	 */
	public int getFirstgid() {
		return firstgid;
	}

	/**
	 * @return the image
	 */
	public String getImage() {
		return image;
	}

	/**
	 * @return the imageheight
	 */
	public int getImageheight() {
		return imageheight;
	}

	/**
	 * @return the imagewidth
	 */
	public int getImagewidth() {
		return imagewidth;
	}

	/**
	 * @return the margin
	 */
	public int getMargin() {
		return margin;
	}

	/**
	 * @return the spacing
	 */
	public int getSpacing() {
		return spacing;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return the properties
	 */
	public ObjectMap<String, Object> getProperties() {
		return properties;
	}

	/**
	 * @return the tileheight
	 */
	public int getTileheight() {
		return tileheight;
	}

	/**
	 * @return the tilewidth
	 */
	public int getTilewidth() {
		return tilewidth;
	}

	/**
	 * @return the texture
	 */
	public TextureRegion getTexture() {
		return texture;
	}

	/**
	 * @return the tiles
	 */
	public Array<TextureRegion> getTiles() {
		return tiles;
	}
	
	
}
