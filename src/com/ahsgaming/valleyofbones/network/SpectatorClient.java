package com.ahsgaming.valleyofbones.network;

import com.ahsgaming.valleyofbones.*;
import com.ahsgaming.valleyofbones.screens.GameSetupConfig;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Array;
import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;

import java.io.IOException;

/**
 * valley-of-bones
 * (c) 2013 Jami Couch
 * User: jami
 * Date: 12/19/13
 * Time: 7:34 AM
 */
public class SpectatorClient implements NetController {
    public String LOG = "SpectatorClient";

    VOBGame game;
    GameSetupConfig gameConfig;
    GameController gameController;
    GameResult gameResult;
    Array<Command> commandQueue;
    KryoCommon.GameUpdate gameUpdate;

    Client client;
    boolean isConnecting = false;
    boolean isReconnect = false;
    KryoCommon.Error error;

    Array<Player> players;
    Array<String> spectators;
    int firstTurnPid = -1;

    boolean stopClient = false;
    boolean recdEndTurn = false;

    public SpectatorClient(VOBGame vobGame, GameSetupConfig cfg) {
        game = vobGame;
        gameConfig = cfg;
        commandQueue = new Array<Command>();

        client = new Client();
        client.start();

        players = new Array<Player>();
        spectators = new Array<String>();

        KryoCommon.register(client);

        client.addListener(new Listener() {
            @Override
            public void connected(Connection connection) {
                super.connected(connection);
                KryoCommon.Spectator sp = new KryoCommon.Spectator();
                sp.name = gameConfig.playerName;
                client.sendTCP(sp);
            }

            @Override
            public void disconnected(Connection connection) {
                super.disconnected(connection);
                if (gameResult != null) {
                    isReconnect = true; // attempt reconnect
                }
            }

            @Override
            public void received(Connection c, Object obj) {
                super.received(c, obj);
                if (isConnecting) {
                    if (obj instanceof KryoCommon.VersionError) {
                        Gdx.app.log(LOG, "VersionError");
                        error = (KryoCommon.VersionError)obj;
                        c.close();
                    }

                    if (obj instanceof KryoCommon.GameFullError) {
                        Gdx.app.log(LOG, "GameFullError");
                        error = (KryoCommon.GameFullError)obj;
                        c.close();
                    }

                    if (obj instanceof KryoCommon.Spectator) {
                        Gdx.app.log(LOG, "Spectator rec'd");
                    }
                    isConnecting = false;
                    return;
                }

                if (obj instanceof Command[]) {
                    Gdx.app.log(LOG, "Command history rec'd (" + ((Command[])obj).length + ")");
                    commandQueue.addAll((Command[])obj);
                }

                if (obj instanceof KryoCommon.GameUpdate) {
                    gameUpdate = (KryoCommon.GameUpdate)obj;
                }

                if (gameController == null) {
                    if (obj instanceof KryoCommon.GameDetails) {
                        Gdx.app.log(LOG, "GameDetails rec'd");
                        gameConfig.mapName = ((KryoCommon.GameDetails) obj).map;
                    }

                    if (obj instanceof KryoCommon.StartGame) {
                        Gdx.app.log(LOG, "StartGame rec'd");
                        game.setLoadGame();
                        firstTurnPid = ((KryoCommon.StartGame)obj).currentPlayer;
                    }

                    if (obj instanceof KryoCommon.RegisteredPlayer[]) {
                        Gdx.app.log(LOG, "PlayerList rec'd");
                        KryoCommon.RegisteredPlayer[] plist = (KryoCommon.RegisteredPlayer[])obj;
                        players.clear();
                        for (KryoCommon.RegisteredPlayer rp: plist) {
                            Player pl = new Player(rp.id, rp.name, Player.AUTOCOLORS[rp.color]);
                            players.add(pl);
                        }
                    }

                    if (obj instanceof KryoCommon.Spectator[]) {
                        Gdx.app.log(LOG, "Spectator list rec'd");
                        KryoCommon.Spectator[] slist = (KryoCommon.Spectator[])obj;
                        spectators.clear();
                        for (KryoCommon.Spectator s: slist) {
                            spectators.add(s.name);
                        }
                    }
                    return;
                }

                if (obj instanceof Command) {
                    commandQueue.add((Command)obj);
                }

                if (obj instanceof GameResult) {
                    Gdx.app.log(LOG, "GameResult rec'd");
                    gameResult = (GameResult)obj;
                }
            }
        });
        connect();
    }

    public void connect() {
        isConnecting = true;
        new Thread() {
            public void run() {
                try {
                    Gdx.app.log(LOG, "Connecting to " + gameConfig.hostName + ":" + gameConfig.hostPort);
                    client.connect(5000, gameConfig.hostName, gameConfig.hostPort);
                } catch (IOException e) {
                    Gdx.app.log(LOG, "Client connection failed: " + e.getMessage());
                    e.printStackTrace();
                    isConnecting = false;
                }
            }
        }.start();
    }

    public void reconnect() {
        isConnecting = true;
        new Thread() {
            public void run() {
                try {
                    Gdx.app.log(LOG, "Attempting to reconnect");
                    client.reconnect(5000);
                } catch (IOException e) {
                    Gdx.app.log(LOG, "Reconnect failed: " + e.getMessage());
                    e.printStackTrace();
                    stopClient = true;
                }
                isReconnect = false;
                isConnecting = false;
            }
        }.start();
    }

    @Override
    public void startGame() {
        gameController = new GameController(gameConfig, players);
        gameController.LOG += "#Spectator";
        gameController.setCurrentPlayer(firstTurnPid);
    }

    @Override
    public void sendStartGame() {
        // no-op
    }

    @Override
    public void setGameController(GameController controller) {
        gameController = controller;
    }

    @Override
    public GameController getGameController() {
        return gameController;
    }


    @Override
    public void endGame() {
        client.stop();
        gameController.setState(GameStates.GAMEOVER);
        game.setGameResult(gameResult);
    }

    @Override
    public void stop() {
        stopClient = true;
    }

    @Override
    public boolean update(float delta) {
        if (stopClient) {
            client.stop();
            return false;
        }

        if (gameController == null) return true;

        if (gameResult != null) {
            endGame();
            return false;
        }

        gameController.update(delta);

        if (isReconnect && !isConnecting) reconnect();

        while (commandQueue.size > 0) {
            Gdx.app.log(LOG, "Queuing command...");
            if (commandQueue.first().turn > gameController.getGameTurn()) {
                gameController.setNextTurn(true);
                gameController.doTurn();
            } else {
                Command cmd = commandQueue.removeIndex(0);
                if (cmd instanceof EndTurn) {
                    gameController.setNextTurn(true);
                    gameController.doTurn();
                } else {
                    gameController.queueCommand(cmd);
                }
            }
            gameController.update(0);
        }

        if (gameUpdate != null) {
            while (gameUpdate.turn > gameController.getGameTurn()) {
                gameController.setNextTurn(true);
                gameController.doTurn();
                gameController.update(0);
            }
            gameController.setCurrentPlayer(gameUpdate.currentPlayer);
            gameController.setTurnTimer(gameUpdate.timer);
            gameUpdate = null;
        }

        if (recdEndTurn) {
            gameController.setNextTurn(true);
            gameController.doTurn();
            recdEndTurn = false;
        }

        return true;
    }

    @Override
    public void addAIPlayer() {
        // no-op
    }

    @Override
    public void removePlayer(int playerId) {
        // no-op
    }

    @Override
    public Array<Player> getPlayers() {
        return players;
    }

    @Override
    public Player getPlayer() {
        return null;
    }

    @Override
    public void sendCommand(Command cmd) {
        // no-op
    }

    @Override
    public void sendAICommand(Command cmd) {
        // no-op
    }

    @Override
    public boolean isConnected() {
        if (client == null) return false;

        return client.isConnected();
    }

    @Override
    public boolean isConnecting() {
        return isConnecting;
    }

    @Override
    public void setMap(String map) {
        // TODO
    }

    @Override
    public Array<String> getSpectators() {
        return spectators;
    }
}
