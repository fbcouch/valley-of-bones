package com.ahsgaming.valleyofbones.units.behavior;

import com.ahsgaming.valleyofbones.GameController;
import com.ahsgaming.valleyofbones.ai.AStar;
import com.badlogic.gdx.math.Vector2;

/**
 * valley-of-bones
 * (c) 2013 Jami Couch
 * User: jami
 * Date: 3/15/14
 * Time: 10:03 AM
 */
public class NoMove implements MoveBehavior {
    @Override
    public AStar.AStarNode findPath(GameController gameController, Vector2 boardPosition) {
        return null;
    }

    @Override
    public void move(GameController gameController, Vector2 boardPosition) {
        // no-op
    }
}
