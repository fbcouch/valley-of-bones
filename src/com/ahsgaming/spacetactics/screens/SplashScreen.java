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

import com.ahsgaming.spacetactics.SpaceTacticsGame;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;

/**
 * @author jami
 *
 */
public class SplashScreen extends AbstractScreen {

	private Image splashImage;
	
	/**
	 * @param game
	 */
	public SplashScreen(SpaceTacticsGame game) {
		super(game);
		
		splashImage = null;
	}

	/**
	 * Overridden/Implemented methods
	 */
	
	@Override
	public void show() {
		super.show();
		
		Texture tex = new Texture(Gdx.files.internal("data/libgdx.png"));
		tex.setFilter(TextureFilter.Linear, TextureFilter.Linear);
		
		splashImage = new Image(new TextureRegion(tex, 0, 0, 512, 275));
		
	}

	@Override
	public void resize(int width, int height) {
		super.resize(width, height);
		stage.addActor(new Label("Test", getSkin()));
		stage.addActor(splashImage);
		
		System.out.println("test");
	}
	
	@Override
	public void dispose() {
		super.dispose();
	}
	

}
