package com.ahsgaming.valleyofbones.network;

import com.ahsgaming.valleyofbones.GameController;

public class Pause extends Command {
	public boolean isAuto;
	
	@Override
	public boolean equals(Object o) {
		return o instanceof Unpause && super.equals(o) && ((Pause)o).isAuto == isAuto;
	}

    @Override
    public boolean validate(GameController gameController) {
        return isAuto || owner == -1 || gameController.getMaxPauses() == 0 || gameController.getPlayerById(owner).getPauses() < gameController.getMaxPauses();
    }

    @Override
    public void execute(GameController gameController) {
        gameController.executePause(this);
    }
}
