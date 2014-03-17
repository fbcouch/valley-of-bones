package com.ahsgaming.valleyofbones.units.behavior;

import com.ahsgaming.valleyofbones.units.AbstractUnit;

/**
 * valley-of-bones
 * (c) 2013 Jami Couch
 * User: jami
 * Date: 3/15/14
 * Time: 10:08 AM
 */
public class NoAttack implements AttackBehavior {
    @Override
    public boolean attack(AbstractUnit unit) {
        return false;
    }
}
