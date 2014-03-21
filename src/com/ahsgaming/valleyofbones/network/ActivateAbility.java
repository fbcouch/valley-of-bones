package com.ahsgaming.valleyofbones.network;

import com.ahsgaming.valleyofbones.GameController;
import com.ahsgaming.valleyofbones.units.AbstractUnit;

/**
 * Created with IntelliJ IDEA.
 * User: jami
 * Date: 5/23/13
 * Time: 12:29 AM
 * To change this template use File | Settings | File Templates.
 */
public class ActivateAbility extends ActionResetCommand {
    public int unit;

    @Override
    public boolean equals(Object o) {
        return o instanceof ActivateAbility && super.equals(o) && ((ActivateAbility)o).unit == unit;
    }

    @Override
    public boolean validate(GameController gameController) {
        if (!super.validate(gameController)) return false;

        AbstractUnit u = gameController.getUnitManager().getUnit(unit);
        return (u.getOwner() != null && u.getOwner().getPlayerId() == owner);
    }

    @Override
    public void execute(GameController gameController) {
        super.execute(gameController);
        gameController.getUnitManager().activateAbility(gameController.getUnitManager().getUnit(unit));
    }
}
