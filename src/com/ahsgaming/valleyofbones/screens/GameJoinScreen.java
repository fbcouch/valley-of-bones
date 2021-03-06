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
import com.ahsgaming.valleyofbones.network.Auth;
import com.ahsgaming.valleyofbones.network.GameServer;
import com.ahsgaming.valleyofbones.network.KryoCommon;
import com.ahsgaming.valleyofbones.network.MPGameClient;
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
    String globalServerUrl = GameServer.globalServerUrl;

    Label lblNickname;
	TextField txtNickname;
	
	Label lblJoinHostname;
	TextField txtJoinHostname;
	
	TextButton btnConnect;
	TextButton btnCancel;
    TextButton btnSpectate;

    CheckBox chkAuth;
	
	Label lblStatus;
	
	MPGameSetupScreen gsScreen = null;
    Array<ServerObj> servers;
    List listNames, listServers, listPlayers, listStatus;

    Auth.AuthPlayer authPlayer = null;
    Auth.AuthError authError = null;
	
	/**
	 * @param game
	 */
	public GameJoinScreen(VOBGame game) {
		super(game);
		Auth.authenticate(game.profile.name, game.profile.token, new Auth.Callback() {

            @Override
            public void result(Object result) {
                if (result instanceof Auth.AuthPlayer) authPlayer = (Auth.AuthPlayer)result;
            }

            @Override
            public void error(Object error) {
                if (error instanceof Auth.AuthError) authError = (Auth.AuthError)error;
            }
        });
	}
	
	public void setupScreen() {
		Table table = new Table(getSkin());
		table.setFillParent(true);
		stage.addActor(table);

        table.add("Name:").pad(4).left();
        lblNickname = new Label(game.profile.name, getSkin());
		table.add(lblNickname).pad(4).left();

        chkAuth = new CheckBox(" Authenticating...", getSkin());
        table.add(chkAuth).colspan(2).left();
		
		table.row();

        table.add("Server Name", "small-grey");
        table.add("Address", "small-grey");
        table.add("Players", "small-grey");
        table.add("Status", "small-grey");
        table.row();

        listNames = new List(new Object[]{}, getSkin());
        listServers = new List(new Object[]{}, getSkin());
        listPlayers = new List(new Object[]{}, getSkin());
        listStatus = new List(new Object[]{}, getSkin());

        table.add(listNames).pad(5);
        table.add(listServers).pad(5);
        table.add(listPlayers).pad(5);
        table.add(listStatus).pad(5);

        ChangeListener changeAll = new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                int index = ((List)actor).getSelectedIndex();

                listNames.setSelectedIndex(index);
                listServers.setSelectedIndex(index);
                listPlayers.setSelectedIndex(index);
                listStatus.setSelectedIndex(index);

                ServerObj server = servers.get(index);
                txtJoinHostname.setText(String.format("%s:%d", server.ipAddr, server.port));
            }
        };

        listNames.addListener(changeAll);
        listServers.addListener(changeAll);
        listPlayers.addListener(changeAll);
        listStatus.addListener(changeAll);

        table.row();
		
		lblJoinHostname = new Label("Join Server:", getSkin());
		table.add(lblJoinHostname).pad(4).left();
		
		txtJoinHostname = new TextField("", getSkin());
		table.add(txtJoinHostname).pad(4).fillX().left().colspan(3);
		
		table.row();
		
		btnCancel = new TextButton("Cancel", getSkin(), "cancel");
		table.add(btnCancel).size(150, 50).pad(4);
		
		btnConnect = new TextButton("Join", getSkin());
		table.add(btnConnect).size(150, 50).pad(4);

        btnSpectate = new TextButton("Spectate", getSkin());
        table.add(btnSpectate).size(150, 50).pad(4);
		
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
                    if (servers.size == 0) return;
                    host = String.format("%s:%d", servers.get(listServers.getSelectedIndex()).ipAddr, servers.get(listServers.getSelectedIndex()).port);
                }
                GameSetupConfig cfg = new GameSetupConfig();
                cfg.hostName = host.split(":")[0];
                cfg.isHost = false;
                cfg.isMulti = true;
                cfg.hostPort = (host.indexOf(':') > -1 ? Integer.parseInt(host.split(":")[1]) : KryoCommon.tcpPort);
                cfg.playerName = game.profile.name;
                cfg.playerKey = (authPlayer != null ? authPlayer.key : "");
                gsScreen = game.getGameSetupScreenMP(cfg);
                lblStatus.setText(String.format("Connecting to host %s", cfg.hostName));
                Gdx.app.log(LOG, String.format("Attempting connection to host %s", cfg.hostName));
            }
        });

        btnSpectate.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);

                Gdx.app.log(LOG, "btnSpectate touched");
                String host = txtJoinHostname.getText();
                if (host.equals("")) {
                    if (servers.size == 0) return;
                    host = String.format("%s:%d", servers.get(listServers.getSelectedIndex()).ipAddr, servers.get(listServers.getSelectedIndex()).port);
                }
                GameSetupConfig cfg = new GameSetupConfig();
                cfg.hostName = host.split(":")[0];
                cfg.isHost = false;
                cfg.isMulti = true;
                cfg.isSpectator = true;
                cfg.hostPort = (host.indexOf(':') > -1 ? Integer.parseInt(host.split(":")[1]) : KryoCommon.tcpPort);
                cfg.playerName = game.profile.name;
                cfg.playerKey = (authPlayer != null ? authPlayer.key : "");
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
                        Gdx.app.log(LOG, response);
                        for (JsonValue server: result) {
                            if (server.getString("version", "").equals(VOBGame.VERSION))
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

        String[] names = new String[servers.size], hosts = new String[servers.size],
                players = new String[servers.size], statuses = new String[servers.size];
        int i = 0;
        for (ServerObj server: servers) {
            names[i] = server.name;
            hosts[i] = String.format("%s:%d", server.ipAddr, server.port);
            players[i] = String.format("%d/%d", server.players, 2);
            statuses[i] = (server.status == 1 ? "Playing" : "Waiting");
            i++;
        }
        listNames.setItems(names);
        listServers.setItems(hosts);
        listPlayers.setItems(players);
        listStatus.setItems(statuses);
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
                        statusText = String.format("Server version (%s) is different from client (%s)", ((KryoCommon.VersionError)error).version, VOBGame.VERSION);
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
		} else {
            if (authPlayer != null) {
                chkAuth.setChecked(true);
                chkAuth.setText(" Looks Good!");
            } else if (authError != null) {
                chkAuth.setChecked(false);
                chkAuth.setText(" Auth Error");
            }
        }
	}
	
	public static class ServerObj {
        int id;
        String ipAddr;
        int port;
        String name;
        int players = 0;
        int status = 0;

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
            this.name = json.getString("name", "null?");
            this.players = json.getInt("players", 0);
            this.status = json.getInt("status", 0);
        }

        public String toString() {
            return String.format("%s (%s:%d)", name, ipAddr, port);
        }
    }

}
