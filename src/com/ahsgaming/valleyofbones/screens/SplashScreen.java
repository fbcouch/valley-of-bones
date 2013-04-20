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
package com.ahsgaming.valleyofbones.screens;

import com.ahsgaming.valleyofbones.VOBGame;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;

/**
 * @author jami
 *
 */
public class SplashScreen extends AbstractScreen {

	private Image splashImage;
	
	/**
	 * @param game
	 */
	public SplashScreen(VOBGame game) {
		super(game);
		
		splashImage = null;
	}

	/**
	 * Overridden/Implemented methods
	 */
	
	@Override
	public void show() {
		super.show();
		
		Texture tex = new Texture(Gdx.files.internal("splash.png"));
		tex.setFilter(TextureFilter.Linear, TextureFilter.Linear);
		
		splashImage = new Image(tex);
		
	}

	@Override
	public void resize(int width, int height) {
		super.resize(width, height);
		
		stage.addActor(splashImage);
		splashImage.setPosition((stage.getWidth() - splashImage.getWidth()) * 0.5f, (stage.getHeight() - splashImage.getHeight()) * 0.5f);
		splashImage.setColor(1, 1, 1, 0);
		splashImage.addAction(Actions.sequence(Actions.fadeIn(1.5f), Actions.delay(1.0f), Actions.fadeOut(1.5f), new Action() {
			public boolean act(float delta) {
				game.setScreen(game.getMainMenuScreen());
				return true;
			}
		}));
		
	}
	
	@Override
	public void render(float delta) {
		super.render(delta);
		
		if (Gdx.input.isKeyPressed(Keys.SPACE) || Gdx.input.isKeyPressed(Keys.ENTER) || Gdx.input.isKeyPressed(Keys.ESCAPE)) {
			game.setScreen(game.getMainMenuScreen());
		}
	}

	@Override
	public void dispose() {
		super.dispose();
	}
	

}
