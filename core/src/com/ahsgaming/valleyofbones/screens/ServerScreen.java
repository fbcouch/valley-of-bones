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

import com.ahsgaming.valleyofbones.VOBServer;
import com.ahsgaming.valleyofbones.network.GameServer;
import com.ahsgaming.valleyofbones.network.KryoCommon;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;


/**
 * @author jami
 *
 */
public class ServerScreen extends AbstractScreen {
    public static final String LOG = "ServerScreen";

    List<String> listNames, listPorts, listPublics, listPlayers, listStatus;
    TextField txtName, txtPort;
    CheckBox chkPublic;
    TextButton btnStopServer;


    int numServers = 0;
	/**
	 * @param game
	 */
	public ServerScreen(VOBServer game) {
		super(game);
		
	}

    protected void addServer() {
        GameSetupConfig cfg = new GameSetupConfig();
        cfg.isMulti = true;
        cfg.hostName = txtName.getText();
        cfg.hostPort = Integer.parseInt(txtPort.getText());
        cfg.isPublic = chkPublic.isChecked();

        game.createGame(cfg);
        updateServers();
    }

    protected void updateServers() {
        int selected = listNames.getSelectedIndex();

        int numServers = ((VOBServer)game).getGameServers().size;
        String[] names = new String[numServers], ports = new String[numServers], publics = new String[numServers],
                players = new String[numServers], statuses = new String[numServers];
        int i = 0;
        for (GameServer server: ((VOBServer)game).getGameServers()) {
            names[i] = server.getGameConfig().hostName;
            ports[i] = Integer.toString(server.getGameConfig().hostPort);
            publics[i] = (server.getGameConfig().isPublic ? "Y" : " ");
            players[i] = String.format("%d/2", (server.isGameStarted() ? server.getPlayers().size : server.getRegisteredPlayers().size));
            statuses[i] = (server.isGameStarted() ? "PLAY" : "WAIT");
            i++;
        }

        listNames.setItems(names);
        listPorts.setItems(ports);
        listPublics.setItems(publics);
        listPlayers.setItems(players);
        listStatus.setItems(statuses);

        if (selected >= -1 && selected < numServers) {
            listNames.setSelectedIndex(selected);
            listPorts.setSelectedIndex(selected);
            listPublics.setSelectedIndex(selected);
            listPlayers.setSelectedIndex(selected);
            listStatus.setSelectedIndex(selected);
        }

    }

	@Override
	public void show() {
		// TODO Auto-generated method stub
		super.show();
	}

	@Override
	public void resize(int width, int height) {
		// TODO Auto-generated method stub
		super.resize(width, height);
		
		Table table = new Table(getSkin());
		table.setFillParent(true);
		
		table.add("Server").pad(10);
        table.add("Port").pad(10);
        table.add("Public?").pad(10);
        table.add("Players").pad(10);
        table.add("Status").pad(10);

		table.row();

        listNames = new List<String>(getSkin());
        table.add(listNames).pad(5);

        listPorts = new List<String>(getSkin());
        table.add(listPorts).pad(5);

        listPublics = new List<String>(getSkin());
        table.add(listPublics).pad(5);

        listPlayers = new List<String>(getSkin());
        table.add(listPlayers).pad(5);

        listStatus = new List<String>(getSkin());
        table.add(listStatus).pad(5);

        ChangeListener noSelect = new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                ((List)actor).setSelectedIndex(-1);
            }
        };

        ChangeListener changeAll = new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                int index = ((List)actor).getSelectedIndex();
                if (index > -1 && index < listNames.getItems().size && listNames.getSelectedIndex() != index)
                    listNames.setSelectedIndex(index);
                if (index > -1 && index < listPorts.getItems().size && listPorts.getSelectedIndex() != index)
                    listPorts.setSelectedIndex(index);
            }
        };

        listNames.addListener(changeAll);

        listPorts.addListener(changeAll);

        listPlayers.addListener(noSelect);

        listPublics.addListener(noSelect);

        listStatus.addListener(noSelect);

        table.row();

        btnStopServer = new TextButton("Kill Server", getSkin(), "cancel");
        table.add(btnStopServer).colspan(5).align(Align.right);

        btnStopServer.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);

                if (listStatus.getSelectedIndex() >= 0 && listStatus.getSelectedIndex() < ((VOBServer)game).getGameServers().size) {
                    ((VOBServer)game).getGameServers().get(listStatus.getSelectedIndex()).setStopServer(true);
                }
            }
        });

        table.row();

        txtName = new TextField("A server", getSkin());
        table.add(txtName).pad(5);

        txtPort = new TextField("" + KryoCommon.tcpPort, getSkin());
        table.add(txtPort).pad(5);

        chkPublic = new CheckBox("", getSkin());
        table.add(chkPublic).pad(5);

        TextButton btnAddServer = new TextButton("Start Server", getSkin());
        table.add(btnAddServer).pad(5).colspan(2);

        btnAddServer.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);

                addServer();
            }
        });


		stage.addActor(table);
	}

	/* (non-Javadoc)
	 * @see com.ahsgaming.spacetactics.screens.AbstractScreen#render(float)
	 */
	@Override
	public void render(float delta) {
		// TODO Auto-generated method stub
		super.render(delta);

        updateServers();
        btnStopServer.setDisabled(listNames.getSelectedIndex() == -1);
	}
	
	

}
