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
import java.util.Objects;

import com.ahsgaming.valleyofbones.GameController;
import com.ahsgaming.valleyofbones.Player;
import com.ahsgaming.valleyofbones.VOBGame;
import com.ahsgaming.valleyofbones.units.Unit;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.ObjectMap;

/**
 * @author jami
 *
 */
public class HexMap {
    public static final String LOG = "HexMap";

    public static final Color HIGHLIGHT = new Color(1, 1, 1, 1);
    public static final Color NORMAL = new Color(0.8f, 0.8f, 0.8f, 1);
    public static final Color DIMMED = new Color(0.6f, 0.6f, 0.6f, 1);
    public static final Color FOG = new Color(0.4f, 0.4f, 0.4f, 1);

    Array<TileSet> tilesets;
    Array<TileLayer> tileLayers;
    Array<Object> objLayers;
    String title = "", description = "";
    String file = "";

    String version = "";

    ObjectMap<String, Object> properties;

    ArrayList<Vector2> controlPoints;
	ArrayList<Vector2> playerSpawns;
	Vector2 bounds;
	Vector2 tileSize;
	
	Group mapGroup;
    Group objectGroup = new Group();
    int objDepth;
    Color[] hexStatus;

    Array<Color> highlighted;        //highlight and dim are transient effects, so we want to be able to clear them easily
    Array<Color> dimmed;

    Player currentPlayer;
    final GameController parent;

	public HexMap(GameController parent, int width, int height, int players, int points) {
		this.parent = parent;

		generateMap(width, height, players, points);
	}

    public HexMap(GameController parent, FileHandle jsonFile) {
        this.parent = parent;

        loadFromFile(jsonFile);
    }

    public HexMap(GameController parent, String jsonString) {
        this.parent = parent;

        loadFromString(jsonString);
    }

    public HexMap(GameController parent, Object json) {
        this.parent = parent;

        loadFromJson(json);
    }

    // TODO split this up into smaller chunks
    public void generateMap(int width, int height, int players, int points) {
        bounds = new Vector2(width, height);
        controlPoints = new ArrayList<Vector2>();
        playerSpawns = new ArrayList<Vector2>();
        this.tileSize = new Vector2(64, 64);

        int things = points + 1;
        Vector2 thingDist = new Vector2(0, 0);
        Vector2 current = new Vector2(0, 0);
        if (width > height) {

            current.set(0, (int)Math.round(height * 0.5f) - 1);
        } else if (height > width) {

            current.set((int)(width * 0.5f), 0);
        }

        playerSpawns.add(new Vector2(current.x + (thingDist.x > 0 ? 0 : 1), current.y + (thingDist.y > 0 ? 1 : 0)));
        playerSpawns.add(new Vector2((current.x > 0 && current.y == 0 ? current.x : width - 2), (current.y > 0 && current.x == 0 ? current.y : height - 2)));

        int pointsLeft = points;

        int spawnDist = getMapDist(playerSpawns.get(0), playerSpawns.get(1));

        if (pointsLeft >= 2 && spawnDist >= 9) {
            // add "home base" control points
            // TODO make this more flexible
            Vector2 cp = new Vector2(playerSpawns.get(0));
            cp.add(3, 0);
            controlPoints.add(cp);
            cp = new Vector2(playerSpawns.get(1));
            cp.add(-3, 0);
            controlPoints.add(cp);

            pointsLeft -= 2;
            spawnDist = getMapDist(controlPoints.get(0), controlPoints.get(1));
            current.set(controlPoints.get(0));
        }

        // TODO make this more flexible
        int cpNotInRow = 0;
        int rowDist = spawnDist / (pointsLeft + 1);
        while (rowDist <= 3) {
            cpNotInRow += 1;
            rowDist = spawnDist / (pointsLeft - cpNotInRow + 1);
        }

        int rows = (spawnDist - 1) / rowDist;
        int remainder = spawnDist % rowDist;
        Gdx.app.log(LOG, String.format("rows %d (rem %d)", rows, remainder));
        for (int r = 0; r < rows; r++) {
            current.add(rowDist + (remainder / 2), 0);
            if (cpNotInRow > 0) {
                int numInRow = cpNotInRow / rows + 1;
                int totalY = 4 * (numInRow - 1);
                current.sub(0, totalY / 2);
                Gdx.app.log(LOG, String.format("num in row %d", numInRow));
                for (int i = 0; i < numInRow; i++) {
                    controlPoints.add(new Vector2(current));
                    current.add(0, 4);
                }
            } else {
                controlPoints.add(new Vector2(current));
            }
        }



        hexStatus = new Color[width * height];
        highlighted = new Array<Color>();
        dimmed = new Array<Color>();

        for (int i = 0; i < hexStatus.length; i++) hexStatus[i] = new Color(NORMAL);
    }

    void loadFromFile(FileHandle jsonFile) {
        JsonReader jsonReader = new JsonReader();
        Object json = jsonReader.parse(jsonFile);

        file = jsonFile.name();

        loadFromJson(json);
    }

    void loadFromString(String jsonString) {
        JsonReader jsonReader = new JsonReader();
        Object json = jsonReader.parse(jsonString);

        loadFromJson(json);
    }

    void loadFromJson(Object json) {
        tilesets = new Array<TileSet>();
        tileLayers = new Array<TileLayer>();
        objLayers = new Array<Object>();
        bounds = new Vector2();
        tileSize = new Vector2();
        properties = new ObjectMap<String, Object>();

        ObjectMap<String, Object> jsonObjects = (ObjectMap<String, Object>)json;

        if (jsonObjects.containsKey("width"))
            bounds.x = (int)Float.parseFloat(jsonObjects.get("width").toString());

        if (jsonObjects.containsKey("height"))
            bounds.y = (int)Float.parseFloat(jsonObjects.get("height").toString());

        if (jsonObjects.containsKey("tilewidth"))
            tileSize.x = (int)Float.parseFloat(jsonObjects.get("tilewidth").toString());

        if (jsonObjects.containsKey("tileheight"))
            tileSize.y = (int)Float.parseFloat(jsonObjects.get("tileheight").toString());

        if (jsonObjects.containsKey("version"))
            version = jsonObjects.get("version").toString();

        if (jsonObjects.containsKey("properties"))
            properties = (ObjectMap<String, Object>)jsonObjects.get("properties");

        if (jsonObjects.containsKey("title"))
            title = jsonObjects.get("title").toString();

        if (jsonObjects.containsKey("description"))
            description = jsonObjects.get("description").toString();

        if (jsonObjects.containsKey("objdepth"))
            objDepth = (int)Float.parseFloat(jsonObjects.get("objdepth").toString());

        if (jsonObjects.containsKey("tilesets")) {
            Array<Object> objs = (Array<Object>)jsonObjects.get("tilesets");
            for (Object o: objs) {
                tilesets.add(new TileSet((ObjectMap<String, Object>)o));
            }
        }

        if (jsonObjects.containsKey("tilelayers")) {
            Array<Object> objs = (Array<Object>)jsonObjects.get("tilelayers");
            for (Object o: objs) {
                tileLayers.add(new TileLayer(this, (ObjectMap<String, Object>)o));
            }
        }

        // TODO load objects properly
        controlPoints = new ArrayList<Vector2>();
        playerSpawns = new ArrayList<Vector2>();
        if (jsonObjects.containsKey("objects")) {
            Array<Object> objs = (Array<Object>)jsonObjects.get("objects");
            for (Object o: objs) {
                ObjectMap<String, Object> om = (ObjectMap<String, Object>)o;
                Vector2 position = new Vector2();
                if (om.containsKey("x"))
                    position.x = (int)Float.parseFloat(om.get("x").toString());

                if (om.containsKey("y"))
                    position.y = (int)Float.parseFloat(om.get("y").toString());

                if (om.containsKey("type")) {
                    if (om.get("type").toString().equals("spawn")) {
                        playerSpawns.add(position);
                    } else if (om.get("type").toString().equals("unit")) {
                        controlPoints.add(position);
                    }
                }
            }
        }

        hexStatus = new Color[(int) (bounds.x * bounds.y)];
        highlighted = new Array<Color>();
        dimmed = new Array<Color>();

        for (int i = 0; i < hexStatus.length; i++) {
            hexStatus[i] = new Color(NORMAL);

        }

    }

    public void update(Player player) {
        currentPlayer = player;

        // change all to FOG unless a UNIT can see them, or they are HIGHLIGHTED or DIMMED
        Array<Unit> units = parent.getUnitsByPlayerId(player.getPlayerId());
        for (int i=0; i< hexStatus.length; i++) {
            Color bsq = hexStatus[i];
            bsq.set(FOG);
            for (Unit u: units)
                if (getMapDist(u.getBoardPosition(), new Vector2(i % bounds.x, (int) (i / bounds.x))) <= u.getAttackRange())
                    bsq.set(NORMAL);

            if (highlighted.contains(bsq, true))
                bsq.set(HIGHLIGHT);

            if (dimmed.contains(bsq, true))
                bsq.set(DIMMED);

            for (TileLayer tl: tileLayers) {
                tl.setTileStatus((int) (i % bounds.x), (int) (i / bounds.x), bsq);
            }
        }
    }

    public void clearHighlight() {
        highlighted.clear();
        if (currentPlayer != null && parent != null) update(currentPlayer);
    }

    public void clearDim() {
        dimmed.clear();
        if (currentPlayer != null && parent != null) update(currentPlayer);
    }

    public void clearHighlightAndDim() {
        highlighted.clear();
        dimmed.clear();
        if (currentPlayer != null && parent != null) update(currentPlayer);
    }

    public void highlightArea(Vector2 center, int radius, boolean dimIfOccupied) {
        Array<Unit> units = parent.getUnits();

        for (int i=0;i< hexStatus.length;i++) {
            Vector2 pos = new Vector2(i % bounds.x, (int) (i / bounds.x));
            if (isBoardPositionVisible(pos) && getMapDist(center, pos) <= radius) {
                highlighted.add(hexStatus[i]);
                hexStatus[i].set(HIGHLIGHT);

                for (Unit u: units) {
                    if (u.getBoardPosition().epsilonEquals(pos, 0.1f)) {
                        dimmed.add(hexStatus[i]);
                        hexStatus[i].set(DIMMED);
                    }
                }
            }
        }
    }

    public void dimIfHighlighted(Array<Vector2> positions) {
        dimmed.clear();
        for (Vector2 p: positions) {
            Color square = hexStatus[(int)p.y % (int)bounds.x + (int)p.x];
            if (highlighted.contains(square, true)) {
                dimmed.add(square);
                square.set(DIMMED);
            }
        }
    }

	public int getWidth() {
		return (int)bounds.x;
	}
	
	public int getHeight() {
		return (int)bounds.y;
	}

    public TextureRegion getTile(int gid) {
        for (TileSet ts: tilesets) {
            if (ts.getFirstgid() <= gid && ts.getLastgid() >= gid) {
                return ts.getTile(gid);
            }
        }
        return null;
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
	
	public Group getMapGroup() {
		if (mapGroup == null) {
			mapGroup = new Group();

			mapGroup.setSize(getMapWidth(), getMapHeight());

            for (int i=0; i < tileLayers.size; i++) {
                if (i == objDepth) mapGroup.addActor(objectGroup);
                mapGroup.addActor(tileLayers.get(i).getGroup());
            }
		}
		return mapGroup;
	}

    public Group getObjectGroup() {
        return objectGroup;
    }

    public boolean isBoardPositionVisible(Vector2 pos) {
        return isBoardPositionVisible((int)pos.x, (int)pos.y);
    }

    public boolean isBoardPositionVisible(float x, float y) {
        return isBoardPositionVisible((int)x, (int)y);
    }

    public boolean isBoardPositionVisible(int x, int y) {
        return (y * (int)bounds.x + x >= 0 && y * (int)bounds.x + x < hexStatus.length && !hexStatus[y * (int)bounds.x + x].equals(FOG));
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
				renderer.line(base.x + getTileWidth() * 0.5f, base.y + getTileHeight(), base.x + getTileWidth(), base.y + getTileHeight() * 0.75f);
				renderer.line(base.x + getTileWidth(), base.y + getTileHeight() * 0.75f, base.x + getTileWidth(), base.y + getTileHeight() * 0.25f);
				renderer.line(base.x + getTileWidth(), base.y + getTileHeight() * 0.25f, base.x + getTileWidth() * 0.5f, base.y);
			}
		}
		
		renderer.end();
	}
	
	public Vector2 boardToMapCoords(int bx, int by) {
		return new Vector2(bx * getTileWidth() + (by % 2 == 1 ? getTileWidth() * 0.5f : 0), by * getTileHeight() * 0.75f);
	}

	public Vector2 boardToMapCoords(float x, float y) {
		return boardToMapCoords((int)x, (int)y);
	}
	
	public Vector2 mapToBoardCoords(float x, float y) {
		Vector2 boardCoords = new Vector2();
		
		float dx = x / getTileWidth();
		float dy = y / (getTileHeight() * 0.75f);
		float mx = x % getTileWidth();
		float my = y % (getTileHeight() * 0.75f);
		
		
		boardCoords.y = (float) Math.floor(dy);
		if (my < getTileHeight() * 0.25) {
			if (Math.floor(dy) % 2 == 1) {
				dx = (x - getTileWidth() * 0.5f) / getTileWidth();
				mx = (x - getTileWidth() * 0.5f) % getTileWidth();
			}
			
			// if (mx, my) <= (y = -0.5x + .25 * Th) or (mx, my) <= (y = 0.5x - 0.25 * Th)
			if (my <= -0.5 * mx + 0.25f * getTileHeight() || my <= 0.5 * mx - 0.25 * getTileHeight()) {
				boardCoords.y -= 1;
			}
		}

		if (boardCoords.y % 2 == 1) {
			dx = (x - getTileWidth() * 0.5f) / getTileWidth();
			mx = (x - getTileWidth() * 0.5f) % getTileWidth();
		} else {
			dx = x / getTileWidth();
			mx = x % getTileWidth();
		}
		boardCoords.x = (float) Math.floor(dx);
		
		return boardCoords;
	}
	
	
	/*
	 * 	  (x - 1!, y + 1)  (x + 1?, y + 1)
	 * 				  \	   /
	 * (x - 1, y) --- (x, y) --- (x + 1, y)
	 * 				  /    \
	 *    (x - 1!, y - 1)  (x + 1?, y - 1)
	 * ! --> y % 2 == 0 ? -1 : 0
	 * ? --> y % 2 == 0 ? 0 : 1
	 */
	public int getMapDist(Vector2 from, Vector2 to) {
		// completion cases:
		if (from.x == to.x) return Math.round(Math.abs(from.y - to.y));
		if (from.y == to.y) return Math.round(Math.abs(from.x - to.x));
		
		
		// otherwise, move along the gradient
		from = new Vector2(from);
		
		float dx = to.x - from.x, dy = to.y - from.y;
		if (from.y % 2 == 0 && dx < 0) {
			from.x -= 1;
		} else if (from.y % 2 == 1 && dx > 0) {
			from.x += 1;
		}
		
		if (dy > 0) {
			from.y += 1;
		} else {
			from.y -= 1;
		}
		
		return 1 + getMapDist(from, to);
	}
}
