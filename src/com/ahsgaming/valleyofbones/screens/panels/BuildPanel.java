package com.ahsgaming.valleyofbones.screens.panels;

import com.ahsgaming.valleyofbones.GameController;
import com.ahsgaming.valleyofbones.Player;
import com.ahsgaming.valleyofbones.VOBGame;
import com.ahsgaming.valleyofbones.screens.LevelScreen;
import com.ahsgaming.valleyofbones.units.Prototypes;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.*;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;

/**
 * valley-of-bones
 * (c) 2013 Jami Couch
 * User: jami
 * Date: 12/9/13
 * Time: 5:33 PM
 */
public class BuildPanel extends Group {
    public static String LOG = "BuildPanel";

    GameController gController;
    LevelScreen levelScreen;
    Player player;
    Skin skin;

    Image imgBackground, imgInfantryTab;//, imgMechTab;
    Array<BuildItem> infantryItems;//, mechItems;
    Array<Prototypes.JsonProto> itemProtos;
    Image imgNext;

    int selected, page;

    public BuildPanel(GameController controller, Player player, LevelScreen lvlScreen) {
        this.gController = controller;
        this.player = player;
        this.levelScreen = lvlScreen;
        skin = levelScreen.getSkin();

        imgBackground = new Image(VOBGame.instance.getTextureManager().getSpriteFromAtlas("assets", "build-hud-bg"));
//        addActor(imgBackground);

//        imgInfantryTab = new Image(VOBGame.instance.getTextureManager().getSpriteFromAtlas("assets", "build-infantry-tab"));
//        addActor(imgInfantryTab);
//        imgInfantryTab.addListener(new ClickListener() {
//            @Override
//            public void clicked(InputEvent event, float x, float y) {
//                super.clicked(event, x, y);    //To change body of overridden methods use File | Settings | File Templates.
//
//                select(0);
//            }
//        });

        for (int i = 0; i < 4; i++) {
            Image btnBg = new Image(VOBGame.instance.getTextureManager().getSpriteFromAtlas("assets", "unit-highlight"));
            addActor(btnBg);
            btnBg.setPosition((i % 2) * btnBg.getWidth() * 0.5f, btnBg.getHeight() * i * 0.75f);
        }

        imgNext = new Image(VOBGame.instance.getTextureManager().getSpriteFromAtlas("assets", "unit-highlight"));
        imgNext.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);

                next();
            }
        });

//        imgMechTab = new Image(VOBGame.instance.getTextureManager().getSpriteFromAtlas("assets", "build-mech-tab"));
//        addActor(imgMechTab);
//        imgMechTab.addListener(new ClickListener(){
//            @Override
//            public void clicked(InputEvent event, float x, float y) {
//                super.clicked(event, x, y);    //To change body of overridden methods use File | Settings | File Templates.
//
//                select(1);
//            }
//        });

        itemProtos = Prototypes.getPlayerCanBuild((player != null ? player.getRace() : "terran"));
        infantryItems = new Array<BuildItem>();
//        mechItems = new Array<BuildItem>();

        for (Prototypes.JsonProto jp: itemProtos) {
            final BuildItem item = new BuildItem(jp, skin);

            String type = jp.getProperty("subtype").asString();
//            if (type.equals("armored")) {
//                mechItems.add(item);
//            } else {
            infantryItems.add(item);
//            }

            item.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    super.clicked(event, x, y);    //To change body of overridden methods use File | Settings | File Templates.

                    click(item);
                }
            });
        }

        layout();
    }

    public void layout() {
//        imgInfantryTab.setPosition(-15 * VOBGame.SCALE, imgBackground.getTop() - 4 * VOBGame.SCALE);
//        imgMechTab.setPosition(imgInfantryTab.getRight() - 25 * VOBGame.SCALE, imgInfantryTab.getY());
        float y = 111 * VOBGame.SCALE;
//        switch(selected) {
//            default:
//            case 0:
//                imgInfantryTab.setZIndex(2);
//                imgMechTab.setZIndex(0);
//                for (Group g: mechItems) g.remove();
//
        for (Group g: infantryItems) g.remove();
        imgNext.remove();

        int start = page * 3, end = (infantryItems.size <= 4 ? 4 : Math.min(infantryItems.size, start + 3));
        for (int i = start; i < end; i++) {
            Group g = infantryItems.get(i);
            addActor(g);
            g.setPosition(((i % 3 +1) % 2) * 24 * VOBGame.SCALE, y);
            y -= g.getHeight() * 0.75f;
        }

        if (infantryItems.size > 4) {
            addActor(imgNext);
            imgNext.setPosition(0, 0);
        }

//                break;
//            case 1:
//                imgInfantryTab.setZIndex(0);
//                imgMechTab.setZIndex(2);
//                for (Group g: infantryItems) g.remove();
//
//                for (Group g: mechItems) {
//                    addActor(g);
//                    g.setPosition(5, y);
//                    y -= g.getHeight();
//                }
//                break;
//        }

        setSize(imgBackground.getRight(), imgBackground.getTop());
    }

    public void update() {
        Array<BuildItem> items;
//        switch(selected) {
//            default:
//            case 0:
                items = infantryItems;
//                break;
//            case 1:
//                items = mechItems;
//                break;
//        }

        for (BuildItem item: items) {
            if (player == null || player.canBuild(item.proto.id, gController)) {
                item.icon.setColor(1, 1, 1, 1);
            } else {
                item.icon.setColor(0.8f, 0.4f, 0.4f, 1);
            }
            if (player != null) {
                item.setCost(player.getProtoCost(item.proto, gController));
            }
            item.setHighlight(levelScreen.isBuildMode() && levelScreen.getBuildProto() == item.proto);
        }
    }

    public void click(BuildItem item) {
        levelScreen.setBuildMode(item.proto);
    }

    public void select(int sel) {
        selected = sel;
        layout();
    }

    public void next() {
        page++;
        if (infantryItems.size <= 4 || page * 3 >= infantryItems.size) {
            page = 0;
        }
        layout();
    }

    public static class BuildItem extends Group {

        Skin skin;
        Image icon, imgSupply, imgMoney, imgHighlight, iconSubtype;
        Label lblSupply, lblMoney;
        Prototypes.JsonProto proto;
        Group tooltip;

        public BuildItem(Prototypes.JsonProto proto, Skin skin) {
            this.proto = proto;
            this.skin = skin;

            imgHighlight = new Image(VOBGame.instance.getTextureManager().getSpriteFromAtlas("assets", "unit-highlight"));
//            imgHighlight.setScale(0.75f);

            icon = new Image(VOBGame.instance.getTextureManager().getSpriteFromAtlas("assets", proto.image));
            icon.setScale(0.75f);
            addActor(icon);

            if (proto.hasProperty("subtype")) {
                iconSubtype = new Image(VOBGame.instance.getTextureManager().getSpriteFromAtlas("assets", proto.getProperty("subtype").asString() + "-small"));
                addActor(iconSubtype);
                iconSubtype.setPosition(icon.getWidth() * 0.75f - iconSubtype.getWidth(), icon.getY());
            }

            tooltip = new Group();

            imgSupply = new Image(VOBGame.instance.getTextureManager().getSpriteFromAtlas("assets", "supply-small"));
            imgSupply.setPosition(0, 0);
            tooltip.addActor(imgSupply);

            lblSupply = new Label(Integer.toString(proto.food), skin, "small-font", new Color(0.8f, 0.8f, 0.8f, 1));
            lblSupply.setFontScale(VOBGame.SCALE);
            lblSupply.setPosition(imgSupply.getRight(), imgSupply.getY() + (imgSupply.getHeight() - lblSupply.getHeight()) * 0.5f);
            tooltip.addActor(lblSupply);

            imgMoney = new Image(VOBGame.instance.getTextureManager().getSpriteFromAtlas("assets", "money-small"));
            imgMoney.setPosition(imgSupply.getX(), imgSupply.getY() + imgSupply.getHeight() * imgSupply.getScaleY() + 1);
            tooltip.addActor(imgMoney);

            lblMoney = new Label(Integer.toString(proto.cost), skin, "small-font", new Color(0.8f, 0.8f, 0.8f, 1));
            lblMoney.setFontScale(VOBGame.SCALE);
            lblMoney.setPosition(imgMoney.getRight(), imgMoney.getY() + (imgMoney.getHeight() - lblMoney.getHeight()) * 0.5f);
            tooltip.addActor(lblMoney);

            setSize(icon.getWidth() * icon.getScaleX(), icon.getHeight() * icon.getScaleY());

            addListener(new InputListener() {
                @Override
                public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                    super.enter(event, x, y, pointer, fromActor);

                    addActor(tooltip);
                    tooltip.setPosition(x, y);
                }

                @Override
                public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                    super.exit(event, x, y, pointer, toActor);

                    removeActor(tooltip);
                }

                @Override
                public boolean mouseMoved(InputEvent event, float x, float y) {
//                    tooltip.setPosition(x, y);
                    return super.mouseMoved(event, x, y);
                }
            });
        }

        public void setCost(int cost) {
            lblMoney.setText(Integer.toString(cost));
        }

        public void setHighlight(boolean highlight) {
            if (highlight) {
                addActor(imgHighlight);
                imgHighlight.setZIndex(0);
            } else {
                imgHighlight.remove();
            }
        }
    }
}

