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
package com.ahsgaming.valleyofbones.units;

import java.util.ArrayList;

import com.ahsgaming.valleyofbones.GameController;
import com.ahsgaming.valleyofbones.GameObject;
import com.ahsgaming.valleyofbones.Player;
import com.ahsgaming.valleyofbones.TextureManager;
import com.ahsgaming.valleyofbones.network.Attack;
import com.ahsgaming.valleyofbones.network.Build;
import com.ahsgaming.valleyofbones.network.Command;
import com.ahsgaming.valleyofbones.network.Move;
import com.ahsgaming.valleyofbones.network.Upgrade;
import com.ahsgaming.valleyofbones.units.Prototypes.JsonProto;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.ObjectMap;

/**
 * @author jami
 *
 */
public class Unit extends GameObject implements Selectable, Targetable {
	public String LOG = "Unit";

	boolean selectable = true, targetable = true;
	
	int attackDamage = 0;
	float attackSpeed = 0;
	int armor = 0, cost = 0, curHP = 0, maxHP = 0, food = 0;
	int moveSpeed = 0;
	
	String protoId = "";
	String type = "";
	String sImage = "";
	ObjectMap<String, Object> properties = new ObjectMap<String, Object>();
	
	ArrayList<Command> commandQueue = new ArrayList<Command>();
	GameObject commandTarget;
	
	final JsonProto proto;
	
	/**
	 * Constructors
	 */
	
	public Unit(int id, JsonProto proto) {
		this(id, null, proto);
	}
	
	public Unit(int id, Player owner, JsonProto proto) {
		// TODO load from atlas
		super(id, owner, TextureManager.getTexture(proto.image + ".png"));
		
		this.proto = proto;
		this.protoId = proto.id;
		type = proto.type;
		sImage = proto.image;
		properties.putAll(proto.properties);
		parseProperties();
	}
	
	public void parseProperties() {
		
		if (properties.containsKey("attackdamage"))
			attackDamage = (int)Float.parseFloat(properties.get("attackdamage").toString());
		
		if (properties.containsKey("attackspeed"))
			attackSpeed = Float.parseFloat(properties.get("attackspeed").toString());
		
		if (properties.containsKey("armor"))
			armor = (int)Float.parseFloat(properties.get("armor").toString());
		
		if (properties.containsKey("cost"))
			cost = (int)Float.parseFloat(properties.get("cost").toString());
		
		if (properties.containsKey("curhp"))
			curHP = (int)Float.parseFloat(properties.get("curhp").toString());
		
		if (properties.containsKey("food"))
			food = (int)Float.parseFloat(properties.get("food").toString());
		
		if (properties.containsKey("maxhp"))
			maxHP = (int)Float.parseFloat(properties.get("maxhp").toString());
		
		if (properties.containsKey("movespeed"))
			moveSpeed = (int)Float.parseFloat(properties.get("movespeed").toString());
		
	}
	
	public void updateProperties() { 
		properties.put("attackdamage", attackDamage);
		properties.put("attackspeed", attackSpeed);
		properties.put("armor", armor);
		properties.put("cost", cost);
		properties.put("curhp", curHP);
		properties.put("food", food);
		properties.put("maxhp", maxHP);
		properties.put("movespeed", moveSpeed);
	}
	
	@Override
	public void takeDamage(float amount) {
		// TODO take into account damage type here
		float damage = amount - getArmor();
		if (damage > 0)
			curHP -= damage;
		// TODO add a hit effect
		
	}
	
	public int getAttackDamage() {
		return attackDamage;
	}

	public void setAttackDamage(int attackDamage) {
		this.attackDamage = attackDamage;
	}

	public float getAttackSpeed() {
		return attackSpeed;
	}

	public void setAttackSpeed(float attackSpeed) {
		this.attackSpeed = attackSpeed;
	}

	public int getArmor() {
		return armor;
	}

	public void setArmor(int armor) {
		this.armor = armor;
	}
	
	public int getCost() {
		return cost;
	}
	
	public void setCost(int cost) {
		this.cost = cost;
	}
	
	public int getCurHP() {
		return curHP;
	}

	public void setCurHP(int curHP) {
		this.curHP = curHP;
	}
	
	public int getFood() {
		return food;
	}
	
	public void setFood(int food) {
		this.food = food;
	}

	public int getMaxHP() {
		return maxHP;
	}

	public void setMaxHP(int maxHP) {
		this.maxHP = maxHP;
	}

	public int getMoveSpeed() {
		return moveSpeed;
	}

	public void setMoveSpeed(int moveSpeed) {
		this.moveSpeed = moveSpeed;
	}

	public ObjectMap<String, Object> getProperties() {
		return properties;
	}

	public void setProperties(ObjectMap<String, Object> properties) {
		this.properties = properties;
	}

	/**
	 * Determines if the target is in range of any weapons
	 * @param target
	 * @return
	 */
	public boolean isInRange(GameObject target) {
		// TODO implement this
		return false;
	}
	
	/**
	 * Finds the nearest Unit not owned by the same player
	 * @param controller
	 * @return
	 */
	public GameObject findTarget(GameController controller) {
		float maxRange = 0;
		// TODO implement this
		/*for (Weapon w: weapons) {
			float range = w.getRange();
			if (range > maxRange) maxRange = range;
		}*/
		
		float maxRangeSq = maxRange * maxRange;
		GameObject candidate = null;
		float candidateVal = 0;
		
		for (GameObject obj: controller.getGameObjects()) {
			// TODO fix this to use some kind of threat assessment rather than just distance
			float objVal = GameObject.getDistanceSq(this, obj);
			if (obj instanceof Targetable && ((Targetable)obj).isTargetable()
					&& obj.getOwner() != this.getOwner() && objVal <= maxRangeSq) {
				if (candidate == null || objVal < candidateVal) {
					candidate = obj;
				}
			}
		}
		
		return candidate;
	}
	
	/* (non-Javadoc)
	 * @see com.ahsgaming.spacetactics.GameObject#moveTo(com.badlogic.gdx.math.Vector2, boolean)
	 */
	@Override
	public void moveTo(Vector2 location, boolean add) {
		// add to the command queue, no longer using path
		Move mv = new Move();
		mv.unit = getObjId();
		mv.toLocation = location;
		mv.isAdd = add;
		mv.isAttack = false;
		doCommand(mv, mv.isAdd);
	}

	/* (non-Javadoc)
	 * @see com.ahsgaming.spacetactics.GameObject#update(com.ahsgaming.spacetactics.GameController, float)
	 */
	@Override
	public void update(GameController controller, float delta) {
		
		if (getCurHP() <= 0) {
			// remove self
			remove = true;
			// TODO add explosion anim or something
			return;
		}
		
		// TODO implement this
	}
	
	public ArrayList<Command> getCommandQueue() {
		return commandQueue;
	}
	
	public void doCommand(Command cmd, boolean add) {
		if (!add) commandQueue.clear();
		commandQueue.add(cmd);
	}
	
	public GameObject getTarget() {
		if (commandTarget != null && (commandTarget.getOwner() != null && commandTarget.getOwner().getPlayerId() == getOwner().getPlayerId())) {
			return null;
		}
		return commandTarget;
	}
	
	public String getProtoId() {
		return protoId;
	}
	
	public boolean isAlive() {
		return (!this.isRemove() && this.curHP > 0);
	}
	
	//-------------------------------------------------------------------------
	// Implemented methods
	//-------------------------------------------------------------------------

	@Override
	public boolean isTargetable() {
		return this.targetable;
	}

	@Override
	public void setTargetable(boolean targetable) {
		this.targetable = targetable;
		
	}

	@Override
	public boolean isSelectable() {
		return selectable;
	}

	@Override
	public void setSelectable(boolean selectable) {
		this.selectable = selectable;
	}
}
