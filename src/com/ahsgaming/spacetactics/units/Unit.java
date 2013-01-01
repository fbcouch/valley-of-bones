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
import com.ahsgaming.spacetactics.units.Prototypes.JsonUnit;
import com.ahsgaming.spacetactics.units.Prototypes.JsonWeapon;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

/**
 * @author jami
 *
 */
public class Unit extends GameObject {
	
	float curHealth, maxHealth;
	float curShield, maxShield;
	float curArmor, maxArmor;
	
	Unit target;
	ArrayList<Weapon> weapons = new ArrayList<Weapon>();
	
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
		super(id, owner, new TextureRegion(new Texture(Gdx.files.internal(proto.image))));
		
		curHealth = proto.health;
		maxHealth = proto.health;
		curShield = proto.shield;
		maxShield = proto.shield;
		curArmor = proto.armor;
		maxArmor = proto.armor;
		maxSpeed = proto.speed;
		maxAccel = proto.accel;
		turnSpeed = proto.turn;
		
	}
	
	public void takeDamage(Bullet b) {
		// TODO take into account damage type here
		if (this.curShield > 0) {
			curShield -= b.getDamage();
			if (curShield < 0) {
				curHealth += curShield;
				curShield = 0;
				// TODO add a hit effect
			} else {
				// TODO add a hit effect
			}
		} else {
			curHealth -= b.getDamage();
			// TODO add a hit effect
		}
	}

	/* (non-Javadoc)
	 * @see com.ahsgaming.spacetactics.GameObject#update(com.ahsgaming.spacetactics.GameController, float)
	 */
	@Override
	public void update(GameController controller, float delta) {
		super.update(controller, delta);
		
		// TODO fire weapons if in range of target
		for (Weapon w: weapons) {
			if (getDistanceSq(this, target) < Math.pow(w.getRange(), 2) && w.canFire()) {
				w.fire(controller);
			}
		}
	}
	
}
