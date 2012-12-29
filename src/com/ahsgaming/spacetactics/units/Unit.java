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

import com.ahsgaming.spacetactics.GameObject;
import com.ahsgaming.spacetactics.Player;
import com.ahsgaming.spacetactics.units.Prototypes.JsonUnit;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

/**
 * @author jami
 *
 */
public class Unit extends GameObject {
	
	private Player owner;
	
	private float curHealth, maxHealth;
	private float curShield, maxShield;
	private float curArmor, maxArmor;
	
	
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
		super(id, region);
		this.owner = owner;
		
		curHealth = health;
		maxHealth = health;
		curShield = shield;
		maxShield = shield;
		curArmor = armor;
		maxArmor = armor;
	}
	
	public Unit(int id, Player owner, JsonUnit proto) {
		super(id, new TextureRegion(new Texture(Gdx.files.internal(proto.image))));
		this.owner = owner;
		
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
}
