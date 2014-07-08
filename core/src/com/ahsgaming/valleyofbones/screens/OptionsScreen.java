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
import com.ahsgaming.valleyofbones.network.Auth;
import com.ahsgaming.valleyofbones.network.GameServer;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Net;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

/**
 * @author jami
 *
 */
public class OptionsScreen extends AbstractScreen {

	TextField txtName, txtToken;
    Button btnAuth;
    CheckBox chkAuthenticated;
    ButtonGroup bgScale;
    Button btnAuto, btnLDPI, btnMDPI, btnHDPI, btnXHDPI;
    ButtonGroup bgFilter;
    Button btnNearest, btnLinear;

    VOBGame.Profile temp = new VOBGame.Profile();

    /**
	 * @param game
	 */
	public OptionsScreen(VOBGame game) {
		super(game);
		// TODO Auto-generated constructor stub
	}

    @Override
    public void show() {
        super.show();    //To change body of overridden methods use File | Settings | File Templates.

        temp.name = game.profile.name;
        temp.token = game.profile.token;
        temp.filter = game.profile.filter;
        temp.scale = game.profile.scale;

        txtName = new TextField(game.profile.name, getSkin());
        txtToken = new TextField(game.profile.token, getSkin());
        btnAuth = new TextButton(" Check: ", getSkin(), "small");
        chkAuthenticated = new CheckBox(" NOT AUTHENTICATED", getSkin());
        chkAuthenticated.setDisabled(true);
        btnAuto = new TextButton("Auto", getSkin(), "toggle");
        btnLDPI = new TextButton("LDPI", getSkin(), "toggle");
        btnMDPI = new TextButton("MDPI", getSkin(), "toggle");
        btnHDPI = new TextButton("HDPI", getSkin(), "toggle");
        btnXHDPI = new TextButton("XHDPI", getSkin(), "toggle");
        bgScale = new ButtonGroup(btnAuto, btnLDPI, btnMDPI, btnHDPI, btnXHDPI);
        bgScale.setMaxCheckCount(1);
        bgScale.setUncheckLast(true);

        if (game.profile.scale == -1) {
            bgScale.setChecked("Auto");
        } else if (game.profile.scale == 0.75f) {
            bgScale.setChecked("LDPI");
        } else if (game.profile.scale == 1) {
            bgScale.setChecked("MDPI");
        } else if (game.profile.scale == 2) {
            bgScale.setChecked("HDPI");
        } else if (game.profile.scale == 4) {
            bgScale.setChecked("XHDPI");
        }

        btnNearest = new TextButton("Nearest", getSkin(), "toggle");
        btnLinear = new TextButton("Linear", getSkin(), "toggle");
        bgFilter = new ButtonGroup(btnNearest, btnLinear);
        bgFilter.setMaxCheckCount(1);
        bgFilter.setUncheckLast(true);

        switch(game.profile.filter) {
            case Linear:
                bgFilter.setChecked("Linear");
                break;
            case Nearest:
                bgFilter.setChecked("Nearest");
                break;
        }
    }

    @Override
    public void resize(int width, int height) {
        super.resize(width, height);    //To change body of overridden methods use File | Settings | File Templates.

        Table table = new Table(getSkin());
        table.setFillParent(true);
        stage.addActor(table);

        table.add(new Label("OPTIONS", getSkin(), "medium")).colspan(2).spaceBottom(30);
        table.row();

        table.add(new Label("Name:", getSkin(), "small")).spaceBottom(15);
        table.add(txtName).space(5).spaceBottom(15).colspan(5).width(205).align(Align.left);

        table.row();

        table.add(new Label("GameJolt Token:", getSkin(), "small")).spaceBottom(15);
        table.add(txtToken).space(5).spaceBottom(15).colspan(2).width(205).align(Align.left);
        table.add(btnAuth).space(5).spaceBottom(15);
        table.add(chkAuthenticated).colspan(2).space(5).spaceBottom(15);

        table.row();

        table.add(new Label("Textures:", getSkin(), "small")).spaceBottom(15);
        table.add(btnAuto).space(5).spaceBottom(15).size(100, 40);
        table.add(btnLDPI).space(5).spaceBottom(15).size(100, 40);
        table.add(btnMDPI).space(5).spaceBottom(15).size(100, 40);
        table.add(btnHDPI).space(5).spaceBottom(15).size(100, 40);
        table.add(btnXHDPI).space(5).spaceBottom(15).size(100, 40);

        table.row();

        table.add(new Label("Filter:", getSkin(), "small")).spaceBottom(15);
        table.add(btnNearest).spaceBottom(15).size(100, 40);
        table.add(btnLinear).spaceBottom(15).size(100, 40);

        table.row();

        TextButton btnSubmit = new TextButton("SAVE", getSkin());
        TextButton btnCancel = new TextButton("CANCEL", getSkin(), "cancel");

        table.add(btnCancel).pad(4).size(150, 50).colspan(3);
        table.add(btnSubmit).pad(4).size(150, 50).colspan(3);

        table.row();

        btnSubmit.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);

                if (txtName.getText().trim().length() > 0) {
                    game.profile.name = txtName.getText().trim();
                    game.profile.token = txtToken.getText().trim();

                    game.profile.filter = (bgFilter.getChecked() == btnLinear ? Texture.TextureFilter.Linear : Texture.TextureFilter.Nearest);

                    if (bgScale.getChecked() == btnLDPI) {
                        game.profile.scale = 0.75f;
                    } else if (bgScale.getChecked() == btnMDPI) {
                        game.profile.scale = 1f;
                    } else if (bgScale.getChecked() == btnHDPI) {
                        game.profile.scale = 2f;
                    } else if (bgScale.getChecked() == btnXHDPI) {
                        game.profile.scale = 4f;
                    } else {
                        game.profile.scale = -1;
                    }

                    game.saveProfile();
                    game.setScreen(game.getMainMenuScreen());
                }
            }
        });

        btnCancel.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);

                game.setScreen(game.getMainMenuScreen());
            }
        });

        btnAuth.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                temp.name = txtName.getText().trim();
                temp.token = txtToken.getText().trim();

                chkAuthenticated.setText(" Authenticating...");
                chkAuthenticated.setChecked(false);

                Auth.authenticate(temp.name, temp.token, new Auth.Callback() {

                    @Override
                    public void result(Object result) {
                        chkAuthenticated.setText(" Looks Good!");
                        chkAuthenticated.setChecked(true);
                    }

                    @Override
                    public void error(Object error) {
                        if (((Auth.AuthError)error).message.equals("Http Error")) {
                            chkAuthenticated.setText(" Http Error");
                        } else {
                            chkAuthenticated.setText(" Invalid name/token");
                        }
                        chkAuthenticated.setChecked(false);
                    }
                });
            }
        });
    }

    @Override
    public void render(float delta) {
        super.render(delta);    //To change body of overridden methods use File | Settings | File Templates.

        if (bgFilter.getChecked() == btnLinear) {
            btnLDPI.setDisabled(true);
            btnMDPI.setDisabled(true);
            if (bgScale.getChecked() == btnLDPI || bgScale.getChecked() == btnMDPI) {
                bgScale.setChecked("HDPI");
            }
        } else {
            btnLDPI.setDisabled(false);
            btnMDPI.setDisabled(false);

        }
    }
}
