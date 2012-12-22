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
package com.ahsgaming.spacetactics.screens;

import com.ahsgaming.spacetactics.GameObject;
import com.ahsgaming.spacetactics.SpaceTacticsGame;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.scenes.scene2d.Group;

/**
 * @author jami
 *
 */
public class LevelScreen extends AbstractScreen {
	private Group grpLevel, grpMap, grpUnits;	// a few groups to handle all the game objects --> grpLevel will hold both grpMap and grpUnits, which will hold different types of objects
	
	/**
	 * @param game
	 */
	public LevelScreen(SpaceTacticsGame game) {
		super(game);
		// TODO implement receiving a loaded level
	}
	
	/**
	 * Implemented methods
	 */
	
	@Override
	public void show() {
		super.show();
		Gdx.app.log(SpaceTacticsGame.LOG, "LevelScreen#show");
		
		grpLevel = new Group();
		grpMap = new Group();
		grpUnits = new Group();
		
		grpLevel.addActor(grpMap);
		grpLevel.addActor(grpUnits);
		
		Texture tex = new Texture(Gdx.files.internal("base-fighter1.png"));
		tex.setFilter(TextureFilter.Linear, TextureFilter.Linear);
		GameObject testObj = new GameObject(tex); 
		grpUnits.addActor(testObj);
	}
	
	@Override
	public void resize(int width, int height) {
		super.resize(width, height);
		
		stage.addActor(grpLevel);
		
		// TODO set the grpLevel position rationally
		grpLevel.setPosition((stage.getWidth() - grpLevel.getWidth()) * 0.5f, (stage.getHeight() - grpLevel.getHeight()) * 0.5f);
	}

}
