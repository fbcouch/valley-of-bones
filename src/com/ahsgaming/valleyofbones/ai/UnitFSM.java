package com.ahsgaming.valleyofbones.ai;

import com.ahsgaming.valleyofbones.GameController;
import com.ahsgaming.valleyofbones.map.HexMap;
import com.ahsgaming.valleyofbones.network.Attack;
import com.ahsgaming.valleyofbones.network.Command;
import com.ahsgaming.valleyofbones.network.Move;
import com.ahsgaming.valleyofbones.units.Unit;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

import java.util.Stack;

/**
 * valley-of-bones
 * (c) 2013 Jami Couch
 * User: jami
 * Date: 1/12/14
 * Time: 9:06 AM
 */
public class UnitFSM {
    public String LOG = "UnitFSM";
    Stack<Directive> directives;
    Unit unit;

    Array<Unit> visibleEnemyUnits;
    Array<Unit> towers;

    public UnitFSM(Unit unit) {
        directives = new Stack<Directive>();
        visibleEnemyUnits = new Array<Unit>();
        towers = new Array<Unit>();
        this.unit = unit;
        LOG += "[" + unit.getProto().id + "(" + unit.getId() + ")]";
    }

    public void order(Directive order) {
        directives.push(order);
    }

    public Command update(GameController controller) {
        if (directives.size() == 0) return null;

        visibleEnemyUnits.clear();
        towers.clear();
        for (Unit u: controller.getUnits()) {
            if (u.getOwner() != unit.getOwner() && u.getOwner() != null && controller.playerCanSee(unit.getOwner(), u)) {
                visibleEnemyUnits.add(u);
            }
            if (u.getData().isCapturable()) {
                towers.add(u);
            }
        }

        Command cmd = null;

        switch(directives.peek().type) {
            case FLEE:
                cmd = flee(controller);
                break;
            case HUNT:
                cmd = hunt(controller);
                break;
            case DEFEND:
                cmd = defend(controller);
                break;
            case CAPTURE:
                cmd = capture(controller);
                break;
        }
        return cmd;
    }

    public Command capture(GameController controller) {
        Directive directive = directives.peek();
        Gdx.app.log(LOG, "Capture " + directive.target.getProto().id + " at " + directive.target.getView().getBoardPosition());
        if (directive.target.getOwner() == unit.getOwner() && directive.target.getData().getCurHP() > 0) {
            directives.pop();
            Gdx.app.log(LOG, "Captured, defending...");
            order(new Directive(Directive.DirectiveType.DEFEND, directive.target));
            return null;
        }

        int dist = controller.getMap().getMapDist(unit.getView().getBoardPosition(), directive.target.getView().getBoardPosition());

        if (dist > 1 && unit.getData().getMovesLeft() > 0) {
            // TODO move closer
            Move mv = moveToward(directive.target.getView().getBoardPosition(), controller);
            if (mv != null)
                return mv;
        }

        if (dist <= unit.getData().getAttackRange() && unit.getData().getAttacksLeft() > 0 && directive.target.getData().getCurHP() > 0) {
            // in range
            Attack at = new Attack();
            at.target = directive.target.getId();
            at.unit = unit.getId();
            return at;
        }

        for (Unit u: towers) {
            if ((u.getOwner() != unit.getOwner() || (u.getOwner() == unit.getOwner() && u.getData().getCurHP() == 0))
                    && controller.getMap().getMapDist(unit.getView().getBoardPosition(), u.getView().getBoardPosition()) < dist) {
                order(new Directive(Directive.DirectiveType.CAPTURE, u));
                return null;
            }
        }

        if (unit.getData().getAttacksLeft() > 0) {
            Unit target = findTarget(controller);
            if (target != null) {
//                if (controller.getMap().getMapDist(directive.target.getView().getBoardPosition(), target.getView().getBoardPosition()) == 1) {
                    order(new Directive(Directive.DirectiveType.HUNT, target));
                    return null;
//                } else {
//                    Attack at = new Attack();
//                    at.target = target.getId();
//                    at.unit = unit.getId();
//                    return at;
//                }
            }
        }
        return null;
    }

    public Command defend(GameController controller) {
        Directive directive = directives.peek();
        Gdx.app.log(LOG, "Defend " + directive.target.getProto().id + " at " + directive.target.getView().getBoardPosition());

        Array<Unit> threats = new Array<Unit>();
        Array<Unit> threatsInRange = new Array<Unit>();
        for (Unit u: visibleEnemyUnits) {
            int dist = HexMap.getMapDist(directive.target.getView().getBoardPosition(), u.getView().getBoardPosition());
            if (u.getData().getAttackRange() + u.getData().getMoveSpeed() > dist) {
                threats.add(u);
                if (unit.getData().getAttackRange() + u.getData().getMovesLeft() <= dist) {
                    threatsInRange.add(u);
                }
            }
        }
        // TODO probably rank these by threat or something?
        if (threatsInRange.size > 0) {
            threats = threatsInRange;
        }

        Gdx.app.log(LOG, "Threats: " + threats.size);

        Unit lowhp = null;
        for (Unit u: threats) {
            if (lowhp == null || u.getData().getCurHP() < lowhp.getData().getCurHP()) {
                lowhp = u;
            }
        }

        if (lowhp != null) {
            order(new Directive(Directive.DirectiveType.HUNT, lowhp));
        } else {
            directives.pop();
        }

        return null;
    }

    public Command hunt(GameController controller) {
        Directive directive = directives.peek();
        Gdx.app.log(LOG, "Hunt " + directive.target.getProto().id + " at " + directive.target.getView().getBoardPosition());

        if (directive.target.getData().getCurHP() <= 0 || !controller.playerCanSee(unit.getOwner(), directive.target)) {
            directives.pop();
            return null;
        }

        if (controller.getUnitManager().canAttack(unit, directive.target)) {
            Attack at = new Attack();
            at.target = directive.target.getId();
            at.unit = unit.getId();
            return at;
        }

        if (unit.getData().getMovesLeft() > 0
                && controller.getMap().getMapDist(unit.getView().getBoardPosition(), directive.target.getView().getBoardPosition()) > unit.getData().getAttackRange() + unit.getData().getMoveSpeed() - directive.target.getData().getMoveSpeed()) {
            Move mv = moveToward(directive.target.getView().getBoardPosition(), controller);
            if (mv != null)
                return mv;
        }

        return null;
    }

    public Command flee(GameController controller) {
        directives.pop();
        return null;
    }

    public static class AStarNode {
        AStarNode parent;
        Vector2 location;
        float gx, hx;

        public static AStarNode createAStarNode(AStarNode parent, Vector2 location, float gx, float hx) {
            AStarNode node = new AStarNode();
            node.parent = parent;
            node.location = location;
            node.gx = gx;
            node.hx = hx;
            return node;
        }
    }

    Move moveToward(Vector2 boardPosition, GameController controller) {
        Array<AStarNode> openList = new Array<AStarNode>();
        Array<AStarNode> closedList = new Array<AStarNode>();

        openList.add(AStarNode.createAStarNode(null, unit.getView().getBoardPosition(), 0, controller.getMap().getMapDist(unit.getView().getBoardPosition(), boardPosition)));

        while (openList.size > 0) {
            AStarNode least = null;
            for (AStarNode node: openList) {
                if (least == null || node.gx + node.hx < least.gx + least.hx) {
                    least = node;
                }
            }

            openList.removeValue(least, true);
            closedList.add(least);

            if (least.location.epsilonEquals(boardPosition, 0.1f)) {
                break;
            }

            for(Vector2 loc: HexMap.getAdjacent((int) least.location.x, (int) least.location.y)) {
                if (loc.epsilonEquals(boardPosition, 0.1f) || controller.getMap().isBoardPositionTraversible((int)loc.x, (int)loc.y) && controller.isBoardPosEmpty(loc)) {
                    for (AStarNode node: closedList) {
                        if (node.location.epsilonEquals(loc, 0.1f))
                            continue;
                    }

                    int openIndex = -1;
                    for (int i = 0; i < openList.size; i++) {
                        if (openList.get(i).location.epsilonEquals(loc, 0.1f)) {
                            openIndex = i;
                        }
                    }

                    if (openIndex == -1 || openList.get(openIndex).gx > least.gx + 1) {
                        openList.add(AStarNode.createAStarNode(least, loc, least.gx + 1, controller.getMap().getMapDist(loc, boardPosition)));
                    }
                }
            }
        }

        if (closedList.size > 0) {
            AStarNode target = closedList.peek();
            while (target.gx > unit.getData().getMovesLeft() || !controller.isBoardPosEmpty(target.location)) {
                target = target.parent;
            }

            Move mv = new Move();
            mv.unit = unit.getId();
            mv.toLocation = target.location;
            return mv;
        }
        return null;
    }

    Unit findTarget(GameController controller) {
        // TODO evaluate unit priority - for now, just attack the lowest hp
        Unit lowHp = null;
        for (Unit u: visibleEnemyUnits) {
            if (!u.getProto().id.equals("tower-base") && controller.getMap().getMapDist(unit.getView().getBoardPosition(), u.getView().getBoardPosition()) <= unit.getData().getAttackRange()) {
                if (lowHp == null || (u.getData().getCurHP() < lowHp.getData().getCurHP() && u.getData().getCurHP() > 0)) {
                    lowHp = u;
                }
            }
        }

        return lowHp;
    }

}