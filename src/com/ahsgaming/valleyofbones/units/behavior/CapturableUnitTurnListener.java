package com.ahsgaming.valleyofbones.units.behavior;

import com.ahsgaming.valleyofbones.Player;
import com.ahsgaming.valleyofbones.map.HexMap;
import com.ahsgaming.valleyofbones.units.AbstractUnit;
import com.ahsgaming.valleyofbones.units.UnitManager;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.TimeUtils;

/**
 * valley-of-bones
 * (c) 2013 Jami Couch
 * User: jami
 * Date: 3/17/14
 * Time: 5:15 PM
 */
public class CapturableUnitTurnListener extends UnitTurnListener {
    UnitTurnListener listener;

    public CapturableUnitTurnListener(UnitTurnListener listener) {
        super(listener.unit);
        this.listener = listener;
    }

    @Override
    public void update(UnitManager unitManager, float delta) {
        findNewOwner(unitManager);

        if (unit.getData().getCurHP() <= 0) {
            unit.getData().setCurHP(0);
            if (unit.getOwner() != unit.getData().getUncontested()) {
                unit.getData().setModified(TimeUtils.millis());
            }
            unit.setOwner(unit.getData().getUncontested());
            unit.getData().setAttacksLeft(0);
            unit.getData().setMovesLeft(0);
        }

        listener.update(unitManager, delta);
    }

    @Override
    public void startTurn(int turn) {
        listener.startTurn(turn);
    }

    @Override
    public void endTurn(int turn) {
        listener.endTurn(turn);
    }

    void findNewOwner(UnitManager unitManager) {
        Player p = null;
        int capUnitCount = 0;
        unit.getData().setCapUnitCount(0);

        Vector2[] adjacent = HexMap.getAdjacent(unit.getView().getBoardPosition());
        for (AbstractUnit u: unitManager.getUnitsInArea(unit.getView().getBoardPosition(), 1)) {
            if (u == unit || u.getOwner() == null || u.getData().isBuilding())
                continue;

            boolean adj = false;
            for (Vector2 pos: adjacent) {
                if (pos.epsilonEquals(u.getView().getBoardPosition(), 0.1f)) {
                    adj = true;
                    break;
                }
            }
            if (!adj)
                continue;

            if (p == null || p == u.getOwner()) {
                p = u.getOwner();
                capUnitCount++;
            } else {
                unit.getData().setUncontested(null);
                unit.getData().setCapUnitCount(0);
                return;
            }
        }
        unit.getData().setUncontested(p);
        unit.getData().setCapUnitCount(capUnitCount);
    }
}
