package com.ahsgaming.valleyofbones.units.behavior;

import com.ahsgaming.valleyofbones.units.AbstractUnit;
import com.ahsgaming.valleyofbones.units.UnitManager;

/**
 * Created by jami on 7/12/14.
 */
public class SabotageTurnListener extends UnitTurnListener {
    UnitTurnListener listener;

    public SabotageTurnListener(UnitTurnListener listener) {
        super(listener.unit);
        this.listener = listener;
    }

    @Override
    public void update(UnitManager unitManager, float delta) {
        listener.update(unitManager, delta);
    }

    @Override
    public void startTurn(int turn) {
        listener.startTurn(turn);
        unit.getData().setInvisible(true);
    }

    @Override
    public void endTurn(int turn) {
        listener.endTurn(turn);
    }
}
