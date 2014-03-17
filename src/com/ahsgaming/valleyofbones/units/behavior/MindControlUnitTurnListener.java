package com.ahsgaming.valleyofbones.units.behavior;

import com.ahsgaming.valleyofbones.units.AbstractUnit;
import com.ahsgaming.valleyofbones.units.UnitManager;

/**
 * valley-of-bones
 * (c) 2013 Jami Couch
 * User: jami
 * Date: 3/17/14
 * Time: 5:24 PM
 */
public class MindControlUnitTurnListener extends UnitTurnListener {
    UnitTurnListener listener;

    public MindControlUnitTurnListener(UnitTurnListener listener) {
        super(listener.unit);
        this.listener = listener;
    }

    @Override
    public void update(UnitManager unitManager, float delta) {
        if (unit.getData().getCurHP() <= 0) {
            if (unit.getData().getMindControlUnit() != null && unit.getData().getMindControlUnit().getData().getCurHP() > 0) {
                AbstractUnit controlled = unit.getData().getMindControlUnit();
                controlled.setOwner(controlled.getOriginalOwner());
                controlled.getData().setAttacksLeft(0);
                controlled.getData().setMovesLeft(0);
            }
        }

        listener.update(unitManager, delta);
    }

    @Override
    public void startTurn(int turn) {
        if (unit.getData().getMindControlUnit() != null) {
            if (!unit.getData().getMindControlUnit().getData().isAlive()) {
                unit.getData().setMindControlUnit(null);
            }
        }

        listener.startTurn(turn);
    }

    @Override
    public void endTurn(int turn) {
        listener.endTurn(turn);
    }
}
