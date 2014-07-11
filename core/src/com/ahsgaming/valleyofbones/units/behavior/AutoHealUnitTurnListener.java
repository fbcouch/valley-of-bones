package com.ahsgaming.valleyofbones.units.behavior;

import com.ahsgaming.valleyofbones.units.AbstractUnit;
import com.ahsgaming.valleyofbones.units.UnitManager;

/**
 * valley-of-bones
 * (c) 2013 Jami Couch
 * User: jami
 * Date: 3/15/14
 * Time: 3:51 PM
 */
public class AutoHealUnitTurnListener extends UnitTurnListener {
    UnitTurnListener listener;

    public AutoHealUnitTurnListener(UnitTurnListener listener) {
        super(listener.unit);
        this.listener = listener;
    }

    @Override
    public void startTurn(int turn) {
        if (unit.getData().getCurHP() > 0) {
            unit.getData().setCurHP(
                    Math.min(
                            unit.getData().getMaxHP(),
                            unit.getData().getCurHP() + unit.getData().getAutoheal()
                        )
            );
        }

        listener.startTurn(turn);
    }

    @Override
    public void endTurn(int turn) {
        listener.endTurn(turn);
    }

    @Override
    public void update(UnitManager unitManager, float delta) {
        listener.update(unitManager, delta);
    }
}
