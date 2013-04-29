package com.ahsgaming.valleyofbones.screens.panels;

import com.ahsgaming.valleyofbones.TextureManager;
import com.ahsgaming.valleyofbones.VOBGame;
import com.ahsgaming.valleyofbones.screens.LevelScreen;
import com.ahsgaming.valleyofbones.units.Prototypes;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;

/**
 * Created with IntelliJ IDEA.
 * User: jami
 * Date: 4/29/13
 * Time: 10:25 AM
 * To change this template use File | Settings | File Templates.
 */
public class BuildPanel extends Panel {
    public static final String LOG = "BuildPanel";

    public BuildPanel(VOBGame game, LevelScreen levelScreen, String icon) {
        super(game, levelScreen, icon);
    }

    @Override
    public void update(float delta) {
        super.update(delta);

        // need to get all the buildings this guy can build
        Array<Prototypes.JsonProto> items = Prototypes.getPlayerCanBuild(game.getPlayer(), game.getNetController().getGameController());
        if (!items.equals(this.items)) {
            this.items = items;
            dirty = true;
        }

    }

    @Override
    public void rebuild() {
        super.rebuild();


        int x = 0, i = 0;
        int spacing = 4;
        for (Prototypes.JsonProto jp: items) {
            final Image btn;
            btn = new Image(TextureManager.getTexture(jp.image + ".png"));
            this.addActor(btn);
            btn.setX(x);
            x += btn.getWidth() + spacing;
            buttonMap.put(btn, jp);
            btn.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    super.clicked(event, x, y);

                    buttonClicked(btn);
                }
            });
            i++;
        }

        icon.setPosition(x, 0);
        this.setWidth(x + icon.getWidth());
    }

    @Override
    public boolean buttonClicked(Image button) {
        if (super.buttonClicked(button)) return true;

        Prototypes.JsonProto jp = buttonMap.get(button);

        levelScreen.setBuildMode(jp);
        return true;
    }
}
