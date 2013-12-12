package com.ahsgaming.valleyofbones.network;

import com.badlogic.gdx.utils.Array;

public class EndTurn extends Command {

    public Command[] commands;

    public EndTurn() {

    }

    public EndTurn(Array<Command> queue) {
        commands = new Command[queue.size];
        for (int i=0;i<commands.length;i++)
            commands[i] = queue.get(i);
    }

	@Override
	public boolean equals(Object o) {
		return o instanceof EndTurn && super.equals(o);
	}

    @Override
    public String toJson() {
        return "{ \"type\": \"EndTurn\", " + getJsonItems() + "}";
    }
}
