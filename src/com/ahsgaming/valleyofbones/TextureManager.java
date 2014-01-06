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
package com.ahsgaming.valleyofbones;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.ObjectMap;

/**
 * @author jami
 *
 */
public class TextureManager {
	public static String LOG = "TextureManager";
	
	private ObjectMap<String, TextureRegion> map = new ObjectMap<String, TextureRegion>();

    public static TextureFilter defaultMinFilter = TextureFilter.Nearest;
    public static TextureFilter defaultMaxFilter = TextureFilter.Nearest;

    String size = "mdpi";

    public void clear() {
        dispose();
        map.clear();
        atlases.clear();
    }

    public void dispose() {
        for (String k: map.keys()) {
            map.get(k).getTexture().dispose();
        }
        for (String k: atlases.keys()) {
            atlases.get(k).dispose();
        }
    }
	
	public TextureRegion getTexture(String file) {
		if (map.containsKey(file)) return map.get(file);

		if (!Gdx.files.internal(file).exists()) return null;

		Texture tex = new Texture(Gdx.files.internal(file));
		tex.setFilter(defaultMinFilter, defaultMaxFilter);
		TextureRegion reg = new TextureRegion(tex); 
		
		map.put(file, reg);
		return reg;
	}
	
	public void loadTexture(String file) {
		getTexture(file);
	}
	
	public TextureRegion loadTextureRegion(String file, int x, int y, int w, int h) {
		return new TextureRegion(getTexture(file), x, y, w, h);
	}
	
	private ObjectMap<String, TextureAtlas> atlases = new ObjectMap<String, TextureAtlas>();
	
	public Sprite getSpriteFromAtlas(String atlas, String name, int id) {
        if (!atlases.containsKey(atlas)) {
            atlases.put(atlas, new TextureAtlas(Gdx.files.internal(atlas + "-" + size + ".atlas")));
            for (Texture t: atlases.get(atlas).getTextures()) {
                t.setFilter(defaultMinFilter, defaultMaxFilter);
            }
        }

		if (id == -1)
			return atlases.get(atlas).createSprite(name);
		
		return atlases.get(atlas).createSprite(name, id);
	}

    public Sprite getSpriteFromAtlas(String atlas, String name) {
        return getSpriteFromAtlas(atlas, name, -1);
    }

    public Array<Sprite> getSpritesFromAtlas(String atlas, String name) {
		if (!atlases.containsKey(atlas)) {
			atlases.put(atlas, new TextureAtlas(Gdx.files.internal(atlas + "-" + size + ".atlas")));
            for (Texture t: atlases.get(atlas).getTextures()) {
                t.setFilter(defaultMinFilter, defaultMaxFilter);
            }
        }
			
		return atlases.get(atlas).createSprites(name);
	}

    public TextureAtlas getTextureAtlas(String atlas) {
        if (!atlases.containsKey(atlas)) {
            atlases.put(atlas, new TextureAtlas(Gdx.files.internal(atlas + "-" + size + ".atlas")));
            for (Texture t: atlases.get(atlas).getTextures()) {
                t.setFilter(defaultMinFilter, defaultMaxFilter);
            }
        }
        return atlases.get(atlas);
    }
	
	public void loadTexturePackage(String name) {
		map.clear();
		JsonReader jsonReader = new JsonReader();
		Object rObj = jsonReader.parse(Gdx.files.internal(name + "/package.json"));
		ObjectMap<String, Object> mapObjs = (ObjectMap<String, Object>)rObj;
		for (String key : mapObjs.keys()) {
			ObjectMap<String, Object> createObj = (ObjectMap<String, Object>)mapObjs.get(key);
			String file = "";
			int x = 0, y = 0, w = 0, h = 0;
			
			if (createObj.containsKey("file")) {
				file = name + "/" + createObj.get("file").toString();
			}
			
			if (createObj.containsKey("x")) {
				x = (int)Float.parseFloat(createObj.get("x").toString());
			}
			
			if (createObj.containsKey("y")) {
				y = (int)Float.parseFloat(createObj.get("y").toString());
			}
			
			if (createObj.containsKey("w")) {
				w = (int)Float.parseFloat(createObj.get("w").toString());
			}
			
			if (createObj.containsKey("h")) {
				h = (int)Float.parseFloat(createObj.get("h").toString());
			}
			Gdx.app.log(LOG, String.format("Loading %s from %s", key, file));
			map.put(key, loadTextureRegion(file, x, y, w, h));
		}
	}

	
}
