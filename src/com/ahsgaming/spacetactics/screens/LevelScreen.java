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

import com.ahsgaming.spacetactics.GameController;
import com.ahsgaming.spacetactics.SpaceTacticsGame;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.g2d.tiled.TiledMap;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Group;

/**
 * @author jami
 *
 */
public class LevelScreen extends AbstractScreen {
	private Group grpLevel;
	
	private GameController gController = null;
	
	
	// camera 'center' position - this will always remain within the bounds of the map
	private Vector2 posCamera = new Vector2();
	
	/**
	 * @param game
	 */
	public LevelScreen(SpaceTacticsGame game, GameController gController) {
		super(game);
		this.gController = gController;
	}
	
	/**
	 * Methods
	 */
	
	private void clampCamera() {
		TiledMap map = gController.getMap();
		
		if (posCamera.x < 0) posCamera.x = 0;
		if (posCamera.x > map.width * map.tileWidth) posCamera.x = map.width * map.tileWidth;
		
		if (posCamera.y < 0) posCamera.y = 0;
		if (posCamera.y > map.height * map.tileHeight) posCamera.y = map.height * map.tileHeight;
	}
	
	/**
	 * Implemented methods
	 */
	
	@Override
	public void show() {
		super.show();
		Gdx.app.log(SpaceTacticsGame.LOG, "LevelScreen#show");
		
		grpLevel = gController.getGroup();
		
	}
	
	@Override
	public void resize(int width, int height) {
		super.resize(width, height);
		
		stage.addActor(grpLevel);
		
		// TODO set the grpLevel position rationally
		grpLevel.setPosition((stage.getWidth() - grpLevel.getWidth()) * 0.5f, (stage.getHeight() - grpLevel.getHeight()) * 0.5f);
	}
	
	@Override
	public void render(float delta) {
		super.render(delta);
		gController.update(delta);
		
		// move the camera around
		if (Gdx.input.isKeyPressed(Keys.UP) || Gdx.input.getY() <= game.getMouseScrollSize()) {
			// move 'up'
			posCamera.y += game.getKeyScrollSpeed() * delta;
		} else if (Gdx.input.isKeyPressed(Keys.DOWN) || Gdx.input.getY() >= stage.getHeight() - game.getMouseScrollSize()) {
			// move 'down'
			posCamera.y -= game.getKeyScrollSpeed() * delta;
		}
		
		if (Gdx.input.isKeyPressed(Keys.LEFT) || Gdx.input.getX() <= game.getMouseScrollSize()) {
			// move 'left'
			posCamera.x -= game.getKeyScrollSpeed() * delta;
		} else if (Gdx.input.isKeyPressed(Keys.RIGHT) || Gdx.input.getX() >= stage.getWidth() - game.getMouseScrollSize()) {
			// move 'right'
			posCamera.x += game.getKeyScrollSpeed() * delta;
		}
		
		clampCamera();
		
		grpLevel.setPosition(-1 * posCamera.x, -1 * posCamera.y);
	}

	
}
