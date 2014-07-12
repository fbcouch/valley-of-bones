package com.ahsgaming.valleyofbones;

import com.ahsgaming.valleyofbones.network.GameServer;
import com.ahsgaming.valleyofbones.network.NetController;
import com.ahsgaming.valleyofbones.screens.GameSetupConfig;
import com.ahsgaming.valleyofbones.screens.ServerScreen;
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

    Array<GameServer> gameServers;
    GameSetupConfig cfg;

    public VOBServer(String name, int port, boolean pub) {
        super();
        gameServers = new Array<GameServer>();
        cfg = new GameSetupConfig();
        cfg.isMulti = true;
        cfg.hostName = name;
        cfg.hostPort = port;
        cfg.isPublic = pub;
    }

    @Override
    public NetController createGame(GameSetupConfig cfg) {
        GameServer server = new GameServer(this, cfg);
        gameServers.add(server);
        return server;
    }

    @Override
    public void create() {
        textureManager = new ServerTextureManager();
        createGame(cfg);
    }

    @Override
    public void dispose() {
        super.dispose();    //To change body of overridden methods use File | Settings | File Templates.

        for (GameServer server: gameServers) {
            server.stop();
        }

        try {
            Thread.sleep(25); // wait 25 ms to make sure the server can send an http request
        } catch (InterruptedException e) {

        }

    }

    @Override
    public void render() {
        if (this.getScreen() != null) this.getScreen().render(Gdx.graphics.getDeltaTime());

        Array<GameServer> remove = new Array<GameServer>();
        for (int s = 0; s < gameServers.size; s++) {
            GameServer server = gameServers.get(s);
            if (server.isLoadGame() && !server.isGameStarted()) {
                server.startGame();
            }

            if (server.getGameResult() != null) {
                server.reset();
            }

            server.update(Gdx.graphics.getDeltaTime());

            if (server.isStopServer()) {
                server.stop();
                remove.add(server);
            }
        }
        gameServers.removeAll(remove, true);

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

    public ServerScreen getServerScreen() {
        return new ServerScreen(this);
    }

    public Array<GameServer> getGameServers() {
        return gameServers;
    }
}
