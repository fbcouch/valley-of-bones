package com.ahsgaming.valleyofbones.network;

import com.badlogic.gdx.math.Vector2;

public class Refund extends Command {
	public int unit;

	@Override
	public boolean equals(Object o) {
		if (o instanceof Refund && super.equals(o)) {
			Refund r = (Refund)o;
			return unit == r.unit;
		}
		return false;
	}
}
