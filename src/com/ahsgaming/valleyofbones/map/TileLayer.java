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
	Array<Integer> data;
	boolean traversible = false;
    boolean collidable = false;
	boolean visible = true;
	float opacity = 1;

	public static TileLayer createFromJson(JsonValue value) {
        TileLayer tileLayer = new TileLayer();

        tileLayer.traversible = value.getBoolean("traversible", true);
        tileLayer.collidable = value.getBoolean("collidable", false);
        tileLayer.visible = value.getBoolean("visible", true);
        tileLayer.opacity = value.getFloat("opacity", 1);

        tileLayer.data = new Array<Integer>();
        for (JsonValue v: value.get("data")) {
            tileLayer.data.add(v.asInt());
        }

        return tileLayer;
    }
}
