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

import java.util.ArrayList;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;

/**
 * @author jami
 *
 */
public class GameObject extends Actor {
	protected Vector2 accel, velocity;
	protected float maxSpeed;
	protected float maxAccel;
	protected float turnSpeed;
	
	protected TextureRegion image;
	
	protected Rectangle collideBox = new Rectangle(0, 0, 0, 0);
	
	protected boolean remove = false;
	
	protected final int objId;
	protected Player owner;
	
	protected ArrayList<Vector2> path;
	
	protected float localRotation = 0;
	
	
	/**
	 * Constructors
	 */
	
	public GameObject(int id, Player owner) {
		super();
		objId = id;
		this.owner = owner;
		accel = new Vector2();
		velocity = new Vector2();
		maxSpeed = 0;
		maxAccel = 0;
		turnSpeed = 0;
		image = null;
		path = new ArrayList<Vector2>();
	}
	
	public GameObject(int id, Player owner, Texture texture) {
		this(id, owner, new TextureRegion(texture));
	}
	
	public GameObject(int id, Player owner, TextureRegion region) {
		this(id, owner);
		image = region;
		this.setBounds(0, 0, image.getRegionWidth(), image.getRegionHeight());
		collideBox.set(0, 0, image.getRegionWidth(), image.getRegionHeight());
	}
	
	/**
	 * Methods
	 */
	
	/**
	 * Use this for game loop updates so that it can be easily controlled (unlike act, which will be called regardless of gamestate)
	 * @param delta
	 */
	public void update(float delta) {
		// TODO do other stuff...
		
		// Move to the first location in the path
		if (path.size() > 0) {
			Vector2 loc = path.get(0);
			Rectangle box = new Rectangle(getX() + collideBox.x, getY() + collideBox.y, collideBox.width, collideBox.height);
			if (box.contains(loc.x, loc.y)) {
				// we made it, remove this point from the path
				path.remove(loc);
			} else {
				accelToward(loc, delta);
			}
		} else {
			this.velocity.set(0,0);
			this.accel.set(0,0);
		}
	}
	
	private void accelToward(Vector2 loc, float delta) {
		Vector2 moveVector = new Vector2(loc.x - (getX() + getWidth() * 0.5f), loc.y - (getY() + getHeight() * 0.5f));
		
		rotateToAngle(moveVector.angle(), delta);
		
		this.accel.set(this.maxAccel, 0);
		this.accel.rotate(this.getRotation());
	}
	
	private void rotateToAngle(float degrees, float delta) {
		// TODO deal with special cases
		
		if (getRotation() > 360) setRotation(getRotation() % 360);
		while (getRotation() < 0) setRotation(getRotation() + 360);
		
		float dTheta = (degrees - this.getRotation());
	
		if (dTheta > 180) dTheta -= 360;
		if (dTheta < -180) dTheta += 360;
		
		if (dTheta > this.turnSpeed * delta) {
			this.rotate(this.turnSpeed * delta);
		} else if (dTheta < -1 * this.turnSpeed * delta) {
			this.rotate(-1 * this.turnSpeed * delta);
		} else {
			this.setRotation(degrees);
		}
	}
	
	public boolean canCollide(GameObject obj) {
		return true;
	}
	
	public void collide(GameObject obj) {
		
	}
	
	public boolean isColliding(Rectangle box) {
		Rectangle thisBox = new Rectangle(this.getX() + collideBox.x, this.getY() + collideBox.y, collideBox.width, collideBox.height);
		return thisBox.overlaps(box);
	}
	
	public void moveTo(Vector2 location, boolean add) {
		if (!add) {
			path.clear();
		}
		path.add(location);
	}
	
	/**
	 * Implemented methods
	 */
	
	@Override
	public void draw(SpriteBatch batch, float parentAlpha) {
		
		if (image != null) {
			Color color = getColor();
			batch.setColor(color.r, color.g, color.b, color.a * parentAlpha);
			//batch.draw(image, getX(), getY(), image.getU(), image.getV(), image.getRegionWidth(), image.getRegionHeight(), 1, 1, getRotation());
			batch.draw(image, getX(), getY(), getWidth() * 0.5f, getHeight() * 0.5f, getWidth(), getHeight(), 1, 1, getRotation());
		}
	}
	
	@Override
	public void act(float delta) {
		super.act(delta);
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

	/**
	 * @return the maxSpeed
	 */
	public float getMaxSpeed() {
		return maxSpeed;
	}

	/**
	 * @param maxSpeed the maxSpeed to set
	 */
	public void setMaxSpeed(float maxSpeed) {
		this.maxSpeed = maxSpeed;
	}

	/**
	 * @return the maxAccel
	 */
	public float getMaxAccel() {
		return maxAccel;
	}

	/**
	 * @param maxAccel the maxAccel to set
	 */
	public void setMaxAccel(float maxAccel) {
		this.maxAccel = maxAccel;
	}

	/**
	 * @return the turnSpeed
	 */
	public float getTurnSpeed() {
		return turnSpeed;
	}

	/**
	 * @param turnSpeed the turnSpeed to set
	 */
	public void setTurnSpeed(float turnSpeed) {
		this.turnSpeed = turnSpeed;
	}
	
	/**
	 * @return the objId
	 */
	public int getObjId() {
		return objId;
	}

	/**
	 * @return the path
	 */
	public ArrayList<Vector2> getPath() {
		return path;
	}

	/**
	 * @return the ownerId
	 */
	public Player getOwner() {
		return owner;
	}

	/**
	 * @param ownerId the ownerId to set
	 */
	public void setOwner(Player owner) {
		this.owner = owner;
	}
	
}
