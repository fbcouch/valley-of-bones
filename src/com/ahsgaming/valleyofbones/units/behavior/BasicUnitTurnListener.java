package com.ahsgaming.valleyofbones.units.behavior;

import com.ahsgaming.valleyofbones.units.AbstractUnit;
import com.ahsgaming.valleyofbones.units.UnitManager;
import com.badlogic.gdx.utils.TimeUtils;

/**
 * valley-of-bones
 * (c) 2013 Jami Couch
 * User: jami
 * Date: 3/15/14
 * Time: 4:58 PM
 */
public class BasicUnitTurnListener extends UnitTurnListener {
    public BasicUnitTurnListener(AbstractUnit unit) {
        super(unit);
    }

    @Override
    public void startTurn(int turn) {
        if (!unit.getData().isBuilding()) {
            unit.getData().setMovesLeft((unit.getData().getMovesLeft() % 1) + unit.getData().getMoveSpeed() * (unit.getData().isStealthActive() ? 0.5f : 1f));
            unit.getData().setMovesThisTurn(0);
            unit.getData().setAttacksLeft((unit.getData().getAttacksLeft() % 1) + unit.getData().getAttackSpeed());
        }

        unit.getView().clearPath();
        unit.getView().addToPath(unit.getView().getBoardPosition());

        unit.getData().setStealthEntered(false);
        unit.getData().setMindControlUsed(false);
        unit.getData().setVirginUnit(false);

        unit.getData().setModified(TimeUtils.millis());
    }

    @Override
    public void endTurn(int turn) {
        if (unit.getData().isBuilding()) {
            unit.getData().setBuildTimeLeft(unit.getData().getBuildTime() - 1);

            if (unit.getData().getBuildTimeLeft() <= 0) {
                unit.getData().setBuilding(false);
            }

            unit.getData().setModified(TimeUtils.millis());
        }
    }

    @Override
    public void update(UnitManager unitManager, float delta) {
        unit.getView().act(delta);
    }
}
