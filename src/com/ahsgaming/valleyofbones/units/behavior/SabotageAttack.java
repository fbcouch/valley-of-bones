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
    public boolean attack(UnitManager unitManager, AbstractUnit unit) {
        return false; // TODO
    }
}
