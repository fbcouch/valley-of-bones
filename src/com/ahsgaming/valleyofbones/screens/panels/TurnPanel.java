package com.ahsgaming.valleyofbones.screens.panels;

import com.ahsgaming.valleyofbones.GameController;
import com.ahsgaming.valleyofbones.Player;
import com.ahsgaming.valleyofbones.VOBGame;
import com.ahsgaming.valleyofbones.screens.LevelScreen;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

/**
 * valley-of-bones
 * (c) 2013 Jami Couch
 * User: jami
 * Date: 12/9/13
 * Time: 5:32 PM
 */
public class TurnPanel extends Group {
    GameController gController;

    static String TIME = "%d:%02d";

    Image imgBackground, imgP1Indicator, imgP2Indicator, imgIndicatorOverlay;
    Label lblPlayer1, lblPlayer2, lblTime;
    Player player1, player2, lastPlayer, thePlayer;
    Skin skin;

    InfoPanel infoPanel, infoPanel2;
    Group endTurn;

    int lastTick;

    public TurnPanel(GameController controller, Player player, Skin skin) {
        this.gController = controller;
        this.skin = skin;
        this.thePlayer = player;

        imgBackground = new Image(VOBGame.instance.getTextureManager().getSpriteFromAtlas("assets", "turn-hud-bg"));
        addActor(imgBackground);

        imgP1Indicator = new Image(VOBGame.instance.getTextureManager().getSpriteFromAtlas("assets", "turn-indicator-base"));
        addActor(imgP1Indicator);

        imgP2Indicator = new Image(VOBGame.instance.getTextureManager().getSpriteFromAtlas("assets", "turn-indicator-base"));
        addActor(imgP2Indicator);

        imgIndicatorOverlay = new Image(VOBGame.instance.getTextureManager().getSpriteFromAtlas("assets", "turn-indicator-overlay"));

        if (thePlayer == null || thePlayer == gController.getPlayers().get(0)) {
            player1 = gController.getPlayers().get(0);
            player2 = gController.getPlayers().get(1);
        } else {
            player1 = gController.getPlayers().get(1);
            player2 = gController.getPlayers().get(0);
        }

        lblPlayer1 = new Label(player1.getPlayerName(), skin, "small-font", player1.getPlayerColor());
        lblPlayer1.setFontScale(VOBGame.SCALE);
        addActor(lblPlayer1);


        lblPlayer2 = new Label(player2.getPlayerName(), skin, "small-font", player2.getPlayerColor());
        lblPlayer2.setFontScale(VOBGame.SCALE);
        addActor(lblPlayer2);

        lblTime = new Label(String.format(TIME, 0, 0), skin, "small-font", new Color(0.8f, 0.8f, 0.8f, 1));
        lblTime.setFontScale(VOBGame.SCALE);
        addActor(lblTime);

        if (thePlayer == null) {
            infoPanel = new InfoPanel(player1, false, skin);
            addActor(infoPanel);
            infoPanel.setZIndex(0);
            infoPanel2 = new InfoPanel(player2, true, skin);
            addActor(infoPanel2);
            infoPanel2.setZIndex(0);
        } else {
            infoPanel = new InfoPanel(thePlayer, (thePlayer == player2), skin);
            addActor(infoPanel);
            infoPanel.setZIndex(0);

            endTurn = new Group();
            addActor(endTurn);
            endTurn.setZIndex(1);

            Image bg = new Image(VOBGame.instance.getTextureManager().getSpriteFromAtlas("assets", "turn-hud-bg-small"));
            endTurn.addActor(bg);
            endTurn.setSize(bg.getWidth(), bg.getHeight());

            Label end = new Label("End Turn", skin, "small-font", new Color(0.8f, 0.8f, 0.8f, 1));
            end.setFontScale(VOBGame.SCALE);
            endTurn.addActor(end);
            if (end.getWidth() > endTurn.getWidth() - 70)
                end.setFontScale((endTurn.getWidth() - 70) / end.getWidth());

            end.setX((thePlayer != player1 ? 25 * VOBGame.SCALE : 45 * VOBGame.SCALE) + (endTurn.getWidth() - 70 * VOBGame.SCALE - end.getWidth() * end.getFontScaleX()) * 0.5f);
            end.setY(endTurn.getHeight() * 0.5f - end.getHeight() * 0.5f);

            endTurn.setX(imgBackground.getWidth() * 0.5f - endTurn.getWidth() * 0.5f);

            endTurn.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    super.clicked(event, x, y);
                    LevelScreen.getInstance().endTurn();
                }
            });
        }

        layout();
    }

    public void layout() {
        imgBackground.setPosition(0, 0);
        setSize(imgBackground.getWidth(), imgBackground.getHeight());

        lblTime.setPosition(
                197 * VOBGame.SCALE,
                (imgBackground.getHeight() - lblTime.getHeight()) * 0.5f
        );

        imgP1Indicator.setPosition(
                (imgBackground.getWidth() * 0.4f - imgP1Indicator.getWidth() * 0.5f),
                (imgBackground.getHeight() - imgP1Indicator.getHeight()) * 0.5f
        );

        imgP2Indicator.setPosition(
                (imgBackground.getWidth() * 0.6f - imgP2Indicator.getWidth() * 0.5f),
                (imgBackground.getHeight() - imgP2Indicator.getHeight()) * 0.5f
        );

        if (lblPlayer1.getWidth() > imgP1Indicator.getX() - 25 * VOBGame.SCALE)
            lblPlayer1.setFontScale((imgP1Indicator.getX() - 25 * VOBGame.SCALE) / lblPlayer1.getWidth());

        lblPlayer1.setPosition(
                25 * VOBGame.SCALE,
                (imgBackground.getHeight() - lblPlayer1.getHeight()) * 0.5f
        );

        Gdx.app.log("width", Float.toString(lblPlayer2.getWidth()));
        Gdx.app.log("max", Float.toString((getWidth() - imgP2Indicator.getRight() - 25 * VOBGame.SCALE)));
        if (lblPlayer2.getWidth() > getWidth() - imgP2Indicator.getRight() - 25 * VOBGame.SCALE)
            lblPlayer2.setFontScale((getWidth() - imgP2Indicator.getRight() - 25 * VOBGame.SCALE) / lblPlayer2.getWidth());
        Gdx.app.log("scale", Float.toString(lblPlayer2.getFontScaleX()));

        lblPlayer2.setPosition(
                imgBackground.getWidth() - lblPlayer2.getWidth() * lblPlayer2.getFontScaleX() - 25 * VOBGame.SCALE,
                (imgBackground.getHeight() - lblPlayer2.getHeight()) * 0.5f
        );

        if (thePlayer == null) {
            infoPanel.setPosition(
                    -infoPanel.getWidth() + 45 * VOBGame.SCALE,
                    imgBackground.getHeight() - infoPanel.getHeight()
            );
            infoPanel2.setPosition(
                    imgBackground.getWidth() - 45 * VOBGame.SCALE,
                    imgBackground.getHeight() - infoPanel2.getHeight()
            );
        } else {
            if (thePlayer == player1) {
                infoPanel.setPosition(
                        -infoPanel.getWidth() + 45 * VOBGame.SCALE,
                        imgBackground.getHeight() - infoPanel.getHeight()
                );
            } else {
                infoPanel.setPosition(
                        imgBackground.getWidth() - 45 * VOBGame.SCALE,
                        imgBackground.getHeight() - infoPanel.getHeight()
                );

            }
            endTurn.setY(imgBackground.getHeight() - endTurn.getHeight());
        }
    }

    public void update(boolean isCurrent) {
        if (lastTick != Math.floor(gController.getTurnTimer())) {
            lastTick = (int)Math.floor(gController.getTurnTimer());
            lblTime.setText(String.format(TIME, lastTick / 60, (lastTick % 60)));

            if (gController.getTurnTimer() <= 5 && isCurrent) {
                lblTime.addAction(Actions.sequence(Actions.color(new Color(1.0f, 0, 0, 1.0f)), Actions.delay(0.2f), Actions.color(new Color(0.8f, 0.8f, 0.8f, 1f))));
            }
        }

        if (lastPlayer != gController.getCurrentPlayer()) {
            lastPlayer = gController.getCurrentPlayer();
            if (lastPlayer == player1) {
                addActor(imgIndicatorOverlay);
                imgIndicatorOverlay.setColor(player1.getPlayerColor());
                imgIndicatorOverlay.setPosition(imgP1Indicator.getX(), imgP1Indicator.getY());
            } else if (lastPlayer == player2) {
                addActor(imgIndicatorOverlay);
                imgIndicatorOverlay.setColor(player2.getPlayerColor());
                imgIndicatorOverlay.setPosition(imgP2Indicator.getX(), imgP2Indicator.getY());
            } else {
                imgIndicatorOverlay.remove();
            }
            if (thePlayer != null) {
                if (thePlayer == player1) {
                    if (isCurrent) {
                        endTurn.addAction(Actions.moveTo(imgBackground.getWidth() - 45 * VOBGame.SCALE, endTurn.getY(), 0.5f));
                    } else {
                        endTurn.addAction(Actions.moveTo(imgBackground.getWidth() - endTurn.getWidth() + 10 * VOBGame.SCALE, endTurn.getY(), 0.5f));
                    }

                } else {
                    if (isCurrent) {
                        endTurn.addAction(Actions.moveTo(-endTurn.getWidth() + 45 * VOBGame.SCALE, endTurn.getY(), 0.5f));
                    } else {
                        endTurn.addAction(Actions.moveTo(-10 * VOBGame.SCALE, endTurn.getY(), 0.5f));
                    }
                }
            }
        }

        infoPanel.update();
        if (infoPanel2 != null) infoPanel2.update();
    }

    public static class InfoPanel extends Group {
        public static String LOG = "InfoPanel";

        Player player;
        Skin skin;
        Image imgBackground, imgMoney, imgSupply;
        Label lblMoney, lblSupply;

        float lastMoney, lastCurSupply, lastMaxSupply;

        boolean pullRight;

        float padLeft = 25 * VOBGame.SCALE, padRight = 45 * VOBGame.SCALE;

        public InfoPanel(Player player, boolean pullRight, Skin skin) {
            this.player = player;
            this.pullRight = pullRight;
            this.skin = skin;

            if (pullRight) {
                padLeft += padRight;
                padRight = padLeft - padRight;
                padLeft -= padRight;
            }

            imgBackground = new Image(VOBGame.instance.getTextureManager().getSpriteFromAtlas("assets", "turn-hud-bg-small"));
            addActor(imgBackground);

            imgMoney = new Image(VOBGame.instance.getTextureManager().getSpriteFromAtlas("assets", "money-small"));
            addActor(imgMoney);

            imgSupply = new Image(VOBGame.instance.getTextureManager().getSpriteFromAtlas("assets", "supply-small"));
            addActor(imgSupply);

            lblMoney = new Label("0000", skin, "small-font", new Color(0.8f, 0.8f, 0.8f, 1));
            lblMoney.setFontScale(VOBGame.SCALE);
            addActor(lblMoney);

            lblSupply = new Label("00/00", skin, "small-font", new Color(0.8f, 0.8f, 0.8f, 1));
            lblSupply.setFontScale(VOBGame.SCALE);
            addActor(lblSupply);

            layout();
        }

        public void layout() {
            imgBackground.setPosition(0, 0);

            lblMoney.setText(String.format("%04d", (int)player.getBankMoney()));
            lblSupply.setText(String.format("%02d/%02d", player.getCurFood(), player.getMaxFood()));

            imgMoney.setPosition(padLeft, (imgBackground.getHeight() - imgMoney.getHeight()) * 0.5f);
            imgSupply.setPosition(
                    padLeft + (imgBackground.getWidth() - padLeft - padRight) * 0.5f,
                    (imgBackground.getHeight() - imgSupply.getHeight()) * 0.5f
            );

            if (lblMoney.getWidth() > imgSupply.getX() - (imgMoney.getX() + imgMoney.getWidth() * imgMoney.getScaleX()))
                lblMoney.setFontScale((imgSupply.getX() - (imgMoney.getX() + imgMoney.getWidth() * imgMoney.getScaleX())) / lblMoney.getWidth() );
            lblMoney.setPosition(imgMoney.getX() + imgMoney.getWidth() * imgMoney.getScaleX(), imgMoney.getY() + (imgMoney.getHeight() - lblMoney.getHeight()) * 0.5f);

            if (lblSupply.getWidth() > imgBackground.getWidth() - padRight - (imgSupply.getX() + imgSupply.getWidth() * imgSupply.getScaleX()))
                lblSupply.setFontScale((imgBackground.getWidth() - padRight - (imgSupply.getX() + imgSupply.getWidth() * imgSupply.getScaleX())) / lblSupply.getWidth() );
            lblSupply.setPosition(imgSupply.getX() + imgSupply.getWidth() * imgSupply.getScaleX(), imgSupply.getY() + (imgSupply.getHeight() - lblSupply.getHeight()) * 0.5f);

            setSize(imgBackground.getWidth(), imgBackground.getHeight());
        }

        public void update() {
            if (lastMoney != player.getBankMoney() || lastCurSupply != player.getCurFood() || lastMaxSupply != player.getMaxFood()) {
                layout();
                lastMoney = player.getBankMoney();
                lastCurSupply = player.getCurFood();
                lastMaxSupply = player.getMaxFood();
            }
        }
    }
}

