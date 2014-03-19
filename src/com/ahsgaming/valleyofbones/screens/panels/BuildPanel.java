package com.ahsgaming.valleyofbones.screens.panels;

import com.ahsgaming.valleyofbones.GameController;
import com.ahsgaming.valleyofbones.Player;
import com.ahsgaming.valleyofbones.VOBGame;
import com.ahsgaming.valleyofbones.screens.LevelScreen;
import com.ahsgaming.valleyofbones.units.Prototypes;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.*;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.JsonValue;

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

    Array<BuildItem> items, allItems;
    Array<Prototypes.JsonProto> itemProtos;
    Image imgNext;

    int selected, page;

    public BuildPanel(GameController controller, Player player, LevelScreen lvlScreen) {
        this.gController = controller;
        this.player = player;
        this.levelScreen = lvlScreen;
        skin = levelScreen.getSkin();

        imgNext = new Image(VOBGame.instance.getTextureManager().getSpriteFromAtlas("assets", "next-button"));
        imgNext.setScale(0.75f);
        imgNext.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);

                next();
            }
        });

        itemProtos = Prototypes.getPlayerCanBuild((player != null ? player.getRace() : "terran"));
        items = new Array<BuildItem>();

        for (Prototypes.JsonProto jp: itemProtos) {
            final BuildItem item = new BuildItem(jp, skin);

            items.add(item);

            item.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    super.clicked(event, x, y);    //To change body of overridden methods use File | Settings | File Templates.

                    click(item);
                }

                @Override
                public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                    item.showTooltip(x, y);
                }

                @Override
                public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                    item.hideTooltip();
                }
            });
        }

        layout();
    }

    public void layout() {
        float y = imgNext.getHeight() * imgNext.getScaleY();

        for (Group g: items) g.remove();
        imgNext.remove();

        int start = page * 3, end = (items.size <= 4 ? 4 : Math.min(items.size, start + 3));
        for (int i = start; i < end; i++) {
            Group g = items.get(i);
            addActor(g);
            g.setPosition(0, y);
            y += g.getHeight();
        }

        if (items.size > 4) {
            addActor(imgNext);
            imgNext.setPosition(0, 0);
        }

        setSize(items.get(end - 1).getWidth(), items.get(end - 1).getTop());
    }

    public void update() {
        for (BuildItem item: items) {
            if (player == null || player.canBuild(item.proto.id, gController)) {
                item.icon.setColor(1, 1, 1, 1);
            } else {
                item.icon.setColor(0.8f, 0.4f, 0.4f, 1);
            }
            if (player != null) {
                item.setCost(player.getProtoCost(item.proto, gController));
            }
            item.setHighlight(levelScreen.isBuildMode() && levelScreen.getBuildProto() == item.proto, levelScreen.isCurrentPlayer() && gController.getCurrentPlayer().canBuild(item.proto.id, gController));
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
        if (items.size <= 4 || page * 3 >= items.size) {
            page = 0;
        }
        layout();
    }

    public static class BuildItem extends Group {

        Skin skin;
        Image icon, imgSupply, imgMoney, imgHighlight, iconSubtype, imgBackground;
        Label lblSupply, lblMoney;
        Prototypes.JsonProto proto;
        Group tooltip;

        public BuildItem(Prototypes.JsonProto proto, Skin skin) {
            this.proto = proto;
            this.skin = skin;

            imgBackground = new Image(VOBGame.instance.getTextureManager().getSpriteFromAtlas("assets", "button-bg"));
            addActor(imgBackground);

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
        }

        public void showTooltip(float x, float y) {
            addActor(tooltip);
            tooltip.setPosition(imgBackground.getWidth() + 2 * VOBGame.SCALE, 3 * VOBGame.SCALE);
        }

        public void hideTooltip() {
            removeActor(tooltip);
        }

        public void setCost(int cost) {
            lblMoney.setText(Integer.toString(cost));
        }

        public void setHighlight(boolean highlight, boolean canBuild) {
            if (highlight) {
                if (canBuild) {
                    imgHighlight.setColor(1f, 1, 1, 1);
                } else {
                    imgHighlight.setColor(0.8f, 0, 0, 1);
                }
                addActor(imgHighlight);
                imgHighlight.setZIndex(1);
            } else {
                imgHighlight.remove();
            }
        }
    }
}

