package com.ahsgaming.valleyofbones.network;

public class Attack extends Command {
	public int unit;
	public int target;
	
	@Override
	public boolean equals(Object o) {
		return o instanceof Attack && super.equals(o) && ((Attack)o).unit == unit && ((Attack)o).target == target;
	}
}
