package com.ahsgaming.valleyofbones.network;

public class Pause extends Command {
	public boolean isAuto;
	
	@Override
	public boolean equals(Object o) {
		return o instanceof Unpause && super.equals(o) && ((Pause)o).isAuto == isAuto;
	}
}
