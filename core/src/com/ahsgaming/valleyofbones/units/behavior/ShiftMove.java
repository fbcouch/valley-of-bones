package com.ahsgaming.valleyofbones.units.behavior;

import com.ahsgaming.valleyofbones.GameController;
import com.ahsgaming.valleyofbones.ai.AStar;
import com.ahsgaming.valleyofbones.units.AbstractUnit;
import com.ahsgaming.valleyofbones.units.UnitView;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;

/**
 * valley-of-bones
 * (c) 2013 Jami Couch
 * User: jami
 * Date: 3/15/14
 * Time: 4:44 PM
 */
public class ShiftMove implements MoveBehavior {
    AbstractUnit unit;

    public ShiftMove(AbstractUnit unit) {
        this.unit = unit;
    }

    @Override
    public AStar.AStarNode findPath(GameController gameController, Vector2 boardPosition) {
        return null;
    }

    @Override
    public void move(GameController gameController, Vector2 boardPosition) {
        if (unit.getData().getMovesThisTurn() > 0) return;

        unit.getView().setLastBoardPosition(unit.getView().getBoardPosition());
        unit.getView().setBoardPosition(boardPosition);
        unit.getData().setMovesThisTurn(unit.getData().getMovesThisTurn() + 1);
        unit.getData().setMovesLeft(0);

        Vector2 pos = gameController.getMap().boardToMapCoords(boardPosition.x, boardPosition.y);
        unit.getView().addAction(UnitView.Actions.sequence(
                UnitView.Actions.colorTo(new Color(1, 1, 1, 0), 0.4f),
                UnitView.Actions.moveTo(pos.x, pos.y, 0.2f),
                UnitView.Actions.colorTo(new Color(1, 1, 1, 1), 0.4f)
        ));
    }
}
