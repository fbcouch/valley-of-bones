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

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.Array;

/**
 * @author jami
 *
 */
public class GameObject {
	
	protected TextureRegion image;
	
	protected boolean remove = false;
	
	protected final int objId;
	protected Player owner;
	
	protected ArrayList<Vector2> path;
	
	protected float localRotation = 0;
	
	protected Vector2 boardPos = new Vector2(0, 0), lastBoardPos;
    protected Vector2 position = new Vector2();
    protected Vector2 size = new Vector2();
    protected Color color = new Color(1, 1, 1, 1);
    protected float rotation = 0;

    Array<Action> actions;
	
	/**
	 * Constructors
	 */
	
	public GameObject(int id, Player owner) {
		super();
		objId = id;
		this.owner = owner;
		image = null;
		path = new ArrayList<Vector2>();
        actions = new Array<Action>();
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
	 */
	public void update(GameController controller, float delta) {
		if (actions.size > 0) {
            actions.get(0).update(this, delta);
            if (actions.get(0).isDone()) {
                actions.removeIndex(0);
            }
        }
	}
	
	public float takeDamage(float amount) { return 0; }

    public void addAction(Action action) {
        actions.add(action);
    }

    public void removeAction(Action action) {
        actions.removeValue(action, true);
    }

    public boolean hasActions() {
        return actions.size > 0;
    }

    public Action getCurAction() {
        return (actions.size > 0 ? actions.first() : null);
    }
	
	/**
	 * Implemented methods
	 */

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
	
	/**
	 * Getters & Setters
	 */

	public Vector2 getPosition() {
		return new Vector2(getX(), getY());
	}

    public void setPosition(float x, float y) {
        position.set(x, y);
    }

    public float getX() {
        return position.x;
    }

    public void setX(float x) {
        position.set(x, position.y);
    }

    public float getY() {
        return position.y;
    }

    public void setY(float y) {
        position.set(position.x, y);
    }

    public Vector2 getSize() {
        return new Vector2(size);
    }

    public void setSize(float width, float height) {
        size.set(width, height);
    }

    public void setBounds(float x, float y, float width, float height) {
        setPosition(x, y);
        setSize(width, height);
    }

    public float getWidth() {
        return size.x;
    }

    public void setWidth(float width) {
        size.set(width, size.y);
    }

    public float getHeight() {
        return size.y;
    }

    public void setHeight(float height) {
        size.set(size.x, height);
    }

    public float getRotation() {
        return rotation;
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

    public Color getColor() {
        return new Color(color);
    }

    public void setColor(Color c) {
        color.set(c);
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

    public void setLastBoardPos(Vector2 boardPos) {
        if (lastBoardPos == null) lastBoardPos = new Vector2(boardPos);
        else lastBoardPos.set(boardPos);
    }

    public Vector2 getLastBoardPos() {
        return lastBoardPos;
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
	 * @param owner the owner to set
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

    public static class Actions {
        public static Action delay(float delay) {
            DelayAction da = new DelayAction();
            da.duration = delay;
            return da;
        }

        public static Action moveTo(float x, float y, float duration) {
            MoveAction ma = new MoveAction();
            ma.end = new Vector2(x, y);
            ma.duration = duration;
            return ma;
        }

        public static Action colorTo(Color c, float duration) {
            ColorAction ca = new ColorAction();
            ca.end = new Color(c);
            ca.duration = duration;
            return ca;
        }

        public static Action sequence(Action... actions) {
            SequenceAction sa = new SequenceAction();
            for (Action a: actions) {
                sa.actions.add(a);
            }
            return sa;
        }
    }

    public static class Action {
        float duration = 0;
        float total = 0;

        public void update(GameObject obj, float delta) {

        }

        public boolean isDone() {
            return (duration <= 0 || total >= duration);
        }
    }

    public static class DelayAction extends Action {
        @Override
        public void update(GameObject obj, float delta) {
            total += delta;
        }
    }

    public static class MoveAction extends Action {
        Vector2 start, end;

        @Override
        public void update(GameObject obj, float delta) {
            if (duration == 0) return;

            if (start == null) {
                start = new Vector2(obj.getPosition());
            }

            if (end == null) {
                end = new Vector2(start);
            }

            total += delta;

            if (total >= duration) {
                obj.setPosition(end.x, end.y);
            } else {
                obj.setPosition(
                        start.x + (end.x - start.x) * total / duration,
                        start.y + (end.y - start.y) * total / duration
                );
            }
        }
    }

    public static class ColorAction extends Action {
        Color start, end;

        @Override
        public void update(GameObject obj, float delta) {
            if (duration == 0) return;

            if (start == null) {
                start = new Color(obj.getColor());
            }

            if (end == null) {
                end = new Color(start);
            }

            total += delta;

            if (total >= duration) {
                obj.setColor(end);
            } else {
                float pct = total / duration;
                obj.setColor(new Color(
                        start.r + (end.r - start.r) * pct,
                        start.g + (end.g - start.g) * pct,
                        start.b + (end.b - start.b) * pct,
                        start.a + (end.a - start.a) * pct
                ));
            }
        }
    }

    public static class SequenceAction extends Action {
        Array<Action> actions = new Array<Action>();

        @Override
        public void update(GameObject obj, float delta) {
            if (actions.size == 0) return;

            actions.get(0).update(obj, delta);
            if (actions.get(0).isDone()) actions.removeIndex(0);
        }

        @Override
        public boolean isDone() {
            return actions.size == 0;
        }
    }
}
