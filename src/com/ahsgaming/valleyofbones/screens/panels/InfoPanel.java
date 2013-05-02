package com.ahsgaming.valleyofbones.screens.panels;

import com.ahsgaming.valleyofbones.VOBGame;
import com.ahsgaming.valleyofbones.screens.LevelScreen;
import com.ahsgaming.valleyofbones.units.Unit;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
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

    final String HEALTH = "HP %d/%d";
    final String ATTACK = "ATTACK %d (RANGE %d)";
    final String MOVE = "SPEED %d";
    final String ATTACK_LEFT = "ATTACKS LEFT %d";
    final String MOVE_LEFT = "MOVES LEFT %d";

    Label lblTitle, lblHealth, lblAttack, lblMove, lblAttacksLeft, lblMovesLeft;

    Unit selected;

    public InfoPanel(VOBGame game, LevelScreen levelScreen, String icon, Skin skin) {
        super(game, levelScreen, icon, skin);
        this.skin = skin;
        this.horizontal = false;

        lblTitle = new Label("Nothing selected", skin, "medium");
        lblHealth = new Label(String.format(HEALTH, 0, 0), skin, "small");
        lblAttack = new Label(String.format(ATTACK, 0, 0), skin, "small");
        lblMove = new Label(String.format(MOVE, 0), skin, "small");
        lblAttacksLeft = new Label(String.format(ATTACK_LEFT, 0), skin, "small");
        lblMovesLeft = new Label(String.format(MOVE_LEFT, 0), skin, "small");
        expand();
    }

    @Override
    public void update(float delta) {

        if (selected != null) {
            lblTitle.setText(selected.getProtoId());
            lblHealth.setText(String.format(HEALTH, selected.getCurHP(), selected.getMaxHP()));
            lblAttack.setText(String.format(ATTACK, selected.getAttackDamage(), selected.getAttackRange()));
            lblMove.setText(String.format(MOVE, (int)selected.getMoveSpeed()));
            lblAttacksLeft.setText(String.format(ATTACK_LEFT, selected.getAttacksLeft()));
            lblMovesLeft.setText(String.format(MOVE_LEFT, selected.getMovesLeft()));
        }

        dirty = true;
        super.update(delta);

        if (selected != null)
            expand();
        else
            contract();

    }

    @Override
    public void rebuild() {


            this.clear();

            this.addActor(icon);
            icon.setPosition(0, 0);

            Array<Label> labels = new Array<Label>();

            labels.add(lblTitle);
            labels.add(lblHealth);
            labels.add(lblAttack);
            labels.add(lblMove);
            labels.add(lblAttacksLeft);
            labels.add(lblMovesLeft);

            int y = 0;
            while(labels.size > 0) {
                Label lbl = labels.pop();
                lbl.setPosition(0, y);
                y += lbl.getHeight();
                this.addActor(lbl);
            }
            icon.setPosition(0, y);
        built = true;
        dirty = false;
    }



    public void setSelected(Unit unit) {
        selected = unit;
    }
}
