package com.ahsgaming.valleyofbones.network;

import com.ahsgaming.valleyofbones.GameController;
import com.ahsgaming.valleyofbones.units.AbstractUnit;

/**
 * valley-of-bones
 * (c) 2013 Jami Couch
 * User: jami
 * Date: 3/20/14
 * Time: 5:26 PM
 */
public class Heal extends ActionResetCommand {

    public int unit, target;

    @Override
    public boolean equals(Object o) {
        return o instanceof Heal && super.equals(o) && ((Heal)o).unit == unit && ((Heal)o).target == target;    //To change body of overridden methods use File | Settings | File Templates.
    }

    @Override
    public String toJson() {
        return "{ \"type\": \"Heal\", " + getJsonItems() + "}";
    }

    @Override
    protected String getJsonItems() {
        return super.getJsonItems() + String.format(", \"unit\": \"%d\", \"target\": \"%d\"", unit, target);
    }

    @Override
    public boolean validate(GameController gameController) {
        if (!super.validate(gameController)) return false;

        AbstractUnit healer = gameController.getUnitManager().getUnit(unit);
        AbstractUnit healed = gameController.getUnitManager().getUnit(target);

        return healer != null && healed != null
                && healer.getOwner().getPlayerId() == owner
                && gameController.getUnitManager().canHeal(healer, healed);
    }

    @Override
    public void execute(GameController gameController) {
        super.execute(gameController);
        AbstractUnit healer = gameController.getUnitManager().getUnit(unit);
        AbstractUnit healed = gameController.getUnitManager().getUnit(target);

        healer.getData().setAttacksLeft(healer.getData().getAttacksLeft() - 1);

        healer.heal(healed);
    }
}
