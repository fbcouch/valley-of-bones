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

import com.ahsgaming.valleyofbones.DamageTypes;
import com.ahsgaming.valleyofbones.GameController;
import com.ahsgaming.valleyofbones.GameObject;
import com.ahsgaming.valleyofbones.Player;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

/**
 * @author jami
 *
 */
public class Bullet extends GameObject {
	public String LOG = "Bullet";
	
	GameObject parentUnit, target;
	float ticksRemaining = 0;
	float damage = 0;
	DamageTypes damageType = DamageTypes.NORMAL;

	/**
	 * @param id
	 * @param owner
	 * @param region
	 */
	public Bullet(int id, Player owner, GameObject parentUnit, 
			GameObject target, Vector2 position, BulletProto proto) {
		super(id, owner, proto.image);
		
		this.parentUnit = parentUnit;
		this.target = target;
		
		this.ticksRemaining = proto.lifetime;
		this.damage = proto.damage;
		this.damageType = proto.damageType;
		this.maxSpeed = proto.speed;
		this.maxAccel = proto.accel;
		this.turnSpeed = proto.turn;
		
		this.setPosition(position.x - getWidth() * 0.5f, position.y - getHeight() * 0.5f);
		
		if (target != null) {
			Vector2 toTarget = new Vector2();
			toTarget.set(target.getPosition("center"));
			toTarget.sub(parentUnit.getPosition("center"));
			setRotation(toTarget.angle());
		} else {
			setRotation(parentUnit.getRotation());
		}
		
		if (maxAccel == 0) {
			// start at max speed (not accelerating)
			Vector2 vel = new Vector2(maxSpeed, 0);
			vel.rotate(getRotation());
			setVelocity(vel);
			setAccel(new Vector2());
		} else {
			Vector2 accel = new Vector2(maxAccel, 0);
			accel.rotate(getRotation());
			setAccel(accel);
			setVelocity(new Vector2());
		}
	}
	
	@Override
	public void update(GameController controller, float delta) {
		//super.update(controller, delta);
		
		ticksRemaining -= delta;
		if (ticksRemaining <= 0) {
			if (target != null) {
				target.takeDamage(this);
			} else {
				Gdx.app.log(LOG, "Target is null");
			}
			this.setRemove(true);
		} else {
			if (target != null) {
				Rectangle tRect = new Rectangle(target.getX(), target.getY(), target.getWidth(), target.getHeight());
				if (tRect.overlaps(new Rectangle(getX(), getY(), getWidth(), getHeight()))) {
					target.takeDamage(this);
					this.setRemove(true);
				}
			}
		}
	}

	/**
	 * @return the parent
	 */
	public GameObject getParentUnit() {
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
	public GameObject getTarget() {
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
