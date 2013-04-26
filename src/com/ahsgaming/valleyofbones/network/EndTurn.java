package com.ahsgaming.valleyofbones.network;

public class EndTurn extends Command {

	@Override
	public boolean equals(Object o) {
		return o instanceof EndTurn && super.equals(o);
	}
}
