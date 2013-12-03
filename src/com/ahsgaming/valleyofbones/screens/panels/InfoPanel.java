package com.ahsgaming.valleyofbones.screens.panels;

import com.ahsgaming.valleyofbones.TextureManager;
import com.ahsgaming.valleyofbones.VOBGame;
import com.ahsgaming.valleyofbones.screens.LevelScreen;
import com.ahsgaming.valleyofbones.units.ProgressBar;
import com.ahsgaming.valleyofbones.units.Unit;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Actor;
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
public class InfoPanel extends Panel {
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
    Image iconHealth, iconAttack, iconRange, iconArmor, iconMove, iconAttacksLeft, iconMovesLeft, iconRefund;

    Unit selected;

    ProgressBar healthBar;

    public InfoPanel(VOBGame game, LevelScreen lvlScreen, String icon, Skin skin) {
        super(game, lvlScreen, icon, skin);
        this.skin = skin;
        this.horizontal = false;

        iconHealth = new Image(TextureManager.getSpriteFromAtlas("assets", "hospital-cross"));
        iconAttack = new Image(TextureManager.getSpriteFromAtlas("assets", "crossed-swords"));
        iconRange = new Image(TextureManager.getSpriteFromAtlas("assets", "archery-target"));
        iconArmor = new Image(TextureManager.getSpriteFromAtlas("assets", "checked-shield"));
        iconMove = new Image(TextureManager.getSpriteFromAtlas("assets", "radial-balance"));
        iconMovesLeft = new Image(TextureManager.getSpriteFromAtlas("assets", "walking-boot"));
        iconAttacksLeft = new Image(TextureManager.getSpriteFromAtlas("assets", "rune-sword"));
        iconRefund = new Image(TextureManager.getSpriteFromAtlas("assets", "skull-crossed-bones"));

        iconRefund.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                levelScreen.refundUnit(selected);
            }

            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                levelScreen.setClickInterrupt(true);
                return super.touchDown(event, x, y, pointer, button);
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                super.touchUp(event, x, y, pointer, button);
                levelScreen.setClickInterrupt(false);
            }
        });

        lblTitle = new Label("NOTHING", skin, "medium");
        lblHealth = new Label(String.format(HEALTH, 0, 0), skin, "small");
        lblAttack = new Label(String.format(ATTACK, 0), skin, "medium");
        lblRange = new Label(String.format(RANGE, 0), skin, "medium");
        lblArmor = new Label(String.format(ARMOR, 0), skin, "medium");
        lblMove = new Label(String.format(MOVE, 0), skin, "medium");
        lblAttacksLeft = new Label(String.format(ATTACK_LEFT, 0), skin, "medium");
        lblMovesLeft = new Label(String.format(MOVE_LEFT, 0), skin, "medium");
        lblRefund = new Label(String.format(REFUND, 0), skin, "medium");

        healthBar = new ProgressBar();
        healthBar.setSize(lblHealth.getWidth(), 4);
        expanded = true;


    }

    @Override
    public void update(float delta) {

        if (selected != null) {
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

            healthBar.setSize(lblHealth.getWidth(), 4);
            healthBar.setCurrent((float)selected.getCurHP() / selected.getMaxHP());

        }

        dirty = true;
        super.update(delta);

//        if (selected != null)
//            expand();
//        else
//            contract();

    }

    @Override
    public void draw(SpriteBatch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);

        healthBar.draw(batch, getX() + lblHealth.getX(), getY() + lblHealth.getTop(), parentAlpha);
    }

    @Override
    public void rebuild() {
        super.rebuild();

        int y = 0;
        float x = 0;

        if (levelScreen.canRefund(selected)) {
            iconRefund.setPosition(0, y);
            addActor(iconRefund);

            lblRefund.setPosition(iconRefund.getRight(), y);
            addActor(lblRefund);
            y += iconRefund.getHeight();

        }

        iconMovesLeft.setPosition(0, y);
        addActor(iconMovesLeft);

        lblMovesLeft.setPosition(iconMovesLeft.getRight(), y);
        addActor(lblMovesLeft);

        iconAttacksLeft.setPosition(lblMovesLeft.getRight(), y);
        addActor(iconAttacksLeft);

        lblAttacksLeft.setPosition(iconAttacksLeft.getRight(), y);
        if (lblAttacksLeft.getRight() > x) x = lblAttacksLeft.getRight();
        y += iconAttacksLeft.getHeight();
        addActor(lblAttacksLeft);

        iconMove.setPosition(0, y);
        addActor(iconMove);

        lblMove.setPosition(iconMove.getRight(), y);
        addActor(lblMove);
//        if (lblMove.getRight() > x) x = lblMove.getRight();
//        y += iconMove.getHeight();

        iconArmor.setPosition(lblMove.getRight(), y);
        addActor(iconArmor);

        lblArmor.setPosition(iconArmor.getRight(), y);
        if (lblArmor.getRight() > x) x = lblArmor.getRight();
        y += iconArmor.getHeight();
        addActor(lblArmor);

        iconAttack.setPosition(0, y);
        addActor(iconAttack);

        lblAttack.setPosition(iconAttack.getWidth(), y);
        addActor(lblAttack);

        iconRange.setPosition(lblAttack.getRight(), y);
        addActor(iconRange);

        lblRange.setPosition(iconRange.getRight(), y);
        addActor(lblRange);
        if (lblRange.getRight() > x) x = lblRange.getRight();
        y += iconRange.getHeight();

        iconHealth.setPosition(0, y);
        addActor(iconHealth);

        lblHealth.setPosition(iconHealth.getWidth(), y);
        if (lblHealth.getRight() > x) x = lblHealth.getRight();
        y += iconHealth.getHeight();
        addActor(lblHealth);

        lblTitle.setPosition(0, y);
        addActor(lblTitle);
        if (lblTitle.getRight() > x) x = lblTitle.getRight();
        y += lblTitle.getHeight();

        setSize(x, y);
        built = true;
        dirty = false;
    }



    public void setSelected(Unit unit) {
        selected = unit;
    }
}
