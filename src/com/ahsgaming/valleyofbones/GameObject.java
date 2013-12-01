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
package com.ahsgaming.valleyofbones;

import java.util.ArrayList;

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
	
	protected TextureRegion image;
	
	protected boolean remove = false;
	
	protected final int objId;
	protected Player owner;
	
	protected ArrayList<Vector2> path;
	
	protected float localRotation = 0;
	
	protected Vector2 boardPos = new Vector2(0, 0);
	
	/**
	 * Constructors
	 */
	
	public GameObject(int id, Player owner) {
		super();
		objId = id;
		this.owner = owner;
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
	}
	
	/**
	 * Methods
	 */
	
	/**
	 * Use this for game loop updates so that it can be easily controlled (unlike act, which will be called regardless of gamestate)
	 * @param delta
	 */
	public void update(GameController controller) {
		// TODO implement this
		return;
	}
	
	public boolean canCollide(GameObject obj) {
		return true;
	}
	
	public void collide(GameObject obj) {
		
	}
	
	public boolean isColliding(Rectangle box) {
		Rectangle thisBox = new Rectangle(this.getX(), this.getY(), getWidth(), getHeight());
		return thisBox.overlaps(box);
	}
	
	public boolean isColliding(Vector2 mapCoords) {
		Vector2 local = new Vector2(mapCoords);
		local.set(local.x - getX(), local.y - getY());
		return new Rectangle(0, 0, getWidth(), getHeight()).contains(local.x, local.y);
	}
	
	public void moveTo(Vector2 location, boolean add) {
		if (!add) {
			path.clear();
		}
		path.add(location);
	}
	
	public float takeDamage(float amount) { return 0; }
	
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

    public void draw(SpriteBatch batch, float x, float y, float parentAlpha) {
        if (image != null) {
            Color color = getColor();
            batch.setColor(color.r, color.g, color.b, color.a * parentAlpha);
            //batch.draw(image, getX(), getY(), image.getU(), image.getV(), image.getRegionWidth(), image.getRegionHeight(), 1, 1, getRotation());
            batch.draw(image, x + getX(), y + getY(), getWidth() * 0.5f, getHeight() * 0.5f, getWidth(), getHeight(), 1, 1, getRotation());
        }
    }
	
	@Override
	public void act(float delta) {
		super.act(delta);
	}
	
	/**
	 * Getters & Setters
	 */

	public Vector2 getPosition() {
		return new Vector2(getX(), getY());
	}
	
	/**
	 * 
	 * @param loc bottom-left, bottom-center, bottom-right, middle-left, center, middle-right, top-left, top-center, top-right
	 * @return
	 */
	public Vector2 getPosition(String loc) {
		if (loc.equals("bottom-left")) {
			return new Vector2(getX(), getY());
		} else if (loc.equals("bottom-center")) {
			return new Vector2(getX() + getWidth() * 0.5f, getY());
		} else if (loc.equals("bottom-right")) {
			return new Vector2(getX() + getWidth(), getY());
		} else if (loc.equals("middle-left")) {
			return new Vector2(getX(), getY() + getHeight() * 0.5f);
		} else if (loc.equals("center")) {
			return new Vector2(getX() + getWidth() * 0.5f, getY() + getHeight() * 0.5f);
		} else if (loc.equals("middle-right")) {
			return new Vector2(getX() + getWidth(), getY() + getHeight() * 0.5f);
		} else if (loc.equals("top-left")) {
			return new Vector2(getX(), getY() + getHeight());
		} else if (loc.equals("top-center")) {
			return new Vector2(getX() + getWidth() * 0.5f, getY() + getHeight());
		} else if (loc.equals("top-right")) {
			return new Vector2(getX() + getWidth(), getY() + getHeight());
		}
		return getPosition();
	}
	
	public void setPosition(Vector2 position) {
		setX(position.x);
		setY(position.y);
	}
	
	/**
	 * 
	 * @param pos
	 * @param loc bottom-left, bottom-center, bottom-right, middle-left, center, middle-right, top-left, top-center, top-right
	 * @return
	 */
	public void setPosition(Vector2 pos, String loc) {
		if (loc.equals("bottom-left")) {
			setX(pos.x);
			setY(pos.y);
		} else if (loc.equals("bottom-center")) {
			setX(pos.x - getWidth() * 0.5f);
			setY(pos.y);
		} else if (loc.equals("bottom-right")) {
			setX(pos.x - getWidth());
			setY(pos.y);
		} else if (loc.equals("middle-left")) {
			setX(pos.x);
			setY(pos.y - getHeight() * 0.5f);
		} else if (loc.equals("center")) {
			setX(pos.x - getWidth() * 0.5f);
			setY(pos.y - getHeight() * 0.5f);
		} else if (loc.equals("middle-right")) {
			setX(pos.x - getWidth());
			setY(pos.y - getHeight() * 0.5f);
		} else if (loc.equals("top-left")) {
			setX(pos.x);
			setY(pos.y - getHeight());
		} else if (loc.equals("top-center")) {
			setX(pos.x - getWidth() * 0.5f);
			setY(pos.y - getHeight());
		} else if (loc.equals("top-right")) {
			setX(pos.x - getWidth());
			setY(pos.y - getHeight());
		}
	}
	
	public void setBoardPosition(int x, int y) {
		boardPos.set(x, y);
	}
	
	public void setBoardPosition(float x, float y) {
		setBoardPosition((int)x, (int)y);
	}
	
	public void setBoardPosition(Vector2 boardPos) {
		setBoardPosition((int)boardPos.x, (int)boardPos.y);
	}
	
	public Vector2 getBoardPosition() {
		return boardPos;
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
	
	public Rectangle getRectangle() {
		return new Rectangle(getX(), getY(), getWidth(), getHeight());
	}
	
	
	public static float getDistanceSq(GameObject obj1, GameObject obj2) {
		return (float) (Math.pow((obj1.getX() + obj1.getWidth() * 0.5f)
				- (obj2.getX() + obj2.getWidth() * 0.5f), 2)
				+ Math.pow((obj1.getY() + obj1.getHeight() * 0.5f)
						- (obj2.getY() + obj2.getHeight() * 0.5f), 2));
	}
}
