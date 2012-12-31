package com.ahsgaming.spacetactics.network;

import com.badlogic.gdx.math.Vector2;

public class Move extends Command {
	public int unit;
	public Vector2 toLocation;
	public boolean isAttack = false;
	public boolean isAdd = false;
}
