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

    Unit selected;

    public InfoPanel(VOBGame game, LevelScreen levelScreen, String icon, Skin skin) {
        super(game, levelScreen, icon, skin);
        this.skin = skin;
        this.horizontal = false;
    }

    @Override
    public void update(float delta) {
        dirty = true;
        super.update(delta);
    }

    @Override
    public void rebuild() {
        if (selected != null) {

            this.clear();

            this.addActor(icon);
            icon.setPosition(0, 0);

            Array<Label> labels = new Array<Label>();


            Label lblTitle = new Label(selected.getProtoId(), skin, "medium");
            labels.add(lblTitle);

            Label lblHealth = new Label(String.format("HP %d/%d", selected.getCurHP(), selected.getMaxHP()), skin, "small");
            labels.add(lblHealth);

            Label lblAttack = new Label(String.format("ATTACKS LEFT %d (DAMAGE %d)", selected.getAttacksLeft(), selected.getAttackDamage()), skin, "small");
            labels.add(lblAttack);

            Label lblMoves = new Label(String.format("MOVES LEFT %d", selected.getMovesLeft()), skin, "small");
            labels.add(lblMoves);

            int y = 0;
            while(labels.size > 0) {
                Label lbl = labels.pop();
                lbl.setPosition(0, y);
                y += lbl.getHeight();
                this.addActor(lbl);
            }
            icon.setPosition(0, y);

            expand();
        } else {
            contract();
        }
        built = true;
        dirty = false;
    }



    public void setSelected(Unit unit) {
        selected = unit;
    }
}
