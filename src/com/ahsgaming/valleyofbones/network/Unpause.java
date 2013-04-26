package com.ahsgaming.valleyofbones.network;

public class Unpause extends Command {
	
	@Override
	public boolean equals(Object o) {
		return o instanceof Unpause && super.equals(o);
	}
}
