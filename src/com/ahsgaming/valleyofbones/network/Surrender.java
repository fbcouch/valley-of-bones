package com.ahsgaming.valleyofbones.network;

public class Surrender extends Command {
	
	@Override
	public boolean equals(Object o) {
		return o instanceof Surrender && super.equals(o);
	}
}
