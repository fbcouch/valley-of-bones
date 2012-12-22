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

import java.io.StringWriter;

import com.ahsgaming.spacetactics.GameObject;
import com.ahsgaming.spacetactics.Player;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Json;

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
	
	public Unit(TextureRegion region) {
		this(null, region, 10, 0, 0);
	}
	
	public Unit(Player owner, TextureRegion region) {
		this(owner, region, 10, 0, 0);
	}
	
	public Unit(Player owner, TextureRegion region, float health, float shield, float armor) {
		super(region);
		this.owner = owner;
		
		curHealth = health;
		maxHealth = health;
		curShield = shield;
		maxShield = shield;
		curArmor = armor;
		maxArmor = armor;
	}
	
	
	public static class JsonUnit {
		String id, name;
		int health, shield, armor;
		String image;
		JsonUnit subunit;
	}
	
	public static void main(String[] args) {
		
		JsonUnit unit = new JsonUnit();
		unit.id = "space-station-base";
		unit.name = "Space Station (L1)";
		unit.health = 1000;
		unit.armor = 0;
		unit.shield = 1000;
		unit.image = "base-fighter1.png";
		unit.subunit = new JsonUnit();
		unit.subunit.id = "test";
		
		
		StringWriter writer = new StringWriter();
		
		Json json = new Json();
		
		json.toJson(unit, unit.getClass(), writer);
		
		JsonUnit unit2 = new JsonUnit();
		unit2 = json.fromJson(JsonUnit.class, writer.toString());
		System.out.println(writer.toString());
		System.out.println(unit2.id);
		System.out.println(unit2.name);
		System.out.println(unit2.health);
		System.out.println(unit2.armor);
		System.out.println(unit2.shield);
		System.out.println(unit2.image);
	}
}
