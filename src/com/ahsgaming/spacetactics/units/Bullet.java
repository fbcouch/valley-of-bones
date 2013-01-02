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

import com.ahsgaming.spacetactics.DamageTypes;
import com.ahsgaming.spacetactics.GameController;
import com.ahsgaming.spacetactics.GameObject;
import com.ahsgaming.spacetactics.Player;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

/**
 * @author jami
 *
 */
public class Bullet extends GameObject {
	
	Unit parentUnit, target;
	float ticksRemaining = 0;
	float damage = 0;
	DamageTypes damageType = DamageTypes.NORMAL;

	/**
	 * @param id
	 * @param owner
	 * @param region
	 */
	public Bullet(int id, Player owner, Unit parentUnit, 
			Unit target, Vector2 position, BulletProto proto) {
		super(id, owner, proto.image);
		
		this.parentUnit = parentUnit;
		this.target = target;
		
		this.ticksRemaining = proto.lifetime;
		this.damage = proto.damage;
		this.damageType = proto.damageType;
		this.maxSpeed = proto.speed;
		this.maxAccel = proto.accel;
		this.turnSpeed = proto.turn;
		
		this.setPosition(position.x, position.y);
	}
	
	@Override
	public void update(GameController controller, float delta) {
		//super.update(controller, delta);
		
		ticksRemaining -= delta;
		if (ticksRemaining <= 0) {
			if (target != null) target.takeDamage(this);
			this.setRemove(true);
		}
	}

	/**
	 * @return the parent
	 */
	public Unit getParentUnit() {
		return parentUnit;
	}

	/**
	 * @param parent the parent to set
	 */
	public void setParentUnit(Unit parentUnit) {
		this.parentUnit = parentUnit;
	}

	/**
	 * @return the target
	 */
	public Unit getTarget() {
		return target;
	}

	/**
	 * @param target the target to set
	 */
	public void setTarget(Unit target) {
		this.target = target;
	}

	/**
	 * @return the ticksRemaining
	 */
	public float getTicksRemaining() {
		return ticksRemaining;
	}

	/**
	 * @param ticksRemaining the ticksRemaining to set
	 */
	public void setTicksRemaining(float ticksRemaining) {
		this.ticksRemaining = ticksRemaining;
	}

	/**
	 * @return the damage
	 */
	public float getDamage() {
		return damage;
	}

	/**
	 * @param damage the damage to set
	 */
	public void setDamage(float damage) {
		this.damage = damage;
	}

	/**
	 * @return the damageType
	 */
	public DamageTypes getDamageType() {
		return damageType;
	}

	/**
	 * @param damageType the damageType to set
	 */
	public void setDamageType(DamageTypes damageType) {
		this.damageType = damageType;
	}
	
	public static class BulletProto {
		public float damage = 0, lifetime = 0;
		public float speed = 0, accel = 0, turn = 0;
		public TextureRegion image = null;
		public DamageTypes damageType = DamageTypes.NORMAL;
	}
}
