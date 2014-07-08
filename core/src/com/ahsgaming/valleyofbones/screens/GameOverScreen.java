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

import com.ahsgaming.valleyofbones.GameResult;
import com.ahsgaming.valleyofbones.Player;
import com.ahsgaming.valleyofbones.TextureManager;
import com.ahsgaming.valleyofbones.VOBGame;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;

/**
 * @author jami
 *
 */
public class GameOverScreen extends AbstractScreen {

    GameResult result;
    Array<Player> players = new Array<Player>();
	/**
	 * @param game
	 */
	public GameOverScreen(VOBGame game, GameResult result, Array<Player> players) {
		super(game);
		this.result = result;
        this.players = players;
	}

    public void updateLayout() {

        Table table = new Table(getSkin());
        stage.addActor(table);

        if (game.getPlayer() != null) {
            if (result.winner == game.getPlayer().getPlayerId()) {
                table.add("VICTORY", "large-font", "white").pad(4).colspan(4).center();
            } else {
                table.add("DEFEAT", "large-font", "white").pad(4).colspan(4).center();
            }
        } else {
            table.add("GAME OVER", "large-font", "white").pad(4).colspan(4).center();
        }

        table.row();

        table.row();

        table.add().pad(4);

        table.add("Name", "small-font", "white").pad(4).minWidth(250);

        Image imgMoney = new Image(VOBGame.instance.getTextureManager().getSpriteFromAtlas("assets", "supply"));
        table.add(imgMoney).pad(4).size(imgMoney.getWidth() / VOBGame.SCALE, imgMoney.getHeight() / VOBGame.SCALE);

        Image imgSupply = new Image(VOBGame.instance.getTextureManager().getSpriteFromAtlas("assets", "money"));
        table.add(imgSupply).pad(4).size(imgSupply.getWidth() / VOBGame.SCALE, imgSupply.getHeight() / VOBGame.SCALE);

        table.row();

        addPlayerRow(table, players, result.winner, "*");

        for (int l=0; l < result.losers.length; l++) {
            addPlayerRow(table, players, result.losers[l], "");
        }

        TextButton btnMainMenu = new TextButton("Back to Main Menu", getSkin());
        table.add(btnMainMenu).left().minSize(150, 50).pad(4).colspan(4);

        table.row();

        btnMainMenu.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);

                game.setScreen(new MainMenuScreen(game));
            }
        });

        table.setPosition((stage.getWidth() - table.getWidth()) * 0.5f, stage.getHeight() * 0.75f - table.getHeight());
    }

    public void addPlayerRow(Table table, Array<Player> players, int playerId, String pre) {
        Player p = null;
        for (Player pl: players) {
            if (pl.getPlayerId() == playerId) {
                p = pl;
                break;
            }
        }

        if (p != null) {
            table.add(pre, "small-font", p.getPlayerColor()).pad(4);

            table.add(p.getPlayerName(), "small-font", p.getPlayerColor()).pad(4).left();

            table.add(String.format("%02d/%02d", p.getCurFood(), p.getMaxFood()), "small-font", p.getPlayerColor()).pad(4);

            table.add(String.format("$%04d", (int)p.getBankMoney()), "small-font", p.getPlayerColor()).pad(4);

            table.row().pad(4);
        }
    }

    @Override
    public void show() {
        super.show();

    }

    @Override
    public void resize(int width, int height) {
        super.resize(width, height);
        updateLayout();
    }

    @Override
    public void render(float delta) {
        super.render(delta);
    }

    /*
     * Getters/setters
     */

    public GameResult getResult() {
        return result;
    }

    public void setResult(GameResult result) {
        this.result = result;
    }
}
