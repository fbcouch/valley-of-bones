package com.ahsgaming.valleyofbones.units.behavior;

import com.ahsgaming.valleyofbones.units.AbstractUnit;
import com.ahsgaming.valleyofbones.units.UnitManager;
import com.badlogic.gdx.utils.TimeUtils;

/**
 * valley-of-bones
 * (c) 2013 Jami Couch
 * User: jami
 * Date: 3/17/14
 * Time: 6:30 PM
 */
public class MindControlAbility implements AbilityBehavior, AttackBehavior {
    AbstractUnit unit;

    public MindControlAbility(AbstractUnit unit) {
        this.unit = unit;
    }

    @Override
    public void activateAbility() {
        if (!unit.getData().isMindControlUsed() && unit.getData().getMindControlUnit() != null) {
            AbstractUnit controlled = unit.getData().getMindControlUnit();
            controlled.setOwner(controlled.getOriginalOwner());
            // change: can't kill units, this just releases them from control
            unit.getData().setMindControlUsed(true);
            unit.getData().setMindControlUnit(null);
            unit.getData().setAttacksLeft(0);
            unit.getData().setModified(TimeUtils.millis());
        }
    }

    @Override
    public boolean attack(UnitManager unitManager, AbstractUnit defender) {
        if (!unit.getData().isMindControlUsed() && unit.getData().getMindControlUnit() == null && defender.getData().isControllable()) {
            unit.getData().setMindControlUnit(defender);
            unit.getData().setMindControlUsed(true);
            unit.getData().setAttacksLeft(0);

            defender.setOwner(unit.getOwner());
            defender.getData().setAttacksLeft(0);
            defender.getData().setMovesLeft(0);
            return true;
        }
        return false;
    }
}
