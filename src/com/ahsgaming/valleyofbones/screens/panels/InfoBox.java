package com.ahsgaming.valleyofbones.screens.panels;

import com.ahsgaming.valleyofbones.TextureManager;
import com.ahsgaming.valleyofbones.units.Prototypes;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Array;


/**
 * Created with IntelliJ IDEA.
 * User: jami
 * Date: 5/3/13
 * Time: 5:53 PM
 * To change this template use File | Settings | File Templates.
 */
public class InfoBox extends Group {
    public static final String LOG = "InfoBox";

    final String FOOD = "%02d";
    final String MONEY = "%04d";

    Prototypes.JsonProto proto;

    Skin skin;

    Image iconHealth, iconAttack, iconRange, iconSpeed, iconArmor;
    Label lblHealth, lblAttack, lblRange, lblSpeed, lblArmor;

    Image iconFood;
    Image iconMoney;
    Label lblTitle;
    Label lblFood;
    Label lblMoney;

    public InfoBox(Prototypes.JsonProto proto, Skin skin) {
        super();

        iconFood = new Image(TextureManager.getSpriteFromAtlas("assets", "supply"));
        iconMoney = new Image(TextureManager.getSpriteFromAtlas("assets", "money"));
        iconHealth = new Image(TextureManager.getSpriteFromAtlas("assets", "hospital-cross"));
        iconAttack = new Image(TextureManager.getSpriteFromAtlas("assets", "crossed-swords"));
        iconRange = new Image(TextureManager.getSpriteFromAtlas("assets", "archery-target"));
        iconSpeed = new Image(TextureManager.getSpriteFromAtlas("assets", "radial-balance"));
        iconArmor = new Image(TextureManager.getSpriteFromAtlas("assets", "checked-shield"));
        lblTitle = new Label(" ", skin, "medium");
        lblFood = new Label(String.format(FOOD, 0), skin, "medium");
        lblMoney = new Label(String.format(MONEY, 0), skin, "medium");
        lblHealth = new Label(" ", skin, "medium");
        lblAttack = new Label(String.format("%d", 0), skin, "medium");
        lblRange = new Label(String.format("%d", 0), skin, "medium");
        lblSpeed = new Label(String.format("%d", 0), skin, "medium");
        lblArmor = new Label(String.format("%d", 0), skin, "medium");

        this.addActor(iconFood);
        this.addActor(iconMoney);
        this.addActor(iconHealth);
        this.addActor(iconAttack);
        this.addActor(iconRange);
        this.addActor(iconSpeed);
        this.addActor(iconArmor);
        this.addActor(lblTitle);
        this.addActor(lblFood);
        this.addActor(lblMoney);
        this.addActor(lblHealth);
        this.addActor(lblAttack);
        this.addActor(lblRange);
        this.addActor(lblSpeed);
        this.addActor(lblArmor);

        this.setProto(proto);
    }

    public void setProto(Prototypes.JsonProto proto) {
        this.proto = proto;

        if (proto == null) return;

        lblTitle.setText(this.proto.title);

        int money = 0;
        if (this.proto.hasProperty("cost"))
            money = this.proto.getProperty("cost").asInt();
        lblMoney.setText(String.format(MONEY, money));

        int food = 0;
        if (this.proto.hasProperty("food"))
            food = this.proto.getProperty("food").asInt();
        lblFood.setText(String.format(FOOD, food));

        int maxhp = 0;
        if (this.proto.hasProperty("maxhp"))
            maxhp = this.proto.getProperty("maxhp").asInt();
        lblHealth.setText(String.format("%d", maxhp));

        int attack = 0;
        if (this.proto.hasProperty("attackdamage"))
            attack = this.proto.getProperty("attackdamage").asInt();
        lblAttack.setText(String.format("%d", attack));

        int range = 0;
        if (this.proto.hasProperty("attackrange"))
            range = this.proto.getProperty("attackrange").asInt();
        lblRange.setText(String.format("%d", range));

        int speed = 0;
        if (this.proto.hasProperty("movespeed"))
            speed = this.proto.getProperty("movespeed").asInt();
        lblSpeed.setText(String.format("%d", speed));

        int armor = 0;
        if (this.proto.hasProperty("armor"))
            range = this.proto.getProperty("armor").asInt();
        lblArmor.setText(String.format("%d", range));

        Array<Label> labels = new Array<Label>();
        labels.add(lblFood);
        labels.add(lblMoney);
        labels.add(lblHealth);
        labels.add(lblAttack);
        labels.add(lblRange);
        labels.add(lblSpeed);
        labels.add(lblArmor);

        for (Label lbl: labels) {
            lbl.invalidate();
            lbl.layout();
            lbl.setSize(lbl.getPrefWidth(), lbl.getPrefHeight());
        }

        int y = 0;
        iconSpeed.setPosition(0, y);
        lblSpeed.setPosition(iconSpeed.getRight(), y);

        iconArmor.setPosition(lblSpeed.getRight(), y);
        lblArmor.setPosition(iconArmor.getRight(), y);
        if (iconArmor.getHeight() > lblArmor.getHeight()) y += iconArmor.getHeight(); else y += lblArmor.getHeight();

        iconHealth.setPosition(0, y);
        lblHealth.setPosition(iconHealth.getRight(), y);

        iconAttack.setPosition(lblHealth.getRight(), y);
        lblAttack.setPosition(iconAttack.getRight(), y);

        iconRange.setPosition(lblAttack.getRight(), y);
        lblRange.setPosition(iconRange.getRight(), y);
        if (iconRange.getHeight() > lblRange.getHeight()) y += iconRange.getHeight(); else y += lblRange.getHeight();

        iconMoney.setPosition(0, y);
        lblMoney.setPosition(iconMoney.getRight(), y);

        iconFood.setPosition(lblMoney.getRight(), y);
        lblFood.setPosition(iconFood.getRight(), y);
        if (iconFood.getHeight() > lblFood.getHeight()) y += iconFood.getHeight(); else y += lblFood.getHeight();

        lblTitle.setPosition(0, y);
        y += lblTitle.getHeight();

        float x = lblTitle.getRight();
        if (lblMoney.getRight() > x) x = lblMoney.getRight();
        if (lblFood.getRight() > x) x = lblFood.getRight();

        setSize(x, y);
    }

}
