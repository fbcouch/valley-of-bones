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
package com.ahsgaming.valleyofbones.map;

import java.util.ArrayList;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Vector2;

/**
 * @author jami
 *
 */
public class HexMap {

	ArrayList<Vector2> controlPoints;
	ArrayList<Vector2> playerSpawns;
	Vector2 bounds;
	Vector2 tileSize;
	
	/**
	 * 
	 */
	public HexMap(int width, int height, int players, int points) {
		bounds = new Vector2(width, height);
		controlPoints = new ArrayList<Vector2>();
		playerSpawns = new ArrayList<Vector2>();
		this.tileSize = new Vector2(64, 64);
		
		int things = points + 1;
		Vector2 thingDist = new Vector2(0, 0);
		Vector2 current = new Vector2(0, 0);
		if (width > height) {
			thingDist.set((int) (width / things), 0);
			current.set(0, (int)(height * 0.5f) - 1);
		} else if (height > width) {
			thingDist.set(0, (int) (height / things));
			current.set((int)(width * 0.5f), 0);
		} else {
			int d = (int)Math.sqrt((height * height) + (width * width));
			thingDist.set((int) (d / things), 0);
			thingDist.rotate(45);
			thingDist.set((int)thingDist.x, (int)thingDist.y);
		}
		//current.add(thingDist);
		playerSpawns.add(new Vector2(current.x + (thingDist.x > 0 ? (thingDist.x / thingDist.x) : 0), current.y + (thingDist.y > 0 ? (thingDist.y / thingDist.y) : 0)));
		
		for (int i = 0; i < points; i++) {
			current.add(thingDist);
			controlPoints.add(new Vector2(current));
		}
		
		current.add(thingDist);
		
		
	}
	
	public int getWidth() {
		return (int)bounds.x;
	}
	
	public int getHeight() {
		return (int)bounds.y;
	}
	
	public int getTileWidth() {
		return (int)tileSize.x;
	}
	
	public int getTileHeight() {
		return (int)tileSize.y;
	}
	
	public int getMapWidth() {
		return (int)(bounds.x * tileSize.x + tileSize.x * 0.5f);
	}
	
	public int getMapHeight() {
		return (int)(bounds.y * tileSize.y * 0.75f + tileSize.y * 0.25f);
	}
	
	public ArrayList<Vector2> getPlayerSpawns() {
		return playerSpawns;
	}
	
	public ArrayList<Vector2> getControlPoints() {
		return controlPoints;
	}
	
	public void drawDebug(Vector2 offset) {
		ShapeRenderer renderer = new ShapeRenderer();
		renderer.begin(ShapeType.Line);
		renderer.setColor(1, 1, 1, 1);
		for (int x = 0; x < bounds.x; x ++) {
			for (int y = 0; y < bounds.y; y++) {
				Vector2 base = new Vector2(offset.x + x * getTileWidth() + (y % 2 == 1 ? getTileWidth() * 0.5f : 0), offset.y + y * getTileHeight() * 0.75f);
				renderer.line(base.x + getTileWidth() * 0.5f, base.y, base.x, base.y + getTileHeight() * 0.25f);
				renderer.line(base.x, base.y + getTileHeight() * 0.25f, base.x, base.y + getTileHeight() * 0.75f);
				renderer.line(base.x, base.y + getTileHeight() * 0.75f, base.x + getTileWidth() * 0.5f, base.y + getTileHeight());
				//if (y == bounds.y - 1 || x == bounds.x - 1)
					renderer.line(base.x + getTileWidth() * 0.5f, base.y + getTileHeight(), base.x + getTileWidth(), base.y + getTileHeight() * 0.75f);
				//if (x == bounds.x - 1)
					renderer.line(base.x + getTileWidth(), base.y + getTileHeight() * 0.75f, base.x + getTileWidth(), base.y + getTileHeight() * 0.25f);
				//if (x == 0) 
					renderer.line(base.x + getTileWidth(), base.y + getTileHeight() * 0.25f, base.x + getTileWidth() * 0.5f, base.y);
			}
		}
		
		renderer.end();
	}
	
	public Vector2 boardToScreenCoords(int bx, int by) {
		return new Vector2(bx * getTileWidth() + (by % 2 == 1 ? getTileWidth() * 0.5f : 0), by * getTileHeight() * 0.75f);
	}
}
