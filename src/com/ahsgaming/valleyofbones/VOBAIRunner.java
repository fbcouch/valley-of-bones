package com.ahsgaming.valleyofbones;

import com.ahsgaming.valleyofbones.network.GameServer;
import com.ahsgaming.valleyofbones.network.SPGameClient;
import com.ahsgaming.valleyofbones.screens.AIRunnerScreen;
import com.ahsgaming.valleyofbones.screens.GameSetupScreen;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.FPSLogger;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;

import java.io.FileWriter;

/**
 * valley-of-bones
 * (c) 2013 Jami Couch
 * User: jami
 * Date: 1/9/14
 * Time: 5:44 PM
 */
public class VOBAIRunner extends VOBGame {
    public static final String LOG = "VOBAIRunner";

    Array<SPGameClient> gameClients;

    Array<AIPlayer.GenomicAI> aiPlayers;
    Array<Integer> wins;

    public static int NUM_PER_GENERATION = 8;
    public static float MUTATION_RATE = 0.05f;

    int generation = 0;

    public VOBAIRunner() {
        super();
        gameClients = new Array<SPGameClient>();
        aiPlayers = new Array<AIPlayer.GenomicAI>();
        wins = new Array<Integer>();
    }

    @Override
    public void createGame(GameSetupScreen.GameSetupConfig cfg) {
        super.createGame(cfg);
    }

    @Override
    public void create() {
//        VOBGame.DEBUG_AI = true;
        textureManager = new TextureManager();
        setScreen(new AIRunnerScreen(this));
        Json json = new Json();

//        aiPlayers.add(json.fromJson(AIPlayer.GenomicAI.class, Gdx.files.local("ai/9gwe4pqw").readString()));
//        wins.add(0);
        for (int i = 0; i < NUM_PER_GENERATION; i++) {
            aiPlayers.add(AIPlayer.GenomicAI.generateRandom());
            wins.add(0);
        }

        startGeneration();
    }

    public void startGeneration() {
        generation++;
        wins.clear();
        for (int i = 0; i < aiPlayers.size; i++) {
            wins.add(0);
        }

        GameSetupScreen.GameSetupConfig config = new GameSetupScreen.GameSetupConfig();
        config.isMulti = false;

        for (int i = 0; i < NUM_PER_GENERATION; i++) {
            for (int j = i + 1; j < NUM_PER_GENERATION; j++) {

                SPGameClient client = new SPGameClient(this, config);
                client.removePlayer(client.getPlayer().getPlayerId());
                gameClients.add(client);

                client.addAIPlayer(0);
                client.addAIPlayer(1);

                ((AIPlayer)client.getPlayers().get(0)).genome = aiPlayers.get(i);
                ((AIPlayer)client.getPlayers().get(1)).genome = aiPlayers.get(j);

                client = new SPGameClient(this, config);
                client.removePlayer(client.getPlayer().getPlayerId());
                gameClients.add(client);

                client.addAIPlayer(0);
                client.addAIPlayer(1);

                ((AIPlayer)client.getPlayers().get(1)).genome = aiPlayers.get(i);
                ((AIPlayer)client.getPlayers().get(0)).genome = aiPlayers.get(j);
            }
        }

        for (SPGameClient client: gameClients) {
            client.startGame();
        }
    }

    @Override
    public void dispose() {
        super.dispose();

        for (SPGameClient game: gameClients) {
            game.stop();
        }
    }

    @Override
    public void render() {
        if (this.getScreen() != null) this.getScreen().render(Gdx.graphics.getDeltaTime());

//        fpsLogger.log();
        Array<SPGameClient> toRemove = new Array<SPGameClient>();
        for (SPGameClient client: gameClients) {
            if (client.getGameController().getState() == GameStates.GAMEOVER) {
                Gdx.app.log(LOG, "Winner: " + client.getGameController().getGameResult().winner);
                int i = aiPlayers.indexOf(((AIPlayer) client.getPlayers().get(client.getGameController().getGameResult().winner - 1)).genome, true);
                wins.set(i, wins.get(i) + 1);
                aiPlayers.get(i).wins++;
                toRemove.add(client);
            } else if (client.getGameController().getGameTurn() >= 100) {
                Gdx.app.log(LOG, "Tie");

                toRemove.add(client);
            } else {
                client.update(1);
            }
        }
        gameClients.removeAll(toRemove, true);

        if (gameClients.size == 0) {
            System.out.println("-------------------------------------");
            Gdx.app.log(LOG, "generation " + generation + " finished");

            for (int i = 0; i < aiPlayers.size; i++) {
                Gdx.app.log(aiPlayers.get(i).id, wins.get(i) + " wins");
            }

            Array<AIPlayer.GenomicAI> toKeep = new Array<AIPlayer.GenomicAI>();
            boolean cont = true;
            while (toKeep.size < NUM_PER_GENERATION / 4 && cont) {
                int maxWins = -1;
                for (int i = 0; i < aiPlayers.size; i++) {
                    if (maxWins == -1 || wins.get(maxWins) < wins.get(i)) {
                        maxWins = i;
                    }
                }

                if (wins.get(maxWins) > 0) {
                    toKeep.add(aiPlayers.get(maxWins));
                    aiPlayers.removeIndex(maxWins);
                } else {
                    cont = false;
                }
            }

            aiPlayers.clear();
            aiPlayers.addAll(toKeep);

            for (AIPlayer.GenomicAI ai: toKeep) {
                AIPlayer.GenomicAI child = AIPlayer.GenomicAI.clone(ai);
                child.mutate(MUTATION_RATE);
                aiPlayers.add(child);
                child = AIPlayer.GenomicAI.clone(ai);
                child.mutate(MUTATION_RATE);
                aiPlayers.add(child);
            }

            Json json = new Json();
            for (AIPlayer.GenomicAI ai: aiPlayers) {

                Gdx.files.local("ai/" + ai.id).writeString(json.prettyPrint(ai), false);
            }

            while(aiPlayers.size < NUM_PER_GENERATION) {
                aiPlayers.add(AIPlayer.GenomicAI.generateRandom());
            }

            startGeneration();
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

    public Array<SPGameClient> getGameClients() {
        return gameClients;
    }
}
