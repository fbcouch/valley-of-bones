package com.ahsgaming.valleyofbones.ai;

import com.ahsgaming.valleyofbones.GameController;
import com.ahsgaming.valleyofbones.map.HexMap;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

/**
 * valley-of-bones
 * (c) 2013 Jami Couch
 * User: jami
 * Date: 1/30/14
 * Time: 5:19 PM
 */
public class AStar {

    public static AStarNode getPath(Vector2 from, Vector2 to, GameController controller, int limit) {
        Array<AStarNode> openList = new Array<AStar.AStarNode>();
        Array<AStar.AStarNode> closedList = new Array<AStar.AStarNode>();

        openList.add(AStar.AStarNode.createAStarNode(null, from, 0, controller.getMap().getMapDist(from, to)));

        while (openList.size > 0) {
            AStar.AStarNode least = null;
            for (AStar.AStarNode node: openList) {
                if (least == null || node.gx + node.hx < least.gx + least.hx) {
                    least = node;
                }
            }

            openList.removeValue(least, true);
            closedList.add(least);

            if (least.location.epsilonEquals(to, 0.1f)) {
                break;
            }

            for(Vector2 loc: HexMap.getAdjacent((int) least.location.x, (int) least.location.y)) {
                if (loc.epsilonEquals(to, 0.1f) || controller.getMap().getMapData().isBoardPositionTraversible((int)loc.x, (int)loc.y) && controller.isBoardPosEmpty(loc)) {
                    for (AStar.AStarNode node: closedList) {
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
                        if (limit == -1 || least.gx + 1 <= limit) {
                            openList.add(AStar.AStarNode.createAStarNode(least, loc, least.gx + 1, controller.getMap().getMapDist(loc, to)));
                        }
                    }
                }
            }
        }

        return (closedList.size > 0 && closedList.peek().location.epsilonEquals(to, 0.1f) ? closedList.peek() : null);
    }

    public static AStarNode getPath(Vector2 from, Vector2 to, GameController controller) {
        return getPath(from, to, controller, -1);
    }

    public static class AStarNode {
        public AStarNode parent;
        public Vector2 location;
        public float gx, hx;

        public static AStarNode createAStarNode(AStarNode parent, Vector2 location, float gx, float hx) {
            AStarNode node = new AStarNode();
            node.parent = parent;
            node.location = location;
            node.gx = gx;
            node.hx = hx;
            return node;
        }
    }
}
