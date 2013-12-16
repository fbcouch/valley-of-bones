package com.ahsgaming.valleyofbones.units;

import com.ahsgaming.valleyofbones.TextureManager;
import com.ahsgaming.valleyofbones.VOBGame;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;

/**
 * Created with IntelliJ IDEA.
 * User: jami
 * Date: 5/4/13
 * Time: 10:38 AM
 * To change this template use File | Settings | File Templates.
 */
public class ProgressBar {
    public static final String LOG = "ProgressBar";

    float current = 1;
    Color highColor = new Color(0, 1, 0, 1);
    Color medColor = new Color(1, 1, 0, 1);
    Color lowColor = new Color(1, 0, 0, 1);

    TextureRegion img;

    Vector2 size;

    public ProgressBar() {
        super();
        size = new Vector2(1, 1);
        img = VOBGame.instance.getTextureManager().getSpriteFromAtlas("assets", "hud-bar");
    }

    public void draw(SpriteBatch batch, float x, float y, float parentAlpha) {
        Color color = lowColor;
        if (current >= 0.66f) color = highColor;
        else if (current >= 0.33f) color = medColor;

        batch.setColor(batch.getColor().mul(color));
        batch.getColor().a *= parentAlpha;
        batch.draw(img, x, y, getWidth() * current, getHeight());
    }

    public float getWidth() { return size.x; }

    public void setWidth(float width) { size.x = width; }

    public float getHeight() { return size.y; }

    public void setHeight(float height) { size.y = height; }

    public Vector2 getSize() { return size; }

    public void setSize(float width, float height) { size.set(width, height); }

    public float getCurrent() {
        return current;
    }

    public void setCurrent(float current) {
        this.current = current;
    }

    public Color getHighColor() {
        return highColor;
    }

    public void setHighColor(Color highColor) {
        this.highColor = highColor;
    }

    public Color getMedColor() {
        return medColor;
    }

    public void setMedColor(Color medColor) {
        this.medColor = medColor;
    }

    public Color getLowColor() {
        return lowColor;
    }

    public void setLowColor(Color lowColor) {
        this.lowColor = lowColor;
    }
}
