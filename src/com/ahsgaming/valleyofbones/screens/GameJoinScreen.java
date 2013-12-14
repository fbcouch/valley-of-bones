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
import com.ahsgaming.valleyofbones.network.MPGameClient;
import com.ahsgaming.valleyofbones.screens.GameSetupScreen.GameSetupConfig;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Net;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.esotericsoftware.kryonet.Client;

/**
 * @author jami
 * This screen should display a textbox for the user to enter his nickname 
 * (registration later?), a tabbed interface to select either local games
 * (autodetect these) or WAN games (grab a list from ahsgaming.com?) 
 */
public class GameJoinScreen extends AbstractScreen {
	public String LOG = "GameJoinScreen";
    public static final boolean DEBUG = false;
    String globalServerUrl = (VOBGame.DEBUG_GLOBAL_SERVER ? "http://localhost:4730" : "http://secure-caverns-9874.herokuapp.com");

    Label lblNickname;
	TextField txtNickname;
	
	Label lblJoinHostname;
	TextField txtJoinHostname;
	
	TextButton btnConnect;
	TextButton btnCancel;
	
	Label lblStatus;
	
	GameSetupScreen gsScreen = null;
    Array<ServerObj> servers;
    List lstServers;
	
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
		
		txtNickname = new TextField(game.playerName, getSkin());
		table.add(txtNickname).pad(4).left();
		
		table.row();

        lstServers = new List(new Object[]{}, getSkin());
        table.add(lstServers).colspan(2);

        lstServers.addListener(new ChangeListener() {

            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Gdx.app.log(LOG, "" + lstServers.getSelectedIndex());
                ServerObj server = servers.get(lstServers.getSelectedIndex());
                txtJoinHostname.setText(String.format("%s:%d", server.ipAddr, server.port));
            }
        });

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

            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);

                Gdx.app.log(LOG, "btnConnect touched");
                String host = txtJoinHostname.getText();
                if (host.equals("")) {
                    host = String.format("%s:%d", servers.get(lstServers.getSelectedIndex()).ipAddr, servers.get(lstServers.getSelectedIndex()).port);
                }
                GameSetupConfig cfg = new GameSetupConfig();
                cfg.hostName = host.split(":")[0];
                cfg.isHost = false;
                cfg.isMulti = true;
                cfg.hostPort = (host.indexOf(':') > -1 ? Integer.parseInt(host.split(":")[1]) : KryoCommon.tcpPort);
                cfg.playerName = txtNickname.getText();
                gsScreen = game.getGameSetupScreenMP(cfg);
                lblStatus.setText(String.format("Connecting to host %s", cfg.hostName));
                Gdx.app.log(LOG, String.format("Attempting connection to host %s", cfg.hostName));
            }
        });

        servers = new Array<ServerObj>();

        Net.HttpRequest httpGet = new Net.HttpRequest(Net.HttpMethods.GET);
        httpGet.setUrl(globalServerUrl);

        Gdx.net.sendHttpRequest(httpGet, new Net.HttpResponseListener() {
            @Override
            public void handleHttpResponse(Net.HttpResponse httpResponse) {
                String response = httpResponse.getResultAsString();
                int code = httpResponse.getStatus().getStatusCode();
                switch(code) {
                    case 400:
                    case 404:
                        Gdx.app.log(LOG, "Server retrieval failed");
                        Gdx.app.log(LOG, response);
                        break;
                    case 200:
                        Gdx.app.log(LOG, "Got server list");
                        JsonReader reader = new JsonReader();
                        JsonValue result = reader.parse(response);
                        for (JsonValue server: result) {
                            servers.add(new ServerObj(server));
                        }
                        break;
                    default:
                        Gdx.app.log(LOG, String.format("Unknown response code: %d", code));
                        Gdx.app.log(LOG, response);
                }
                updateServerList();
            }

            @Override
            public void failed(Throwable t) {
                Gdx.app.log(LOG, "GET server request failed");
                t.printStackTrace();
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
//					txtJoinHostname.setText(iAddress.getHostAddress());
                    servers.add(new ServerObj(-1, iAddress.getHostAddress(), KryoCommon.tcpPort, "LAN Server"));
                    updateServerList();
				}
			}
		}.start();
		
		
	}
	
	
	public void updateServerList() {
        lstServers.setItems(servers.toArray());
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
				if (game.getNetController() instanceof MPGameClient && ((MPGameClient)game.getNetController()).getError() != null) {
                    KryoCommon.Error error = ((MPGameClient)game.getNetController()).getError();

                    String statusText = "An unknown error occured";

                    if (error instanceof KryoCommon.VersionError) {
                        statusText = String.format("Server version (%d) is different from client (%d)", ((KryoCommon.VersionError)error).version, VOBGame.VERSION);
                    } else if (error instanceof KryoCommon.GameFullError) {
                        statusText = "Game is full";
                    }

                    this.lblStatus.setText(statusText);
                } else {
                    this.lblStatus.setText(String.format("Failed to connect to host (%s)",
                            gsScreen.config.hostName));
                }
				gsScreen = null;
				game.closeGame();

			}
		}
	}
	
	public static class ServerObj {
        int id;
        String ipAddr;
        int port;
        String name;

        public ServerObj() {}

        public ServerObj(int id, String ipAddr, int port, String name) {
            this.id = id;
            this.ipAddr = ipAddr;
            this.port = port;
            this.name = name;
        }

        public ServerObj(JsonValue json) {
            this.id = json.getInt("id");
            this.ipAddr = json.getString("ip");
            this.port = json.getInt("port");
            this.name = json.getString("name");
        }

        public String toString() {
            return String.format("%s (%s:%d)", name, ipAddr, port);
        }
    }

}
