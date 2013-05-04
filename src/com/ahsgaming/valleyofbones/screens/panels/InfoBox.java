package com.ahsgaming.valleyofbones.screens.panels;

import com.ahsgaming.valleyofbones.units.Prototypes;
import com.badlogic.gdx.scenes.scene2d.Group;

import java.awt.*;

/**
 * Created with IntelliJ IDEA.
 * User: jami
 * Date: 5/3/13
 * Time: 5:53 PM
 * To change this template use File | Settings | File Templates.
 */
public class InfoBox extends Group {
    public static final String LOG = "InfoBox";

    Prototypes.JsonProto proto;

    Image iconFood;
    Image iconMoney;
    Label lblTitle;

    public InfoBox(Prototypes.JsonProto proto) {
        super();



        this.setProto(proto);

    }

    public void setProto(Prototypes.JsonProto proto) {

    }

}
