package com.ahsgaming.valleyofbones.network;

public class StartTurn extends Command {
	
	@Override
	public boolean equals(Object o) {
		return o instanceof StartTurn && super.equals(o);
	}
}
