package com.ahsgaming.valleyofbones.network;

public class Upgrade extends Command {
	public int unit;
	public String upgrade;
	
	@Override
	public boolean equals(Object o) {
		return o instanceof Upgrade && super.equals(o) && ((Upgrade)o).unit == unit && ((Upgrade)o).upgrade.equals(upgrade);
	}
}
