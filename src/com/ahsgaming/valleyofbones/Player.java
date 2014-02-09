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

import com.ahsgaming.valleyofbones.network.Build;
import com.ahsgaming.valleyofbones.network.Command;
import com.ahsgaming.valleyofbones.network.KryoCommon;
import com.ahsgaming.valleyofbones.network.Upgrade;
import com.ahsgaming.valleyofbones.units.Prototypes;
import com.ahsgaming.valleyofbones.units.Prototypes.JsonProto;
import com.ahsgaming.valleyofbones.units.Unit;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.TimeUtils;

import java.util.HashMap;

/**
 * @author jami
 * TODO theoretically, this class should probably handle inputs for the player or something...
 */
public class Player {
	public String LOG = "Player"; 
	public static final Color COLOR_RED = new Color(1, 0.2f, 0.2f, 1);
	public static final Color COLOR_BLUE = new Color(0.3f, 0.3f, 1, 1);
	public static final Color COLOR_GREEN = new Color(0, 0.8f, 0, 1);
	public static final Color COLOR_PURPLE = new Color(0.6f, 0, 1, 1);
	public static final Color[] AUTOCOLORS = {
            COLOR_RED,
            COLOR_BLUE,
            COLOR_GREEN,
            COLOR_PURPLE,
            new Color(0.0f, 0.5f, 0.5f, 1.0f), // teal
            new Color(1f, 0.4f, 0f, 1.0f) // orange
    };

    boolean isLoaded = false;
    boolean isReady = false;
    boolean isAI = false;
	
	int playerId = -1;
	Color playerColor = new Color(1, 1, 1, 1);
	String name = "New Cadet";
    String key = "";
    int pauses = 0;
	
	float bankMoney = 0, upkeep = 0;
	int curFood = 0, maxFood = 0;
	
	int teamId = 0;
	
	Unit baseUnit = null; // when this unit dies, this player is out (in some game types)
	
	/**
	 * 
	 */
	public Player(int id, String name, Color color) {
		this(id, color);
		this.name = name;
	}

    public Player(int id, String name, Color color, String key) {
        this(id, name, color);
        this.key = key;
    }
	
	public Player(int id, Color color) {
		playerId = id;
		playerColor = color;
	}

    public void startTurn(GameController controller) {
        updateFoodAndUpkeep(controller, true);
    }

	public void update(GameController controller, float delta) {
        updateFoodAndUpkeep(controller, false);
	}
	
	public void updateFood(GameController controller) {
		updateFoodAndUpkeep(controller, false);
	}
	
	public void updateFoodAndUpkeep(GameController controller, boolean updateBank) {
		float upkeep = 0;
		int food = 0, mFood = 0;
        Array<Unit> units = controller.getUnitsByPlayerId(playerId);
        HashMap<String, Integer> unitCount = new HashMap<String, Integer>();
		for (Unit unit: units) {
            if (!unitCount.containsKey(unit.getProto().id)) {
                unitCount.put(unit.getProto().id, 0);
            }
            unitCount.put(unit.getProto().id, unitCount.get(unit.getProto().id) + 1);
            int count = unitCount.get(unit.getProto().id);
            int unitFood = 0;
            float unitUpkeep = 0;
            if (count > unit.getData().getFood().size) {
                unitFood = unit.getData().getFood().peek();
            } else {
                unitFood = unit.getData().getFood().get(count - 1);
            }
			if (unitFood < 0) mFood -= unitFood;
			else food += unitFood;

            if (count > unit.getData().getUpkeep().size) {
                unitUpkeep = unit.getData().getUpkeep().peek();
            } else {
                unitUpkeep = unit.getData().getUpkeep().get(count - 1);
            }
			
			upkeep += unitUpkeep;
		}
		curFood = food;
		maxFood = mFood;
		this.upkeep = upkeep;

        if (controller.getGameTurn() == 1 && upkeep < 0) {
            upkeep *= 0.5f;
            if (upkeep > -45) upkeep = -45;
        }

		if (updateBank) bankMoney -= upkeep;
	}
	
	public boolean canBuild(String protoId, GameController controller) {
		JsonProto proto = Prototypes.getProto(protoId);

		if (!checkRequirements(proto, controller)) return false; // TODO speed this up?

        // TODO took out checkFoodAndCost for now because it is very expensive

		return (proto.cost > 0 && proto.cost <= bankMoney && curFood + proto.food <= maxFood);
	}
	
	public boolean canUpgrade(Unit unit, String protoId, GameController controller) {
		JsonProto proto = Prototypes.getProto(protoId);
		
		// TODO check if the upgrade can apply to this unit
		
		if (!checkRequirements(proto, controller)) return false;
		
		// figure out if food/cost limitations are OK
		int food = 0, cost = 0;
		if (proto.hasProperty("food"))
			food = (int)Float.parseFloat(proto.getProperty("food").toString());
		if (proto.hasProperty("cost"))
			cost = (int)Float.parseFloat(proto.getProperty("cost").toString());
		
		return checkFoodAndCost(food, cost, controller);
	}
	
	public boolean hasAUnit(String id, GameController controller) {
		Array<Unit> units = controller.getUnitsByPlayerId(getPlayerId());
		for (Unit u: units) {
			if (u.getProto().id.equals(id))
				return true;
		}
		return false;
	}
	
	public boolean checkFoodAndCost(int food, int cost, GameController controller) {
		int qFood = 0, qCost = 0;
		Command c = null;
		JsonProto proto = null;
		for (int i=0;i<controller.getCommandQueue().size;i++) {
			c = controller.getCommandQueue().get(i);
			if (c.owner != getPlayerId()) continue;

			proto = null;
			if (c instanceof Build) {
				proto = Prototypes.getProto(((Build)c).building);

			} else if (c instanceof Upgrade) {
				proto = Prototypes.getProto(((Upgrade)c).upgrade);
			}

			if (proto != null && proto.hasProperty("food")) {
				int foodToAdd = (int)Float.parseFloat(proto.getProperty("food").toString());
				qFood += (foodToAdd > 0 ? foodToAdd: 0);	// cannot borrow against future food
			}

			if (proto != null && proto.hasProperty("cost")) {
				int costToAdd = (int)Float.parseFloat(proto.getProperty("cost").toString());
				qCost += (costToAdd > 0 ? costToAdd: 0);	// cannot borrow against future cost (theoretically - that shouldn't really happen)
			}
		}
		
		return ((food <= 0 || food <= maxFood - curFood - qFood) && (cost <= 0 || bankMoney >= cost + qCost));
	}
	
	public boolean checkRequirements(JsonProto proto, GameController controller) {
		// check requirements
		if (proto.hasProperty("requires")) {
			JsonValue requires = proto.getProperty("requires");
			for (Object o: requires) {
				if (!hasAUnit(o.toString(), controller)) return false;
			}
		}
		return true;
	}
	
	
	public String getPlayerName() {
		return name;
	}
	
	@Override
	public String toString() {
		return String.format("(%d)%s $%04d // Food: %02d/%02d", playerId, name, (int)bankMoney, curFood, maxFood);
	}
	
	
	public void setPlayerName(String name) {
		this.name = name;
	}
	
	public int getPlayerId() {
		return playerId;
	}
	
	public void setPlayerId(int id) {
		playerId = id;
	}
	
	public Color getPlayerColor() {
		return playerColor;
	}
	
	public void setPlayerColor(Color color) {
		playerColor = color;
	}

	/**
	 * @return the bankMoney
	 */
	public float getBankMoney() {
		return bankMoney;
	}

	/**
	 * @param bankMoney the bankMoney to set
	 */
	public void setBankMoney(float bankMoney) {
		this.bankMoney = bankMoney;
	}

	/**
	 * @return the rateMoney
	 */
	public float getRateMoney() {
		return upkeep;
	}

	/**
	 * @param rateMoney the rateMoney to set
	 */
	public void setRateMoney(float rateMoney) {
		this.upkeep = rateMoney;
	}

	/**
	 * @return the curFood
	 */
	public int getCurFood() {
		return curFood;
	}

	/**
	 * @param curFood the curFood to set
	 */
	public void setCurFood(int curFood) {
		this.curFood = curFood;
	}

	/**
	 * @return the maxFood
	 */
	public int getMaxFood() {
		return maxFood;
	}

	/**
	 * @param maxFood the maxFood to set
	 */
	public void setMaxFood(int maxFood) {
		this.maxFood = maxFood;
	}

    public int getPauses() {
        return pauses;
    }

    public void setPauses(int pauses) {
        this.pauses = pauses;
    }

    public void addPause() { pauses++; }


    public int getTeam() {
		return teamId;
	}
	
	public void setTeam(int teamId) {
		this.teamId = teamId;
	}
	
	public static Color getUnusedColor(Array<Player> players) {
		Array<Color> usedColors = new Array<Color>();
		for (Player p: players) {
			usedColors.add(p.getPlayerColor());
		}
		
		Color use = Player.AUTOCOLORS[0];
		for (Color color: Player.AUTOCOLORS) {
			if (!usedColors.contains(color, true)) {
				use = color;
				break;
			}
		}
		
		return use;
	}

    public static int getUnusedColorId(Array<KryoCommon.RegisteredPlayer> players) {
        boolean[] colorsUsed = new boolean[Player.AUTOCOLORS.length];
        for (KryoCommon.RegisteredPlayer p: players) {
            colorsUsed[p.color] = true;
        }

        int i = 0;
        while (i < Player.AUTOCOLORS.length && colorsUsed[i]) { i++; }
        i = Math.min(i, Player.AUTOCOLORS.length - 1);
        return i;
    }

	/**
	 * @return the baseUnit
	 */
	public Unit getBaseUnit() {
		return baseUnit;
	}

	/**
	 * @param baseUnit the baseUnit to set
	 */
	public void setBaseUnit(Unit baseUnit) {
		this.baseUnit = baseUnit;
	}
	
	/**
	 * Return true if the base unit is still alive
	 * @return
	 */
	public boolean isAlive() {
		if (baseUnit == null) return false;
		return baseUnit.getData().getCurHP() > 0;
	}

    public boolean isLoaded() {
        return isLoaded;
    }

    public void setLoaded(boolean loaded) {
        isLoaded = loaded;
    }

    public boolean isReady() {
        return isReady;
    }

    public void setReady(boolean ready) {
        isReady = ready;
    }

    public boolean isAI() {
        return isAI;
    }

    public void setAI(boolean AI) {
        isAI = AI;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getKeyString() {
        return String.format("{ \"username\": \"%s\", \"key\": \"%s\" }", getPlayerName(), getKey());
    }
}
