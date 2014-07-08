package com.ahsgaming.valleyofbones.units;

import com.ahsgaming.valleyofbones.GameController;
import com.ahsgaming.valleyofbones.Player;
import com.ahsgaming.valleyofbones.ai.AStar;
import com.ahsgaming.valleyofbones.map.HexMap;
import com.ahsgaming.valleyofbones.map.MapView;
import com.ahsgaming.valleyofbones.screens.LevelScreen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.TimeUtils;

import java.util.HashMap;

/**
 * valley-of-bones
 * (c) 2013 Jami Couch
 * Created on 1/16/14 by jami
 * ahsgaming.com
 */
public class UnitManager implements EventListener {
    HashMap<Integer, AbstractUnit> units;
    Array<AbstractUnit> reserveUnits;
    GameController gameController;

    public UnitManager(GameController gameController) {
        units = new HashMap<Integer, AbstractUnit>();
        reserveUnits = new Array<AbstractUnit>();
        this.gameController = gameController;
    }

    public void addUnit(AbstractUnit unit) {
        units.put(unit.getId(), unit);
        unit.register(this);
    }

    public void removeUnit(int id) {
        units.remove(id).emit();
    }

    public void reserveUnit(int id) {
        if (units.containsKey(id)) {
            reserveUnits.add(units.get(id));
        }
        removeUnit(id);
    }

    public AbstractUnit getUnit(int id) {
        return units.get(id);
    }

    public AbstractUnit getUnit(Vector2 boardPos) {
        for (AbstractUnit u: units.values()) {
            if (u.view.boardPosition.epsilonEquals(boardPos, 0.1f))
                return u;
        }
        return null;
    }

    public Array<AbstractUnit> getUnits() {
        Array<AbstractUnit> unitArray = new Array<AbstractUnit>();
        for (AbstractUnit u: units.values()) {
            unitArray.add(u);
        }
        return unitArray;
    }

    public Array<AbstractUnit> getUnits(int playerId) {
        Array<AbstractUnit> returnVal = new Array<AbstractUnit>();
        for (AbstractUnit unit: units.values()) {
            if (unit.owner != null && unit.owner.getPlayerId() == playerId) {
                returnVal.add(unit);
            }
        }
        return returnVal;
    }

    public Array<AbstractUnit> getUnitsInArea(Vector2 boardPos, int radius) {
        Array<AbstractUnit> returnVal = new Array<AbstractUnit>();
        for (AbstractUnit unit: units.values()) {
            if (HexMap.getMapDist(boardPos, unit.view.boardPosition) <= radius)
                returnVal.add(unit);
        }
        return returnVal;
    }

    public void update(float delta) {
        Array<Integer> toRemove = new Array<Integer>();
        for (AbstractUnit unit: units.values()) {
            updateUnit(unit, delta);

            if (!unit.getData().isAlive() && !unit.getView().hasActions()) {
                toRemove.add(unit.getId());
            }
        }
        for (int i: toRemove) {
            AbstractUnit unit = units.remove(i);
            for (int j = 0; j < reserveUnits.size; j++) {
                if (reserveUnits.get(j).getView().getBoardPosition().epsilonEquals(unit.getView().getBoardPosition(), 0.1f)) {
                    addUnit(reserveUnits.removeIndex(j));
                    j--;
                }
            }

            unit.emit();
            gameController.getMap().invalidateViews();
        }
    }

    public void updateUnit(AbstractUnit unit, float delta) {
        unit.update(this, delta);
    }

    public void startTurn(Player player) {
        for (AbstractUnit unit: units.values()) {
            if (unit.data.ability.equals("increasing-returns") || unit.getOwner() == player) {
                unit.startTurn(gameController.getGameTurn());
            }
        }
    }

    public void endTurn(Player player) {
        for (AbstractUnit unit: units.values()) {
            if (unit.owner != player) continue;

            unit.endTurn(gameController.getGameTurn());
        }
    }

    public boolean attack (AbstractUnit attacker, AbstractUnit defender) {
        if (canAttack(attacker, defender)) {
            attacker.attack(this, defender);
            return true;
        }
        return false;
    }

    public void moveUnit(AbstractUnit unit, Vector2 boardPosition) {
        unit.move(gameController, boardPosition);
    }

    public void activateAbility(AbstractUnit unit) {
        unit.activateAbility();
    }

    public boolean canAttack(AbstractUnit attacker, AbstractUnit defender) {
        return (
                attacker.data.attacksLeft > 0
                && HexMap.getMapDist(attacker.view.boardPosition, defender.view.boardPosition) <= attacker.data.attackRange
                && ((!defender.getData().isInvisible() && canPlayerSee(attacker.owner, defender)) || canPlayerDetect(attacker.owner, defender))
        );
    }

    public boolean canHeal(AbstractUnit healer, AbstractUnit healed) {
        return (
                healer.getData().getAttacksLeft() > 0
                && healer.getData().getHeal() > 0
                && healed.getData().getCurHP() < healed.getData().getMaxHP()
                && HexMap.getMapDist(healer.getView().getBoardPosition(), healed.getView().getBoardPosition()) <= 1
        );
    }

    public boolean canPlayerSee(Player player, AbstractUnit unit) {
        if (unit.owner == player || player == null) return true;

        for (AbstractUnit u: units.values()) {
            if (u.owner == player && canUnitSee(u, unit)) {
                return true;
            }
        }
        return false;
    }

    public boolean canPlayerDetect(Player player, AbstractUnit unit) {
        if (unit.owner == player || player == null) return true;

        for (AbstractUnit u: units.values()) {
            if (u.owner == player && u.data.isDetector() && !u.data.building && canUnitSee(u, unit)) {
                return true;
            }
        }
        return false;
    }

    boolean canUnitSee(AbstractUnit looker, AbstractUnit target) {
        return (
                (!target.data.isInvisible() || looker.data.isDetector())
                && HexMap.getMapDist(looker.view.boardPosition, target.view.boardPosition) <= looker.data.sightRange
        );
    }

    @Override
    public void update() {
        gameController.getMap().invalidateViews();
    }
}
