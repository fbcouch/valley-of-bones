package com.ahsgaming.valleyofbones.network;

public class Command {
	public int owner;
	public int turn;
	public boolean isAdd = false;
	
	@Override
	public boolean equals(Object o) {
		if (o instanceof Command) {
			Command c = (Command)o;
			return (c.owner == owner && c.turn == turn && c.isAdd == isAdd);
		}
		return false;
	}

    public String toJson() {
        return "{" + getJsonItems() + "}";
    }

    protected String getJsonItems() {
        return String.format("\"owner\": %d, \"turn\": %d, \"isAdd\": %b", owner, turn, isAdd);
    }
}
