package com.ahsgaming.valleyofbones.units.behavior;

import com.ahsgaming.valleyofbones.units.AbstractUnit;
import com.ahsgaming.valleyofbones.units.UnitManager;

/**
 * valley-of-bones
 * (c) 2013 Jami Couch
 * User: jami
 * Date: 3/15/14
 * Time: 3:54 PM
 */
public class SabotageAttack implements AttackBehavior {

    AbstractUnit unit;

    public SabotageAttack(AbstractUnit unit) {
        this.unit = unit;
    }

    @Override
    public boolean attack(UnitManager unitManager, AbstractUnit defender) {
        if (defender.getData().getProtoId().equals("castle-base")) return false;

        unit.getData().setAttacksLeft(unit.getData().getAttacksLeft() - 1);
        float damage = 0;
        if (defender.getData().getSubtype().equals("light") || defender.getData().getSubtype().equals("building")) {
            damage = defender.getData().getCurHP() + defender.getData().getArmor();
        } else {
            damage = (float) Math.floor(defender.getData().getCurHP() * 0.5) + defender.getData().getArmor();
        }

        defender.defend(damage);

        unit.getData().setInvisible(false);

        unit.getView().attackAnim();
        return true;
    }
}
