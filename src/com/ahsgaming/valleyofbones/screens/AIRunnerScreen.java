package com.ahsgaming.valleyofbones.screens;

import com.ahsgaming.valleyofbones.AIPlayer;
import com.ahsgaming.valleyofbones.VOBAIRunner;
import com.ahsgaming.valleyofbones.VOBGame;
import com.ahsgaming.valleyofbones.network.SPGameClient;
import com.badlogic.gdx.scenes.scene2d.ui.List;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.utils.Array;

/**
 * valley-of-bones
 * (c) 2013 Jami Couch
 * User: jami
 * Date: 1/9/14
 * Time: 5:50 PM
 */
public class AIRunnerScreen extends AbstractScreen {

    List listGames;

    float updateTimer = 0; // only update once per minute
    float updateInterval = 60;

    /**
     * Constructor
     *
     * @param game
     */
    public AIRunnerScreen(VOBAIRunner game) {
        super(game);
    }

    @Override
    public void resize(int width, int height) {
        super.resize(width, height);

        listGames = new List(new String[]{"test"}, getSkin());
        ScrollPane pane = new ScrollPane(listGames, getSkin());
        pane.setFillParent(true);
        stage.addActor(pane);
    }

    @Override
    public void render(float delta) {
        super.render(delta);

        updateTimer -= delta;

        if (updateTimer <= 0) {
            updateTimer = updateInterval;

            Array<String> items = new Array<String>();
            for (SPGameClient client: ((VOBAIRunner)game).getGameClients()) {
                AIPlayer p1 = (AIPlayer)client.getPlayers().get(0);
                AIPlayer p2 = (AIPlayer)client.getPlayers().get(1);

                items.add(String.format("%s ($%.0f %d/%d %d/%d) vs %s ($%.0f %d/%d %d/%d) - %d", p1.getGenome().id,
                        p1.getBankMoney(), p1.getCurFood(), p1.getMaxFood(), p1.getBaseUnit().getCurHP(), p1.getBaseUnit().getMaxHP(),
                        p2.getGenome().id, p2.getBankMoney(), p2.getCurFood(), p2.getMaxFood(), p2.getBaseUnit().getCurHP(), p2.getBaseUnit().getMaxHP(),
                        client.getGameController().getGameTurn()));
            }

            listGames.setItems(items.toArray());
        }
    }

    @Override
    public void pause() {
        super.pause();    //To change body of overridden methods use File | Settings | File Templates.
        updateInterval = 600;
    }

    @Override
    public void resume() {
        super.resume();    //To change body of overridden methods use File | Settings | File Templates.
        updateInterval = 5;
        if (updateTimer > updateInterval) updateTimer = updateInterval;
    }
}
