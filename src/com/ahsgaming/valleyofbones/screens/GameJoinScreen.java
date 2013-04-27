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

import java.net.InetAddress;

import com.ahsgaming.valleyofbones.VOBGame;
import com.ahsgaming.valleyofbones.network.KryoCommon;
import com.ahsgaming.valleyofbones.screens.GameSetupScreen.GameSetupConfig;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.esotericsoftware.kryonet.Client;

/**
 * @author jami
 * This screen should display a textbox for the user to enter his nickname 
 * (registration later?), a tabbed interface to select either local games
 * (autodetect these) or WAN games (grab a list from ahsgaming.com?) 
 */
public class GameJoinScreen extends AbstractScreen {
	public String LOG = "GameJoinScreen";
	
	Label lblNickname;
	TextField txtNickname;
	
	Label lblJoinHostname;
	TextField txtJoinHostname;
	
	TextButton btnConnect;
	TextButton btnCancel;
	
	Label lblStatus;
	
	GameSetupScreen gsScreen = null;
	
	/**
	 * @param game
	 */
	public GameJoinScreen(VOBGame game) {
		super(game);
		
	}
	
	public void setupScreen() {
		Table table = new Table(getSkin());
		table.setFillParent(true);
		stage.addActor(table);
		
		lblNickname = new Label("Nickname:", getSkin());
		table.add(lblNickname).pad(4).left();
		
		txtNickname = new TextField("Newb", getSkin());
		table.add(txtNickname).pad(4).left();
		
		table.row();
		
		table.row();
		
		lblJoinHostname = new Label("Join Server:", getSkin());
		table.add(lblJoinHostname).pad(4).left();
		
		txtJoinHostname = new TextField("", getSkin());
		table.add(txtJoinHostname).pad(4).left();
		
		table.row();
		
		btnCancel = new TextButton("Cancel", getSkin(), "cancel");
		table.add(btnCancel).size(150, 50).pad(4);
		
		btnConnect = new TextButton("Connect", getSkin());
		table.add(btnConnect).size(150, 50).pad(4);
		
		table.row();

		lblStatus = new Label("", getSkin());
		table.add(lblStatus).colspan(2).pad(4);

		
		btnCancel.addListener(new ClickListener() {

			/* (non-Javadoc)
			 * @see com.badlogic.gdx.scenes.scene2d.utils.ClickListener#touchUp(com.badlogic.gdx.scenes.scene2d.InputEvent, float, float, int, int)
			 */
			@Override
			public void touchUp(InputEvent event, float x, float y,
					int pointer, int button) {
				super.touchUp(event, x, y, pointer, button);
				game.setScreen(game.getMainMenuScreen());
			}
		});
		
		btnConnect.addListener(new ClickListener() {

			/* (non-Javadoc)
			 * @see com.badlogic.gdx.scenes.scene2d.utils.ClickListener#touchUp(com.badlogic.gdx.scenes.scene2d.InputEvent, float, float, int, int)
			 */
			@Override
			public void touchUp(InputEvent event, float x, float y,
					int pointer, int button) {
				super.touchUp(event, x, y, pointer, button);
				Gdx.app.log(LOG, "btnConnect touched");
				GameSetupConfig cfg = new GameSetupConfig();
				cfg.hostName = txtJoinHostname.getText();
				cfg.isHost = false;
				cfg.isMulti = true;
				cfg.playerName = txtNickname.getText();
				gsScreen = game.getGameSetupScreenMP(cfg);
				lblStatus.setText(String.format("Connecting to host %s", cfg.hostName));
				Gdx.app.log(LOG, String.format("Attempting connection to host %s", cfg.hostName));
			}
			
		});
		
		// discover host
		new Thread() {
			public void run() {
				// for whatever reason, without this line the discoverHost call cant
				// access the network...not very forward compatible I guess
				System.setProperty("java.net.preferIPv4Stack", "true");
				Client c = new Client();
				c.start();
				
				InetAddress iAddress = c.discoverHost(KryoCommon.udpPort, 5000);
				if (iAddress != null && txtJoinHostname.getText().equals("")) {
					txtJoinHostname.setText(iAddress.getHostAddress());
				}
			}
		}.start();
		
		
	}
	
	
	

	/* (non-Javadoc)
	 * @see com.ahsgaming.spacetactics.screens.AbstractScreen#show()
	 */
	@Override
	public void show() {
		super.show();
		
	}

	/* (non-Javadoc)
	 * @see com.ahsgaming.spacetactics.screens.AbstractScreen#resize(int, int)
	 */
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
		
		if (gsScreen != null) {
			if (game.isConnected()) {
				game.setScreen(gsScreen);
			} else if (!game.isConnecting()) {
				this.lblStatus.setText(String.format("Failed to connect to host (%s)", 
						gsScreen.config.hostName));
				gsScreen = null;
				game.closeGame();
			}
		}
	}
	
	

}
