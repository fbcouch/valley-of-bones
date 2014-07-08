package com.ahsgaming.valleyofbones.screens;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Disableable;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.utils.Pools;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.*;

/**
 * valley-of-bones
 * (c) 2013 Jami Couch
 * Created on 1/30/14 by jami
 * ahsgaming.com
 */
public class ImageSelectBox extends Widget implements Disableable {
    static final Vector2 tmpCoords = new Vector2();

    ImageSelectBoxStyle style;
    Image[] items;
    int selectedIndex = 0;

    ImageSelectList list;
    private float prefWidth, prefHeight;
    private ClickListener clickListener;
    int maxListCount;
    boolean disabled;

    public ImageSelectBox (Image[] items, Skin skin) {
        this(items, skin.get(ImageSelectBoxStyle.class));
    }

    public ImageSelectBox (Image[] items, Skin skin, String styleName) {
        this(items, skin.get(styleName, ImageSelectBoxStyle.class));
    }

    public ImageSelectBox (Image[] items, ImageSelectBoxStyle style) {
        setStyle(style);
        setItems(items);
        setWidth(getPrefWidth());
        setHeight(getPrefHeight());

        addListener(clickListener = new ClickListener() {
            public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
                if (pointer == 0 && button != 0) return false;
                if (disabled) return false;
                Stage stage = getStage();
                if (list == null) list = new ImageSelectList();
                list.show(stage);
                return true;
            }
        });
    }

    /** Set the max number of items to display when the select box is opened. Set to 0 (the default) to display as many as fit in
     * the stage height. */
    public void setMaxListCount (int maxListCount) {
        this.maxListCount = maxListCount;
    }

    /** @return Max number of items to display when the box is opened, or <= 0 to display them all. */
    public int getMaxListCount () {
        return maxListCount;
    }

    public void setStyle (ImageSelectBoxStyle style) {
        if (style == null) throw new IllegalArgumentException("style cannot be null.");
        this.style = style;
        if (items != null)
            setItems(items);
        else
            invalidateHierarchy();
    }

    /** Returns the select box's style. Modifying the returned style may not have an effect until {@link #setStyle(com.ahsgaming.valleyofbones.screens.ImageSelectBox.ImageSelectBoxStyle)}
     * is called. */
    public ImageSelectBoxStyle getStyle () {
        return style;
    }

    public void setItems (Image[] items) {
        if (items == null) throw new IllegalArgumentException("items cannot be null.");



        this.items = items;
        selectedIndex = 0;

        Drawable bg = style.background;

        prefHeight = Math.max(bg.getTopHeight() + bg.getBottomHeight(),
                bg.getMinHeight());

        float maxItemWidth = 0;
        for (int i = 0; i < this.items.length; i++)
            maxItemWidth = Math.max(this.items[i].getWidth(), maxItemWidth);

        prefWidth = bg.getLeftWidth() + bg.getRightWidth() + maxItemWidth;

        ImageList.ImageListStyle listStyle = style.listStyle;
        ScrollPane.ScrollPaneStyle scrollStyle = style.scrollStyle;
        prefWidth = Math.max(
                prefWidth,
                maxItemWidth
                        + scrollStyle.background.getLeftWidth()
                        + scrollStyle.background.getRightWidth()
                        + listStyle.selection.getLeftWidth()
                        + listStyle.selection.getRightWidth()
                        + Math.max(style.scrollStyle.vScroll != null ? style.scrollStyle.vScroll.getMinWidth() : 0,
                        style.scrollStyle.vScrollKnob != null ? style.scrollStyle.vScrollKnob.getMinWidth() : 0));

        if (this.items.length > 0) {
            ChangeListener.ChangeEvent changeEvent = Pools.obtain(ChangeListener.ChangeEvent.class);
            ImageSelectBox.this.fire(changeEvent);
            Pools.free(changeEvent);
        }

        invalidateHierarchy();
    }

    public Image[] getItems () {
        return items;
    }

    @Override
    public void draw (SpriteBatch batch, float parentAlpha) {
        Drawable background;
        if (disabled && style.backgroundDisabled != null)
            background = style.backgroundDisabled;
        else if (list != null && list.getParent() != null && style.backgroundOpen != null)
            background = style.backgroundOpen;
        else if (clickListener.isOver() && style.backgroundOver != null)
            background = style.backgroundOver;
        else
            background = style.background;

        Color color = getColor();
        float x = getX();
        float y = getY();
        float width = getWidth();
        float height = getHeight();

        batch.setColor(color.r, color.g, color.b, color.a * parentAlpha);
        background.draw(batch, x, y, width, height);
        if (items.length > 0) {
//            float availableWidth = width - background.getLeftWidth() - background.getRightWidth();
//            int numGlyphs = font.computeVisibleGlyphs(items[selectedIndex], 0, items[selectedIndex].length(), availableWidth);
//            bounds.set(font.getBounds(items[selectedIndex]));
//            height -= background.getBottomHeight() + background.getTopHeight();
//            float textY = (int)(height / 2 + background.getBottomHeight() + bounds.height / 2);
//            font.setColor(fontColor.r, fontColor.g, fontColor.b, fontColor.a * parentAlpha);
//            font.draw(batch, items[selectedIndex], x + background.getLeftWidth(), y + textY, 0, numGlyphs);
            batch.setColor(new Color(batch.getColor()).mul(items[selectedIndex].getColor()));
            items[selectedIndex].getDrawable().draw(batch, x + background.getLeftWidth(), y + items[selectedIndex].getHeight() * 0.5f + background.getBottomHeight(), items[selectedIndex].getWidth(), items[selectedIndex].getHeight());
        }
    }

    /** Sets the selected item via it's index
     * @param selection the selection index */
    public void setSelection (int selection) {
        if (selection < 0) throw new IllegalArgumentException("selection cannot be < 0.");
        this.selectedIndex = selection;
    }

    public void setSelection (String item) {
        for (int i = 0; i < items.length; i++) {
            if (items[i].equals(item)) {
                selectedIndex = i;
            }
        }
    }

    /** @return the index of the current selection. The top item has an index of 0 */
    public int getSelectionIndex () {
        return selectedIndex;
    }

    /** @return the string of the currently selected item */
    public Image getSelection () {
        return items[selectedIndex];
    }

    public void setDisabled (boolean disabled) {
        if (disabled && !this.disabled) hideList();
        this.disabled = disabled;
    }

    public float getPrefWidth () {
        return prefWidth;
    }

    public float getPrefHeight () {
        return prefHeight;
    }

    public void hideList () {
        if (list == null || list.getParent() == null) return;
        list.addAction(sequence(fadeOut(0.15f, Interpolation.fade), removeActor()));
    }

    class ImageSelectList extends ScrollPane {
        final ImageList list;
        final Vector2 screenCoords = new Vector2();

        public ImageSelectList() {
            super(null, style.scrollStyle);

            setOverscroll(false, false);
            setFadeScrollBars(false);

            list = new ImageList(new Image[0], style.listStyle);
            setWidget(list);
            list.addListener(new InputListener() {
                public boolean mouseMoved (InputEvent event, float x, float y) {
//                    list.setSelectedIndex(Math.min(items.length - 1, (int)((list.getHeight() - y) / list.getItemHeight())));
                    list.touchDown(y);
                    return true;
                }
            });

            addListener(new InputListener() {
                public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
                    if (event.getTarget() == list) return true;
                    hideList();
                    return false;
                }

                public void touchUp (InputEvent event, float x, float y, int pointer, int button) {
                    if (hit(x, y, true) == list) {
                        setSelection(list.getSelectedIndex());
                        ChangeListener.ChangeEvent changeEvent = Pools.obtain(ChangeListener.ChangeEvent.class);
                        ImageSelectBox.this.fire(changeEvent);
                        Pools.free(changeEvent);
                        hideList();
                    }
                }
            });
        }

        public void show (Stage stage) {
            stage.addActor(this);

            ImageSelectBox.this.localToStageCoordinates(tmpCoords.set(0, 0));
            screenCoords.set(tmpCoords);

            list.setItems(items);
            list.setSelectedIndex(selectedIndex);

            // Show the list above or below the select box, limited to a number of items and the available height in the stage.
            float itemHeight = 16; // TODO list.getItemHeight();
            float height = itemHeight * (maxListCount <= 0 ? items.length : Math.min(maxListCount, items.length));
            Drawable background = getStyle().background;
            if (background != null) height += background.getTopHeight() + background.getBottomHeight();

            float heightBelow = tmpCoords.y;
            float heightAbove = stage.getCamera().viewportHeight - tmpCoords.y - ImageSelectBox.this.getHeight();
            boolean below = true;
            if (height > heightBelow) {
                if (heightAbove > heightBelow) {
                    below = false;
                    height = Math.min(height, heightAbove);
                } else
                    height = heightBelow;
            }

            if (below)
                setY(tmpCoords.y - height);
            else
                setY(tmpCoords.y + ImageSelectBox.this.getHeight());
            setX(tmpCoords.x);
            setWidth(ImageSelectBox.this.getWidth());
            setHeight(height);

            scrollToCenter(0, list.getHeight() - selectedIndex * itemHeight - itemHeight / 2, 0, 0);
            updateVisualScroll();

            clearActions();
            getColor().a = 0;
            addAction(fadeIn(0.3f, Interpolation.fade));

            stage.setScrollFocus(this);
        }

        @Override
        public Actor hit (float x, float y, boolean touchable) {
            Actor actor = super.hit(x, y, touchable);
            return actor != null ? actor : this;
        }

        public void act (float delta) {
            super.act(delta);
            ImageSelectBox.this.localToStageCoordinates(tmpCoords.set(0, 0));
            if (tmpCoords.x != screenCoords.x || tmpCoords.y != screenCoords.y) hideList();
        }
    }

    /** The style for a select box, see {@link SelectBox}.
     * @author mzechner
     * @author Nathan Sweet */
    static public class ImageSelectBoxStyle {
        public Drawable background;
        public ScrollPane.ScrollPaneStyle scrollStyle;
        public ImageList.ImageListStyle listStyle;
        /** Optional. */
        public Drawable backgroundOver, backgroundOpen, backgroundDisabled;

        public ImageSelectBoxStyle() {
        }

        public ImageSelectBoxStyle(Drawable background, ScrollPane.ScrollPaneStyle scrollStyle,
                                   ImageList.ImageListStyle listStyle) {
            this.background = background;
            this.scrollStyle = scrollStyle;
            this.listStyle = listStyle;
        }

        public ImageSelectBoxStyle(ImageSelectBoxStyle style) {
            this.background = style.background;
            this.backgroundOver = style.backgroundOver;
            this.backgroundOpen = style.backgroundOpen;
            this.backgroundDisabled = style.backgroundDisabled;
            this.scrollStyle = new ScrollPane.ScrollPaneStyle(style.scrollStyle);
            this.listStyle = new ImageList.ImageListStyle(style.listStyle);
        }
    }
}

