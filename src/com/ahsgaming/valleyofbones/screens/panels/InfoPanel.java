package com.ahsgaming.valleyofbones.screens.panels;

import com.ahsgaming.valleyofbones.TextureManager;
import com.ahsgaming.valleyofbones.VOBGame;
import com.ahsgaming.valleyofbones.screens.LevelScreen;
import com.ahsgaming.valleyofbones.units.ProgressBar;
import com.ahsgaming.valleyofbones.units.Unit;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
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
    Image imgBackground, iconHealth, iconAttack, iconRange, iconArmor, iconMove, iconAttacksLeft, iconMovesLeft, iconRefund, imgUnit, iconAbility;
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

        iconHealth = new Image(VOBGame.instance.getTextureManager().getSpriteFromAtlas("assets", "hospital-cross-small"));
        iconAttack = new Image(VOBGame.instance.getTextureManager().getSpriteFromAtlas("assets", "crossed-swords-small"));
        iconRange = new Image(VOBGame.instance.getTextureManager().getSpriteFromAtlas("assets", "archery-target-small"));
        iconArmor = new Image(VOBGame.instance.getTextureManager().getSpriteFromAtlas("assets", "checked-shield-small"));
        iconMove = new Image(VOBGame.instance.getTextureManager().getSpriteFromAtlas("assets", "radial-balance-small"));
        iconMovesLeft = new Image(VOBGame.instance.getTextureManager().getSpriteFromAtlas("assets", "walking-boot-small"));
        iconAttacksLeft = new Image(VOBGame.instance.getTextureManager().getSpriteFromAtlas("assets", "rune-sword-small"));
        iconRefund = new Image(VOBGame.instance.getTextureManager().getSpriteFromAtlas("assets", "skull-crossed-bones-small"));
        imgBackground = new Image(VOBGame.instance.getTextureManager().getSpriteFromAtlas("assets", "selection-hud-bg"));

        iconRefund.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                levelScreen.refundUnit(selected);
            }
        });


        lblTitle = new Label("NOTHING", skin, "small");
        lblTitle.setFontScale(VOBGame.SCALE);
        lblHealth = new Label(String.format(HEALTH, 0, 0), skin, "small");
        lblHealth.setFontScale(VOBGame.SCALE);
        lblAttack = new Label(String.format(ATTACK, 0), skin, "small");
        lblAttack.setFontScale(VOBGame.SCALE);
        lblRange = new Label(String.format(RANGE, 0), skin, "small");
        lblRange.setFontScale(VOBGame.SCALE);
        lblArmor = new Label(String.format(ARMOR, 0), skin, "small");
        lblArmor.setFontScale(VOBGame.SCALE);
        lblMove = new Label(String.format(MOVE, 0), skin, "small");
        lblMove.setFontScale(VOBGame.SCALE);
        lblAttacksLeft = new Label(String.format(ATTACK_LEFT, 0), skin, "small");
        lblAttacksLeft.setFontScale(VOBGame.SCALE);
        lblMovesLeft = new Label(String.format(MOVE_LEFT, 0), skin, "small");
        lblMovesLeft.setFontScale(VOBGame.SCALE);
        lblRefund = new Label(String.format(REFUND, 0), skin, "small");
        lblRefund.setFontScale(VOBGame.SCALE);

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

            if (iconAbility != null) removeActor(iconAbility);

            if (selected.getAbility().length() > 0) {
                Gdx.app.log(LOG, selected.getAbility());
                iconAbility = new Image(VOBGame.instance.getTextureManager().getSpriteFromAtlas("assets", selected.getAbility()));
                if (selected.isAbilityActive()) {
                    iconAbility.setColor(0.0f, 0.8f, 1.0f, 1.0f);
                }
                addActor(iconAbility);

                iconAbility.addListener(new ClickListener(){
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        super.clicked(event, x, y);

                        levelScreen.activateAbility(selected.getObjId());
                    }
                });
            } else {
                iconAbility = null;
            }

            for (Label lbl: labels) {
                lbl.invalidate();
                lbl.layout();
                lbl.setSize(lbl.getPrefWidth(), lbl.getPrefHeight());
            }

            healthBar.setSize(iconMovesLeft.getX() - iconHealth.getRight(), 4);
            healthBar.setCurrent((float)selected.getCurHP() / (float)selected.getMaxHP());
            lastSelected = selected;

            layout();
        }
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);
        healthBar.draw((SpriteBatch)batch, getX() + lblHealth.getX(), getY() + iconHealth.getY() + iconHealth.getHeight() * iconHealth.getScaleY() - healthBar.getHeight(), parentAlpha);
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

        lblAttack.setPosition(iconAttack.getX() + iconAttack.getWidth() * iconAttack.getScaleX(), y);

        iconRange.setPosition(lblAttack.getRight(), y);

        lblRange.setPosition(iconRange.getX() + iconRange.getWidth() * iconRange.getScaleX(), y);

        iconHealth.setPosition(x, y);

        lblHealth.setPosition(iconHealth.getX() + iconHealth.getWidth() * iconHealth.getScaleX(), y);
        lblHealth.setFontScale(0.75f * VOBGame.SCALE);

        iconMove.setPosition(Math.max(lblHealth.getRight(), lblRange.getRight()), y);

        lblMove.setPosition(iconMove.getX() + iconMove.getWidth() * iconMove.getScaleX(), y);

        iconArmor.setPosition(lblMove.getRight(), y);

        lblArmor.setPosition(iconArmor.getX() + iconArmor.getWidth() * iconArmor.getScaleX(), y);
        y += iconArmor.getHeight() * iconArmor.getScaleY() + 5;

        iconHealth.setY(y);
        lblHealth.setY(y);

        iconMovesLeft.setPosition(iconMove.getX(), y);

        lblMovesLeft.setPosition(iconMovesLeft.getX() + iconMovesLeft.getWidth() * iconMovesLeft.getScaleX(), y);

        iconAttacksLeft.setPosition(iconArmor.getX(), y);

        lblAttacksLeft.setPosition(iconAttacksLeft.getX() + iconAttacksLeft.getWidth() * iconAttacksLeft.getScaleX(), y);
        y += iconAttacksLeft.getHeight() * iconAttacksLeft.getScaleY() + 5;

        lblTitle.setPosition(x, y);
        y += lblTitle.getHeight();

        if (iconAbility != null) {
            iconAbility.setPosition(lblAttacksLeft.getX() + lblAttacksLeft.getWidth() + 5, lblAttacksLeft.getTop() - iconAbility.getHeight());
        }

        healthBar.setSize(iconMovesLeft.getX() - (iconHealth.getX() + iconHealth.getWidth() * iconHealth.getScaleX()), 4 * VOBGame.SCALE);
    }

    public void setSelected(Unit unit) {
        selected = unit;
    }
}
