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

import java.util.ArrayList;

import com.ahsgaming.spacetactics.Player;
import com.ahsgaming.spacetactics.SpaceTacticsGame;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

/**
 * @author jami
 *
 */
public class GameSetupScreen extends AbstractScreen {
	public String LOG = "GameSetupScreen";
	GameSetupConfig config;
	
	ArrayList<Player> pList;
	
	/**
	 * @param game
	 */
	public GameSetupScreen(SpaceTacticsGame game, GameSetupConfig cfg) {
		super(game);
		config = cfg;
		game.createGame(cfg);
	}
	
	public void setupScreen() {
		
		Label gameTypeLbl = new Label("Game Type: " + (config.isMulti ? "Multiplayer" : "Single Player"), getSkin());
		
		Label mapLbl = new Label("Map: " + config.mapName, getSkin());
		
		Table table = new Table(getSkin());
		table.setFillParent(true);
		stage.addActor(table);
		
		table.add(gameTypeLbl).colspan(7).center();
		table.row();
		table.add(mapLbl).colspan(7);
		table.row();

		
		TextButton addTeam1AI = new TextButton("+AI", getSkin());
		addTeam1AI.addListener(new ClickListener() {

			/* (non-Javadoc)
			 * @see com.badlogic.gdx.scenes.scene2d.utils.ClickListener#touchUp(com.badlogic.gdx.scenes.scene2d.InputEvent, float, float, int, int)
			 */
			@Override
			public void touchUp(InputEvent event, float x, float y,
					int pointer, int button) {
				game.addAIPlayer(0);
			}
			
		});
		
		TextButton addTeam2AI = new TextButton("+AI", getSkin());
		addTeam2AI.addListener(new ClickListener() {

			/* (non-Javadoc)
			 * @see com.badlogic.gdx.scenes.scene2d.utils.ClickListener#touchUp(com.badlogic.gdx.scenes.scene2d.InputEvent, float, float, int, int)
			 */
			@Override
			public void touchUp(InputEvent event, float x, float y,
					int pointer, int button) {
				game.addAIPlayer(1);
			}
			
		});
		
		table.add(new Label("Team One", getSkin())).colspan(2);
		table.add(addTeam1AI).right();
		table.add();
		table.add(new Label("Team Two", getSkin())).colspan(2);
		table.add(addTeam2AI).right();
		table.row();
		
		
		ArrayList<String> team1 = new ArrayList<String>();
		ArrayList<String> team2 = new ArrayList<String>();
		
		pList = new ArrayList<Player>();
		pList.addAll(game.getPlayers());
		
		for (Player p: pList) {
			if (p.getTeam() == 0) team1.add(String.format("%s (%d)", p.getPlayerName(), p.getTeam()));
			if (p.getTeam() == 1) team2.add(String.format("%s (%d)", p.getPlayerName(), p.getTeam()));
		}
		
		for (int i=0; i < team1.size() || i < team2.size(); i++) {
			if (i < team1.size()) {
				table.add(new Label(team1.get(i), getSkin())).left().colspan(3);
			} else {
				table.add().colspan(3);
			}
			
			table.add();
			
			if (i < team2.size()) {
				table.add(new Label(team2.get(i), getSkin())).left().colspan(3);
			} else {
				table.add().colspan(3);
			}
			table.row();
		}
		
		if (config.isMulti) {
			table.add(new Label("Spectators", getSkin())).left().colspan(2).colspan(7);
		
			table.row();
			
			//table.add(new List(new String[]{"Unmei: hello world!", "Unmei: line two..."}, getSkin())).fill();
			
			VerticalGroup vg = new VerticalGroup();
			vg.setAlignment(Align.left);
			vg.addActor(new Label("Unmei: hello world!", getSkin()));
			vg.addActor(new Label("Unmei: line two...", getSkin()));
			
			table.add(new ScrollPane(vg)).fillX().colspan(6);
		} else {
			table.add().colspan(6);
		}
		
		
		TextButton start = new TextButton("Start Game",getSkin());
		start.addListener(new ClickListener() {

			/* (non-Javadoc)
			 * @see com.badlogic.gdx.scenes.scene2d.utils.ClickListener#touchUp(com.badlogic.gdx.scenes.scene2d.InputEvent, float, float, int, int)
			 */
			@Override
			public void touchUp(InputEvent event, float x, float y,
					int pointer, int button) {
				super.touchUp(event, x, y, pointer, button);
				
				game.startGame();
			}
			
		});
		
		table.add(start).right().bottom();
		
		table.row();
		
		if (config.isMulti) { 
			table.add(new TextField("", getSkin())).fill().colspan(6);
		} else {
			table.add().colspan(6);
		}
		
		TextButton cancel = new TextButton("Cancel", getSkin());
		cancel.addListener(new ClickListener() {

			/* (non-Javadoc)
			 * @see com.badlogic.gdx.scenes.scene2d.utils.ClickListener#touchUp(com.badlogic.gdx.scenes.scene2d.InputEvent, float, float, int, int)
			 */
			@Override
			public void touchUp(InputEvent event, float x, float y,
					int pointer, int button) {
				super.touchUp(event, x, y, pointer, button);
				
				game.setScreen(game.getMainMenuScreen());
				game.closeGame();
				
			}
			
		});
		
		table.add(cancel).right();
		
		table.row();
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
		
		setupScreen();
	}
	
	/* (non-Javadoc)
	 * @see com.ahsgaming.spacetactics.screens.AbstractScreen#render(float)
	 */
	@Override
	public void render(float delta) {
		super.render(delta);
		
		//Gdx.app.log(LOG, Integer.toString(game.getPlayers().size()));
		
		if (!pList.equals(game.getPlayers())) {
			stage.clear();
			setupScreen();
		}
	}
	
	public static class GameSetupConfig {
		public String mapName = "blank.tmx";
		public boolean isMulti = false;
		public boolean isHost = true;
		public String hostName = "localhost";
		public String playerName = "New Player";
	}



	

}
