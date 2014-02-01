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
package com.ahsgaming.valleyofbones.ai;

import com.ahsgaming.valleyofbones.GameController;
import com.ahsgaming.valleyofbones.Player;
import com.ahsgaming.valleyofbones.Utils;
import com.ahsgaming.valleyofbones.VOBGame;
import com.ahsgaming.valleyofbones.map.HexMap;
import com.ahsgaming.valleyofbones.network.*;
import com.ahsgaming.valleyofbones.units.Prototypes;
import com.ahsgaming.valleyofbones.units.Unit;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;

import java.text.DecimalFormat;
import java.util.HashMap;

/**
 * @author jami
 *
 */
public class AIPlayer extends Player {
	public String LOG = "AIPlayer";

    float countdown = 0.5f;
    float timer = 0.5f;

    float[] goalMatrix, threatMatrix, valueMatrix;
    boolean[] visibilityMatrix;

    NetController netController;
    GenomicAI genome;

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
	
	@Override
	public void update(GameController controller, float delta) {
		super.update(controller, delta);

        if (genome == null) {
            Json json = new Json();
            genome = json.fromJson(GenomicAI.class, Gdx.files.internal("ai/xjix3xuv").readString());
            Gdx.app.log(LOG, "Loaded ai: " + genome.id);
        }

        if (controller.getCurrentPlayer().getPlayerId() == getPlayerId()) {
            timer -= delta;
            if (timer < 0) {
                timer = countdown;

                if (VOBGame.DEBUG_AI) Gdx.app.log(LOG, "start...");
                // first, create the visibility matrix
                if (visibilityMatrix == null) {
                    if (VOBGame.DEBUG_AI) Gdx.app.log(LOG, "...create visibility matrix");
                    visibilityMatrix = createVisibilityMatrix(controller.getMap(), controller.getUnitsByPlayerId(getPlayerId()));
                    return;
                }

                // next, create the value matrix and threat matrix
                if (valueMatrix == null) {
                    Array<Unit> visibleUnits = new Array<Unit>();
                    for (Unit unit: controller.getUnits()) {
                        if (visibilityMatrix[(int)(unit.getView().getBoardPosition().y * controller.getMap().getWidth() + unit.getView().getBoardPosition().x)]) {
                            visibleUnits.add(unit);
                        }
                    }
                    if (VOBGame.DEBUG_AI) Gdx.app.log(LOG, "...create value/threat matrices");
                    valueMatrix = createUnitMatrix(controller.getMap(), visibleUnits, false);
                    threatMatrix = createUnitMatrix(controller.getMap(), visibleUnits, true);
                    return;
                }

                // create goalMatrix (based on knowledge of the map)
                if (goalMatrix == null)  {
                    if (VOBGame.DEBUG_AI) Gdx.app.log(LOG, "...create goal matrix");
                    goalMatrix = createGoalMatrix(controller.getMap(), controller.getUnits());

                    if (VOBGame.DEBUG_AI) {
                        DecimalFormat formatter = new DecimalFormat("00.0");
                        for (int y = 0; y < controller.getMap().getHeight(); y++) {
                            if (y % 2 == 1) {
                                System.out.print("   ");
                            }
                            for (int x = 0; x < controller.getMap().getWidth(); x++) {
                                int i = y * controller.getMap().getWidth() + x;
                                float sum = (visibilityMatrix[i] ? goalMatrix[i] + valueMatrix[i] + threatMatrix[i] : 0);
                                System.out.print((sum >= 0 ? " " : "") + formatter.format(sum) + " ");
                            }
                            System.out.println("\n");
                        }
                    }

                    return;
                }

                // move units
                for (Unit unit: controller.getUnitsByPlayerId(getPlayerId())) {
                    if (unit.getData().getMovesLeft() < 1) continue;
                    Vector2[] adjacent = HexMap.getAdjacent((int) unit.getView().getBoardPosition().x, (int) unit.getView().getBoardPosition().y);
                    Vector2 finalPos = new Vector2(unit.getView().getBoardPosition());
                    float finalSum = goalMatrix[(int)finalPos.y * controller.getMap().getWidth() + (int)finalPos.x]
                            + valueMatrix[(int)finalPos.y * controller.getMap().getWidth() + (int)finalPos.x]
                            + threatMatrix[(int)finalPos.y * controller.getMap().getWidth() + (int)finalPos.x];
                    for (Vector2 v: adjacent) {
                        if (v.x < 0 || v.x >= controller.getMap().getWidth() || v.y < 0 || v.y >= controller.getMap().getHeight()) continue;
                        float curSum = goalMatrix[(int)v.y * controller.getMap().getWidth() + (int)v.x]
                                + valueMatrix[(int)v.y * controller.getMap().getWidth() + (int)v.x]
                                + threatMatrix[(int)v.y * controller.getMap().getWidth() + (int)v.x];
                        if (curSum > finalSum && controller.isBoardPosEmpty(v)) {
                            finalPos.set(v);
                            finalSum = curSum;
                        }
                    }
                    if (finalPos.x != unit.getView().getBoardPosition().x || finalPos.y != unit.getView().getBoardPosition().y) {
                        // move!
                        Move mv = new Move();
                        mv.owner = getPlayerId();
                        mv.turn = controller.getGameTurn();
                        mv.unit = unit.getId();
                        mv.toLocation = finalPos;
                        netController.sendAICommand(mv);
                        valueMatrix = null;
                        threatMatrix = null;
                        return;
                    }
                }

                // attack
                Array<Unit> visibleUnits = new Array<Unit>();
                Array<Unit> friendlyUnits = controller.getUnitsByPlayerId(this.getPlayerId());
                for (Unit unit: controller.getUnits()) {
                    if (unit.getOwner() == this || unit.getOwner() == null) continue;
                    if (visibilityMatrix[(int)(unit.getView().getBoardPosition().y * controller.getMap().getWidth() + unit.getView().getBoardPosition().x)]
                            && !unit.getData().isInvisible() || controller.getMap().detectorCanSee(this, friendlyUnits, unit.getView().getBoardPosition())) {
                        visibleUnits.add(unit);
                    }
                }
                for (Unit unit: controller.getUnitsByPlayerId(getPlayerId())) {
                    if (unit.getData().getAttacksLeft() < 1) continue;
                    Unit toAttack = null;
                    float toAttackThreat = 0;
                    for (Unit o: visibleUnits) {
                        float thisThreat = threatMatrix[controller.getMap().getWidth() * (int)o.getView().getBoardPosition().y + (int)o.getView().getBoardPosition().x];
                        if (controller.getUnitManager().canAttack(unit, o) && (toAttack == null || thisThreat > toAttackThreat)) {
                            toAttack = o;
                            toAttackThreat = thisThreat;
                        }
                    }
                    if (toAttack != null) {
//                        Gdx.app.log(LOG + "$attack", String.format("%s --> %s", unit.getProtoId(), toAttack.getProtoId()));
                        Attack at = new Attack();
                        at.owner = getPlayerId();
                        at.turn = controller.getGameTurn();
                        at.unit = unit.getId();
                        at.target = toAttack.getId();
                        netController.sendAICommand(at);
                        valueMatrix = null;
                        threatMatrix = null;
                        return;
                    }
                }

                // build units // TODO build other than marines (ie: implement chromosome 11)

                int positionToBuild = -1;
                for (int i = 0; i < valueMatrix.length; i ++) {
                    if (visibilityMatrix[i]) {
                        float sum = threatMatrix[i] + valueMatrix[i] + goalMatrix[i];

                        if ((positionToBuild == -1 || valueMatrix[positionToBuild] + valueMatrix[positionToBuild] + goalMatrix[positionToBuild] < sum)) {
//                                Gdx.app.log(LOG, i + ":" + controller.isBoardPosEmpty(
//                                        i % controller.getMap().getWidth(),
//                                        i / controller.getMap().getWidth()));
                            if (controller.isBoardPosEmpty(i % controller.getMap().getWidth(), i / controller.getMap().getWidth())) {
                                positionToBuild = i;
                            }
                        }
                    }
                }
                Prototypes.JsonProto unitToBuild = null;

                if (positionToBuild >= 0) {
                    Vector2 buildPosition = new Vector2(positionToBuild % controller.getMap().getWidth(), positionToBuild / controller.getMap().getWidth());
                    Array<String> protoIds = new Array<String>();
                    HashMap<String, Float> buildScores = new HashMap<String, Float>();
                    for (Prototypes.JsonProto proto: Prototypes.getAll()) {
                        protoIds.add(proto.id);
                        if (proto.cost > 0) {
                            buildScores.put(proto.id, 0f);
                        }
                    }

                    for (Unit unit: controller.getUnits()) {
                        if (unit.getOwner() == this || !visibilityMatrix[(int)(unit.getView().getBoardPosition().y * controller.getMap().getWidth() + unit.getView().getBoardPosition().x)]) continue;

                        int unitIndex = protoIds.indexOf(unit.getProto().id, false);
                        int unitDistance = controller.getMap().getMapDist(unit.getView().getBoardPosition(), buildPosition);
                        for (String key: buildScores.keySet()) {
                            buildScores.put(key, buildScores.get(key) + ((Array<Float>)genome.chromosomes.get(10).genes.get(key)).get(unitIndex) / unitDistance);
                        }
                    }

                    String maxScore = null;
                    float maxBuildScore = 0;
                    while(unitToBuild == null && buildScores.keySet().size() > 0) {
                        for (String id: buildScores.keySet()) {
                            if (maxScore == null || buildScores.get(id) > buildScores.get(maxScore)) {
                                maxScore = id;
                                if (buildScores.get(id) > maxBuildScore) {
                                    maxBuildScore = buildScores.get(id);
                                }
                            }
                        }

                        if (!maxScore.equals("saboteur") && buildScores.get(maxScore) > 0 && buildScores.get(maxScore) >= maxBuildScore - (Float)genome.chromosomes.get(10).genes.get("wait") && getBankMoney() >= Prototypes.getProto(maxScore).cost) {
                            unitToBuild = Prototypes.getProto(maxScore);
                        } else {
                            buildScores.remove(maxScore);
                            maxScore = null;
                        }

                    }
                }

                if (unitToBuild != null) {
                    Build build = new Build();
                    build.owner = getPlayerId();
                    build.turn = controller.getGameTurn();
                    build.building = unitToBuild.id;
                    build.location = new Vector2(positionToBuild % controller.getMap().getWidth(), positionToBuild / controller.getMap().getWidth());
                    netController.sendAICommand(build);
                    valueMatrix = null;
                    return;
                }

                EndTurn et = new EndTurn();
                et.owner = getPlayerId();
                et.turn = controller.getGameTurn();
                netController.sendAICommand(et);
            }
        } else {
            visibilityMatrix = null;
            valueMatrix = null;
            threatMatrix = null;
            goalMatrix = null;
        }

	}

    public float[] createGoalMatrix(HexMap map, Array<Unit> units) {
        int mapWidth = map.getWidth(), mapHeight = map.getHeight();
        float[] goalMatrix = new float[mapWidth * mapHeight];

        for (int y = 0; y < mapHeight; y++) {
            for (int x = 0; x < mapWidth; x++) {
                goalMatrix[y * mapWidth + x] = (map.getMapData().isBoardPositionTraversible(x, y) ? calcGoalMatrix(x, y, map, units) : -1);
            }
        }

        return goalMatrix;
    }

    public float calcGoalMatrix(int x, int y, HexMap map, Array<Unit> units) {
        float total = 0;
        for (Unit unit: units) {
            if (unit.getData().getType().equals("building")) {
                int dist = map.getMapDist(unit.getView().getBoardPosition(), new Vector2(x, y));
                Float value = 0f;
                Array<Float> coeff_array = new Array<Float>();
                if (unit.getProto().id.equals("tower")) {
                    value = (Float)genome.chromosomes.get(9).genes.get("tower_val");
                    if (!visibilityMatrix[(int)(unit.getView().getBoardPosition().y * map.getWidth() + unit.getView().getBoardPosition().x)] || unit.getOwner() == null) {
                        // neutral tower
                        coeff_array = (Array<Float>)genome.chromosomes.get(9).genes.get("tower_n_coeff");
                    } else if (unit.getOwner() == this) {
                        // friendly tower
                        coeff_array = (Array<Float>)genome.chromosomes.get(9).genes.get("tower_f_coeff");
                    } else {
                        // enemy tower
                        coeff_array = (Array<Float>)genome.chromosomes.get(9).genes.get("tower_e_coeff");
                    }
                } else {

                    if (unit.getOwner() == this) {
                        // friendly castle
                        value = threatMatrix[(int)(unit.getView().getBoardPosition().y * map.getWidth() + unit.getView().getBoardPosition().x)];
                        coeff_array = (Array<Float>)genome.chromosomes.get(9).genes.get("castle_f_coeff");
                    } else {
                        // enemy castle
                        value = (Float)genome.chromosomes.get(9).genes.get("castle_e_value");
                        coeff_array = (Array<Float>)genome.chromosomes.get(9).genes.get("castle_e_coeff");
                    }
                }
                total += calc_value(value, dist, coeff_array);
            }
        }
        return total;
    }

    public float calc_value(float value, int distance, Array<Float> coeff_array) {
        float total = 0;
        if (distance == 0) {
            if (coeff_array.size > 0) {
                return value * coeff_array.get(0);
            }
            return 0;
        }

        for (int i = 0; i < coeff_array.size; i++) {
            total += value / Math.pow(distance, i) * coeff_array.get(i);
        }

        return total;
    }

    public boolean[] createVisibilityMatrix(HexMap map, Array<Unit> units) {
        boolean[] matrix = new boolean[map.getWidth() * map.getHeight()];
        for (Unit unit: units) {
            if (unit.getData().isBuilding()) continue;
            int range = unit.getData().getSightRange();
            for (int x = (int)Math.max(unit.getView().getBoardPosition().x - range - 1, 0); x < Math.min(unit.getView().getBoardPosition().x + range + 1, map.getWidth()); x++) {
                for (int y = (int)Math.max(unit.getView().getBoardPosition().y - range - 1, 0); y < Math.min(unit.getView().getBoardPosition().y + range + 1, map.getHeight()); y++) {
                    if (map.getMapDist(new Vector2(x, y), unit.getView().getBoardPosition()) <= range)
                        matrix[y * map.getWidth() + x] = true;
                }
            }
        }
        return matrix;
    }

    public float[] createUnitMatrix(HexMap map, Array<Unit> units, boolean threat) {
        float[] unitMatrix = new float[map.getWidth() * map.getHeight()];
        for (int y = 0; y < map.getHeight(); y++) {
            for (int x = 0; x < map.getWidth(); x++) {
                unitMatrix[y * map.getWidth() + x] = calcUnitMatrix(x, y, map, units, threat);
            }
        }
        return unitMatrix;
    }

    public float calcUnitMatrix(int x, int y, HexMap map, Array<Unit> units, boolean threat) {
        float total = 0;
        for (Unit unit: units) {
            if (unit.getOwner() == this) {
                int i = Prototypes.getAll().indexOf(Prototypes.getProto(unit.getProto().id), true);
                total += calc_value(
                        (Float)genome.chromosomes.get(i).genes.get((threat ? "threat" : "value")),   // TODO this is a horrible way to look this up
                        map.getMapDist(unit.getView().getBoardPosition(), new Vector2(x, y)),
                        (Array<Float>)genome.chromosomes.get(i).genes.get((threat ? "threat_coeff" : "value_coeff"))
                );
            }
        }
        return total;
    }

    public GenomicAI getGenome() {
        return genome;
    }

    public void setGenome(GenomicAI genome) {
        this.genome = genome;
    }

    @Override
    public boolean isLoaded() {
        return true;
    }

    @Override
    public boolean isReady() {
        return true;
    }

    public static class GenomicAI {
        public String id;
        public int wins, losses;
        public Array<Chromosome> chromosomes = new Array<Chromosome>();

        public void mutate(float rate) {
            for (Chromosome chromo: chromosomes) {
                for (String gene: chromo.genes.keySet()) {
                    Object val = chromo.genes.get(gene);
                    if (val instanceof Float) {
                        chromo.genes.put(gene, mutatedValue((Float)val, rate, 10f));
                    } else if (val instanceof Array) {
                        Array<Float> valArray = (Array<Float>)val;
                        for (int i = 0; i < valArray.size; i++) {
                            valArray.set(i, mutatedValue(valArray.get(i), rate, 10f));
                        }
                    }
                }
            }
        }

        float mutatedValue(float currentValue, float mutationRate, float mutationCoeff) {
            if (Math.random() < mutationRate) {
                return currentValue + (float)((Math.random() < 0.5 ? 1 : -1) * Math.pow((Math.random() * 2 - 1), 2) * mutationCoeff);
            }
            return currentValue;
        }

        public static GenomicAI generateRandom() {
            GenomicAI ai = new GenomicAI();

            ai.id = Utils.getRandomId(8);
            ai.wins = 0;
            ai.losses = 0;

            float range = 10f;

            for (Prototypes.JsonProto proto: Prototypes.getAll()) {
                Chromosome chromo = new Chromosome();

                chromo.genes.put("threat", (float)Math.random() * range - (range / 2));
                Array<Float> coeff_array = new Array<Float>(5);
                for (int i = 1; i <= 5; i++)
                    coeff_array.add((float)(Math.random() * range - (range / 2))/ i);
                chromo.genes.put("threat_coeff", coeff_array);
                coeff_array = new Array<Float>(5);;

                chromo.genes.put("value", (float)(Math.random() * range - (range / 2)));
                for (int i = 1; i <= 5; i++)
                    coeff_array.add((float)(Math.random() * range - (range / 2)));
                chromo.genes.put("value_coeff", coeff_array);

                ai.chromosomes.add(chromo);
            }

            Chromosome goal_chromo = new Chromosome();
            Array<Float> coeff_array = new Array<Float>(5);

            goal_chromo.genes.put("tower_val", (float)(Math.pow(Math.random() * range, 2)));

            for (int i = 1; i <= 5; i++)
                coeff_array.add((float)(Math.random() * range - (range / 2)) / i);
            goal_chromo.genes.put("tower_e_coeff", coeff_array);
            coeff_array = new Array<Float>(5);;

            for (int i = 1; i <= 5; i++)
                coeff_array.add((float)(Math.random() * range - (range / 2)) / i);
            goal_chromo.genes.put("tower_n_coeff", coeff_array);
            coeff_array = new Array<Float>(5);;

            for (int i = 1; i <= 5; i++)
                coeff_array.add((float)(Math.random() * range - (range / 2)) / i);
            goal_chromo.genes.put("tower_f_coeff", coeff_array);
            coeff_array = new Array<Float>(5);;

            goal_chromo.genes.put("castle_e_value", (float)(Math.pow(Math.random() * range, 2)));

            for (int i = 1; i <= 5; i++)
                coeff_array.add((float)(Math.random() * range - (range / 2)) / i);
            goal_chromo.genes.put("castle_e_coeff", coeff_array);
            coeff_array = new Array<Float>(5);;

            for (int i = 1; i <= 5; i++)
                coeff_array.add((float)(Math.random() * range - (range / 2)) / i);
            goal_chromo.genes.put("castle_f_coeff", coeff_array);
            coeff_array = new Array<Float>(5);;

            ai.chromosomes.add(goal_chromo);

            Chromosome build_chromo = new Chromosome();

            Array<Prototypes.JsonProto> protoArray = Prototypes.getAll();

            for (Prototypes.JsonProto proto: protoArray) {
                if (proto.cost > 0) {
                    // buildable
                    for (int i = 0; i < protoArray.size; i++)
                        coeff_array.add((float)Math.random() * range - (range / 2));
                    build_chromo.genes.put(proto.id, coeff_array);
                    coeff_array = new Array<Float>(5);;
                }
            }

            build_chromo.genes.put("wait", (float)Math.random() * 10f);

            ai.chromosomes.add(build_chromo);

            return ai;
        }

        public static GenomicAI clone(GenomicAI genome) {
            Json json = new Json();

            GenomicAI child = json.fromJson(GenomicAI.class, json.toJson(genome));
            child.id = Utils.getRandomId(8);
            child.wins = 0;
            child.losses = 0;

            return child;
        }

        public static Array<GenomicAI> breed(int offspring, Array<GenomicAI> parents, float mutationRate) {
            Array<GenomicAI> children = new Array<GenomicAI>();
            for (int i = 0; i < offspring; i++) {
                GenomicAI child = new GenomicAI();
                child.id = Utils.getRandomId(8);
                children.add(child);

                for (int c = 0; c < parents.get(0).chromosomes.size; c++) {
                    Chromosome chromo = new Chromosome();
                    child.chromosomes.add(chromo);
                    for (String gene: parents.get(0).chromosomes.get(c).genes.keySet()) {
                        int p = (int)Math.floor(Math.random() * parents.size);
                        chromo.genes.put(gene, parents.get(p).chromosomes.get(c).genes.get(gene));
                    }
                }
            }

            Json json = new Json();
            for (int i = 0; i < children.size; i++) {
                children.set(i, json.fromJson(GenomicAI.class, json.toJson(children.get(i))));
                children.get(i).mutate(mutationRate);
            }

            return children;
        }
    }

    public static class Chromosome {
        HashMap<String, Object> genes = new HashMap<String, Object>();
    }
}
