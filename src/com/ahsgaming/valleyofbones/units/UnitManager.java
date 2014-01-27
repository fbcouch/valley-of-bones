package com.ahsgaming.valleyofbones.units;

import com.ahsgaming.valleyofbones.GameController;
import com.ahsgaming.valleyofbones.Player;
import com.ahsgaming.valleyofbones.map.HexMap;
import com.ahsgaming.valleyofbones.screens.LevelScreen;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.TimeUtils;

import java.util.Collection;
import java.util.HashMap;

/**
 * valley-of-bones
 * (c) 2013 Jami Couch
 * Created on 1/16/14 by jami
 * ahsgaming.com
 */
public class UnitManager {
    HashMap<Integer, Unit> units;
    GameController gameController;

    public UnitManager(GameController gameController) {
        units = new HashMap<Integer, Unit>();
        this.gameController = gameController;
    }

    public void addUnit(Unit unit) {
        units.put(unit.getId(), unit);
    }

    public void removeUnit(int id) {
        units.remove(id);
    }

    public Unit getUnit(int id) {
        return units.get(id);
    }

    public Unit getUnit(Vector2 boardPos) {
        for (Unit u: units.values()) {
            if (u.view.boardPosition.epsilonEquals(boardPos, 0.1f))
                return u;
        }
        return null;
    }

    public Array<Unit> getUnits() {
        Array<Unit> unitArray = new Array<Unit>();
        for (Unit u: units.values()) {
            unitArray.add(u);
        }
        return unitArray;
    }

    public Array<Unit> getUnits(int playerId) {
        Array<Unit> returnVal = new Array<Unit>();
        for (Unit unit: units.values()) {
            if (unit.owner != null && unit.owner.getPlayerId() == playerId) {
                returnVal.add(unit);
            }
        }
        return returnVal;
    }

    public Array<Unit> getUnitsInArea(Vector2 boardPos, int radius) {
        Array<Unit> returnVal = new Array<Unit>();
        for (Unit unit: units.values()) {
            if (HexMap.getMapDist(boardPos, unit.view.boardPosition) <= radius)
                returnVal.add(unit);
        }
        return returnVal;
    }

    public void update(float delta) {
        Array<Integer> toRemove = new Array<Integer>();
        for (Unit unit: units.values()) {
            updateUnit(unit, delta);

            if (!unit.getData().isAlive() && !unit.getView().hasActions()) {
                toRemove.add(unit.getId());
            }
        }
        for (int i: toRemove) {
            units.remove(i);
        }
    }

    public void updateUnit(Unit unit, float delta) {
        if (unit.data.capturable)
            findNewOwner(unit);

        if (unit.data.curHP <= 0) {
            if (unit.data.capturable) {
                unit.data.curHP = 0;
                if (unit.owner != unit.data.uncontested) {
                    // set map dirty
                    unit.data.modified = TimeUtils.millis();
                }
                unit.owner = unit.data.uncontested;
                unit.data.attacksLeft = 0;
                unit.data.movesLeft = 0;
            }
        }

        unit.view.act(delta);
    }

    public void startTurn(Player player) {
        for (Unit unit: units.values()) {
            if (unit.owner != player) continue;

            if (!unit.data.building) {
                unit.data.movesLeft = (unit.data.movesLeft % 1) + unit.data.moveSpeed * (unit.data.stealthActive ? 0.5f : 1f);
                unit.data.attacksLeft = (unit.data.attacksLeft % 1) + unit.data.attackSpeed;
            }

            if (unit.data.capturable && unit.data.uncontested == unit.owner)
                unit.data.setCurHP(unit.data.curHP += 5 * unit.data.capUnitCount);

            unit.view.clearPath();
            unit.view.addToPath(unit.view.getBoardPosition());

            unit.data.stealthEntered = false;

            unit.data.modified = TimeUtils.millis();
        }
    }

    public void endTurn(Player player) {
        for (Unit unit: units.values()) {
            if (unit.owner != player) continue;

            if (unit.data.building) {
                unit.data.buildTimeLeft--;

                if (unit.data.buildTimeLeft <= 0)
                    unit.data.building = false;
            }

            unit.data.modified = TimeUtils.millis();
        }
    }

    public boolean attack (Unit attacker, Unit defender) {
        if (canAttack(attacker, defender)) {
            if (attacker.data.ability.equals("sabotage")) {
                if (defender.data.capturable) {
                    defender.owner = attacker.owner;
                    defender.data.movesLeft = 0;
                    defender.data.attacksLeft = 0;
                } else {
                    if (defender.data.protoId.equals("castle-base"))
                        return false; // saboteur can't attack castle
                    applyDamage(defender, defender.data.curHP + defender.data.armor - 1);
                }
                applyDamage(attacker, attacker.data.curHP + attacker.data.armor);
            } else {
                attacker.data.attacksLeft--;
                applyDamage(defender, attacker.data.attackDamage * attacker.data.getBonus(defender.data.subtype));

                if (attacker.data.stealthActive)
                    activateAbility(attacker);

                attacker.view.addAction(UnitView.Actions.sequence(
                        UnitView.Actions.colorTo(new Color(1, 1, 0.5f, 1), 0.1f),
                        UnitView.Actions.delay(0.2f),
                        UnitView.Actions.colorTo(new Color(1, 1, 1, 1), 0.1f)
                ));
            }
            attacker.data.modified = TimeUtils.millis();
            return true;
        }
        return false;
    }

    public void moveUnit(Unit unit, Vector2 boardPosition) {
        if (canUnitMove(unit, boardPosition)) {
            int dist = HexMap.getMapDist(unit.view.boardPosition, boardPosition);
            unit.data.movesLeft -= dist;

            unit.view.lastBoardPosition = unit.view.boardPosition;
            unit.view.boardPosition = boardPosition;
            unit.view.addToPath(boardPosition);
            // TODO add unit animation (need static ref to boardToMapCoords)
            Vector2 pos = gameController.getMap().boardToMapCoords(boardPosition.x, boardPosition.y);
            unit.view.addAction(UnitView.Actions.moveTo(pos.x, pos.y, dist / unit.data.moveSpeed));
            unit.data.modified = TimeUtils.millis();
        }
    }

    public boolean canUnitMove(Unit unit, Vector2 boardPosition) {
        // TODO perhaps A* this to find a real route?
        if (HexMap.getMapDist(unit.view.boardPosition, boardPosition) > unit.data.moveSpeed)
            return false;

        for (Unit u: units.values()) {
            if (u.view.boardPosition.epsilonEquals(boardPosition, 0.1f))
                return false;
        }

        return true;
    }

    public void activateAbility(Unit unit) {
        if (unit.data.ability.equals("stealth")) {
            if (unit.data.stealthEntered && !unit.data.stealthActive)
                return; // cant re-enter stealth this turn

            unit.data.stealthActive = !unit.data.stealthActive;

            if (unit.data.stealthActive) {
                unit.data.stealthEntered = true;
                unit.data.movesLeft = (float)Math.floor(unit.data.moveSpeed * 0.5f) - unit.data.moveSpeed - unit.data.movesLeft;
                if (unit.data.movesLeft < 0) unit.data.movesLeft = 0;
            } else {
                unit.data.movesLeft = unit.data.moveSpeed - (float)Math.floor(unit.data.moveSpeed * 0.5f - unit.data.movesLeft);
            }
            unit.data.modified = TimeUtils.millis();
        }
    }

    public void applyDamage(Unit unit, float amount) {
        float damage = amount - unit.data.armor;
        if (damage > 0) {
            unit.data.curHP -= damage;

            // TODO figure out a better way to do this
            if (LevelScreen.getInstance() != null)
                LevelScreen.getInstance().addFloatingLabel(String.format("-%d", (int)damage), unit.view.getX() + unit.view.getWidth() * 0.5f, unit.view.getY() + unit.view.getHeight() * 0.5f);

            unit.view.addAction(UnitView.Actions.sequence(
                    UnitView.Actions.colorTo(new Color(1.0f, 0.5f, 0.5f, 1.0f), 0.1f),
                    UnitView.Actions.colorTo(new Color(1.0f, 1.0f, 1.0f, 1.0f), 0.1f),
                    UnitView.Actions.colorTo(new Color(1.0f, 0.5f, 0.5f, 1.0f), 0.1f),
                    UnitView.Actions.colorTo(new Color(1.0f, 1.0f, 1.0f, 1.0f), 0.1f)
            ));

            unit.data.modified = TimeUtils.millis();
        }
    }

    public boolean canAttack(Unit attacker, Unit defender) {
        return (
                attacker.data.attacksLeft > 0
                && HexMap.getMapDist(attacker.view.boardPosition, defender.view.boardPosition) <= attacker.data.attackRange
                && canPlayerSee(attacker.owner, defender)
        );
    }

    public boolean canPlayerSee(Player player, Unit unit) {
        if (unit.owner == player || player == null) return true;

        for (Unit u: units.values()) {
            if (u.owner == player && canUnitSee(u, unit)) {
                return true;
            }
        }
        return false;
    }

    boolean canUnitSee(Unit looker, Unit target) {
        return (
                (!target.data.isInvisible() || looker.data.isDetector())
                && HexMap.getMapDist(looker.view.boardPosition, target.view.boardPosition) <= looker.data.sightRange
        );
    }

    void findNewOwner(Unit unit) {
        Player p = null;
        unit.data.capUnitCount = 0;
        Vector2[] adjacent = HexMap.getAdjacent(unit.view.boardPosition);
        for (Unit u: units.values()) {
            if (u == unit || u.owner == null || u.data.building)
                continue;

            boolean adj = false;
            for (Vector2 pos: adjacent) {
                if (pos.epsilonEquals(u.view.boardPosition, 0.1f)) {
                    adj = true;
                    break;
                }
            }
            if (!adj)
                continue;

            if (p == null || p == u.owner) {
                p = u.owner;
                unit.data.capUnitCount++;
            } else {
                unit.data.uncontested = null;
                unit.data.capUnitCount = 0;
                return;
            }
        }
        unit.data.uncontested = p;
    }
}
