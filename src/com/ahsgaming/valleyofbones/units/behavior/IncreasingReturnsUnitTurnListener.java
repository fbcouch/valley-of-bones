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
public class IncreasingReturnsUnitTurnListener extends UnitTurnListener {
    UnitTurnListener listener;

    public IncreasingReturnsUnitTurnListener(UnitTurnListener listener) {
        super(listener.unit);
        this.listener = listener;
    }

    @Override
    public void startTurn(int turn) {
        for (int i = 0; i < unit.getData().getUpkeep().size; i++) {
            if (turn % unit.getData().getAbilityArgs().get("interval") == 0) {
                unit.getData().getUpkeep().set(
                        i,
                        Math.max(
                                unit.getData().getAbilityArgs().get("max"),
                                unit.getData().getUpkeep().get(i) + unit.getData().getAbilityArgs().get("bonus")
                        )
                );
            }
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
