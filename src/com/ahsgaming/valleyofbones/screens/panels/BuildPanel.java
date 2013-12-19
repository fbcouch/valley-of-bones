package com.ahsgaming.valleyofbones.screens.panels;

import com.ahsgaming.valleyofbones.GameController;
import com.ahsgaming.valleyofbones.Player;
import com.ahsgaming.valleyofbones.VOBGame;
import com.ahsgaming.valleyofbones.screens.LevelScreen;
import com.ahsgaming.valleyofbones.units.Prototypes;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
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

    Image imgBackground, imgInfantryTab, imgMechTab;
    Array<BuildItem> infantryItems, mechItems;
    Array<Prototypes.JsonProto> itemProtos;

    int selected;

    public BuildPanel(GameController controller, Player player, LevelScreen lvlScreen) {
        this.gController = controller;
        this.player = player;
        this.levelScreen = lvlScreen;
        skin = levelScreen.getSkin();

        imgBackground = new Image(VOBGame.instance.getTextureManager().getSpriteFromAtlas("assets", "build-hud-bg"));
        addActor(imgBackground);

        imgInfantryTab = new Image(VOBGame.instance.getTextureManager().getSpriteFromAtlas("assets", "build-infantry-tab"));
        addActor(imgInfantryTab);
        imgInfantryTab.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);    //To change body of overridden methods use File | Settings | File Templates.

                select(0);
            }
        });

        imgMechTab = new Image(VOBGame.instance.getTextureManager().getSpriteFromAtlas("assets", "build-mech-tab"));
        addActor(imgMechTab);
        imgMechTab.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);    //To change body of overridden methods use File | Settings | File Templates.

                select(1);
            }
        });

        itemProtos = Prototypes.getPlayerCanBuild();
        infantryItems = new Array<BuildItem>();
        mechItems = new Array<BuildItem>();

        for (Prototypes.JsonProto jp: itemProtos) {
            final BuildItem item = new BuildItem(jp, skin);

            String type = jp.getProperty("subtype").asString();
            if (type.equals("armored")) {
                mechItems.add(item);
            } else {
                infantryItems.add(item);
            }

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
        imgInfantryTab.setPosition(-15, imgBackground.getTop() - 4);
        imgMechTab.setPosition(imgInfantryTab.getRight() - 25, imgInfantryTab.getY());
        int y = 150;
        switch(selected) {
            default:
            case 0:
                imgInfantryTab.setZIndex(2);
                imgMechTab.setZIndex(0);
                for (Group g: mechItems) g.remove();

                for (Group g: infantryItems) {
                    addActor(g);
                    g.setPosition(5, y);
                    y -= g.getHeight();
                }
                break;
            case 1:
                imgInfantryTab.setZIndex(0);
                imgMechTab.setZIndex(2);
                for (Group g: infantryItems) g.remove();

                for (Group g: mechItems) {
                    addActor(g);
                    g.setPosition(5, y);
                    y -= g.getHeight();
                }
                break;
        }

        setSize(imgBackground.getRight(), Math.max(imgInfantryTab.getTop(), imgMechTab.getTop()));
    }

    public void update() {
        Array<BuildItem> items;
        switch(selected) {
            default:
            case 0:
                items = infantryItems;
                break;
            case 1:
                items = mechItems;
                break;
        }

        for (BuildItem item: items) {
            if (player == null || player.canBuild(item.proto.id, gController)) {
                item.icon.setColor(1, 1, 1, 1);
            } else {
                item.icon.setColor(0.8f, 0.4f, 0.4f, 1);
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

    public static class BuildItem extends Group {

        Skin skin;
        Image icon, imgSupply, imgMoney, imgHiglight;
        Label lblSupply, lblMoney;
        Prototypes.JsonProto proto;

        public BuildItem(Prototypes.JsonProto proto, Skin skin) {
            this.proto = proto;
            this.skin = skin;

            imgHiglight = new Image(VOBGame.instance.getTextureManager().getSpriteFromAtlas("assets", "unit-highlight"));
            imgHiglight.setScale(0.75f);

            icon = new Image(VOBGame.instance.getTextureManager().getSpriteFromAtlas("assets", proto.image));
            icon.setScale(0.75f);
            addActor(icon);

            imgSupply = new Image(VOBGame.instance.getTextureManager().getSpriteFromAtlas("assets", "supply"));
            imgSupply.setScale(20 / imgSupply.getWidth());
            imgSupply.setPosition(icon.getX() + icon.getWidth() * icon.getScaleX(), 0);
            addActor(imgSupply);

            lblSupply = new Label(Integer.toString(proto.food), skin, "small-font", new Color(0.8f, 0.8f, 0.8f, 1));
            lblSupply.setPosition(imgSupply.getRight(), imgSupply.getY());
            addActor(lblSupply);

            imgMoney = new Image(VOBGame.instance.getTextureManager().getSpriteFromAtlas("assets", "money"));
            imgMoney.setScale(20 / imgMoney.getWidth());
            imgMoney.setPosition(imgSupply.getX(), imgSupply.getY() + imgSupply.getHeight() * imgSupply.getScaleY() + 1);
            addActor(imgMoney);

            lblMoney = new Label(Integer.toString(proto.cost), skin, "small-font", new Color(0.8f, 0.8f, 0.8f, 1));
            lblMoney.setPosition(imgMoney.getRight(), imgMoney.getY());
            addActor(lblMoney);

            setSize(Math.max(lblSupply.getRight(), lblMoney.getRight()), icon.getHeight() * icon.getScaleY());
        }

        public void setHighlight(boolean highlight) {
            if (highlight) {
                addActor(imgHiglight);
                imgHiglight.setZIndex(0);
            } else {
                imgHiglight.remove();
            }
        }
    }
}

