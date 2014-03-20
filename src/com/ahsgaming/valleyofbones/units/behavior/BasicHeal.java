package com.ahsgaming.valleyofbones.units.behavior;

import com.ahsgaming.valleyofbones.units.AbstractUnit;
import com.ahsgaming.valleyofbones.units.UnitManager;

/**
 * valley-of-bones
 * (c) 2013 Jami Couch
 * User: jami
 * Date: 3/15/14
 * Time: 9:23 AM
 */
public class BasicHeal implements HealBehavior {
    AbstractUnit unit;

    public BasicHeal(AbstractUnit unit) {
        this.unit = unit;
    }

    @Override
    public void heal(AbstractUnit target) {
        target.getData().setCurHP(
                Math.min(
                        target.getData().getMaxHP(),
                        target.getData().getCurHP() + unit.getData().getHeal() - target.getData().getArmor()
                ));

        target.getView().healedAnim();
        unit.getView().attackAnim();
    }
}
