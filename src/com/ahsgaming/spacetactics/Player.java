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
package com.ahsgaming.spacetactics;

import com.ahsgaming.spacetactics.units.Prototypes;
import com.ahsgaming.spacetactics.units.Prototypes.JsonProto;
import com.ahsgaming.spacetactics.units.Unit;
import com.badlogic.gdx.graphics.Color;

/**
 * @author jami
 * TODO theoretically, this class should probably handle inputs for the player or something...
 */
public class Player {
	public String LOG = "Player"; 
	public static final Color COLOR_RED = new Color(1, 0, 0, 1);
	public static final Color COLOR_BLUE = new Color(0, 0, 1, 1);
	public static final Color COLOR_GREEN = new Color(0, 1, 0, 1);
	public static final Color COLOR_PURPLE = new Color(1, 0, 1, 1);
	public static final Color[] AUTOCOLORS = {COLOR_RED, COLOR_BLUE, COLOR_GREEN, COLOR_PURPLE};
	
	int playerId = -1;
	Color playerColor = new Color(1, 1, 1, 1);
	String name = "New Cadet";
	
	float bankMoney = 2000, rateMoney = 1;
	float curFood = 0, maxFood = 0;
	
	/**
	 * 
	 */
	public Player(int id, String name, Color color) {
		this(id, color);
		this.name = name;
	}
	
	public Player(int id, Color color) {
		playerId = id;
		playerColor = color;
	}
	
	public void update(GameController controller, float delta) {
		bankMoney += rateMoney * delta;
		
		float food = 0, mFood = 0;
		for (Unit unit: controller.getUnitsByPlayerId(playerId)) {
			JsonProto up = Prototypes.getProto(unit.getProtoId());
			if (up.food < 0) mFood -= up.food;
			else food += up.food;
		}
		curFood = food;
		maxFood = mFood;
	}
	
	public boolean canBuild(String protoId, GameController controller) {
		// TODO implement this
		JsonProto proto = Prototypes.getProto(protoId);
		if ((proto.food <= 0 || proto.food <= maxFood - curFood) && bankMoney >= proto.cost) {
			return true;
		}
		return false;
	}
	
	
	public String getPlayerName() {
		return name;
	}
	
	@Override
	public String toString() {
		return String.format("(%d)%s $%05d // Food: %03d/%03d", playerId, name, (int)bankMoney, (int)curFood, (int)maxFood);
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
		return rateMoney;
	}

	/**
	 * @param rateMoney the rateMoney to set
	 */
	public void setRateMoney(float rateMoney) {
		this.rateMoney = rateMoney;
	}

	/**
	 * @return the curFood
	 */
	public float getCurFood() {
		return curFood;
	}

	/**
	 * @param curFood the curFood to set
	 */
	public void setCurFood(float curFood) {
		this.curFood = curFood;
	}

	/**
	 * @return the maxFood
	 */
	public float getMaxFood() {
		return maxFood;
	}

	/**
	 * @param maxFood the maxFood to set
	 */
	public void setMaxFood(float maxFood) {
		this.maxFood = maxFood;
	}
	
	
}
