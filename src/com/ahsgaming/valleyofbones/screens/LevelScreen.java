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
package com.ahsgaming.valleyofbones.screens;

import com.ahsgaming.valleyofbones.*;
import com.ahsgaming.valleyofbones.map.HexMap;
import com.ahsgaming.valleyofbones.network.*;
import com.ahsgaming.valleyofbones.screens.panels.*;
import com.ahsgaming.valleyofbones.units.Prototypes;
import com.ahsgaming.valleyofbones.units.Unit;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;

/**
 * @author jami
 *
 */
public class LevelScreen extends AbstractScreen {
	public String LOG = "LevelScreen";

    protected static LevelScreen instance;

	private Group grpLevel;
	
	protected GameController gController = null;

	ShapeRenderer shapeRenderer;

    // second spritebatch for map, that way it can be scaled without affecting the UI
    SpriteBatch mapSpriteBatch;
    OrthographicCamera mapCamera;
    Vector2 mapScale = new Vector2(1, 1);
	
	
	// camera 'center' position - this will always remain within the bounds of the map
    protected Vector2 posCamera = new Vector2();
	
	// UX stuff
    float lastTurnTick;
	
	Group grpPreviews = new Group();

    BuildPanel buildPanel;
    Panel upgradePanel;
    InfoPanel selectionPanel;
    SurrenderPanel surrenderPanel;
    TurnPanel turnPanel;

    boolean clickInterrupt = false;

    boolean buildMode = false;
    Prototypes.JsonProto buildProto = null;
    Image buildImage = null;

    GameObject lastSelected = null;

	/**
	 * @param game
	 */
	public LevelScreen(VOBGame game, GameController gController) {
		super(game);
		this.gController = gController;
		shapeRenderer = new ShapeRenderer();
        instance = this;
	}
	
	/**
	 * Methods
	 */

    protected void clampCamera() {
		HexMap map = gController.getMap();
		
		if (posCamera.x < 0) posCamera.x = 0;
		if (posCamera.x > map.getMapWidth()) posCamera.x = map.getMapWidth();
		
		if (posCamera.y < 0) posCamera.y = 0;
		if (posCamera.y > map.getMapHeight()) posCamera.y = map.getMapHeight();
	}
	
	private void drawUnitBoxes() {
		if (gController.getSelectedObject() != null) {
            shapeRenderer.setProjectionMatrix(mapCamera.combined); // BUGFIX: rescaling the window threw off the selection drawings

            GameObject obj = gController.getSelectedObject();
			shapeRenderer.begin(ShapeType.Line);
			shapeRenderer.setColor((obj.getOwner() != null ? obj.getOwner().getPlayerColor() : new Color(1, 1, 1, 1)));
			Vector2 start = gController.getMap().boardToMapCoords(obj.getBoardPosition().x, obj.getBoardPosition().y);
			start.sub((posCamera.x - mapCamera.viewportWidth * 0.5f), (posCamera.y - mapCamera.viewportHeight * 0.5f));

			Vector2 base = new Vector2(start.x, start.y);
			shapeRenderer.line(base.x + gController.getMap().getTileWidth() * 0.5f, base.y, base.x, base.y + gController.getMap().getTileHeight() * 0.25f);
			shapeRenderer.line(base.x, base.y + gController.getMap().getTileHeight() * 0.25f, base.x, base.y + gController.getMap().getTileHeight() * 0.75f);
			shapeRenderer.line(base.x, base.y + gController.getMap().getTileHeight() * 0.75f, base.x + gController.getMap().getTileWidth() * 0.5f, base.y + gController.getMap().getTileHeight());
			shapeRenderer.line(base.x + gController.getMap().getTileWidth() * 0.5f, base.y + gController.getMap().getTileHeight(), base.x + gController.getMap().getTileWidth(), base.y + gController.getMap().getTileHeight() * 0.75f);
			shapeRenderer.line(base.x + gController.getMap().getTileWidth(), base.y + gController.getMap().getTileHeight() * 0.75f, base.x + gController.getMap().getTileWidth(), base.y + gController.getMap().getTileHeight() * 0.25f);
			shapeRenderer.line(base.x + gController.getMap().getTileWidth(), base.y + gController.getMap().getTileHeight() * 0.25f, base.x + gController.getMap().getTileWidth() * 0.5f, base.y);
			shapeRenderer.end();

            if (obj instanceof Unit && ((Unit)obj).getAttackRange() > 0) {
                shapeRenderer.begin(ShapeType.Line);
                Color color = new Color((obj.getOwner() != null ? obj.getOwner().getPlayerColor() : new Color(1, 1, 1, 1)));
                color.mul(0.5f);
                shapeRenderer.setColor(color);

                int r = ((Unit)obj).getAttackRange(); // todo set this to attack range
                int segments = 6 + 12 * r;
                int segperside = segments / 6;
                Vector2 tileSize = new Vector2(gController.getMap().getTileWidth(), gController.getMap().getTileHeight());
                Vector2 origin = new Vector2(start.x - (r - 1) * tileSize.x * 0.5f,
                        start.y - r * tileSize.y * 0.75f);
                Vector2 cur = new Vector2(origin);
                Vector2 next = new Vector2();

                Vector2 slope = new Vector2(-1, 1);
                for (int side = 0; side < 6; side ++) {
                    for (int seg = 0; seg < segperside; seg++) {


                        if ((seg % 2 == 0 && slope.x == -1 * (slope.y != 0 ? slope.y : 1)) || (seg % 2 == 1 && slope.x == (slope.y != 0 ? slope.y : 1))) {
                            if (slope.y != 0) {
                                next.x = cur.x + (tileSize.x * 0.5f * slope.x);
                                next.y = cur.y + (0.25f * tileSize.y * slope.y);
                            } else {
                                next.x = cur.x + (tileSize.x * 0.5f * slope.x);
                                next.y = cur.y - (0.25f * tileSize.y);
                            }
                        } else {
                            if (slope.y != 0) {
                                next.x = cur.x;
                                next.y = cur.y + (0.5f * tileSize.y * slope.y);
                            } else {
                                next.x = cur.x + (tileSize.x * 0.5f * slope.x);
                                next.y = cur.y + (0.25f * tileSize.y);
                            }

                        }
                        shapeRenderer.line(cur.x, cur.y, next.x, next.y);
                        cur.set(next);
                    }

                    if (slope.x == -1 && slope.y == 1)
                        slope.set(1, 1);
                    else if (slope.x == 1 && slope.y == 1)
                        slope.set(1, 0);
                    else if (slope.x == 1 && slope.y == 0)
                        slope.set(1, -1);
                    else if (slope.x == 1 && slope.y == -1)
                        slope.set(-1, -1);
                    else if (slope.x == -1 && slope.y == -1)
                        slope.set(-1, 0);
                }


                shapeRenderer.end();
            }
		}
	}

    protected void build(Vector2 boardPos) {
        if (!buildMode) return;

        if (boardPos.x < 0 || boardPos.y < 0 || boardPos.x >= gController.getMap().getWidth() || boardPos.y >= gController.getMap().getHeight()) {
            unsetBuildMode();
        } else if (game.getPlayer().canBuild(buildProto.id, gController) && gController.isBoardPosEmpty(boardPos) && gController.getMap().isBoardPositionVisible(boardPos)) {
            Build bld = new Build();
            bld.owner = game.getPlayer().getPlayerId();
            bld.turn = gController.getGameTurn();
            bld.building = buildProto.id;
            bld.location = boardPos;
            game.sendCommand(bld);
            if (!(Gdx.input.isKeyPressed(Keys.SHIFT_LEFT) || Gdx.input.isKeyPressed(Keys.SHIFT_RIGHT))) unsetBuildMode();
        }
    }

    protected void attack(int unit, int target) {
        Attack at = new Attack();
        at.owner = game.getPlayer().getPlayerId();
        at.turn = gController.getGameTurn();
        at.unit = unit;
        at.target = target;

        game.sendCommand(at);
    }

    protected void move(int unit, Vector2 boardPos) {
        Move mv = new Move();
        mv.owner = game.getPlayer().getPlayerId();
        mv.turn = gController.getGameTurn();
        mv.unit = unit;
        mv.toLocation = boardPos;

        game.sendCommand(mv);
    }

    protected void endTurn() {
        if (!isCurrentPlayer()) return;

        EndTurn et = new EndTurn();
        et.owner = game.getPlayer().getPlayerId();
        et.turn = gController.getGameTurn();

        game.sendCommand(et);
    }

    public boolean canRefund(Unit unit) {
        return gController.canPlayerRefundUnit(game.getPlayer(), unit);
    }

    public void refundUnit(Unit unit) {
        if (canRefund(unit)) {
            int refund = unit.getRefund();
            Refund r = new Refund();
            r.owner = game.getPlayer().getPlayerId();
            r.turn = gController.getGameTurn();
            r.unit = unit.getObjId();
            game.sendCommand(r);
            addFloatingLabel(String.format("+$%02d", refund), unit.getX() + unit.getWidth() * 0.5f, unit.getY());
        }
    }

    public void setBuildMode(Prototypes.JsonProto proto) {
        if (buildMode) unsetBuildMode();
        if (isCurrentPlayer() && game.getPlayer().canBuild(proto.id, gController)) {
            buildMode = true;
            buildProto = proto;
            buildImage = new Image(VOBGame.instance.getTextureManager().getSpriteFromAtlas("assets", proto.image));
            buildImage.setColor(1, 1, 1, 0.5f);
        }
    }

    public boolean isBuildMode() {
        return buildMode;
    }

    public void unsetBuildMode() {
        buildMode = false;
        buildProto = null;
        if (buildImage != null) {
            buildImage.remove();
            buildImage = null;
        }
    }
	
	public boolean getClickInterrupt() {
        return clickInterrupt;
    }

    public void setClickInterrupt(boolean interrupt) {
        clickInterrupt = interrupt;
    }

	public Vector2 screenToMapCoords(float x, float y) {
        x *= mapScale.x;
        y *= mapScale.y;
		return new Vector2(x + (posCamera.x - mapCamera.viewportWidth * 0.5f), y + (posCamera.y - mapCamera.viewportHeight * 0.5f));
	}
	
	public Vector2 mapToScreenCoords(float x, float y) {
        x -= (posCamera.x - mapCamera.viewportWidth * 0.5f);
        x /= mapScale.x;

        y -= (posCamera.y - mapCamera.viewportHeight * 0.5f);
        y /= mapScale.y;
		return new Vector2(x, y);
	}

    public boolean isCurrentPlayer() {
        return gController.getCurrentPlayer() == game.getPlayer();
    }

	
	/**
	 * Implemented methods
	 */
	
	@Override
	public void show() {
		super.show();

        mapSpriteBatch = new SpriteBatch();
        mapCamera = new OrthographicCamera();

		Gdx.app.log(VOBGame.LOG, "LevelScreen#show");
		
		grpLevel = gController.getGroup();

		posCamera.set(gController.getSpawnPoint(game.getPlayer().getPlayerId()));

        buildPanel = new BuildPanel(gController, game.getPlayer(), this);
        upgradePanel = new Panel(game, this, "tinker", getSkin());
        selectionPanel = new InfoPanel(game, this, getSkin());
        turnPanel = new TurnPanel(gController, game.getPlayer(), getSkin());

        surrenderPanel = new SurrenderPanel(game, this, getSkin());

        ClickListener interruptListener = new ClickListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                Gdx.app.log(LOG, "interrupt");
                setClickInterrupt(true);
                return super.touchDown(event, x, y, pointer, button);    //To change body of overridden methods use File | Settings | File Templates.
            }
        };

        buildPanel.addListener(interruptListener);
        selectionPanel.addListener(interruptListener);
        turnPanel.addListener(interruptListener);
	}

    public void zoom(float amount) {
        mapScale.set(mapScale.x * amount, mapScale.y * amount);
        mapCamera.setToOrtho(false, stage.getCamera().viewportWidth * mapScale.x, stage.getCamera().viewportHeight * mapScale.y);

        Vector2 map = screenToMapCoords(stage.getWidth() * 0.5f, stage.getHeight() * 0.5f);
        Vector2 screen = mapToScreenCoords(map.x, map.y);
        Gdx.app.log(LOG, "" + new Vector2(stage.getWidth() * 0.5f, stage.getHeight() * 0.5f));
        Gdx.app.log(LOG, "" + map);
        Gdx.app.log(LOG, "" + screen);
    }
	
	@Override
	public void resize(int width, int height) {
		super.resize(width, height);

        stage.addListener(new LevelScreenInputListener(this));

        stage.addListener(new InputListener() {
            @Override
            public boolean scrolled(InputEvent event, float x, float y, int amount) {
                zoom((amount > 0 ? 0.9f : 1.1f));

                return super.scrolled(event, x, y, amount);    //To change body of overridden methods use File | Settings | File Templates.
            }
        });

        mapCamera.setToOrtho(false, stage.getCamera().viewportWidth * mapScale.x, stage.getCamera().viewportHeight * mapScale.y);  // TODO
//        mapScale = new Vector2(800f / width, 480f / height);
		
		stage.addActor(grpLevel);     // -21fps

		// panels
        // TODO add back upgrade panel
//        stage.addActor(upgradePanel);
//        upgradePanel.setAnchor(0, 64);

        stage.addActor(buildPanel);
        buildPanel.setPosition(-3, -3);

        stage.addActor(selectionPanel);
        selectionPanel.setPosition(stage.getWidth() * 0.5f - selectionPanel.getWidth() * 0.5f, -selectionPanel.getHeight() + 3);

//        stage.addActor(surrenderPanel);
        surrenderPanel.setAnchor(0, stage.getHeight() - 64);

        turnPanel.setPosition(stage.getWidth() * 0.5f - turnPanel.getWidth() * 0.5f, stage.getHeight() - turnPanel.getHeight() + 3);
        stage.addActor(turnPanel);
	}

	public void yourTurnPopup() {
        if (gController.getTurnTimer() > 59 && isCurrentPlayer() && lastTurnTick < gController.getTurnTimer()) {
            popupMessage("YOUR TURN!", "hazard-sign", 1);
        }
        lastTurnTick = gController.getTurnTimer();
	}
	
	@Override
	public void render(float delta) {
//        super.render(delta);
        stage.act(delta);
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        mapCamera.update();
        mapSpriteBatch.setProjectionMatrix(mapCamera.combined);
        mapSpriteBatch.begin();
        gController.getMap().draw(mapSpriteBatch, -posCamera.x + mapCamera.viewportWidth * 0.5f, -posCamera.y + mapCamera.viewportHeight * 0.5f, 1, gController.getUnits());
        mapSpriteBatch.end();

        // DRAW BOXES
        drawUnitBoxes();

        stage.draw();

        if (buildMode && !isCurrentPlayer()) unsetBuildMode();
        if (gController.getSelectedObject() != null && gController.getSelectedObject().isRemove()) gController.clearSelection();

        // clear highlighting if necessary
        gController.getMap().clearHighlightAndDim();
        if (lastSelected != gController.getSelectedObject())
            gController.getMap().setMapDirty(true);

        lastSelected = gController.getSelectedObject();

        selectionPanel.setSelected(null);
        if (lastSelected != null && lastSelected instanceof Unit) {
            gController.getMap().highlightArea(lastSelected.getBoardPosition(), ((Unit) lastSelected).getMovesLeft(), true);

            selectionPanel.setSelected((Unit) lastSelected);
            if (Utils.epsilonEquals(selectionPanel.getY(), -selectionPanel.getHeight() + 3f, 0.01f)) {
                selectionPanel.addAction(Actions.moveBy(0, selectionPanel.getHeight() - 6, 0.5f));
            }
        } else {
            if (Utils.epsilonEquals(selectionPanel.getY(), -3f, 0.01f)) {
                selectionPanel.addAction(Actions.moveBy(0, -selectionPanel.getHeight() + 6, 0.5f));
            }
        }

        gController.getMap().update(game.getPlayer());

        grpLevel.setPosition(-1 * posCamera.x + stage.getWidth() * 0.5f, -1 * posCamera.y + stage.getHeight() * 0.5f);

		yourTurnPopup();
        buildPanel.update();

        selectionPanel.update();

        surrenderPanel.update(delta);

        turnPanel.update(isCurrentPlayer());

        if (buildImage != null) buildImage.remove();
        if (buildMode) {
            // TODO build preview for PC players (though that will go in a listener now)

            if (!isCurrentPlayer()) unsetBuildMode();
        }

	}
	
	public void addFloatingLabel(String text, float x, float y) {
		Gdx.app.log(LOG, "Floating Label!");
		Label lbl = new Label(text, new LabelStyle(getSmallFont(), new Color(1,1,1,1)));
		lbl.setPosition(x - lbl.getWidth() * 0.5f, y - lbl.getHeight() * 0.5f);
		lbl.addAction(Actions.parallel(Actions.fadeOut(1f), Actions.moveBy(0, 64f, 1f)));
		grpLevel.addActor(lbl);
	}

    public Group popupMessage(String text, String icon, float duration) {
        Gdx.app.log(LOG, "Popup message!");
        Group popup = new Group();
        Label lbl = new Label(text, getSkin(), "medium");
        Image img = new Image(VOBGame.instance.getTextureManager().getSpriteFromAtlas("assets", icon));
        popup.addActor(img);
        popup.addActor(lbl);
        lbl.setPosition(img.getRight(), (img.getHeight() - lbl.getHeight()) * 0.5f);
        popup.setSize(lbl.getRight(), img.getTop());
        popup.setPosition((stage.getWidth() - popup.getWidth()) * 0.5f, (stage.getHeight() - popup.getHeight()) * 0.5f);
        popup.setColor(popup.getColor().r, popup.getColor().g, popup.getColor().b, 0);
        stage.addActor(popup);
        popup.addAction(Actions.sequence(Actions.fadeIn(0.5f), Actions.delay(duration), Actions.fadeOut(0.5f)));
        return popup;
    }

    /*
     * Static methods
     */

    public static LevelScreen getInstance() {
        return instance;
    }

    public static class TurnPanel extends Group {
        GameController gController;

        static String TIME = "%d:%02d";

        Image imgBackground, imgP1Indicator, imgP2Indicator, imgIndicatorOverlay;
        Label lblPlayer1, lblPlayer2, lblTime;
        Player player1, player2, lastPlayer, thePlayer;
        Skin skin;

        InfoPanel infoPanel;
        Group endTurn;

        int lastTick;

        public TurnPanel(GameController controller, Player player, Skin skin) {
            this.gController = controller;
            this.skin = skin;
            this.thePlayer = player;

            imgBackground = new Image(VOBGame.instance.getTextureManager().getSpriteFromAtlas("assets", "turn-hud-bg"));
            addActor(imgBackground);

            imgP1Indicator = new Image(VOBGame.instance.getTextureManager().getSpriteFromAtlas("assets", "turn-indicator-base"));
            addActor(imgP1Indicator);

            imgP2Indicator = new Image(VOBGame.instance.getTextureManager().getSpriteFromAtlas("assets", "turn-indicator-base"));
            addActor(imgP2Indicator);

            imgIndicatorOverlay = new Image(VOBGame.instance.getTextureManager().getSpriteFromAtlas("assets", "turn-indicator-overlay"));

            player1 = gController.getPlayers().get(0);
            lblPlayer1 = new Label(player1.getPlayerName(), skin, "small-font", player1.getPlayerColor());
            addActor(lblPlayer1);

            player2 = gController.getPlayers().get(1);
            lblPlayer2 = new Label(player2.getPlayerName(), skin, "small-font", player2.getPlayerColor());
            addActor(lblPlayer2);

            lblTime = new Label(String.format(TIME, 0, 0), skin, "small-font", new Color(0.8f, 0.8f, 0.8f, 1));
            addActor(lblTime);

            infoPanel = new InfoPanel(thePlayer, (thePlayer == player2), skin);
            addActor(infoPanel);
            infoPanel.setZIndex(0);

            endTurn = new Group();
            addActor(endTurn);
            endTurn.setZIndex(1);

            Image bg = new Image(VOBGame.instance.getTextureManager().getSpriteFromAtlas("assets", "turn-hud-bg-small"));
            endTurn.addActor(bg);
            endTurn.setSize(bg.getWidth(), bg.getHeight());

            Label end = new Label("End Turn", skin, "small-font", new Color(0.8f, 0.8f, 0.8f, 1));
            endTurn.addActor(end);
            if (end.getWidth() > endTurn.getWidth() - 70)
                end.setFontScale((endTurn.getWidth() - 70) / end.getWidth());

            end.setX((thePlayer != player1 ? 25 : 45) + (endTurn.getWidth() - 70 - end.getWidth() * end.getFontScaleX()) * 0.5f);
            end.setY(endTurn.getHeight() * 0.5f - (end.getHeight() * end.getFontScaleY()) * 0.5f);

            endTurn.setX(imgBackground.getWidth() * 0.5f - endTurn.getWidth() * 0.5f);

            endTurn.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    super.clicked(event, x, y);
                    LevelScreen.getInstance().endTurn();
                }
            });

            layout();
        }

        public void layout() {
            imgBackground.setPosition(0, 0);
            setSize(imgBackground.getWidth(), imgBackground.getHeight());

            lblTime.setPosition(
                    (imgBackground.getWidth() - lblTime.getWidth()) * 0.5f,
                    (imgBackground.getHeight() - lblTime.getHeight()) * 0.5f
            );

            imgP1Indicator.setPosition(
                    (imgBackground.getWidth() * 0.4f - imgP1Indicator.getWidth() * 0.5f),
                    (imgBackground.getHeight() - imgP1Indicator.getHeight()) * 0.5f
            );

            imgP2Indicator.setPosition(
                    (imgBackground.getWidth() * 0.6f - imgP2Indicator.getWidth() * 0.5f),
                    (imgBackground.getHeight() - imgP2Indicator.getHeight()) * 0.5f
            );

            if (lblPlayer1.getWidth() > imgP1Indicator.getX() - 25)
                lblPlayer1.setFontScale((imgP1Indicator.getX() - 25) / lblPlayer1.getWidth());

            lblPlayer1.setPosition(
                    25,
                    (imgBackground.getHeight() - lblPlayer1.getHeight()) * 0.5f
            );

            Gdx.app.log("width", Float.toString(lblPlayer2.getWidth()));
            Gdx.app.log("max", Float.toString((getWidth() - imgP2Indicator.getRight() - 25)));
            if (lblPlayer2.getWidth() > getWidth() - imgP2Indicator.getRight() - 25)
                lblPlayer2.setFontScale((getWidth() - imgP2Indicator.getRight() - 25) / lblPlayer2.getWidth());
            Gdx.app.log("scale", Float.toString(lblPlayer2.getFontScaleX()));

            lblPlayer2.setPosition(
                    imgBackground.getWidth() - lblPlayer2.getWidth() * lblPlayer2.getFontScaleX() - 25,
                    (imgBackground.getHeight() - lblPlayer2.getHeight()) * 0.5f
            );

            if (thePlayer == player1) {
                infoPanel.setPosition(
                        -infoPanel.getWidth() + 45,
                        imgBackground.getHeight() - infoPanel.getHeight()
                );
            } else {
                infoPanel.setPosition(
                        imgBackground.getWidth() - 45,
                        imgBackground.getHeight() - infoPanel.getHeight()
                );

            }
            endTurn.setY(imgBackground.getHeight() - endTurn.getHeight());

        }

        public void update(boolean isCurrent) {
            if (lastTick != Math.floor(gController.getTurnTimer())) {
                lastTick = (int)Math.floor(gController.getTurnTimer());
                lblTime.setText(String.format(TIME, lastTick / 60, (lastTick % 60)));

                if (gController.getTurnTimer() <= 5 && isCurrent) {
                    lblTime.addAction(Actions.sequence(Actions.color(new Color(1.0f, 0, 0, 1.0f)), Actions.delay(0.2f), Actions.color(new Color(0.8f, 0.8f, 0.8f, 1f))));
                }
            }

            if (lastPlayer != gController.getCurrentPlayer()) {
                lastPlayer = gController.getCurrentPlayer();
                if (lastPlayer == player1) {
                    addActor(imgIndicatorOverlay);
                    imgIndicatorOverlay.setColor(player1.getPlayerColor());
                    imgIndicatorOverlay.setPosition(imgP1Indicator.getX(), imgP1Indicator.getY());
                } else if (lastPlayer == player2) {
                    addActor(imgIndicatorOverlay);
                    imgIndicatorOverlay.setColor(player2.getPlayerColor());
                    imgIndicatorOverlay.setPosition(imgP2Indicator.getX(), imgP2Indicator.getY());
                } else {
                    imgIndicatorOverlay.remove();
                }

                if (thePlayer == player1) {
                    if (isCurrent) {
                        endTurn.addAction(Actions.moveTo(imgBackground.getWidth() - 45, endTurn.getY(), 0.5f));
                    } else {
                        endTurn.addAction(Actions.moveTo(imgBackground.getWidth() - endTurn.getWidth() + 10, endTurn.getY(), 0.5f));
                    }

                } else {
                    if (isCurrent) {
                        endTurn.addAction(Actions.moveTo(-endTurn.getWidth() + 45, endTurn.getY(), 0.5f));
                    } else {
                        endTurn.addAction(Actions.moveTo(-10, endTurn.getY(), 0.5f));
                    }
                }
            }

            infoPanel.update();
        }

        public static class InfoPanel extends Group {
            public static String LOG = "InfoPanel";

            Player player;
            Skin skin;
            Image imgBackground, imgMoney, imgSupply;
            Label lblMoney, lblSupply;

            float lastMoney, lastCurSupply, lastMaxSupply;

            boolean pullRight;

            int padLeft = 25, padRight = 45;

            public InfoPanel(Player player, boolean pullRight, Skin skin) {
                this.player = player;
                this.pullRight = pullRight;
                this.skin = skin;

                if (pullRight) {
                    padLeft += padRight;
                    padRight = padLeft - padRight;
                    padLeft -= padRight;
                }

                imgBackground = new Image(VOBGame.instance.getTextureManager().getSpriteFromAtlas("assets", "turn-hud-bg-small"));
                addActor(imgBackground);

                imgMoney = new Image(VOBGame.instance.getTextureManager().getSpriteFromAtlas("assets", "money"));
                imgMoney.setScale(20 / imgMoney.getWidth());
                addActor(imgMoney);

                imgSupply = new Image(VOBGame.instance.getTextureManager().getSpriteFromAtlas("assets", "supply"));
                imgSupply.setScale(20 / imgSupply.getWidth());
                addActor(imgSupply);

                lblMoney = new Label("0000", skin, "small-font", new Color(0.8f, 0.8f, 0.8f, 1));
                addActor(lblMoney);

                lblSupply = new Label("00/00", skin, "small-font", new Color(0.8f, 0.8f, 0.8f, 1));
                addActor(lblSupply);

                layout();
            }

            public void layout() {
                imgBackground.setPosition(0, 0);

                lblMoney.setText(String.format("%04d", (int)player.getBankMoney()));
                lblSupply.setText(String.format("%02d/%02d", player.getCurFood(), player.getMaxFood()));

                imgMoney.setPosition(padLeft, (imgBackground.getHeight() - imgMoney.getHeight() * imgMoney.getScaleY()) * 0.5f);
                imgSupply.setPosition(
                        padLeft + (imgBackground.getWidth() - padLeft - padRight) * 0.5f,
                        (imgBackground.getHeight() - imgSupply.getHeight() * imgSupply.getScaleY()) * 0.5f
                );
                
                if (lblMoney.getWidth() > imgSupply.getX() - (imgMoney.getX() + imgMoney.getWidth() * imgMoney.getScaleX()))
                    lblMoney.setFontScale((imgSupply.getX() - (imgMoney.getX() + imgMoney.getWidth() * imgMoney.getScaleX())) / lblMoney.getWidth() );
                lblMoney.setPosition(imgMoney.getX() + imgMoney.getWidth() * imgMoney.getScaleX(), imgMoney.getY());
                
                if (lblSupply.getWidth() > imgBackground.getWidth() - padRight - (imgSupply.getX() + imgSupply.getWidth() * imgSupply.getScaleX()))
                    lblSupply.setFontScale((imgBackground.getWidth() - padRight - (imgSupply.getX() + imgSupply.getWidth() * imgSupply.getScaleX())) / lblSupply.getWidth() );
                lblSupply.setPosition(imgSupply.getX() + imgSupply.getWidth() * imgSupply.getScaleX(), imgSupply.getY());
                
                setSize(imgBackground.getWidth(), imgBackground.getHeight());
            }

            public void update() {
                if (lastMoney != player.getBankMoney() || lastCurSupply != player.getCurFood() || lastMaxSupply != player.getMaxFood()) {
                    layout();
                    lastMoney = player.getBankMoney();
                    lastCurSupply = player.getCurFood();
                    lastMaxSupply = player.getMaxFood();
                }
            }
        }
    }

    public static class BuildPanel extends Group {
        public static String LOG = "BuildPanel";

        GameController gController;
        LevelScreen levelScreen;
        Player player;
        Skin skin;

        Image imgBackground, imgInfantryTab, imgMechTab;
        Array<BuildItem> infantryItems, mechItems;
        Array<Prototypes.JsonProto> itemProtos;

        int selected;

        public BuildPanel(GameController controller, Player player, LevelScreen lvlScreen) {
            this.gController = controller;
            this.player = player;
            this.levelScreen = lvlScreen;
            skin = levelScreen.getSkin();

            imgBackground = new Image(VOBGame.instance.getTextureManager().getSpriteFromAtlas("assets", "build-hud-bg"));
            addActor(imgBackground);

            imgInfantryTab = new Image(VOBGame.instance.getTextureManager().getSpriteFromAtlas("assets", "build-infantry-tab"));
            addActor(imgInfantryTab);
            imgInfantryTab.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    super.clicked(event, x, y);    //To change body of overridden methods use File | Settings | File Templates.

                    select(0);
                }
            });

            imgMechTab = new Image(VOBGame.instance.getTextureManager().getSpriteFromAtlas("assets", "build-mech-tab"));
            addActor(imgMechTab);
            imgMechTab.addListener(new ClickListener(){
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    super.clicked(event, x, y);    //To change body of overridden methods use File | Settings | File Templates.

                    select(1);
                }
            });

            itemProtos = Prototypes.getPlayerCanBuild(player, controller);
            infantryItems = new Array<BuildItem>();
            mechItems = new Array<BuildItem>();

            for (Prototypes.JsonProto jp: itemProtos) {
                final BuildItem item = new BuildItem(jp, skin);

                String type = jp.getProperty("subtype").asString();
                if (type.equals("armored")) {
                    mechItems.add(item);
                } else {
                    infantryItems.add(item);
                }

                item.addListener(new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        super.clicked(event, x, y);    //To change body of overridden methods use File | Settings | File Templates.

                        click(item);
                    }
                });
            }

            layout();
        }

        public void layout() {
            imgInfantryTab.setPosition(-15, imgBackground.getTop() - 4);
            imgMechTab.setPosition(imgInfantryTab.getRight() - 25, imgInfantryTab.getY());
            int y = 100;
            switch(selected) {
                default:
                case 0:
                    imgInfantryTab.setZIndex(2);
                    imgMechTab.setZIndex(0);
                    for (Group g: mechItems) g.remove();

                    for (Group g: infantryItems) {
                        addActor(g);
                        g.setPosition(5, y);
                        y -= g.getHeight();
                    }
                    break;
                case 1:
                    imgInfantryTab.setZIndex(0);
                    imgMechTab.setZIndex(2);
                    for (Group g: infantryItems) g.remove();

                    for (Group g: mechItems) {
                        addActor(g);
                        g.setPosition(5, y);
                        y -= g.getHeight();
                    }
                    break;
            }

            setSize(imgBackground.getRight(), Math.max(imgInfantryTab.getTop(), imgMechTab.getTop()));
        }

        public void update() {
            Array<BuildItem> items;
            switch(selected) {
                default:
                case 0:
                    items = infantryItems;
                    break;
                case 1:
                    items = mechItems;
                    break;
            }

            for (BuildItem item: items) {
                if (player.canBuild(item.proto.id, gController)) {
                    item.icon.setColor(1, 1, 1, 1);
                } else {
                    item.icon.setColor(0.8f, 0.4f, 0.4f, 1);
                }
                item.setHighlight(levelScreen.buildMode && levelScreen.buildProto == item.proto);
            }
        }

        public void click(BuildItem item) {
            levelScreen.setBuildMode(item.proto);
        }

        public void select(int sel) {
            selected = sel;
            layout();
        }

        public static class BuildItem extends Group {

            Skin skin;
            Image icon, imgSupply, imgMoney, imgHiglight;
            Label lblSupply, lblMoney;
            Prototypes.JsonProto proto;

            public BuildItem(Prototypes.JsonProto proto, Skin skin) {
                this.proto = proto;
                this.skin = skin;

                imgHiglight = new Image(VOBGame.instance.getTextureManager().getSpriteFromAtlas("assets", "unit-highlight"));
                imgHiglight.setScale(0.75f);

                icon = new Image(VOBGame.instance.getTextureManager().getSpriteFromAtlas("assets", proto.image));
                icon.setScale(0.75f);
                addActor(icon);

                imgSupply = new Image(VOBGame.instance.getTextureManager().getSpriteFromAtlas("assets", "supply"));
                imgSupply.setScale(20 / imgSupply.getWidth());
                imgSupply.setPosition(icon.getX() + icon.getWidth() * icon.getScaleX(), 0);
                addActor(imgSupply);

                lblSupply = new Label(Integer.toString(proto.food), skin, "small-font", new Color(0.8f, 0.8f, 0.8f, 1));
                lblSupply.setPosition(imgSupply.getRight(), imgSupply.getY());
                addActor(lblSupply);

                imgMoney = new Image(VOBGame.instance.getTextureManager().getSpriteFromAtlas("assets", "money"));
                imgMoney.setScale(20 / imgMoney.getWidth());
                imgMoney.setPosition(imgSupply.getX(), imgSupply.getY() + imgSupply.getHeight() * imgSupply.getScaleY() + 1);
                addActor(imgMoney);

                lblMoney = new Label(Integer.toString(proto.cost), skin, "small-font", new Color(0.8f, 0.8f, 0.8f, 1));
                lblMoney.setPosition(imgMoney.getRight(), imgMoney.getY());
                addActor(lblMoney);

                setSize(Math.max(lblSupply.getRight(), lblMoney.getRight()), icon.getHeight() * icon.getScaleY());
            }

            public void setHighlight(boolean highlight) {
                if (highlight) {
                    addActor(imgHiglight);
                    imgHiglight.setZIndex(0);
                } else {
                    imgHiglight.remove();
                }
            }
        }
    }
}
