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
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

/**
 * @author jami
 *
 */
public class OptionsScreen extends AbstractScreen {

	TextField txtName;

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

        txtName = new TextField(game.playerName, getSkin());

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
        table.add(txtName).spaceBottom(15);

        table.row();

        TextButton btnSubmit = new TextButton("SAVE", getSkin());
        TextButton btnCancel = new TextButton("CANCEL", getSkin(), "cancel");

        table.add(btnCancel).pad(4).size(150, 50);
        table.add(btnSubmit).pad(4).size(150, 50);

        table.row();

        btnSubmit.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);

                if (txtName.getText().trim().length() > 0) {
                    game.playerName = txtName.getText().trim();
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


    }

    @Override
    public void render(float delta) {
        super.render(delta);    //To change body of overridden methods use File | Settings | File Templates.
    }
}
