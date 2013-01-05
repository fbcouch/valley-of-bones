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
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.utils.Array;

/**
 * @author jami
 *
 */
public class GameSetupScreen extends AbstractScreen {
	
	GameSetupConfig config;
	
	/**
	 * @param game
	 */
	public GameSetupScreen(SpaceTacticsGame game, GameSetupConfig cfg) {
		super(game);
		config = cfg;
	}
	
	/**
	 * Implemented methods
	 */
	
	@Override
	public void show() {
		super.show();
	}
	
	@Override
	public void resize(int width, int height) {
		super.resize(width, height);
		
		
		Label gameTypeLbl = new Label("Game Type: " + (config.isMulti ? "Multiplayer" : "Single Player"), getSkin());
		
		Label mapLbl = new Label("Map: " + config.mapName, getSkin());
		
		
		
		Table table = new Table(getSkin());
		table.setFillParent(true);
		stage.addActor(table);
		
		table.add(gameTypeLbl).colspan(7).center();
		table.row();
		table.add(mapLbl).colspan(7);
		table.row();

		
		table.add(new Label("Team One", getSkin())).colspan(3);
		table.add();
		table.add(new Label("Team Two", getSkin())).colspan(3);
		table.row();
		
		Array<String> tmp = new Array<String>();
		tmp.addAll(new String[]{"P1", "P2", "P3"});
		
		for(String s: tmp) {
			table.add(new Label(s, getSkin())).left().colspan(3);
			table.add();
			table.add(new Label(s, getSkin())).left().colspan(3);
			table.row();
		}
		
		
		table.add(new Label("Spectators", getSkin())).left().colspan(2).colspan(7);
		
		table.row();
		
		//table.add(new List(new String[]{"Unmei: hello world!", "Unmei: line two..."}, getSkin())).fill();
		
		VerticalGroup vg = new VerticalGroup();
		vg.setAlignment(Align.left);
		vg.addActor(new Label("Unmei: hello world!", getSkin()));
		vg.addActor(new Label("Unmei: line two...", getSkin()));
		
		table.add(new ScrollPane(vg)).fillX().colspan(6);
		
		table.add(new TextButton("Start Game", getSkin())).right().bottom();
		
		table.row();
		
		table.add(new TextField("", getSkin())).fill().colspan(6);
		
		table.add(new TextButton("Cancel", getSkin())).right();
		
		table.row();
	}
	
	public static class GameSetupConfig {
		public String mapName = "blank.tmx";
		public boolean isMulti = false;
		public boolean isHost = true;
	}

}
