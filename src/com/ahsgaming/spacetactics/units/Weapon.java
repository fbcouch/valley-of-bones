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
import com.ahsgaming.spacetactics.TextureManager;
import com.ahsgaming.spacetactics.units.Bullet.BulletProto;
import com.ahsgaming.spacetactics.units.Prototypes.JsonWeapon;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

/**
 * @author jami
 *
 */
public class Weapon {
	public String LOG = "Weapon";	
	
	Unit parent;
	String name, id;
	float damage, lifetime;
	float speed, accel, turn;
	float fireRate;
	TextureRegion image;
	
	long lastFireMillis = 0;
	
	BulletProto bulletProto = new BulletProto();
	
	/**
	 * 
	 */
	public Weapon(Unit parent, JsonWeapon proto) {
		this.parent = parent;
		this.name = proto.name;
		this.id = proto.id;
		this.damage = proto.damage;
		this.lifetime = proto.lifetime;
		this.speed = proto.speed;
		this.accel = proto.accel;
		this.turn = proto.turn;
		this.fireRate = proto.fireRate;
		this.image = TextureManager.getTexture(proto.image);
		
		bulletProto.damage = damage;
		bulletProto.damageType = DamageTypes.NORMAL; // TODO fix this when implementing damage types
		bulletProto.lifetime = lifetime;
		bulletProto.speed = speed;
		bulletProto.accel = accel;
		bulletProto.turn = turn;
		bulletProto.image = image;
		
	}

	public boolean canFire() {
		if (System.currentTimeMillis() - lastFireMillis >= fireRate * 1000) return true;
		return false;
	}
	
	public void fire(GameController controller) {
		if (canFire()) {
			// TODO implement hardpoints
			Bullet b = new Bullet(controller.getNextObjectId(), 
					this.parent.getOwner(),	this.parent, this.parent.getTarget(), 
					parent.getPosition("center"), bulletProto);
			
			controller.addGameUnit(b);
			lastFireMillis = System.currentTimeMillis();
		}
	}
	
	public float getRange() {
		float dist = 0;
		float time = lifetime;
		
		if (accel != 0) {
			float accelTime = speed / accel;
			
			if (accelTime < time) {
				dist = (time - accelTime) * speed;
				time -= accelTime;
			}
		}
		
		dist += (0.5f * accel * time * time) + speed * time;
		
		return speed * lifetime;
	}
}
