package com.ahsgaming.valleyofbones.network;

import com.badlogic.gdx.math.Vector2;

public class Move extends Command {
	public int unit;
	public Vector2 toLocation;
	public boolean isAttack = false;
	
	@Override
	public boolean equals(Object o) {
		if (o instanceof Move && super.equals(o)) {
			Move m = (Move)o;
			return m.unit == unit && m.toLocation.epsilonEquals(toLocation, 0.01f) && m.isAttack == isAttack;
		}
		return false;
	}

    @Override
    public String toJson() {
        return "{ \"type\": \"Move\", " + getJsonItems() + "}";
    }

    @Override
    protected String getJsonItems() {
        return super.getJsonItems() + String.format(", \"unit\": \"%d\", \"toLocation\": \"%s\"", unit, toLocation.toString());
    }
}
