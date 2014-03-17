package com.ahsgaming.valleyofbones.units.behavior;

import com.ahsgaming.valleyofbones.screens.LevelScreen;
import com.ahsgaming.valleyofbones.units.AbstractUnit;

/**
 * valley-of-bones
 * (c) 2013 Jami Couch
 * User: jami
 * Date: 3/15/14
 * Time: 9:30 AM
 */
public class BasicDefend implements DefendBehavior {
    AbstractUnit unit;

    public BasicDefend(AbstractUnit unit) {
        this.unit = unit;
    }

    @Override
    public void defend(float damage) {
        damage -= unit.getData().getArmor();
        if (damage > 0) {
            unit.getData().setCurHP((int)(unit.getData().getCurHP() - damage));

            if (LevelScreen.getInstance() != null) {
                LevelScreen.getInstance().addFloatingLabel(String.format("-%d", (int)damage), unit.getView().getX() + unit.getView().getWidth() * 0.5f, unit.getView().getY() + unit.getView().getHeight() * 0.5f);
            }

            unit.getView().damagedAnim();
        }
    }
}
