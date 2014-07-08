package com.ahsgaming.valleyofbones.screens;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.List;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Widget;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.Cullable;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.Pools;

/**
 * valley-of-bones
 * (c) 2013 Jami Couch
 * Created on 1/30/14 by jami
 * ahsgaming.com
 */
public class ImageList extends Widget implements Cullable {
    private ImageListStyle style;
    private Image[] items;
    private int selectedIndex;
    private Rectangle cullingArea;
    private float prefWidth, prefHeight;
    private float imageOffsetX, imageOffsetY;
    private boolean selectable = true;

    public ImageList(Image[] items, Skin skin) {
        this(items, skin.get(ImageListStyle.class));
    }

    public ImageList(Image[] items, Skin skin, String styleName) {
        this(items, skin.get(styleName, ImageListStyle.class));
    }

    public ImageList(Image[] items, ImageListStyle style) {
        setStyle(style);
        setItems(items);
        setWidth(getPrefWidth());
        setHeight(getPrefHeight());

        addListener(new InputListener() {
            public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
                if (pointer == 0 && button != 0) return false;
                if (!isSelectable()) return false; // don't eat touch event when NOT selectable
                ImageList.this.touchDown(y);
                return true;
            }
        });
    }

    /** Sets whether this List's items are selectable. If not selectable, touch events will not be consumed. */
    public void setSelectable (boolean selectable) {
        this.selectable = selectable;
    }

    /** @return True if items are selectable. */
    public boolean isSelectable () {
        return selectable;
    }

    void touchDown (float y) {
        int oldIndex = selectedIndex;
        float curY = getHeight() - imageOffsetY;
        for (int i = 0; i < items.length; i++) {
            if (curY > y && y > curY - items[i].getHeight()) {
                selectedIndex = i;
                break;
            }
            curY -= items[i].getHeight();
        }

        selectedIndex = Math.max(0, selectedIndex);
        selectedIndex = Math.min(items.length - 1, selectedIndex);
        if (oldIndex != selectedIndex) {
            ChangeListener.ChangeEvent changeEvent = Pools.obtain(ChangeListener.ChangeEvent.class);
            if (fire(changeEvent)) selectedIndex = oldIndex;
            Pools.free(changeEvent);
        }
    }

    public void setStyle (ImageListStyle style) {
        if (style == null) throw new IllegalArgumentException("style cannot be null.");
        this.style = style;
        if (items != null)
            setItems(items);
        else
            invalidateHierarchy();
    }

    /** Returns the list's style. Modifying the returned style may not have an effect until {@link #setStyle(com.ahsgaming.valleyofbones.screens.ImageList.ImageListStyle)} is called. */
    public ImageListStyle getStyle () {
        return style;
    }

    @Override

    public void draw (Batch batch, float parentAlpha) {
        Drawable selectedDrawable = style.selection;

        float x = getX();
        float y = getY();

        float itemY = getHeight();
        for (int i = 0; i < items.length; i++) {
            if (cullingArea == null || (itemY - items[i].getHeight() <= cullingArea.y + cullingArea.height && itemY >= cullingArea.y)) {
                if (selectedIndex == i) {
                    Color color = getColor();
                    batch.setColor(color.r, color.g, color.b, color.a * parentAlpha);

                    selectedDrawable.draw(batch, x, y + itemY - items[i].getHeight() - imageOffsetY, getWidth(), items[i].getHeight());
                }
                Color total = new Color(getColor());
                total.mul(items[i].getColor());
                total.a *= parentAlpha;
                batch.setColor(total);
                items[i].getDrawable().draw(batch, x + imageOffsetX, y + itemY - items[i].getHeight() - imageOffsetY, items[i].getWidth(), items[i].getHeight());
            } else if (itemY < cullingArea.y) {
                break;
            }
            itemY -= items[i].getHeight();
        }
    }

    /** @return The index of the currently selected item. The top item has an index of 0. Nothing selected has an index of -1. */
    public int getSelectedIndex () {
        return selectedIndex;
    }

    /** @param index Set to -1 for nothing selected. */
    public void setSelectedIndex (int index) {
        if (index < -1 || index >= items.length)
            throw new GdxRuntimeException("index must be >= -1 and < " + items.length + ": " + index);
        selectedIndex = index;
    }

    /** @return The text of the currently selected item, or null if the list is empty or nothing is selected. */
    public Image getSelection () {
        if (items.length == 0 || selectedIndex == -1) return null;
        return items[selectedIndex];
    }

    /** Sets the selection to the item if found, else sets the selection to nothing.
     * @return The new selected index. */
    public int setSelection (String item) {
        selectedIndex = -1;
        for (int i = 0, n = items.length; i < n; i++) {
            if (items[i].equals(item)) {
                selectedIndex = i;
                break;
            }
        }
        return selectedIndex;
    }

    public void setItems (Image[] items) {
        if (items == null) throw new IllegalArgumentException("items cannot be null.");

        this.items = items;

        selectedIndex = 0;

        final Drawable selectedDrawable = style.selection;

        imageOffsetX = selectedDrawable.getLeftWidth();
        imageOffsetY = 0; //selectedDrawable.getTopHeight();

        prefWidth = 0;
        prefHeight = 0;
        for (int i = 0; i < items.length; i++) {
            prefWidth = Math.max(items[i].getWidth(), prefWidth);
            prefHeight += items[i].getHeight();
        }
        prefWidth += selectedDrawable.getLeftWidth() + selectedDrawable.getRightWidth();
        invalidateHierarchy();
    }

    public Image[] getItems () {
        return items;
    }

    public float getPrefWidth () {
        return prefWidth;
    }

    public float getPrefHeight () {
        return prefHeight;
    }

    public void setCullingArea (Rectangle cullingArea) {
        this.cullingArea = cullingArea;
    }

    /** The style for a list, see {@link List}.
     * @author mzechner
     * @author Nathan Sweet */
    static public class ImageListStyle {
        public Drawable selection;

        public ImageListStyle() {
        }

        public ImageListStyle(Drawable selection) {
            this.selection = selection;
        }

        public ImageListStyle(ImageListStyle style) {
            this.selection = style.selection;
        }
    }
}
