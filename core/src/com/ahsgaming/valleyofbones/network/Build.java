package com.ahsgaming.valleyofbones.network;

import com.badlogic.gdx.math.Vector2;

public class Build extends Command {
	public String building;
	public Vector2 location;
    public int unitId = -1;
	
	@Override
	public boolean equals(Object o) {
		if (o instanceof Build && super.equals(o)) {
			Build b = (Build)o;
			return (b.building == building && b.location.epsilonEquals(location, 0.01f));
		}
		return false;
	}

    @Override
    public String toJson() {
        return "{ \"type\": \"Build\", " + getJsonItems() + "}";
    }

    @Override
    protected String getJsonItems() {
        return super.getJsonItems() + String.format(", \"building\": \"%s\", \"location\": \"%s\", \"id\": %d", building, location.toString(), unitId);
    }
}
