package com.ahsgaming.valleyofbones.units.behavior;

import com.ahsgaming.valleyofbones.units.AbstractUnit;
import com.ahsgaming.valleyofbones.units.UnitManager;

/**
 * valley-of-bones
 * (c) 2013 Jami Couch
 * User: jami
 * Date: 3/17/14
 * Time: 6:04 PM
 */
public class SplashAttack implements AttackBehavior {
    BasicAttack attack;

    public SplashAttack(BasicAttack attack) {
        this.attack = attack;
    }

    @Override
    public boolean attack(UnitManager unitManager, AbstractUnit defender) {
        if (attack.attack(unitManager, defender)) {
            for (AbstractUnit u: unitManager.getUnitsInArea(defender.getView().getBoardPosition(), 1)) {
                float damage = attack.unit.getData().getAttackDamage() * attack.unit.getData().getBonus(defender.getData().getSubtype()) * attack.unit.getData().getSplashDamage();
                if (u != defender) {
                    u.defend(damage);
                }
            }
            return true;
        }
        return false;
    }
}
