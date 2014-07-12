package com.ahsgaming.valleyofbones.units.behavior;

import com.ahsgaming.valleyofbones.units.AbstractUnit;
import com.ahsgaming.valleyofbones.units.UnitManager;
import com.badlogic.gdx.Gdx;

/**
 * valley-of-bones
 * (c) 2013 Jami Couch
 * User: jami
 * Date: 3/15/14
 * Time: 9:23 AM
 */
public class BasicAttack implements AttackBehavior {
    AbstractUnit unit;

    public BasicAttack(AbstractUnit unit) {
        this.unit = unit;
    }

    @Override
    public boolean attack(UnitManager unitManager, AbstractUnit defender) {
        unit.getData().setAttacksLeft(unit.getData().getAttacksLeft() - 1);
        float damage = unit.getData().getAttackDamage() * unit.getData().getBonus(defender.getData().getSubtype());
        defender.defend(damage);

        if (unit.getData().isStealthActive()) {
            unit.getData().setStealthActive(false);
            unit.getData().setInvisible(false);
        }

        unit.getView().attackAnim();
        return true;
    }
}
