package com.ahsgaming.valleyofbones.units.behavior;

import com.ahsgaming.valleyofbones.GameController;
import com.ahsgaming.valleyofbones.ai.AStar;
import com.ahsgaming.valleyofbones.units.AbstractUnit;
import com.ahsgaming.valleyofbones.units.UnitView;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

/**
 * valley-of-bones
 * (c) 2013 Jami Couch
 * User: jami
 * Date: 3/15/14
 * Time: 9:29 AM
 */
public class BasicMove implements MoveBehavior {
    AbstractUnit unit;

    public BasicMove(AbstractUnit unit) {
        this.unit = unit;
    }

    @Override
    public AStar.AStarNode findPath(GameController gameController, Vector2 boardPosition) {
        return AStar.getPath(unit.getView().getBoardPosition(), boardPosition, gameController, (int)unit.getData().getMovesLeft());
    }

    @Override
    public void move(GameController gameController, Vector2 boardPosition) {
        AStar.AStarNode path = findPath(gameController, boardPosition);
        if (path != null && path.gx <= unit.getData().getMovesLeft()) {
            unit.getData().setMovesLeft(unit.getData().getMovesLeft() - path.gx);
            unit.getData().setMovesThisTurn(unit.getData().getMovesThisTurn() + (int)path.gx);
            unit.getView().setLastBoardPosition(unit.getView().getBoardPosition());
            unit.getView().setBoardPosition(boardPosition);

            Array<Vector2> nodes = new Array<Vector2>();
            AStar.AStarNode cur = path;
            while (cur.parent != null) {
                nodes.add(cur.location);
                cur = cur.parent;
            }

            unit.getView().addToPath(unit.getView().getLastBoardPosition());
            while (nodes.size > 0) {
                Vector2 boardPos = nodes.pop();
                unit.getView().addToPath(boardPos);
                Vector2 pos = gameController.getMap().boardToMapCoords(boardPos.x, boardPos.y);
                unit.getView().addAction(UnitView.Actions.moveTo(pos.x, pos.y, 1 / unit.getData().getMoveSpeed()));
            }
        }
    }
}
