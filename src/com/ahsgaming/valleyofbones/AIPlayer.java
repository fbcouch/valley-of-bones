/**
 * Copyright 2012 Jami Couch
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 * This project uses:
 * 
 * LibGDX
 * Copyright 2011 see LibGDX AUTHORS file
 * Licensed under Apache License, Version 2.0 (see above).
 * 
 */
package com.ahsgaming.valleyofbones;

import com.ahsgaming.valleyofbones.map.HexMap;
import com.ahsgaming.valleyofbones.network.*;
import com.ahsgaming.valleyofbones.units.Prototypes;
import com.ahsgaming.valleyofbones.units.Unit;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

import java.text.DecimalFormat;

/**
 * @author jami
 *
 */
public class AIPlayer extends Player {
	public String LOG = "AIPlayer";

    float countdown = 1;
    float timer = 1;

    float[] goalMatrix, unitMatrix;
    boolean[] visibilityMatrix;

    NetController netController;

	/**
	 * @param id
	 * @param name
	 * @param color
	 */
	public AIPlayer(NetController netController, int id, String name, Color color) {
		super(id, name, color);
        this.netController = netController;
	}

	/**
	 * @param id
	 * @param color
	 */
	public AIPlayer(NetController netController, int id, Color color) {
		super(id, color);
        this.netController = netController;
	}
	
	public AIPlayer(NetController netController, int id, String name, Color color, int team) {
		super(id, name, color, team);
        this.netController = netController;
	}
	
	@Override
	public void update(GameController controller) {
		super.update(controller);
        // only create goalMatrix once (based on knowledge of the map)
        if (goalMatrix == null)  {
            goalMatrix = createGoalMatrix(controller.getMap(), controller.getUnits());
        }

        if (controller.getCurrentPlayer().getPlayerId() == getPlayerId()) {
            timer -= Gdx.graphics.getDeltaTime();
            if (timer < 0) {
                timer = countdown;
                // first, create the visibility matrix
                if (visibilityMatrix == null) {
                    visibilityMatrix = createVisibilityMatrix(controller.getMap(), controller.getUnitsByPlayerId(getPlayerId()));
                    return;
                }

                // next, create the unit matrix
                if (unitMatrix == null) {
                    Array<Unit> visibleUnits = new Array<Unit>();
                    for (Unit unit: controller.getUnits()) {
                        if (visibilityMatrix[(int)(unit.getBoardPosition().y * controller.getMap().getWidth() + unit.getBoardPosition().x)]) {
                            visibleUnits.add(unit);
                        }
                    }
                    unitMatrix = createUnitMatrix(controller.getMap(), visibleUnits);
                    if (VOBGame.DEBUG_AI) {
                        DecimalFormat formatter = new DecimalFormat("00.0");
                        for (int y = 0; y < controller.getMap().getHeight(); y++) {
                            if (y % 2 == 1) {
                                System.out.print("   ");
                            }
                            for (int x = 0; x < controller.getMap().getWidth(); x++) {
                                int i = y * controller.getMap().getWidth() + x;
                                float sum = (visibilityMatrix[i] ? goalMatrix[i] + unitMatrix[i] : 0);
                                System.out.print((sum >= 0 ? " " : "") + formatter.format(sum) + " ");
                            }
                            System.out.println("\n");
                        }
                    }
                    return;
                }

                // move units
                for (Unit unit: controller.getUnitsByPlayerId(getPlayerId())) {
                    if (unit.getMovesLeft() < 1) continue;
                    Vector2[] adjacent = controller.getMap().getAdjacent((int)unit.getBoardPosition().x, (int)unit.getBoardPosition().y);
                    Vector2 finalPos = new Vector2(unit.getBoardPosition());
                    float finalSum = goalMatrix[(int)finalPos.y * controller.getMap().getWidth() + (int)finalPos.x]
                            + unitMatrix[(int)finalPos.y * controller.getMap().getWidth() + (int)finalPos.x];
                    for (Vector2 v: adjacent) {
                        if (v.x < 0 || v.x >= controller.getMap().getWidth() || v.y < 0 || v.y >= controller.getMap().getHeight()) continue;
                        float curSum = goalMatrix[(int)v.y * controller.getMap().getWidth() + (int)v.x]
                                + unitMatrix[(int)v.y * controller.getMap().getWidth() + (int)v.x];
                        if (curSum > finalSum && controller.isBoardPosEmpty(v)) {
                            finalPos.set(v);
                            finalSum = curSum;
                        }
                    }
                    if (finalPos.x != unit.getBoardPosition().x || finalPos.y != unit.getBoardPosition().y) {
                        // move!
                        Move mv = new Move();
                        mv.owner = getPlayerId();
                        mv.turn = controller.getGameTurn();
                        mv.unit = unit.getObjId();
                        mv.toLocation = finalPos;
                        netController.sendAICommand(mv);
                        unitMatrix = null;
                        return;
                    }
                }

                // attack
                Array<Unit> visibleUnits = new Array<Unit>();
                for (Unit unit: controller.getUnits()) {
                    if (unit.getOwner() == this || unit.getOwner() == null) continue;
                    if (visibilityMatrix[(int)(unit.getBoardPosition().y * controller.getMap().getWidth() + unit.getBoardPosition().x)]) {
                        visibleUnits.add(unit);
                    }
                }
                for (Unit unit: controller.getUnitsByPlayerId(getPlayerId())) {
                    if (unit.getAttacksLeft() < 1) continue;
                    Unit toAttack = null;
                    for (Unit o: visibleUnits) {
                        if (unit.canAttack(o, controller) && (toAttack == null || o.getMaxHP() > toAttack.getMaxHP())) {
                            toAttack = o;
                        }
                    }
                    if (toAttack != null) {
                        Attack at = new Attack();
                        at.owner = getPlayerId();
                        at.turn = controller.getGameTurn();
                        at.unit = unit.getObjId();
                        at.target = toAttack.getObjId();
                        netController.sendAICommand(at);
                        unitMatrix = null;
                        return;
                    }
                }

                // build units
                Prototypes.JsonProto unitToBuild = null;
                for (Prototypes.JsonProto proto: Prototypes.getPlayerCanBuild()) {
                    if ((proto.cost <= getBankMoney() && proto.food <= getMaxFood() - getCurFood()) && (unitToBuild == null || proto.cost > unitToBuild.cost)) {
                        unitToBuild = proto;
                    }
                }
                int positionToBuild = -1;
                if (unitToBuild != null) {
                    for (int i = 0; i < unitMatrix.length; i ++) {
                        if (visibilityMatrix[i]) {
                            float sum = unitMatrix[i] + goalMatrix[i];

                            if ((positionToBuild == -1 || unitMatrix[positionToBuild] + goalMatrix[positionToBuild] < sum)
                                    /*&& controller.isBoardPosEmpty(
                                            positionToBuild % controller.getMap().getWidth(),
                                            positionToBuild / controller.getMap().getWidth())
                                    */) {
                                Gdx.app.log(LOG, i + ":" + controller.isBoardPosEmpty(
                                        i % controller.getMap().getWidth(),
                                        i / controller.getMap().getWidth()));
                                if (controller.isBoardPosEmpty(i % controller.getMap().getWidth(), i / controller.getMap().getWidth())) {
                                    positionToBuild = i;
                                }
                            }
                        }
                    }
                }
                if (positionToBuild >= 0) {
                    Build build = new Build();
                    build.owner = getPlayerId();
                    build.turn = controller.getGameTurn();
                    build.building = unitToBuild.id;
                    build.location = new Vector2(positionToBuild % controller.getMap().getWidth(), positionToBuild / controller.getMap().getWidth());
                    netController.sendAICommand(build);
                    unitMatrix = null;
                    return;
                }

                EndTurn et = new EndTurn();
                et.owner = getPlayerId();
                et.turn = controller.getGameTurn();
                netController.sendAICommand(et);
            }
        } else {
            visibilityMatrix = null;
            unitMatrix = null;
        }

	}

    public float[] createGoalMatrix(HexMap map, Array<Unit> units) {
        int mapWidth = map.getWidth(), mapHeight = map.getHeight();
        float[] goalMatrix = new float[mapWidth * mapHeight];

        for (int y = 0; y < mapHeight; y++) {
            for (int x = 0; x < mapWidth; x++) {
                goalMatrix[y * mapWidth + x] = (map.isBoardPositionTraversible(x, y) ? calcGoalMatrix(x, y, map, units) : -1);
            }
        }

        return goalMatrix;
    }

    public float calcGoalMatrix(int x, int y, HexMap map, Array<Unit> units) {
        float total = 0;
        for (Unit unit: units) {
            if (unit.getBoardPosition().x == x && unit.getBoardPosition().y == y)
                total += unit.getMaxHP();
            else
                total += unit.getMaxHP() / Math.pow(map.getMapDist(new Vector2(x, y), unit.getBoardPosition()), 2);
        }
        return total;
    }

    public boolean[] createVisibilityMatrix(HexMap map, Array<Unit> units) {
        boolean[] matrix = new boolean[map.getWidth() * map.getHeight()];
        for (Unit unit: units) {
            int range = unit.getAttackRange();
            for (int x = (int)Math.max(unit.getBoardPosition().x - range - 1, 0); x < Math.min(unit.getBoardPosition().x + range + 1, map.getWidth()); x++) {
                for (int y = (int)Math.max(unit.getBoardPosition().y - range - 1, 0); y < Math.min(unit.getBoardPosition().y + range + 1, map.getHeight()); y++) {
                    if (map.getMapDist(new Vector2(x, y), unit.getBoardPosition()) <= range)
                        matrix[y * map.getWidth() + x] = true;
                }
            }
        }
        return matrix;
    }

    public float[] createUnitMatrix(HexMap map, Array<Unit> units) {
        float[] unitMatrix = new float[map.getWidth() * map.getHeight()];
        for (int y = 0; y < map.getHeight(); y++) {
            for (int x = 0; x < map.getWidth(); x++) {
                unitMatrix[y * map.getWidth() + x] = calcUnitMatrix(x, y, map, units);
            }
        }
        return unitMatrix;
    }

    public float calcUnitMatrix(int x, int y, HexMap map, Array<Unit> units) {
        float total = 0;
        for (Unit unit: units) {
            int mul = (unit.getOwner() == this ? -1 : 1);
            if (x == unit.getBoardPosition().x && y == unit.getBoardPosition().y) {
                total += (unit.getMaxHP() * mul);
                return -1 * unit.getMaxHP();
            } else {
                total += (mul * unit.getMaxHP() / map.getMapDist(new Vector2(x, y), unit.getBoardPosition()));
            }
        }
        return total;
    }
}
