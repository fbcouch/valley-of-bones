package com.ahsgaming.valleyofbones.screens;

import com.ahsgaming.valleyofbones.GameController;
import com.ahsgaming.valleyofbones.VOBGame;
import com.badlogic.gdx.Gdx;

/**
 * valley-of-bones
 * (c) 2013 Jami Couch
 * User: jami
 * Date: 12/19/13
 * Time: 8:45 AM
 */
public class SpectatorLevelScreen extends LevelScreen {

    /**
     * @param game
     */
    public SpectatorLevelScreen(VOBGame game, GameController gController) {
        super(game, gController);
    }

    @Override
    public void show() {
        stage.clear();
        Gdx.input.setInputProcessor(stage);


    }

    @Override
    public void resize(int width, int height) {
        super.resize(width, height);
    }
}
