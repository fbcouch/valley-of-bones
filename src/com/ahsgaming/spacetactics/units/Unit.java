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
package com.ahsgaming.spacetactics.units;

import java.util.ArrayList;

import com.ahsgaming.spacetactics.GameController;
import com.ahsgaming.spacetactics.GameObject;
import com.ahsgaming.spacetactics.Player;
import com.ahsgaming.spacetactics.TextureManager;
import com.ahsgaming.spacetactics.network.Attack;
import com.ahsgaming.spacetactics.network.Build;
import com.ahsgaming.spacetactics.network.Command;
import com.ahsgaming.spacetactics.network.Move;
import com.ahsgaming.spacetactics.network.Upgrade;
import com.ahsgaming.spacetactics.units.Prototypes.JsonUnit;
import com.ahsgaming.spacetactics.units.Prototypes.JsonWeapon;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

/**
 * @author jami
 *
 */
public class Unit extends GameObject implements Selectable, Targetable {
	public String LOG = "Unit";
	
	boolean selectable = true, targetable = true;
	
	float curHealth, maxHealth;
	float curShield, maxShield;
	float curArmor, maxArmor;
	
	String protoId = "";

	ArrayList<Weapon> weapons = new ArrayList<Weapon>();
	
	ArrayList<Command> commandQueue = new ArrayList<Command>();
	GameObject commandTarget;
	
	/**
	 * Constructors
	 */
	
	public Unit(int id, TextureRegion region) {
		this(id, null, region, 10, 0, 0);
	}
	
	public Unit(int id, Player owner, TextureRegion region) {
		this(id, owner, region, 10, 0, 0);
	}
	
	public Unit(int id, Player owner, TextureRegion region, float health, float shield, float armor) {
		super(id, owner, region);
		
		curHealth = health;
		maxHealth = health;
		curShield = shield;
		maxShield = shield;
		curArmor = armor;
		maxArmor = armor;
	}
	
	public Unit(int id, Player owner, JsonUnit proto) {
		super(id, owner, TextureManager.getTexture(proto.image));
		
		curHealth = proto.health;
		maxHealth = proto.health;
		curShield = proto.shield;
		maxShield = proto.shield;
		curArmor = proto.armor;
		maxArmor = proto.armor;
		maxSpeed = proto.speed;
		maxAccel = proto.accel;
		turnSpeed = proto.turn;
		
		protoId = proto.id;
		
		if (proto.weapons != null) {
			for (String w: proto.weapons) {
				JsonWeapon jw = (JsonWeapon)Prototypes.getProto(w);
				if (jw == null) {
					Gdx.app.log(LOG, "Could not find weapon " + w);
				} else {
					weapons.add(new Weapon(this, jw));
				}
			}
		}
		
		if (proto.bounds != null) {
			collideBox.set(proto.bounds);
		}
	}
	
	public void takeDamage(Bullet b) {
		// TODO take into account damage type here
		if (this.curShield > 0) {
			curShield -= b.getDamage() - curArmor;
			if (curShield < 0) {
				curHealth += curShield;
				curShield = 0;
				// TODO add a hit effect
			} else {
				// TODO add a hit effect
			}
		} else {
			curHealth -= b.getDamage() - curArmor;
			// TODO add a hit effect
		}
	}
	
	/**
	 * Determines if the target is in range of any weapons
	 * @param target
	 * @return
	 */
	public boolean isInRange(GameObject target) {
		for (Weapon w: weapons) {
			if (GameObject.getDistanceSq(this, target) <= Math.pow(w.getRange(), 2)) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Finds the nearest Unit not owned by the same player
	 * @param controller
	 * @return
	 */
	public GameObject findTarget(GameController controller) {
		float maxRange = 0;
		for (Weapon w: weapons) {
			float range = w.getRange();
			if (range > maxRange) maxRange = range;
		}
		
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
		
		if (curHealth <= 0) {
			// remove self
			remove = true;
			// TODO add explosion anim or something
			return;
		}
		
		if (commandQueue.size() > 0) {
			Command cur = commandQueue.get(0);
			
			if (cur instanceof Attack) {
				// check for completion conditions
				if (commandTarget == null) {
					commandTarget = (Unit)controller.getObjById(((Attack)cur).target);
				} else if (commandTarget.isRemove()) {
					commandTarget = null;
				}
				
				if (commandTarget == null) {
					commandQueue.remove(cur);
				} else {
					accelToward(commandTarget.getPosition("center"), delta);
					
					for (Weapon w: weapons) {
						if (getDistanceSq(this, commandTarget) < Math.pow(w.getRange(), 2) && w.canFire()) {
							w.fire(controller);
						}
					}
				}
				
			} else if (cur instanceof Build) {
				
			} else if (cur instanceof Move) {
				Move mv = (Move)cur;
				if (mv.isAttack) {
					if (commandTarget != null && !commandTarget.isRemove() 
							&& isInRange(commandTarget)) {
						// attack this target
						Attack at = new Attack();
						at.unit = getObjId();
						at.target = commandTarget.getObjId();
						commandQueue.add(0, at);
					} else {
						commandTarget = findTarget(controller);
						
						if (commandTarget == null) {
							if (getRectangle().contains(mv.toLocation.x, mv.toLocation.y)) {
								commandQueue.remove(cur);
							} else {
								accelToward(mv.toLocation, delta);
							}
						}
					}
				} else {
					// not attack move, just move there
					if (getRectangle().contains(mv.toLocation.x, mv.toLocation.y)) {
						commandQueue.remove(cur);
					} else {
						accelToward(mv.toLocation, delta);
					}
				}
			} else if (cur instanceof Upgrade) {
				
			}
		} else {
			
			// Don't have anything to do - stop moving
			if (velocity.len2() > 0) {
				accel.set(-1 * maxAccel, 0);
				accel.rotate(velocity.angle());
			}
		}
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
