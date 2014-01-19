package com.ahsgaming.valleyofbones.map;

import com.ahsgaming.valleyofbones.VOBGame;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;

/**
 * valley-of-bones
 * (c) 2013 Jami Couch
 * User: jami
 * Date: 1/18/14
 * Time: 2:17 PM
 */
public class MapData {
    Array<String> tiles;
    Array<TileLayer> tileLayers;
    Array<MapObject> mapObjects;

    String title, description, version;

    Vector2 tileSize, bounds;

    public boolean isBoardPositionTraversible(int x, int y) {
        if (y * (int)bounds.x + x >= 0 && y * (int)bounds.x + x < bounds.x * bounds.y) {
            boolean traversible = false;
            for (TileLayer tl: tileLayers) {
                if (tl.collidable && tl.data.get(y * (int)bounds.x + x) != 0) {
                    return false;
                }
                if (tl.traversible && tl.data.get(y * (int)bounds.x + x) != 0) {
                    traversible = true;
                }
            }
            return traversible;
        }
        return false;
    }

    public Array<String> getTiles() {
        return tiles;
    }

    public void setTiles(Array<String> tiles) {
        this.tiles = tiles;
    }

    public Array<TileLayer> getTileLayers() {
        return tileLayers;
    }

    public void setTileLayers(Array<TileLayer> tileLayers) {
        this.tileLayers = tileLayers;
    }

    public Array<MapObject> getMapObjects() {
        return mapObjects;
    }

    public void setMapObjects(Array<MapObject> mapObjects) {
        this.mapObjects = mapObjects;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public Vector2 getTileSize() {
        return tileSize;
    }

    public void setTileSize(Vector2 tileSize) {
        this.tileSize = tileSize;
    }

    public Vector2 getBounds() {
        return bounds;
    }

    public void setBounds(Vector2 bounds) {
        this.bounds = bounds;
    }

    public int getMapWidth() {
        return (int)(bounds.x * tileSize.x + tileSize.x * 0.5f);
    }

    public int getMapHeight() {
        return (int)(bounds.y * tileSize.y * 0.75f + tileSize.y * 0.25f);
    }

    public static MapData createFromFile(FileHandle file) {
        JsonReader jsonReader = new JsonReader();

        return createFromJson(jsonReader.parse(file));
    }

    public static MapData createFromJson(JsonValue json) {
        MapData mapData = new MapData();

        mapData.title = json.getString("title", "<Unnamed Map>");
        mapData.description = json.getString("description", "");
        mapData.version = json.getString("version", "");

        mapData.tiles = new Array<String>();
        for (JsonValue tile: json.get("tiles"))
            mapData.tiles.add(tile.asString());

        mapData.tileLayers = new Array<TileLayer>();
        for (JsonValue layer: json.get("tilelayers")) {
            mapData.tileLayers.add(TileLayer.createFromJson(layer));
        }

        mapData.mapObjects = new Array<MapObject>();
        for (JsonValue obj: json.get("objects")) {
            mapData.mapObjects.add(MapObject.createFromJson(obj));
        }

        mapData.tileSize = new Vector2(64, 64);
        mapData.tileSize.x = json.getFloat("tilewidth", mapData.tileSize.x) * VOBGame.SCALE;
        mapData.tileSize.y = json.getFloat("tileheight", mapData.tileSize.y) * VOBGame.SCALE;

        mapData.bounds = new Vector2();
        mapData.bounds.x = json.getInt("width");
        mapData.bounds.y = json.getInt("height");

        return mapData;
    }

    public static class MapObject {
        public String type;
        public String proto;
        public int player, x, y;

        public static MapObject createFromJson(JsonValue value) {
            MapObject mapObject = new MapObject();

            mapObject.type = value.getString("type", "");
            mapObject.proto = value.getString("proto", "");
            mapObject.player = value.getInt("player", -1);
            mapObject.x = value.getInt("x", 0);
            mapObject.y = value.getInt("y", 0);

            return mapObject;
        }
    }
}
