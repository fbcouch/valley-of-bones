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

import com.ahsgaming.valleyofbones.GameController;
import com.ahsgaming.valleyofbones.Player;
import com.ahsgaming.valleyofbones.TextureManager;
import com.ahsgaming.valleyofbones.units.Unit;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.ObjectMap;

/**
 * @author jami
 *
 */
public class HexMap {
    public static final String LOG = "HexMap";

    public static final int HIGHLIGHT = 0;
    public static final int NORMAL = 1;
    public static final int DIMMED = 2;
    public static final int FOG = 3;

    public static final Color[] HEX_COLOR = new Color[] {new Color(1, 1, 1, 1), new Color(0.8f, 0.8f, 0.8f, 1), new Color(0.6f, 0.6f, 0.6f, 1), new Color(0.4f, 0.4f, 0.4f, 1)};

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

    int objDepth;
    int[] hexStatus;
    boolean[] hexHighlight;
    boolean[] hexDimmed;

    Player currentPlayer;
    final GameController parent;

    boolean mapDirty = true;

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

    public HexMap(GameController parent, JsonValue json) {
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
        //Gdx.app.log(LOG, String.format("rows %d (rem %d)", rows, remainder));
        for (int r = 0; r < rows; r++) {
            current.add(rowDist + (remainder / 2), 0);
            if (cpNotInRow > 0) {
                int numInRow = cpNotInRow / rows + 1;
                int totalY = 4 * (numInRow - 1);
                current.sub(0, totalY / 2);
                //Gdx.app.log(LOG, String.format("num in row %d", numInRow));
                for (int i = 0; i < numInRow; i++) {
                    controlPoints.add(new Vector2(current));
                    current.add(0, 4);
                }
            } else {
                controlPoints.add(new Vector2(current));
            }
        }



        hexStatus = new int[width * height];
        hexHighlight = new boolean[width * height];
        hexDimmed = new boolean[width * height];

        for (int i = 0; i < hexStatus.length; i++) hexStatus[i] = NORMAL;
    }

    void loadFromFile(FileHandle jsonFile) {
        JsonReader jsonReader = new JsonReader();
        JsonValue json = jsonReader.parse(jsonFile);

        file = jsonFile.name();

        loadFromJson(json);
    }

    void loadFromString(String jsonString) {
        JsonReader jsonReader = new JsonReader();
        JsonValue json = jsonReader.parse(jsonString);

        loadFromJson(json);
    }

    void loadFromJson(JsonValue json) {
        tilesets = new Array<TileSet>();
        tileLayers = new Array<TileLayer>();
        objLayers = new Array<Object>();
        bounds = new Vector2();
        tileSize = new Vector2();
        properties = new ObjectMap<String, Object>();

        bounds.x = json.getFloat("width", bounds.x);

        bounds.y = json.getFloat("height", bounds.y);

        tileSize.x = json.getFloat("tilewidth", tileSize.x);

        tileSize.y = json.getFloat("tileheight", tileSize.y);

        version = json.getString("version", version);

        for (JsonValue v: json.get("properties"))
            properties.put(v.name(), v.asString());

        title = json.getString("title", "");

        description = json.getString("description", "");

        objDepth = json.getInt("objdepth", 0);

        for (JsonValue v: json.get("tilesets"))
            tilesets.add(new TileSet(v));

        for (JsonValue v: json.get("tilelayers"))
            tileLayers.add(new TileLayer(this, v));

        // TODO load objects properly
        controlPoints = new ArrayList<Vector2>();
        playerSpawns = new ArrayList<Vector2>();

        for (JsonValue v: json.get("objects")) {
            Vector2 position = new Vector2(v.getFloat("x", 0), v.getFloat("y", 0));
            if (v.getString("type", "").equals("spawn"))
                playerSpawns.add(position);
            else if (v.getString("type", "").equals("unit"))
                controlPoints.add(position);
        }

        hexStatus = new int[(int) (bounds.x * bounds.y)];
        hexHighlight = new boolean[(int) (bounds.x * bounds.y)];
        hexDimmed = new boolean[(int) (bounds.x * bounds.y)];

        for (int i = 0; i < hexStatus.length; i++) hexStatus[i] = NORMAL;

    }

    public void update(Player player) {
        currentPlayer = player;

        if (!mapDirty) return;

        mapDirty = false;

        // change all to FOG unless a UNIT can see them, or they are HIGHLIGHTED or DIMMED
        Array<Unit> units = parent.getUnitsByPlayerId(player.getPlayerId());
        for (int i=0; i< hexStatus.length; i++) {
            hexStatus[i] = FOG;

        }
        for (int i = 0; i < units.size; i++) {
            if (units.get(i).isBuilding()) {
                units.removeIndex(i);
                i--;
            }
        }

        int[] unitpositions = new int[units.size];
        int[] radii = new int[units.size];
        for (int u=0;u<units.size;u++) {
            Unit unit = units.get(u);
            unitpositions[u] = (int)(unit.getBoardPosition().y * bounds.x + unit.getBoardPosition().x);
            radii[u] = unit.getSightRange();
        }

        boolean[] notavailable = new boolean[hexStatus.length];

        /*for (Unit u: parent.getUnits()) {
            if (u.getOwner() == null || !u.getOwner().equals(player))
                notavailable[(int)(u.getBoardPosition().y * bounds.x + u.getBoardPosition().x)] = true;
        } */

        boolean[] available = getAvailablePositions(unitpositions, radii, notavailable, false);

        for (int i=0; i< hexStatus.length; i++) {

            if (available[i]) hexStatus[i] = NORMAL;

            if (hexHighlight[i])
                hexStatus[i] = HIGHLIGHT;

            if (hexDimmed[i])
                hexStatus[i] = DIMMED;

//            for (TileLayer tl: tileLayers) {
//                tl.setTileStatus((int) (i % bounds.x), (int) (i / bounds.x), bsq);
//            }
        }
    }

    public void draw(SpriteBatch batch, float x, float y, float alpha, Array<Unit> units) {
        float curX = x;
        float curY = y;
        Color save = batch.getColor();
        int prev = -1;
        boolean odd = false;
        for (int j = 0; j < bounds.y; j++) {
            curX = x + (odd ? tileSize.x * 0.5f : 0);
            odd = !odd;
            for (int i = 0; i < bounds.x; i++) {
                if (prev < 0 || prev != hexStatus[i + j * (int)bounds.x]) {
                    prev = hexStatus[i + j * (int)bounds.x];
                    batch.setColor(HEX_COLOR[prev]);
                }
                for (TileLayer l: tileLayers) {
                    int gid = l.data[i + j * (int)bounds.x];//  getTileData(i, j);
                    if (gid != 0)
                        batch.draw(tilesets.get(0).tiles.get(gid - 1), curX, curY);
                }

                curX += tileSize.x;
            }
            curY += tileSize.y * 0.75;
        }

        // TODO put this at the proper depth
        for (Unit u: units) {
            if (hexStatus[(int)(u.getBoardPosition().x + u.getBoardPosition().y * getWidth())] != FOG
                    && (!u.getInvisible() || currentPlayer == u.getOwner() || detectorCanSee(currentPlayer, units, u.getBoardPosition())))
                u.draw(batch, x, y, alpha);
        }

        batch.setColor(save);
    }

    public void clearHighlight() {
        for (int i = 0; i < hexHighlight.length; i++)
            hexHighlight[i] = false;
    }

    public void clearDim() {
        for (int i = 0; i < hexDimmed.length; i++)
            hexDimmed[i] = false;
    }

    public void clearHighlightAndDim() {
        clearDim();
        clearHighlight();
    }

    public void highlightArea(Vector2 center, int radius, boolean dimIfOccupied) {
        Array<Unit> units = parent.getUnits();

        boolean[] notavailable = new boolean[hexStatus.length];

        for (Unit u: parent.getUnits()) {
            if (!u.getBoardPosition().equals(center)) notavailable[(int)(u.getBoardPosition().y * bounds.x + u.getBoardPosition().x)] = true;
        }

        int[] start = {(int)(center.y * bounds.x + center.x)};
        int[] radii = {radius};
        boolean[] available = getAvailablePositions(start, radii, notavailable, true);

        for (int i=0;i< hexStatus.length;i++) {
            if (available[i] && hexStatus[i] != FOG) {
                if (notavailable[i] || i == start[0]) {
                    hexDimmed[i] = true;
                    hexStatus[i] = DIMMED;
                } else {
                    hexHighlight[i] = true;
                    hexStatus[i] = HIGHLIGHT;
                }

            } else if (hexStatus[i] != FOG){
                if (getMapDist(center, new Vector2((int)(i % bounds.x), (int)(i / bounds.x))) <= radius) {
                    hexDimmed[i] = true;
                    hexStatus[i] = DIMMED;
                }
            }
        }
    }

    public void dimIfHighlighted(Array<Vector2> positions) {
        clearDim();
        for (Vector2 p: positions) {
            int ind = (int)p.y * (int)bounds.x + (int)p.x;
            if (hexHighlight[ind]) {
                hexDimmed[ind] = true;
                hexStatus[ind] = DIMMED;
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

    public boolean isBoardPositionVisible(Vector2 pos) {
        return isBoardPositionVisible((int)pos.x, (int)pos.y);
    }

    public boolean isBoardPositionVisible(float x, float y) {
        return isBoardPositionVisible((int)x, (int)y);
    }

    public boolean isBoardPositionVisible(int x, int y) {
        return (y * (int)bounds.x + x >= 0 && y * (int)bounds.x + x < hexStatus.length && hexStatus[y * (int)bounds.x + x] != FOG);
    }

    public boolean isBoardPositionTraversible(int x, int y) {
        if (y * (int)bounds.x + x >= 0 && y * (int)bounds.x + x < hexStatus.length) {
            boolean traversible = false;
            for (TileLayer tl: tileLayers) {
                if (tl.isCollidable() && tl.getData()[y * (int)bounds.x + x] != 0) {
                    return false;
                }
                if (tl.isTraversible() && tl.getData()[y * (int)bounds.x + x] != 0) {
                    traversible = true;
                }
            }
            return traversible;
        }
        return false;
    }

    public boolean detectorCanSee(Player player, Array<Unit> units, Vector2 boardPosition) {
        for (int u = 0; u < units.size; u++) {
            Unit unit = units.get(u);
            if (unit.getOwner() == player && unit.isDetector() && getMapDist(unit.getBoardPosition(), boardPosition) <= unit.getSightRange()) return true;
        }
        return false;
    }

    public boolean isMapDirty() {
        return mapDirty;
    }

    public void setMapDirty(boolean dirty) {
        mapDirty = dirty;
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

        Vector2 v1, v2;
        if (from.x <= to.x) {
            v1 = from;
            v2 = to;
        } else {
            v1 = to;
            v2 = from;
        }

        int dist = (int)(Math.abs(v2.x - v1.x) + Math.abs(v2.y - v1.y) - Math.floor(Math.abs(v2.y - v1.y) / 2));
        if (v1.y % 2 == 1 && v2.y % 2 != 1) {
            dist--;
        }
        return dist;
	}

    public Vector2[] getAdjacent(int x, int y) {
        Vector2[] adjacent = new Vector2[6];
        adjacent[0] = new Vector2(x + 1, y);
        adjacent[1] = new Vector2(x - 1, y);
        adjacent[2] = new Vector2(x + (y % 2 == 0 ? 0 : 1), y - 1);
        adjacent[3] = new Vector2(x + (y % 2 == 0 ? 0 : 1), y + 1);
        adjacent[4] = new Vector2(x + (y % 2 == 0 ? -1 : 0), y - 1);
        adjacent[5] = new Vector2(x + (y % 2 == 0 ? -1 : 0), y + 1);
        return adjacent;
    }

    public boolean[] getAvailablePositions(int[] start, int[] radii, boolean[] unavailable, boolean requireTraversible) {
        boolean[] available = new boolean[(int)(bounds.x * bounds.y)];

        for (int u=0;u<start.length;u++) {
            int unit = start[u];
            available[unit] = true;
            Array<Integer> current = new Array<Integer>(), next = new Array<Integer>();
            current.add(unit);
            for(int r=0;r<=radii[u];r++) {
                while (current.size > 0) {

                    int p = current.pop();
                    available[p] = true;

                    // check if this position is traversible and not blocked
                    if (requireTraversible && !isBoardPositionTraversible((int) (p % bounds.x), (int) (p / bounds.x))) unavailable[p] = true;

                    if (unavailable[p]) continue; // we can see this point but not beyond it
                    for (Vector2 point: getAdjacent((int)(p % bounds.x), (int)(p / bounds.x))) {
                        if (point.x < 0 || point.y < 0 || point.x >= bounds.x || point.y >= bounds.y)
                            continue;
                        next.add((int)(point.y * bounds.x + point.x));
                    }
                }
                current.addAll(next);
                next.clear();
            }
        }

        return available;
    }
}
