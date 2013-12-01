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
import com.badlogic.gdx.utils.ObjectMap.Entry;
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

    PlayerScore[] playerScores;
    Player[] players;

    Image turnIndicator;

    public ScorePanel(VOBGame game, LevelScreen levelScreen, String icon, Skin skin, Array<Player> players) {
        super(game, levelScreen, icon, skin);

        Pixmap pixmap = new Pixmap(32, 32, Pixmap.Format.RGBA8888);
        pixmap.setColor(1, 1, 1, 1);
        pixmap.fillCircle(15, 15, 10);
        turnIndicator = new Image(TextureManager.getSpriteFromAtlas("assets", "reticule"));

        int y = 0;

        this.players = new Player[players.size];
        for (int i = 0; i < players.size; i++) this.players[i] = players.get(i);
        this.playerScores = new PlayerScore[players.size];

        for (int i = 0; i < this.players.length; i++) {
            playerScores[i] = new PlayerScore(skin, this.players[i]);
        }

        topright = true;
    }

    public void update(float delta, Player currentPlayer) {
        //dirty = true;
        super.update(delta);

        for (int i = 0; i < players.length; i++) {
            playerScores[i].update();
            if (players[i] == currentPlayer)
                turnIndicator.setY(playerScores[i].getY());
        }

    }

    @Override
    public void rebuild() {
        super.rebuild();

        float x = icon.getWidth() + turnIndicator.getWidth();
        int y = 0;
        float width = 0;
        for (int i = 0; i < playerScores.length; i++) {
            addActor(playerScores[i]);
            playerScores[i].setPosition(x, y);
            y += playerScores[i].getHeight();
            if (playerScores[i].getWidth() > width) width = playerScores[i].getWidth();
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
        Player player;

        boolean showAll = false;

        String name;
        int curFood, maxFood, money;

        public PlayerScore(Skin skin, Player player) {
            this(skin, player, false);
        }

        public PlayerScore(Skin skin, Player player, boolean showAll) {
            super();
            this.skin = skin;
            this.player = player;
            this.showAll = showAll;

            // TODO load from atlas
            nameLabel = new Label(player.getPlayerName(), skin, "medium");
            foodIcon = new Image(TextureManager.getSpriteFromAtlas("assets", "supply"));
            moneyIcon = new Image(TextureManager.getSpriteFromAtlas("assets", "money"));
            foodLabel = new Label(String.format(FOOD, 0, 0), skin, "medium");
            moneyLabel = new Label(String.format(MONEY, 0), skin, "medium");

            nameLabel.setColor(player.getPlayerColor());
            foodLabel.setColor(player.getPlayerColor());
            moneyLabel.setColor(player.getPlayerColor());

            addActor(nameLabel);
            if (showAll) {
                addActor(foodIcon);
                addActor(moneyIcon);
                addActor(foodLabel);
                addActor(moneyLabel);
            }

            update();
        }

        public void update() {

            String name = player.getPlayerName();
            if (!name.equals(this.name)) {
                nameLabel.setText(player.getPlayerName());
                nameLabel.invalidate();
                this.name = name;
            }

            int curFood = player.getCurFood(), maxFood = player.getMaxFood();

            if (this.curFood != curFood || this.maxFood != maxFood) {
                foodLabel.setText(String.format(FOOD, player.getCurFood(), player.getMaxFood()));
                foodIcon.invalidate();
                this.curFood = curFood;
                this.maxFood = maxFood;
            }

            int money = (int)player.getBankMoney();
            if (this.money != money) {
                moneyLabel.setText(String.format(MONEY, (int)player.getBankMoney()));
                moneyLabel.invalidate();
                this.money = money;
            }

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
