package com.ahsgaming.valleyofbones.ai;

import com.ahsgaming.valleyofbones.GameController;
import com.ahsgaming.valleyofbones.Player;
import com.ahsgaming.valleyofbones.network.Build;
import com.ahsgaming.valleyofbones.network.Command;
import com.ahsgaming.valleyofbones.network.EndTurn;
import com.ahsgaming.valleyofbones.network.NetController;
import com.ahsgaming.valleyofbones.units.AbstractUnit;
import com.ahsgaming.valleyofbones.units.Prototypes;
import com.ahsgaming.valleyofbones.units.Unit;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

import java.util.HashMap;

/**
 * valley-of-bones
 * (c) 2013 Jami Couch
 * User: jami
 * Date: 1/12/14
 * Time: 8:58 AM
 */
public class FSMAIPlayer extends AIPlayer {

    HashMap<Integer, UnitFSM> unitFSMs;
    Array<Directive> currentGoals;

    public FSMAIPlayer(NetController netController, int id, String name, Color color) {
        super(netController, id, name, color);
        initialize();
    }

    public void initialize() {
        unitFSMs = new HashMap<Integer, UnitFSM>();
        currentGoals = new Array<Directive>();
    }

    @Override
    public void update(GameController controller, float delta) {
        updateFoodAndUpkeep(controller, false); // from Player.update

        countdown -= delta;
        if (controller.getCurrentPlayer() == this && countdown <= 0) {
            countdown = timer;
            // my turn

            Array<AbstractUnit> myUnits = controller.getUnitsByPlayerId(getPlayerId());

            if (visibilityMatrix == null) {
                visibilityMatrix = createVisibilityMatrix(controller.getMap(), myUnits);
            }

            Gdx.app.log(LOG, "1");
            if (sendCommand(updateUnitFSMs(myUnits, controller), controller)) return;
            Gdx.app.log(LOG, "2");
            // TODO this will need a lot of cleanup, right now I just want a prototype that builds marines as close as possible to goals
            // TODO for the moment, the enemy's gate is down - build marines at the first tower and let them wander

            updateGoals(myUnits, controller);

            if (sendCommand(buildUnits(controller), controller)) return;

            sendCommand(new EndTurn(), controller);
        }
        visibilityMatrix = null;
        goalMatrix = null;
    }

    boolean sendCommand(Command cmd, GameController controller) {
        if (cmd == null) return false;

        cmd.owner = getPlayerId();
        cmd.turn = controller.getGameTurn();
        if (!cmd.validate(controller)) {
            return true;            // JC: leave this here as a safeguard, even though I think we're good
        }
        netController.sendAICommand(cmd);
        countdown = timer;
        visibilityMatrix = null;
        return true;
    }

    Command updateUnitFSMs(Array<AbstractUnit> units, GameController controller) {
        for (AbstractUnit unit: units) {
            if (!unitFSMs.containsKey(unit.getId())) {
                unitFSMs.put(unit.getId(), new UnitFSM(unit));
            }
        }

        Command cmd = null;
        Array<Integer> toRemove = new Array<Integer>();
        for (Integer id: unitFSMs.keySet()) {
            UnitFSM fsm = unitFSMs.get(id);
            if (!fsm.unit.getData().isAlive() || fsm.unit.getOwner() != this) {
                toRemove.add(id);
            } else {
                if (fsm.directives.size() == 0 && currentGoals.size > 0) {
                    fsm.order(currentGoals.first());
                }
                cmd = fsm.update(controller);
                if (cmd != null) break;
            }
        }
        for (Integer id: toRemove) {
            unitFSMs.remove(id);
        }

        return cmd;
    }

    void updateGoals(Array<AbstractUnit> myUnits, GameController controller) {
        for (int i = 0; i < currentGoals.size; i++) {
            Directive goal = currentGoals.get(i);
            switch(goal.type) {
                case CAPTURE:
                    if (goal.target.getOwner() == this && goal.target.getData().getCurHP() > 0) {
                        currentGoals.removeIndex(i);
                    }
                    break;
            }
        }

        if (currentGoals.size == 0) {
//                Directive goal = getGoal(controller);
            Directive goal = new Directive(
                    Directive.DirectiveType.CAPTURE,
                    controller.getPlayers().get((controller.getPlayers().indexOf(this, true) + 1) % 2).getBaseUnit()
            );
            if (goal != null) {
                currentGoals.add(goal);
                for (Integer id: unitFSMs.keySet()) {
                    unitFSMs.get(id).order(goal);
                }
            }
        }

        for (AbstractUnit defend: myUnits) {
            if (!defend.getData().isCapturable() && defend != getBaseUnit()) continue;
            Array<AbstractUnit> friendlyUnits = new Array<AbstractUnit>();
            boolean threat = false;
            for (AbstractUnit u: controller.getUnitsInArea(defend.getView().getBoardPosition(), 5)) {
                if (u.getOwner() != this && u.getOwner() != null && controller.playerCanSee(this, u)) {
                    threat = true;
                } else if (u.getOwner() == this) {
                    friendlyUnits.add(u);
                }
            }

            if (threat) {
                for (AbstractUnit u: friendlyUnits) {
                    UnitFSM fsm = unitFSMs.get(u.getId());
                    if (fsm != null && (fsm.directives.size() == 0 || fsm.directives.peek().type == Directive.DirectiveType.CAPTURE))
                        fsm.order(new Directive(Directive.DirectiveType.DEFEND, defend));
                }
            }
        }
    }

    Directive getGoal(GameController controller) {
        AbstractUnit target = null;
        int dist = 0;
        for (AbstractUnit unit: controller.getUnits()) {
            if (unit.getProto().id.equals("tower-base") && unit.getOwner() != this) {
                int unitDist = controller.getMap().getMapDist(getBaseUnit().getView().getBoardPosition(), unit.getView().getBoardPosition());
                if (target == null || unitDist < dist) {
                    target = unit;
                    dist = unitDist;
                }
            }
        }

        if (target == null) {
            for (Player player: controller.getPlayers()) {
                if (player != this) {
                    target = player.getBaseUnit();
                    break;
                }
            }
        }

        if (target != null) {
            return new Directive(Directive.DirectiveType.CAPTURE, target);
        }

        return null;
    }

    Command buildUnits(GameController controller) {
        int cost = getProtoCost(Prototypes.getProto(getRace(), (getRace().equals("terran") ? "marine-base" : "warrior")), controller);
        if (currentGoals.size > 0 && getBankMoney() >= cost && getCurFood() < getMaxFood()) {
            Vector2 targetPos = getBaseUnit().getView().getBoardPosition(); //currentGoals.get(0).target.getBoardPosition();
            Vector2 minDistLoc = null;
            int minDist = 0;
            for (int x = 0; x < controller.getMap().getWidth(); x++) {
                for (int y = 0; y < controller.getMap().getHeight(); y++) {
                    if (visibilityMatrix[y * controller.getMap().getWidth() + x] && controller.isBoardPosEmpty(x, y)) {
                        Vector2 loc = new Vector2(x, y);
                        int dist = controller.getMap().getMapDist(loc, targetPos);
                        if (minDistLoc == null || dist < minDist) {
                            minDistLoc = loc;
                            minDist = dist;
                            if (minDist == 1) {
                                break;
                            }
                        }
                    }
                }
            }
            if (minDistLoc != null) {
                Build bld = new Build();
                bld.building = (getRace().equals("terran") ? "marine-base" : "warrior");
                bld.location = minDistLoc;
                return bld;
            }
        }
        return null;
    }
}
