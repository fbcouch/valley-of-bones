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

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.ObjectMap;

/**
 * @author jami
 *
 */
public class TextureManager {
	public static String LOG = "TextureManager";
	
	private static ObjectMap<String, TextureRegion> map = new ObjectMap<String, TextureRegion>(); 
	
	public static TextureRegion getTexture(String file) {
		if (map.containsKey(file)) return map.get(file);
		
		TextureRegion reg = new TextureRegion(new Texture(Gdx.files.internal(file))); 
		map.put(file, reg);
		return reg;
	}
	
	public static void loadTexture(String file) {
		getTexture(file);
	}
}
