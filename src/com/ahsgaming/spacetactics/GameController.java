/**
 * Copyright 2012 Jami Couch
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 * This project uses:
 * 
 * LibGDX
 * Copyright 2011 see LibGDX AUTHORS file
 * Licensed under Apache License, Version 2.0 (see above).
 * 
 */
package com.ahsgaming.spacetactics;

import java.io.File;
import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.tiled.TiledLoader;
import com.badlogic.gdx.graphics.g2d.tiled.TiledMap;
import com.badlogic.gdx.graphics.g2d.tiled.TiledObject;
import com.badlogic.gdx.graphics.g2d.tiled.TiledObjectGroup;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Group;

/**
 * @author jami
 *
 */
public class GameController {
	public static final String DEFAULT_MAP = "blank.tmx";
	public static final String MAP_DIRECTORY = "maps";
	
	private ArrayList<GameObject> gameObjects;
	private Group grpRoot, grpMap, grpUnits;
	
	private String mapName;
	private TiledMap map;
	
	
	/**
	 * Constructors
	 */
	
	public GameController(String mapName) {
		// TODO load map
		this.mapName = mapName;
		grpRoot = new Group();
		grpMap = new Group();
		grpUnits = new Group();
		grpRoot.addActor(grpMap);
		grpRoot.addActor(grpUnits);
		
		
		this.loadMap();
		this.loadMapObjects();
		
		grpRoot.setSize(map.width * map.tileWidth, map.height * map.tileHeight);
		
		Texture tex = new Texture(Gdx.files.internal("base-fighter1.png"));
		tex.setFilter(TextureFilter.Linear, TextureFilter.Linear);
		GameObject testObj = new GameObject(tex); 
		grpUnits.addActor(testObj);
	}
	
	/**
	 * Methods
	 */
	
	private TiledMap loadMap() {
		// loads the map based on the value in mapName
		if (mapName == null || mapName.length() == 0) mapName = DEFAULT_MAP;
		map = TiledLoader.createMap(Gdx.files.internal(MAP_DIRECTORY + File.separator + mapName));
		
		return map;
	}
	
	private Group loadMapObjects() {
		for (TiledObjectGroup group : map.objectGroups) {
			for (TiledObject obj : group.objects) {
				if (obj.type.contains("team_start")) {
					
					Vector2 objPos = mapToLevelCoords(new Vector2(obj.x - obj.width * 0.5f, obj.y - obj.height * 0.5f));
				}
			}
		}
		
		return grpUnits;
	}
	
	public void update(float delta) {
		
	}

	
	/**
	 * Getters/Setters
	 */
	
	public Group getGroup() {
		return grpRoot;
	}
	
	public TiledMap getMap() {
		return map;
	}
	
	/**
	 * static methods
	 */
	
	/**
	 * converts TiledMap coordinates to LibGDX coordinates
	 * @param mapCoords - Vector2 coordinates in a TiledMap reference frame (0,0 is top-left)
	 * @return Vector2 coordinates in Level/GDX reference frame (0,0 is bottom-left)
	 */
	public Vector2 mapToLevelCoords(Vector2 mapCoords) {
		return new Vector2(mapCoords.x, (map.height * map.tileHeight) - mapCoords.y);
	}
}
