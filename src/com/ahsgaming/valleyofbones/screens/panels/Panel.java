package com.ahsgaming.valleyofbones.screens.panels;

import com.ahsgaming.valleyofbones.TextureManager;
import com.ahsgaming.valleyofbones.VOBGame;
import com.ahsgaming.valleyofbones.screens.LevelScreen;
import com.ahsgaming.valleyofbones.units.Prototypes;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
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

    ObjectMap<Image, Prototypes.JsonProto> buttonMap;

    public Panel(VOBGame game, LevelScreen levelScreen, String icon) {
        this.game = game;
        this.icon = new Image(TextureManager.getTexture(icon + ".png"));
        this.levelScreen = levelScreen;
        this.buttonMap = new ObjectMap<Image, Prototypes.JsonProto>();
    }

    public void update(float delta) {
        // TODO optimize this

        rebuild();
    }

    public void rebuild() {
        this.clear();

        this.addActor(icon);
        icon.setPosition(0, 0);
    }

    public void buttonClicked(Image button) {

    }
}
