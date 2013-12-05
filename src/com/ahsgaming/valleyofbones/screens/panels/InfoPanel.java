package com.ahsgaming.valleyofbones.screens.panels;

import com.ahsgaming.valleyofbones.TextureManager;
import com.ahsgaming.valleyofbones.VOBGame;
import com.ahsgaming.valleyofbones.screens.LevelScreen;
import com.ahsgaming.valleyofbones.units.ProgressBar;
import com.ahsgaming.valleyofbones.units.Unit;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;

/**
 * Created with IntelliJ IDEA.
 * User: jami
 * Date: 5/1/13
 * Time: 11:30 PM
 * To change this template use File | Settings | File Templates.
 */
public class InfoPanel extends Group {
    public static final String LOG = "InfoPanel";

    final String HEALTH = "%d/%d";
    final String ATTACK = "%d";
    final String RANGE = "%d";
    final String ARMOR = "%d";
    final String MOVE = "%d";
    final String ATTACK_LEFT = "%d";
    final String MOVE_LEFT = "%d";
    final String REFUND = "$%d";

    Label lblTitle, lblHealth, lblAttack, lblRange, lblArmor, lblMove, lblAttacksLeft, lblMovesLeft, lblRefund;
    Image imgBackground, iconHealth, iconAttack, iconRange, iconArmor, iconMove, iconAttacksLeft, iconMovesLeft, iconRefund, imgUnit;
    Skin skin;
    VOBGame game;
    LevelScreen levelScreen;

    Unit selected, lastSelected;

    ProgressBar healthBar;

    public InfoPanel(VOBGame game, LevelScreen lvlScreen, Skin skin) {
        super();
        this.game = game;
        this.levelScreen = lvlScreen;
        this.skin = skin;

        iconHealth = new Image(VOBGame.instance.getTextureManager().getSpriteFromAtlas("assets", "hospital-cross"));
        iconAttack = new Image(VOBGame.instance.getTextureManager().getSpriteFromAtlas("assets", "crossed-swords"));
        iconRange = new Image(VOBGame.instance.getTextureManager().getSpriteFromAtlas("assets", "archery-target"));
        iconArmor = new Image(VOBGame.instance.getTextureManager().getSpriteFromAtlas("assets", "checked-shield"));
        iconMove = new Image(VOBGame.instance.getTextureManager().getSpriteFromAtlas("assets", "radial-balance"));
        iconMovesLeft = new Image(VOBGame.instance.getTextureManager().getSpriteFromAtlas("assets", "walking-boot"));
        iconAttacksLeft = new Image(VOBGame.instance.getTextureManager().getSpriteFromAtlas("assets", "rune-sword"));
        iconRefund = new Image(VOBGame.instance.getTextureManager().getSpriteFromAtlas("assets", "skull-crossed-bones"));
        imgBackground = new Image(VOBGame.instance.getTextureManager().getSpriteFromAtlas("assets", "selection-hud-bg"));

        iconRefund.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                levelScreen.refundUnit(selected);
            }
        });


        lblTitle = new Label("NOTHING", skin, "small");
        lblHealth = new Label(String.format(HEALTH, 0, 0), skin, "small");
        lblAttack = new Label(String.format(ATTACK, 0), skin, "small");
        lblRange = new Label(String.format(RANGE, 0), skin, "small");
        lblArmor = new Label(String.format(ARMOR, 0), skin, "small");
        lblMove = new Label(String.format(MOVE, 0), skin, "small");
        lblAttacksLeft = new Label(String.format(ATTACK_LEFT, 0), skin, "small");
        lblMovesLeft = new Label(String.format(MOVE_LEFT, 0), skin, "small");
        lblRefund = new Label(String.format(REFUND, 0), skin, "small");

        healthBar = new ProgressBar();
        healthBar.setSize(lblHealth.getWidth(), 4);


        addActor(imgBackground);
        addActor(iconAttack);
        addActor(lblAttack);
        addActor(iconRange);
        addActor(lblRange);
        addActor(iconHealth);
        addActor(lblHealth);
        addActor(iconMove);
        addActor(lblMove);
        addActor(iconArmor);
        addActor(lblArmor);
        addActor(iconMovesLeft);
        addActor(lblMovesLeft);
        addActor(iconAttacksLeft);
        addActor(lblAttacksLeft);
        addActor(lblTitle);


        layout();

    }

    public void update() {

        if (selected != null && selected != lastSelected) {
            Array<Label> labels = new Array<Label>();

            lblTitle.setText(selected.getTitle());
            labels.add(lblTitle);
            lblHealth.setText(String.format(HEALTH, selected.getCurHP(), selected.getMaxHP()));
            labels.add(lblHealth);
            lblAttack.setText(String.format(ATTACK, selected.getAttackDamage()));
            labels.add(lblAttack);
            lblRange.setText(String.format(RANGE, selected.getAttackRange()));
            labels.add(lblRange);
            lblArmor.setText(String.format(ARMOR, selected.getArmor()));
            labels.add(lblArmor);
            lblMove.setText(String.format(MOVE, (int) selected.getMoveSpeed()));
            labels.add(lblMove);
            lblAttacksLeft.setText(String.format(ATTACK_LEFT, selected.getAttacksLeft()));
            labels.add(lblAttacksLeft);
            lblMovesLeft.setText(String.format(MOVE_LEFT, selected.getMovesLeft()));
            labels.add(lblMovesLeft);
            lblRefund.setText(String.format(REFUND, selected.getRefund()));
            labels.add(lblRefund);

            for (Label lbl: labels) {
                lbl.invalidate();
                lbl.layout();
                lbl.setSize(lbl.getPrefWidth(), lbl.getPrefHeight());
            }

            healthBar.setSize(iconMovesLeft.getX() - iconHealth.getRight(), 4);
            healthBar.setCurrent((float)selected.getCurHP() / selected.getMaxHP());
            lastSelected = selected;

            layout();
        }
    }

    @Override
    public void draw(SpriteBatch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);

        healthBar.draw(batch, getX() + lblHealth.getX(), getY() + iconHealth.getY() + iconHealth.getHeight() * iconHealth.getScaleY() - healthBar.getHeight(), parentAlpha);
    }

    public void layout() {
        setSize(imgBackground.getWidth(), imgBackground.getHeight());

        int y = 0;
        float x = 0;

        if (selected != null) {
            Gdx.app.log(LOG, "unit!");
            if (imgUnit != null) imgUnit.remove();
            imgUnit = new Image(selected.getImage());
            addActor(imgUnit);
            imgUnit.setPosition(25, getHeight() * 0.5f - imgUnit.getHeight() * 0.5f - 5);
            y = (int)imgUnit.getY();
            x = (int)imgUnit.getRight() + 25;
        }

//        if (levelScreen.canRefund(selected)) {
//            iconRefund.setPosition(x, y);
//            addActor(iconRefund);
//
//            lblRefund.setPosition(iconRefund.getRight(), y);
//            addActor(lblRefund);
//            y += iconRefund.getHeight();
//
//        }

        iconAttack.setPosition(x, y);
        iconAttack.setScale(20 / iconAttack.getWidth());

        lblAttack.setPosition(iconAttack.getX() + iconAttack.getWidth() * iconAttack.getScaleX(), y);

        iconRange.setPosition(lblAttack.getRight(), y);
        iconRange.setScale(20 / iconAttack.getWidth());

        lblRange.setPosition(iconRange.getX() + iconRange.getWidth() * iconRange.getScaleX(), y);

        iconHealth.setPosition(x, y);
        iconHealth.setScale(20 / iconAttack.getWidth());

        lblHealth.setPosition(iconHealth.getX() + iconHealth.getWidth() * iconHealth.getScaleX(), y);
        lblHealth.setFontScale(iconHealth.getScaleY());

        iconMove.setPosition(Math.max(lblHealth.getRight(), lblRange.getRight()), y);
        iconMove.setScale(20 / iconAttack.getWidth());

        lblMove.setPosition(iconMove.getX() + iconMove.getWidth() * iconMove.getScaleX(), y);

        iconArmor.setPosition(lblMove.getRight(), y);
        iconArmor.setScale(20 / iconAttack.getWidth());

        lblArmor.setPosition(iconArmor.getX() + iconArmor.getWidth() * iconArmor.getScaleX(), y);
        y += iconArmor.getHeight() * iconArmor.getScaleY() + 5;

        iconHealth.setY(y);
        lblHealth.setY(y);

        iconMovesLeft.setPosition(iconMove.getX(), y);
        iconMovesLeft.setScale(20 / iconAttack.getWidth());

        lblMovesLeft.setPosition(iconMovesLeft.getX() + iconMovesLeft.getWidth() * iconMovesLeft.getScaleX(), y);

        iconAttacksLeft.setPosition(iconArmor.getX(), y);
        iconAttacksLeft.setScale(20 / iconAttack.getWidth());

        lblAttacksLeft.setPosition(iconAttacksLeft.getX() + iconAttacksLeft.getWidth() * iconAttacksLeft.getScaleX(), y);
        y += iconAttacksLeft.getHeight() * iconAttacksLeft.getScaleY() + 5;

        lblTitle.setPosition(x, y);
        y += lblTitle.getHeight();

        healthBar.setSize(iconMovesLeft.getX() - (iconHealth.getX() + iconHealth.getWidth() * iconHealth.getScaleX()), 4);
    }

    public void setSelected(Unit unit) {
        selected = unit;
    }
}
