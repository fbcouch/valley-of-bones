package com.ahsgaming.valleyofbones.network;

import com.ahsgaming.valleyofbones.GameController;
import com.ahsgaming.valleyofbones.ai.AStar;
import com.ahsgaming.valleyofbones.units.AbstractUnit;
import com.badlogic.gdx.math.Vector2;

public class Move extends ActionResetCommand {
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

    @Override
    public boolean validate(GameController gameController) {
        if (!super.validate(gameController)) return false;

        if (toLocation.x < 0 || toLocation.x >= gameController.getMap().getWidth() || toLocation.y < 0 || toLocation.y >= gameController.getMap().getHeight()) return false;

        AbstractUnit u = gameController.getUnitManager().getUnit(unit);

        // TODO seems like the AI is sometimes passing an invalid unit id?
        if (u == null || u.getOwner() == null || u.getOwner().getPlayerId() != owner) {
//            if (u == null) {
//                Gdx.app.log(LOG, "move failed: invalid unit");
//            } else if (u.getOwner() == null) {
//                Gdx.app.log(LOG, "move failed: no owner");
//            } else if (u.getOwner().getPlayerId() != m.owner) {
//                Gdx.app.log(LOG, "move failed: wrong owner");
//            }
            return false;
        }

        if (u.getData().getAbility().equals("shift")) {
            return u.getData().getMovesThisTurn() == 0 && gameController.isBoardPosEmpty(toLocation) && gameController.getMap().isBoardPositionVisible(u.getOwner(), toLocation);
        }

        AStar.AStarNode path = AStar.getPath(u.getView().getBoardPosition(), toLocation, gameController, (int) u.getData().getMovesLeft());

        return (path != null && path.gx <= u.getData().getMovesLeft());
    }

    @Override
    public void execute(GameController gameController) {
        gameController.getUnitManager().moveUnit(gameController.getUnitManager().getUnit(unit), toLocation);
    }
}
