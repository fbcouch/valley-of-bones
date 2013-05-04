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
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

/**
 * @author jami
 *
 */
public class MainMenuScreen extends AbstractScreen {
	public static final float BUTTON_WIDTH = 300f, BUTTON_HEIGHT = 45f, BUTTON_SPACING = 30f;
	
	
	/**
	 * @param game
	 */
	public MainMenuScreen(VOBGame game) {
		super(game);
	}
	
	@Override
	public void resize(int width, int height) {
		super.resize(width, height);
		
		Skin skin = getSkin();
		
		TextButton btnNewGame = new TextButton("Single Player", skin);
		btnNewGame.setSize(BUTTON_WIDTH, BUTTON_HEIGHT);
		btnNewGame.addListener(new ClickListener() {
			@Override
			public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
				Gdx.app.log(VOBGame.LOG, "btnNewGame touched");
				game.setScreen(game.getGameSetupScreen());
			}
		});
		
		TextButton btnJoinMPGame = new TextButton("Multiplayer", skin);
		btnJoinMPGame.setSize(BUTTON_WIDTH, BUTTON_HEIGHT);
		btnJoinMPGame.addListener(new ClickListener() {
			@Override
			public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
				Gdx.app.log(VOBGame.LOG, "btnJoinMPGame touched");
				//game.setScreen(game.getGameSetupScreenMP(false)); // TODO implement a multiplayer version of this
				game.setScreen(game.getGameJoinScreen());
			}
		});
		
		TextButton btnOptions = new TextButton("Options", skin);
		btnOptions.setSize(BUTTON_WIDTH, BUTTON_HEIGHT);
		btnOptions.addListener(new ClickListener() {
			@Override
			public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
				Gdx.app.log(VOBGame.LOG, "btnOptions touched");
				game.setScreen(game.getOptionsScreen());
			}
		});
		
		TextButton btnExit = new TextButton("Exit", skin);
		btnExit.setSize(BUTTON_WIDTH, BUTTON_HEIGHT);
		btnExit.addListener(new ClickListener() {
			@Override
			public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
				Gdx.app.log(VOBGame.LOG, "btnExit touched");
				game.quitGame();
			}
		});
		
		Table table = new Table(skin);
		table.setFillParent(true);
		stage.addActor(table);
		table.add("Valley of Bones", "medium-font", new Color(1, 1, 1, 1)).spaceBottom(50f).colspan(2);
		
		table.row();
		
		table.add(btnNewGame).size(BUTTON_WIDTH, BUTTON_HEIGHT).uniform().fill().spaceBottom(BUTTON_SPACING).colspan(2);
		
		table.row();
		
		table.add(btnJoinMPGame).size(BUTTON_WIDTH, BUTTON_HEIGHT).uniform().fill().spaceBottom(BUTTON_SPACING).colspan(2);
		
		table.row();
		
		table.add(btnOptions).size(BUTTON_WIDTH, BUTTON_HEIGHT).uniform().fill().spaceBottom(BUTTON_SPACING).colspan(2);
		
		table.row();
		
		table.add(btnExit).uniform().size(BUTTON_WIDTH, BUTTON_HEIGHT).spaceBottom(BUTTON_SPACING).colspan(2);
		
		table.row();
	}

}
