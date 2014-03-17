package com.ahsgaming.valleyofbones.units.behavior;

import com.ahsgaming.valleyofbones.units.AbstractUnit;
import com.badlogic.gdx.utils.TimeUtils;

/**
 * valley-of-bones
 * (c) 2013 Jami Couch
 * User: jami
 * Date: 3/15/14
 * Time: 4:18 PM
 */
public class StealthAbility implements AbilityBehavior {
    AbstractUnit unit;

    public StealthAbility(AbstractUnit unit) {
        this.unit = unit;
    }

    @Override
    public void activateAbility() {
        if (!unit.getData().isStealthActive() &&
                (unit.getData().isStealthEntered()
                || unit.getData().getAttacksLeft() != unit.getData().getAttackSpeed()
                || unit.getData().getMovesThisTurn() > Math.floor(unit.getData().getMoveSpeed() * 0.5f))
                ) {
            return;
        }

        if (!unit.getData().isBuilding()) {
            unit.getData().setStealthActive(!unit.getData().isStealthActive());
            if (unit.getData().isStealthActive()) {
                unit.getData().setStealthEntered(true);
                unit.getData().setInvisible(true);
                unit.getData().setMovesLeft((float)Math.floor(unit.getData().getMoveSpeed() * 0.5f) - unit.getData().getMovesThisTurn());
                if (unit.getData().getMovesLeft() < 0) unit.getData().setMovesLeft(0);
            } else {
                unit.getData().setMovesLeft(unit.getData().getMoveSpeed() - unit.getData().getMovesThisTurn());
                unit.getData().setInvisible(false);
            }
            unit.getData().setModified(TimeUtils.millis());
        }
    }
}
