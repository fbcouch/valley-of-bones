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
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;


/**
 * @author jami
 *
 */
public class AbstractScreen implements Screen {

	protected final SpaceTacticsGame game;
	protected Stage stage;
	protected Skin skin;
	protected BitmapFont fontSmall;
	protected BitmapFont fontMed;
	protected BitmapFont fontLarge;
	protected Group gameGroup;
	
	/**
	 * Constructor
	 * @param game
	 */
	public AbstractScreen(SpaceTacticsGame game) {
		this.game = game;
		this.stage = new Stage(0, 0, true);
		this.gameGroup = new Group();
	}
	
	/**
	 * Overridden/Implemented methods
	 */
	
	@Override
	public void show() {
		stage.clear();
		Gdx.input.setInputProcessor(stage);
		
		// TODO load things here
		getSmallFont();
		getMedFont();
		getLargeFont();
		getSkin();
	}

	@Override
	public void resize(int width, int height) {
		stage.setViewport(width, height, true);
		stage.clear();
	}

	@Override
	public void render(float delta) {
		stage.act(delta);
		
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		stage.draw();
	}

	@Override
	public void pause() {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void resume() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void dispose() {
		stage.dispose();
		if (fontSmall != null) fontSmall.dispose();
		if (fontMed != null) fontMed.dispose();
		if (fontLarge != null) fontLarge.dispose();
		if (skin != null) skin.dispose();
	}

	@Override
	public void hide() {
		// TODO Auto-generated method stub
		dispose();
	}
	
	
	/**
	 * Getters and Setters
	 */
	
	public String getName() {
		return getClass().getSimpleName();
	}
	
	public BitmapFont getSmallFont() {
		if (fontSmall == null) {
			fontSmall = new BitmapFont(Gdx.files.internal("fonts/kenpixel-16.fnt"), Gdx.files.internal("fonts/kenpixel-16.png"), false);
		}
		return fontSmall;
	}
	
	public BitmapFont getMedFont() {
		if (fontMed == null) {
			fontMed = new BitmapFont(Gdx.files.internal("fonts/kenpixel-24.fnt"), Gdx.files.internal("fonts/kenpixel-24.png"), false);
		}
		return fontMed;
	}
	
	public BitmapFont getLargeFont() {
		if (fontLarge == null) {
			fontLarge = new BitmapFont(Gdx.files.internal("fonts/kenpixel-32.fnt"), Gdx.files.internal("fonts/kenpixel-32.png"), false);
		}
		return fontLarge;
	}
	
	public Skin getSkin() {
		if (skin == null) {
			skin = new Skin(Gdx.files.internal("data/uiskin.json"));
		}
		return skin;
	}
	
	public Group getGroup() {
		return gameGroup;
	}
}
