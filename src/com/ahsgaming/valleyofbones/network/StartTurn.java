package com.ahsgaming.valleyofbones.network;

import com.ahsgaming.valleyofbones.GameController;

public class StartTurn extends Command {
	
	@Override
	public boolean equals(Object o) {
		return o instanceof StartTurn && super.equals(o);
	}

    @Override
    public void execute(GameController gameController) {

    }


}
