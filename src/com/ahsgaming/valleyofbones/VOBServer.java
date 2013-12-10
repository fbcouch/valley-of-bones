package com.ahsgaming.valleyofbones;

import com.ahsgaming.valleyofbones.network.GameServer;
import com.ahsgaming.valleyofbones.network.NetController;
import com.ahsgaming.valleyofbones.screens.GameSetupScreen;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Array;

/**
 * valley-of-bones
 * (c) 2013 Jami Couch
 * User: jami
 * Date: 12/9/13
 * Time: 6:03 PM
 */
public class VOBServer extends VOBGame {
    public static final String LOG = "VOBServer";

    Array<GameController> gameControllers;
    Array<NetController> netControllers;

    @Override
    public void createGame(GameSetupScreen.GameSetupConfig cfg) {
        netController = new GameServer(this, cfg);
    }

    @Override
    public void startGame() {
        started = true;
        if (netController != null) {
            netController.startGame();
            gController = netController.getGameController();
        }
    }

    @Override
    public void create() {
        textureManager = new TextureManager();

        setScreen(getServerScreen());

        GameSetupScreen.GameSetupConfig cfg = new GameSetupScreen.GameSetupConfig();
        cfg.isMulti = true;
        createGame(cfg);
    }

    @Override
    public void dispose() {
        super.dispose();    //To change body of overridden methods use File | Settings | File Templates.

        try {
            Thread.sleep(25); // wait 25 ms to make sure the server can send an http request
        } catch (InterruptedException e) {

        }

    }

    @Override
    public void render() {
        if (this.getScreen() != null) this.getScreen().render(Gdx.graphics.getDeltaTime());

        if (loadGame) {
            startGame();
            loadGame = false;
        }

        if (netController != null) {
            if (loadGame && !started)
                startGame();

            if (gameResult != null) {
                gameResult = null;
                loadGame = false;
                started = false;
                gController = null;
                netController = null;
                create();
            }

            if (netController != null) netController.update(Gdx.graphics.getDeltaTime());
        }
    }

    @Override
    public void resize(int width, int height) {
        super.resize(width, height);    //To change body of overridden methods use File | Settings | File Templates.
    }

    @Override
    public void pause() {
        super.pause();    //To change body of overridden methods use File | Settings | File Templates.
    }

    @Override
    public void resume() {
        super.resume();    //To change body of overridden methods use File | Settings | File Templates.
    }
}
