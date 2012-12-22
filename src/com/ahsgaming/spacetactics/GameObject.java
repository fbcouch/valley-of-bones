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

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;

/**
 * @author jami
 *
 */
public class GameObject extends Actor {
	private Vector2 accel, velocity;
	
	private TextureRegion image;
	
	private Rectangle collideBox = new Rectangle(0, 0, 0, 0);
	
	private boolean remove = false;
	
	/**
	 * Constructors
	 */
	
	public GameObject() {
		super();
	}
	
	public GameObject(Texture texture) {
		this(new TextureRegion(texture));
	}
	
	public GameObject(TextureRegion region) {
		image = region;
		this.setBounds(0, 0, image.getRegionWidth(), image.getRegionHeight());
		collideBox.set(0, 0, image.getRegionWidth(), image.getRegionHeight());
	}
	
	/**
	 * Methods
	 */
	public boolean canCollide(GameObject obj) {
		return true;
	}
	
	public void collide(GameObject obj) {
		
	}
	
	/**
	 * Implemented methods
	 */
	
	@Override
	public void draw(SpriteBatch batch, float parentAlpha) {
		if (image != null) {
			Color color = getColor();
			batch.setColor(color.r, color.g, color.b, color.a * parentAlpha);
			batch.draw(image, getX(), getY(), image.getU(), image.getV(), image.getRegionWidth(), image.getRegionHeight(), 1, 1, getRotation());
		}
	}
	
	@Override
	public void act(float delta) {
		Gdx.app.log(SpaceTacticsGame.LOG, "GameObject#act");
	}
	
	/**
	 * Getters & Setters
	 */
	
	/**
	 * @return the acceleration of the object
	 */
	public Vector2 getAccel() {
		return accel;
	}

	/**
	 * @param accel set the object's acceleration
	 */
	public void setAccel(Vector2 accel) {
		this.accel = accel;
	}

	/**
	 * @return the velocity of the object
	 */
	public Vector2 getVelocity() {
		return velocity;
	}

	/**
	 * @param velocity set the object's velocity
	 */
	public void setVelocity(Vector2 velocity) {
		this.velocity = velocity;
	}

	/**
	 * @return the image
	 */
	public TextureRegion getImage() {
		return image;
	}

	/**
	 * @param image the image to set
	 */
	public void setImage(TextureRegion image) {
		this.image = image;
	}

	/**
	 * @return the collideBox
	 */
	public Rectangle getCollideBox() {
		return collideBox;
	}

	/**
	 * @param collideBox the collideBox to set
	 */
	public void setCollideBox(Rectangle collideBox) {
		this.collideBox = collideBox;
	}

	/**
	 * @return the remove
	 */
	public boolean isRemove() {
		return remove;
	}

	/**
	 * @param remove the remove to set
	 */
	public void setRemove(boolean remove) {
		this.remove = remove;
	}
}
