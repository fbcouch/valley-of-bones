package com.ahsgaming.valleyofbones.screens.panels;

import com.ahsgaming.valleyofbones.Player;
import com.ahsgaming.valleyofbones.TextureManager;
import com.ahsgaming.valleyofbones.VOBGame;
import com.ahsgaming.valleyofbones.screens.LevelScreen;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;

/**
 * Created with IntelliJ IDEA.
 * User: jami
 * Date: 5/2/13
 * Time: 11:51 AM
 * To change this template use File | Settings | File Templates.
 */
public class ScorePanel extends Panel {
    public static final String LOG = "ScorePanel";

    ObjectMap<Integer, PlayerScore> playerScores = new ObjectMap<Integer, PlayerScore>();

    Image turnIndicator;

    public ScorePanel(VOBGame game, LevelScreen levelScreen, String icon, Skin skin, Array<Player> players) {
        super(game, levelScreen, icon, skin);

        Pixmap pixmap = new Pixmap(32, 32, Pixmap.Format.RGBA8888);
        pixmap.setColor(1, 1, 1, 1);
        pixmap.fillCircle(15, 15, 10);
        turnIndicator = new Image(TextureManager.getSpriteFromAtlas("assets", "reticule"));

        int y = 0;
        for (Player p: players) {
            playerScores.put(p.getPlayerId(), new PlayerScore(skin, p.getPlayerName(), p.getPlayerColor(), false));
        }

        topright = true;
    }

    public void update(float delta, Array<Player> players, Player currentPlayer) {
        //dirty = true;
        super.update(delta);

        for (Player p: players) {
            if (playerScores.containsKey(p.getPlayerId())) {
                PlayerScore ps = playerScores.get(p.getPlayerId());
                ps.update(p.getPlayerName(), p.getCurFood(), p.getMaxFood(), (int)p.getBankMoney());
                if (p == currentPlayer)
                    turnIndicator.setY(ps.getY());
            }
        }
    }

    @Override
    public void rebuild() {
        super.rebuild();

        float x = icon.getWidth() + turnIndicator.getWidth();
        int y = 0;
        float width = 0;
        for (Integer i: playerScores.keys()) {
            PlayerScore ps = playerScores.get(i);
            addActor(ps);
            ps.setPosition(x, y);
            y += ps.getHeight();
            if (ps.getWidth() > width) width = ps.getWidth();
        }

        turnIndicator.setX(icon.getWidth());
        addActor(turnIndicator);

        setWidth(x + width);
        if (icon.getTop() < y) setHeight(y);
    }

    public static class PlayerScore extends Group {
        final String FOOD = "%02d/%02d";
        final String MONEY = "%04d";

        Label nameLabel;

        Image foodIcon;
        Image moneyIcon;

        Label foodLabel;
        Label moneyLabel;

        Skin skin;

        boolean showAll = false;

        public PlayerScore(Skin skin, String name, Color textColor, boolean showAll) {
            super();
            this.skin = skin;
            this.showAll = showAll;

            // TODO load from atlas
            nameLabel = new Label(name, skin, "medium");
            foodIcon = new Image(TextureManager.getSpriteFromAtlas("assets", "supply"));
            moneyIcon = new Image(TextureManager.getSpriteFromAtlas("assets", "money"));
            foodLabel = new Label(String.format(FOOD, 0, 0), skin, "medium");
            moneyLabel = new Label(String.format(MONEY, 0), skin, "medium");

            nameLabel.setColor(textColor);
            foodLabel.setColor(textColor);
            moneyLabel.setColor(textColor);

            addActor(nameLabel);
            if (showAll) {
                addActor(foodIcon);
                addActor(moneyIcon);
                addActor(foodLabel);
                addActor(moneyLabel);
            }

            update(name, 0, 0, 0);
        }

        public void update(String name, int curFood, int maxFood, int money) {
            nameLabel.setText(name);
            foodLabel.setText(String.format(FOOD, curFood, maxFood));
            moneyLabel.setText(String.format(MONEY, money));

            moneyLabel.invalidate();
            foodLabel.invalidate();
            nameLabel.invalidate();

            int x = 0;
            float maxy = 0;
            nameLabel.setX(x);
            x += nameLabel.getPrefWidth();
            maxy = nameLabel.getTop();
            if (showAll) {
                foodIcon.setX(x);
                x += foodIcon.getPrefWidth();
                if (foodIcon.getTop() > maxy) maxy = foodIcon.getTop();

                foodLabel.setX(x);
                x += foodLabel.getPrefWidth();
                if (foodLabel.getTop() > maxy) maxy = foodLabel.getTop();

                moneyIcon.setX(x);
                x += moneyIcon.getPrefWidth();
                if (moneyIcon.getTop() > maxy) maxy = moneyIcon.getTop();

                moneyLabel.setX(x);
                x += moneyLabel.getPrefWidth();
                if (moneyLabel.getTop() > maxy) maxy = moneyLabel.getTop();
            }

            setSize(x, maxy);
        }
    }
}
