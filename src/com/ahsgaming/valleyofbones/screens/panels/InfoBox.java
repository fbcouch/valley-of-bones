package com.ahsgaming.valleyofbones.screens.panels;

import com.ahsgaming.valleyofbones.TextureManager;
import com.ahsgaming.valleyofbones.units.Prototypes;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;


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

    Image iconFood;
    Image iconMoney;
    Label lblTitle;
    Label lblFood;
    Label lblMoney;

    public InfoBox(Prototypes.JsonProto proto, Skin skin) {
        super();

        iconFood = new Image(TextureManager.getTexture("supply.png"));
        iconMoney = new Image(TextureManager.getTexture("money.png"));
        lblTitle = new Label(" ", skin, "medium");
        lblFood = new Label(String.format(FOOD, 0), skin, "medium");
        lblMoney = new Label(String.format(MONEY, 0), skin, "medium");

        this.addActor(iconFood);
        this.addActor(iconMoney);
        this.addActor(lblTitle);
        this.addActor(lblFood);
        this.addActor(lblMoney);

        this.setProto(proto);

    }

    public void setProto(Prototypes.JsonProto proto) {
        this.proto = proto;

        if (proto == null) return;

        lblTitle.setText(this.proto.title);

        int money = 0;
        if (this.proto.hasProperty("cost"))
            money = (int)Float.parseFloat(this.proto.getProperty("cost").toString());
        lblMoney.setText(String.format(MONEY, money));

        int food = 0;
        if (this.proto.hasProperty("food"))
            food = (int)Float.parseFloat(this.proto.getProperty("food").toString());
        lblFood.setText(String.format(FOOD, food));

        int y = 0;
        iconMoney.setPosition(0, y);
        lblMoney.setPosition(iconMoney.getRight(), y);
        if (iconMoney.getTop() > lblMoney.getTop()) y += iconMoney.getHeight(); else y += lblMoney.getHeight();

        iconFood.setPosition(0, y);
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
