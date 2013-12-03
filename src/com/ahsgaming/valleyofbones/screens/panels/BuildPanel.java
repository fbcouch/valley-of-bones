package com.ahsgaming.valleyofbones.screens.panels;

import com.ahsgaming.valleyofbones.TextureManager;
import com.ahsgaming.valleyofbones.VOBGame;
import com.ahsgaming.valleyofbones.screens.LevelScreen;
import com.ahsgaming.valleyofbones.units.Prototypes;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;

/**
 * Created with IntelliJ IDEA.
 * User: jami
 * Date: 4/29/13
 * Time: 10:25 AM
 * To change this template use File | Settings | File Templates.
 */
public class BuildPanel extends Panel {
    public static final String LOG = "BuildPanel";

    InfoBox tooltip;

    boolean buildMode;

    public BuildPanel(VOBGame game, LevelScreen levelScreen, String icon, Skin skin) {
        super(game, levelScreen, icon, skin);

        tooltip = new InfoBox(null, skin);
        items = Prototypes.getPlayerCanBuild(game.getPlayer(), game.getNetController().getGameController());
    }

    @Override
    public void update(float delta) {
        super.update(delta);

        // need to get all the buildings this guy can build
        // TODO future optimization - make proto lookups efficient so that this can actually be done
//        Array<Prototypes.JsonProto> items = Prototypes.getPlayerCanBuild(game.getPlayer(), game.getNetController().getGameController());
//        if (!items.equals(this.items)) {
//            this.items = items;
//            dirty = true;
//        }

        // moved this here so that it updates every time without rebuilding
        for (int i = 0; i < buttons.size; i++) {
            Prototypes.JsonProto jp = items.get(i);
            if (jp != null && !game.getPlayer().canBuild(jp.id, game.getNetController().getGameController()))
                buttons.get(i).setColor(0.8f, 0.4f, 0.4f, 1.0f);
            else
                buttons.get(i).setColor(1, 1, 1, 1);
        }
        Gdx.app.log("buildMode", Boolean.toString(buildMode));
        if (buildMode && !levelScreen.isBuildMode()) {
            buildMode = false;
            tooltip.remove();
        }

    }

    @Override
    public void rebuild() {
        super.rebuild();


        int x = 0, i = 0, y = (items.size / 3);
        int spacing = 4;
        buttons.clear();
        for (Prototypes.JsonProto jp: items) {
            final Image btn;
            Sprite sp = TextureManager.getSpriteFromAtlas("assets", jp.image);
            btn = new Image(sp);
            this.addActor(btn);
            if (i > 0 && i % 3 == 0) {
                x = 0;
                y--;
            }
            btn.setX(x);
            btn.setY(y * (btn.getHeight() + spacing));
            x += btn.getWidth() + spacing;
            final int j = i;
            buttons.add(btn);
            btn.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    super.clicked(event, x, y);

                    buttonClicked(btn, j);
                }

                @Override
                public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                    super.enter(event, x, y, pointer, fromActor);
                    LevelScreen.getInstance().setClickInterrupt(true);
                    entered(btn, j);
                }

                @Override
                public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                    super.exit(event, x, y, pointer, toActor);
                    LevelScreen.getInstance().setClickInterrupt(false);
                    exited(btn, j);
                }
            });

            Label hotkey = new Label(" ", skin, "small");
            hotkey.setPosition(btn.getX(), btn.getY());
            addActor(hotkey);
            if (jp.id.equals("marine-base"))
                hotkey.setText("A");
            else if (jp.id.equals("saboteur-base"))
                hotkey.setText("S");
            else if (jp.id.equals("tank-base"))
                hotkey.setText("D");
            else if (jp.id.equals("sniper-base"))
                hotkey.setText("F");

            i++;
        }

        icon.setPosition(x, 0);
        this.setWidth(x);
        this.setHeight(buttons.get(0).getTop());

        if (!levelScreen.isBuildMode()) tooltip.remove();

//        if (expanded) setPosition(0, getY()); else setPosition(-1 * icon.getX(), getY());
    }

    @Override
    public boolean buttonClicked(Image button, int i) {
        if (super.buttonClicked(button, i)) return true;

        levelScreen.setBuildMode(items.get(i));
        tooltip.setProto(items.get(i));
        addActor(tooltip);
        tooltip.setPosition(0, getHeight());
        buildMode = levelScreen.isBuildMode();
        return true;
    }

    public void entered(Image button, int i) {
        if (!(i >= 0 && i < items.size)) return;
        levelScreen.setBuildMode(items.get(i));
        tooltip.setProto(items.get(i));
        addActor(tooltip);
        tooltip.setPosition(0, getHeight());
    }

    public void exited(Image button, int i) {
        if (!buildMode) tooltip.remove();
    }
}
