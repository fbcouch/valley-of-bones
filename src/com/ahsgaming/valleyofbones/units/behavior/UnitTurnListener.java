package com.ahsgaming.valleyofbones.units.behavior;

import com.ahsgaming.valleyofbones.units.AbstractUnit;
import com.ahsgaming.valleyofbones.units.TurnListener;
import com.ahsgaming.valleyofbones.units.UnitManager;

/**
 * valley-of-bones
 * (c) 2013 Jami Couch
 * User: jami
 * Date: 3/15/14
 * Time: 4:52 PM
 */
public abstract class UnitTurnListener implements TurnListener {
    AbstractUnit unit;

    public UnitTurnListener(AbstractUnit unit) {
        this.unit = unit;
    }

    public abstract void update(UnitManager unitManager, float delta);
}
