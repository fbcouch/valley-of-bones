package com.ahsgaming.valleyofbones.network;

import com.ahsgaming.valleyofbones.GameController;
import com.ahsgaming.valleyofbones.units.AbstractUnit;

public class Attack extends ActionResetCommand {
	public int unit;
	public int target;
	
	@Override
	public boolean equals(Object o) {
		return o instanceof Attack && super.equals(o) && ((Attack)o).unit == unit && ((Attack)o).target == target;
	}

    @Override
    public String toJson() {
        return "{ \"type\": \"Attack\", " + getJsonItems() + "}";
    }

    @Override
    protected String getJsonItems() {
        return super.getJsonItems() + String.format(", \"unit\": \"%d\", \"target\": \"%d\"", unit, target);
    }

    @Override
    public boolean validate(GameController gameController) {
        if (!super.validate(gameController)) return false;

        AbstractUnit attacker = gameController.getUnitManager().getUnit(unit);
        AbstractUnit defender = gameController.getUnitManager().getUnit(target);

        return attacker != null && defender != null
                && attacker.getData().getAttacksLeft() > 0
                && defender.getData().getCurHP() > 0
                && attacker.getOwner().getPlayerId() == owner
                && gameController.getUnitManager().canAttack(attacker, defender);
    }

    @Override
    public void execute(GameController gameController) {
        AbstractUnit attacker = gameController.getUnitManager().getUnit(unit);
        AbstractUnit defender = gameController.getUnitManager().getUnit(target);

        gameController.getUnitManager().attack(attacker, defender);
    }
}
