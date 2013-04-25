package com.ahsgaming.valleyofbones.network;

import com.badlogic.gdx.math.Vector2;

public class Build extends Command {
	public String building;
	public Vector2 location;
	
	@Override
	public boolean equals(Object o) {
		if (o instanceof Build && super.equals(o)) {
			Build b = (Build)o;
			return (b.building == building && b.location.epsilonEquals(location, 0.01f));
		}
		return false;
	}
}
