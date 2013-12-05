package com.ahsgaming.valleyofbones.screens.panels;

import com.ahsgaming.valleyofbones.TextureManager;
import com.ahsgaming.valleyofbones.VOBGame;
import com.ahsgaming.valleyofbones.screens.LevelScreen;
import com.ahsgaming.valleyofbones.units.Prototypes;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
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
    boolean horizontal = true, topright = false;
    Vector2 anchor;

    boolean expanded = false;
    boolean built = false;
    boolean dirty = false;

    Array<Image> buttons;
    Array<Prototypes.JsonProto> items;

    Skin skin;

    public Panel(VOBGame game, LevelScreen levelScreen, String icon, Skin skin) {
        this.game = game;
        this.icon = new Image(VOBGame.instance.getTextureManager().getSpriteFromAtlas("assets", icon));
        this.levelScreen = levelScreen;
        this.buttons = new Array<Image>();
        this.items = new Array<Prototypes.JsonProto>();
        this.skin = skin;
    }

    public void update(float delta) {
        // TODO optimize this
        boolean doexp = false;
        if (!built) {
            if (anchor == null) anchor = new Vector2(getX(), getY()); else setPosition(anchor.x, anchor.y);
            if (topright) doexp = true;
        }
        if (!built || dirty) rebuild();

//        if (doexp) expand();
    }

    public void rebuild() {
        this.clear();

        built = true;
        dirty = false;

    }

    public boolean buttonClicked(Image button, int i) {
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
        Vector2 newPos = getPreferredPosition();
        this.addAction(Actions.moveTo(newPos.x, newPos.y, 0.5f));

    }

    public void contract() {
        if (!expanded) return;

        expanded = false;
        this.clearActions();
        Vector2 newPos = getPreferredPosition();
        this.addAction(Actions.moveTo(newPos.x, newPos.y, 0.5f));
    }

    public void toggle() {
        if (expanded) contract(); else expand();
    }

    public void setAnchor(float x, float y) {
        if (anchor == null)
            anchor = new Vector2(x, y);
        else
            anchor.set(x, y);

        Vector2 newPos = getPreferredPosition();
        setPosition(newPos.x, newPos.y);
    }

    public Vector2 getAnchor() {
        return anchor;
    }

    public Vector2 getPreferredPosition() {
        if (expanded) {
            if (horizontal) {
                if (topright) {
                    return new Vector2(anchor.x - getWidth(), anchor.y);
                } else {
                    return new Vector2(0, anchor.y);
                }
            } else {
                return new Vector2(anchor.x, 0);
            }

        } else {
            if (horizontal) {
                if (topright) {
                    return new Vector2(anchor.x - icon.getWidth(), anchor.y);
                } else {
                    return new Vector2(icon.getX() * -1, anchor.y);
                }
            } else {
                return new Vector2(anchor.x, icon.getY() * -1);
            }
        }
    }
}
