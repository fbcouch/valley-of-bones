package com.ahsgaming.valleyofbones.network;

import com.ahsgaming.valleyofbones.GameController;
import com.ahsgaming.valleyofbones.Player;
import com.ahsgaming.valleyofbones.VOBGame;
import com.ahsgaming.valleyofbones.units.AbstractUnit;
import com.ahsgaming.valleyofbones.units.Prototypes;
import com.ahsgaming.valleyofbones.units.Unit;
import com.ahsgaming.valleyofbones.units.UnitView;
import com.badlogic.gdx.math.Vector2;

public class Build extends ActionResetCommand {
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

    @Override
    public boolean validate(GameController gameController) {
        if (!super.validate(gameController)) return false;

        Player player = gameController.getPlayerById(owner);
        if (player == null) return false;

        Prototypes.JsonProto buildProto = Prototypes.getProto(player.getRace(), building);
        if (buildProto == null) return false;

        String buildOn = buildProto.properties.getString("build-on", "");
        AbstractUnit unitAtLocation = gameController.getUnitAtBoardPos(location);

        return (player.canBuild(building, gameController) && gameController.getMap().isBoardPositionVisible(player, location))
                && ((buildOn.equals("") && unitAtLocation == null) || (unitAtLocation != null && unitAtLocation.getProto().id.equals(buildOn)));
    }

    @Override
    public void execute(GameController gameController) {
        Player player = gameController.getPlayerById(owner);
        Prototypes.JsonProto junit = Prototypes.getProto(player.getRace(), building);
        Vector2 levelPos = gameController.getMap().boardToMapCoords(location.x, location.y);

        AbstractUnit unit = Unit.createUnit(gameController.getNextObjectId(), building, player);
        if (player.getRace().equals("terran") && !junit.type.equals("building")) {
            unit.getView().setPosition(levelPos.x - 300 * VOBGame.SCALE, levelPos.y + 600 * VOBGame.SCALE);
            unit.getView().addAction(UnitView.Actions.moveTo(levelPos.x, levelPos.y, 0.5f));
        } else {
            unit.getView().setPosition(levelPos.x, levelPos.y);
        }
        unit.getView().setBoardPosition(location);
        player.setBankMoney(player.getBankMoney() - player.getProtoCost(unit.getProto(), gameController));
        player.updateFood(gameController);
        unitId = unit.getId();

        AbstractUnit atLocation = gameController.getUnitAtBoardPos(location);
        if (atLocation != null) {
            gameController.getUnitManager().reserveUnit(atLocation.getId());
        }

        gameController.getUnitManager().addUnit(unit);
    }
}
