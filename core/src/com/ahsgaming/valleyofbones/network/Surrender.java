package com.ahsgaming.valleyofbones.network;

import com.ahsgaming.valleyofbones.GameController;
import com.ahsgaming.valleyofbones.Player;

public class Surrender extends Command {
	
	@Override
	public boolean equals(Object o) {
		return o instanceof Surrender && super.equals(o);
	}

    @Override
    public String toJson() {
        return "{ \"type\": \"Surrender\", " + getJsonItems() + "}";
    }

    @Override
    public boolean validate(GameController gameController) {
        return true;
    }

    @Override
    public void execute(GameController gameController) {
        Player p = gameController.getPlayerById(owner);
        p.getBaseUnit().defend(p.getBaseUnit().getData().getCurHP() + p.getBaseUnit().getData().getArmor());
    }
}
