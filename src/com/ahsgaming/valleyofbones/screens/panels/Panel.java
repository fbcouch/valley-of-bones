package com.ahsgaming.valleyofbones.screens.panels;

import com.ahsgaming.valleyofbones.TextureManager;
import com.ahsgaming.valleyofbones.VOBGame;
import com.ahsgaming.valleyofbones.screens.LevelScreen;
import com.ahsgaming.valleyofbones.units.Prototypes;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;

/**
 * Created with IntelliJ IDEA.
 * User: jami
 * Date: 4/29/13
 * Time: 10:36 AM
 * To change this template use File | Settings | File Templates.
 */
public class Panel extends Group {
    public static final String LOG = "Panel";

    final VOBGame game;
    final LevelScreen levelScreen;
    Image icon;
    boolean horizontal = true;

    boolean expanded = false;
    boolean built = false;
    boolean dirty = false;

    ObjectMap<Image, Prototypes.JsonProto> buttonMap;
    Array<Prototypes.JsonProto> items;

    public Panel(VOBGame game, LevelScreen levelScreen, String icon) {
        this.game = game;
        this.icon = new Image(TextureManager.getTexture(icon + ".png"));
        this.levelScreen = levelScreen;
        this.buttonMap = new ObjectMap<Image, Prototypes.JsonProto>();
        this.items = new Array<Prototypes.JsonProto>();

    }

    public void update(float delta) {
        // TODO optimize this

        if (!built || dirty) rebuild();


    }

    public void rebuild() {
        this.clear();

        this.addActor(icon);
        icon.setPosition(0, 0);
        icon.getListeners().clear();
        this.icon.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                buttonClicked(icon);
            }
        });
        built = true;
        dirty = false;

    }

    public boolean buttonClicked(Image button) {
        if (button == icon) {
            toggle();
            return true;
        }
        return false;
    }

    public void expand() {
        if (expanded) return;

        expanded = true;
        this.clearActions();
        if (horizontal)
            this.addAction(Actions.moveTo(0, getY(), 0.5f));
        else
            this.addAction(Actions.moveTo(getX(), 0, 0.5f));
    }

    public void contract() {
        if (!expanded) return;

        expanded = false;
        this.clearActions();
        if (horizontal)
            this.addAction(Actions.moveTo(icon.getX() * -1, getY(), 0.5f));
        else
            this.addAction(Actions.moveTo(getX(), icon.getY() * -1, 0.5f));
    }

    public void toggle() {
        if (expanded) contract(); else expand();
    }
}
