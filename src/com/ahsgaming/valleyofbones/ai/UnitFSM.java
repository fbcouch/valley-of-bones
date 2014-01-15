package com.ahsgaming.valleyofbones.ai;

import com.ahsgaming.valleyofbones.GameController;
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
    public static String LOG = "UnitFSM";
    Stack<Directive> directives;
    Unit unit;

    Array<Unit> visibleEnemyUnits;
    Array<Unit> towers;

    public UnitFSM(Unit unit) {
        directives = new Stack<Directive>();
        visibleEnemyUnits = new Array<Unit>();
        towers = new Array<Unit>();
        this.unit = unit;
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
            if (u.isCapturable()) {
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
        if (directive.target.getOwner() == unit.getOwner() && directive.target.getCurHP() > 0) {
            directives.pop();
            return null;
        }

        int dist = controller.getMap().getMapDist(unit.getBoardPosition(), directive.target.getBoardPosition());

        if (dist > 1 && unit.getMovesLeft() > 0) {
            // TODO move closer
            Move mv = moveToward(directive.target.getBoardPosition(), controller);
            if (mv != null)
                return mv;
        }

        if (dist <= unit.getAttackRange() && unit.getAttacksLeft() > 0 && directive.target.getCurHP() > 0) {
            // in range
            Attack at = new Attack();
            at.target = directive.target.getObjId();
            at.unit = unit.getObjId();
            return at;
        }

        for (Unit u: towers) {
            if (u.getOwner() != unit.getOwner() && controller.getMap().getMapDist(unit.getBoardPosition(), u.getBoardPosition()) < dist) {
                order(new Directive(Directive.DirectiveType.CAPTURE, u));
                return null;
            }
        }

        if (unit.getAttacksLeft() > 0) {
            Unit target = findTarget(controller);
            if (target != null) {
//                if (controller.getMap().getMapDist(directive.target.getBoardPosition(), target.getBoardPosition()) == 1) {
                    order(new Directive(Directive.DirectiveType.HUNT, target));
                    return null;
//                } else {
//                    Attack at = new Attack();
//                    at.target = target.getObjId();
//                    at.unit = unit.getObjId();
//                    return at;
//                }
            }
        }
        return null;
    }

    public Command defend(GameController controller) {
        directives.pop();
        return null;
    }

    public Command hunt(GameController controller) {
        Gdx.app.log(LOG, "Hunt");
        Directive directive = directives.peek();
        if (directive.target.getCurHP() <= 0 || !controller.playerCanSee(unit.getOwner(), directive.target)) {
            directives.pop();
            return null;
        }

        if (unit.canAttack(directive.target, controller)) {
            Attack at = new Attack();
            at.target = directive.target.getObjId();
            at.unit = unit.getObjId();
            return at;
        }

        if (unit.getMovesLeft() > 0
                && controller.getMap().getMapDist(unit.getBoardPosition(), directive.target.getBoardPosition()) > unit.getAttackRange() + unit.getMoveSpeed() - directive.target.getMoveSpeed()) {
            Move mv = moveToward(directive.target.getBoardPosition(), controller);
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

        openList.add(AStarNode.createAStarNode(null, unit.getBoardPosition(), 0, controller.getMap().getMapDist(unit.getBoardPosition(), boardPosition)));

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

            for(Vector2 loc: controller.getMap().getAdjacent((int)least.location.x, (int)least.location.y)) {
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
            while (target.gx > unit.getMovesLeft() || !controller.isBoardPosEmpty(target.location)) {
                target = target.parent;
            }

            Move mv = new Move();
            mv.unit = unit.getObjId();
            mv.toLocation = target.location;
            return mv;
        }
        return null;
    }

    Unit findTarget(GameController controller) {
        // TODO evaluate unit priority - for now, just attack the lowest hp
        Unit lowHp = null;
        for (Unit u: visibleEnemyUnits) {
            if (controller.getMap().getMapDist(unit.getBoardPosition(), u.getBoardPosition()) <= unit.getAttackRange()) {
                if (lowHp == null || (u.getCurHP() < lowHp.getCurHP() && u.getCurHP() > 0)) {
                    lowHp = u;
                }
            }
        }

        return lowHp;
    }

}