package com.ahsgaming.valleyofbones.network;

import com.ahsgaming.valleyofbones.GameController;

public class Unpause extends Command {
	
	@Override
	public boolean equals(Object o) {
		return o instanceof Unpause && super.equals(o);
	}

    @Override
    public boolean validate(GameController gameController) {
        return true;
    }

    @Override
    public void execute(GameController gameController) {
        gameController.executeUnpause(this);
    }
}
