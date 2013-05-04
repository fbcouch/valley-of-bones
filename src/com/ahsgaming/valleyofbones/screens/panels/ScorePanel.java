package com.ahsgaming.valleyofbones.screens.panels;

import com.ahsgaming.valleyofbones.Player;
import com.ahsgaming.valleyofbones.TextureManager;
import com.ahsgaming.valleyofbones.VOBGame;
import com.ahsgaming.valleyofbones.screens.LevelScreen;
import com.badlogic.gdx.graphics.Color;
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

    public ScorePanel(VOBGame game, LevelScreen levelScreen, String icon, Skin skin, Array<Player> players) {
        super(game, levelScreen, icon, skin);

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
                playerScores.get(p.getPlayerId()).update(p.getPlayerName(), p.getCurFood(), p.getMaxFood(), (int)p.getBankMoney(), p == currentPlayer);
            }
        }
    }

    @Override
    public void rebuild() {
        super.rebuild();

        int y = 0;
        float width = 0;
        for (Integer i: playerScores.keys()) {
            PlayerScore ps = playerScores.get(i);
            addActor(ps);
            ps.setPosition(icon.getWidth(), y);
            y += ps.getHeight();
            if (ps.getWidth() > width) width = ps.getWidth();
        }

        setWidth(icon.getWidth() + width);
        if (icon.getTop() < y) setHeight(y);
    }

    private static class PlayerScore extends Group {
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
            nameLabel = new Label(name, skin, "small");
            foodIcon = new Image(TextureManager.getTexture("supply.png"));
            moneyIcon = new Image(TextureManager.getTexture("money.png"));
            foodLabel = new Label(String.format(FOOD, 0, 0), skin, "small");
            moneyLabel = new Label(String.format(MONEY, 0), skin, "small");

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

            update(name, 0, 0, 0, false);
        }

        public void update(String name, int curFood, int maxFood, int money, boolean current) {
            nameLabel.setText(name);
            foodLabel.setText(String.format(FOOD, curFood, maxFood));
            moneyLabel.setText(String.format(MONEY, money));

            if (current) {
                moneyLabel.setFontScale(1.25f);
                foodLabel.setFontScale(1.25f);
                nameLabel.setFontScale(1.25f);
            } else {
                moneyLabel.setFontScale(1f);
                foodLabel.setFontScale(1f);
                nameLabel.setFontScale(1f);
            }
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

            float extra = nameLabel.getPrefWidth() * 0.25f;
            if (showAll) extra += (foodLabel.getPrefWidth() + moneyLabel.getPrefWidth()) * 0.25f;

            setSize(x + (current ? 0 : extra), maxy);
        }
    }
}
