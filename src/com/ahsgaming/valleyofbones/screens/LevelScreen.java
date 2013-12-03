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

import com.ahsgaming.valleyofbones.GameController;
import com.ahsgaming.valleyofbones.GameObject;
import com.ahsgaming.valleyofbones.Player;
import com.ahsgaming.valleyofbones.TextureManager;
import com.ahsgaming.valleyofbones.VOBGame;
import com.ahsgaming.valleyofbones.map.HexMap;
import com.ahsgaming.valleyofbones.network.*;
import com.ahsgaming.valleyofbones.screens.panels.*;
import com.ahsgaming.valleyofbones.units.Prototypes;
import com.ahsgaming.valleyofbones.units.Unit;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Array;

/**
 * @author jami
 *
 */
public class LevelScreen extends AbstractScreen {
	public String LOG = "LevelScreen";

    protected static LevelScreen instance;

	private Group grpLevel;
	
	private GameController gController = null;
	
	// input stuff
	private boolean rightBtnDown = false;
	
	ShapeRenderer shapeRenderer;

    // second spritebatch for map, that way it can be scaled without affecting the UI
    SpriteBatch mapSpriteBatch;
    OrthographicCamera mapCamera;
    Vector2 mapScale;
	
	
	// camera 'center' position - this will always remain within the bounds of the map
	private Vector2 posCamera = new Vector2();
	
	// UX stuff
	Group grpTurnPane = new Group();
	Label lblTurnTimer;
	TextButton btnTurnDone;
    float lastTurnTick = 0;
    int lastTickSecs = -1;

    Texture greyBg;
	
	Group grpPreviews = new Group();
	Array<Command> commandsPreviewed = new Array<Command>();

    Panel buildPanel;
    Panel upgradePanel;
    InfoPanel selectionPanel;
    ScorePanel scorePanel;
    ScorePanel.PlayerScore playerScore;
    SurrenderPanel surrenderPanel;

    boolean clickInterrupt = false;

	private boolean vKeyDown = false, bKeyDown = false, delKeyDown = false, spaceKeyDown = false;

    boolean buildMode = false;
    Prototypes.JsonProto buildProto = null;
    Image buildImage = null;

    GameObject lastSelected = null;

    Player lastCurrentPlayer = null;

    boolean mapDirty = true;
	
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
	
	private void clampCamera() {
		HexMap map = gController.getMap();
		
		if (posCamera.x < 0) posCamera.x = 0;
		if (posCamera.x > map.getWidth() * map.getTileWidth()) posCamera.x = map.getWidth() * map.getTileWidth();
		
		if (posCamera.y < 0) posCamera.y = 0;
		if (posCamera.y > map.getHeight() * map.getTileHeight() * 0.75) posCamera.y = map.getHeight() * map.getTileHeight() * 0.75f;
	}
	
	private void drawUnitBoxes() {
		if (gController.getSelectedObject() != null) {
            shapeRenderer.setProjectionMatrix(mapCamera.combined); // BUGFIX: rescaling the window threw off the selection drawings

            GameObject obj = gController.getSelectedObject();
			shapeRenderer.begin(ShapeType.Line);
			shapeRenderer.setColor((obj.getOwner() != null ? obj.getOwner().getPlayerColor() : new Color(1, 1, 1, 1)));
			Vector2 start = gController.getMap().boardToMapCoords(obj.getBoardPosition().x, obj.getBoardPosition().y);
			start = mapToScreenCoords(start.x, start.y);

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
	
	private void doCameraMovement(float delta) {
		if (Gdx.input.isKeyPressed(Keys.UP) || Gdx.input.getY() <= game.getMouseScrollSize()) {
			// move 'up'
			posCamera.y += game.getKeyScrollSpeed() * delta;
		} else if (Gdx.input.isKeyPressed(Keys.DOWN) || Gdx.input.getY() >= stage.getHeight() - game.getMouseScrollSize()) {
			// move 'down'
			posCamera.y -= game.getKeyScrollSpeed() * delta;
		}
		
		if (Gdx.input.isKeyPressed(Keys.LEFT) || Gdx.input.getX() <= game.getMouseScrollSize()) {
			// move 'left'
			posCamera.x -= game.getKeyScrollSpeed() * delta;
		} else if (Gdx.input.isKeyPressed(Keys.RIGHT) || Gdx.input.getX() >= stage.getWidth() - game.getMouseScrollSize()) {
			// move 'right'
			posCamera.x += game.getKeyScrollSpeed() * delta;
		}
		
		clampCamera();
	}
	
	private void doProcessInput(float delta) {
		Vector2 mapPos = screenToMapCoords(Gdx.input.getX() * mapScale.x, (Gdx.graphics.getHeight() - Gdx.input.getY()) * mapScale.y);
		Vector2 boardPos = gController.getMap().mapToBoardCoords(mapPos.x, mapPos.y);

		if (Gdx.input.isButtonPressed(Buttons.LEFT)) {
            Gdx.app.log("mapPos", mapPos.toString());
            Gdx.app.log("boardPos", boardPos.toString());
			if (buildMode){
                if (isCurrentPlayer()) {

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

			} else {						
				if (!clickInterrupt) {
                    gController.selectObjAtBoardPos(boardPos, game.getPlayer());
                    if (gController.getSelectedObject() != null && gController.getSelectedObject() instanceof Unit) {
                        Unit u = (Unit)gController.getSelectedObject();
                        Gdx.app.log(LOG, String.format("Selected: %s (%d/%d)", u.getProtoId(), u.getCurHP(), u.getMaxHP()));
                        gController.getMap().highlightArea(u.getBoardPosition(), u.getMovesLeft(), true);
                    }
                }
			}
		}
		
		if (Gdx.input.isButtonPressed(Buttons.RIGHT)){
			rightBtnDown = true;
		} else {

            if (rightBtnDown && buildMode) {
                unsetBuildMode();
            } else if (rightBtnDown && gController.getSelectedObject() != null && gController.getSelectedObject() instanceof Unit) {

				if (isCurrentPlayer()) {
                    // TODO issue context-dependent commands
                    Array<GameObject> objsUnderCursor = null;
                    GameObject target = null;

                    Unit unit = (Unit)gController.getSelectedObject();
                    if (objsUnderCursor == null) {
                        objsUnderCursor = gController.getObjsAtPosition(screenToMapCoords(Gdx.input.getX(), stage.getHeight() - Gdx.input.getY()));

                        for (GameObject cur: objsUnderCursor) {
                            if (cur.getOwner() != game.getPlayer()) {
                                // TODO find the object with the highest 'threat'?
                                if ((cur instanceof Unit && (!((Unit)cur).getInvisible() || unit.isDetector())))
                                    target = cur;
                            }
                        }
                    }

                    if (target != null) {
                        // attack this target!
                        Attack at = new Attack();
                        at.owner = game.getPlayer().getPlayerId();
                        at.turn = gController.getGameTurn();
                        at.unit = unit.getObjId();
                        at.target = target.getObjId();

                        game.sendCommand(at);
                    } else {
                        // move to this location

                        Move mv = new Move();
                        mv.owner = game.getPlayer().getPlayerId();
                        mv.turn = gController.getGameTurn();
                        mv.unit = unit.getObjId();
                        mv.toLocation = boardPos;
                        if (gController.validate(mv))
                            game.sendCommand(mv);

                    }

                }

			}
            rightBtnDown = false;
		}
		
		if (!Gdx.input.isButtonPressed(Buttons.RIGHT) && !Gdx.input.isButtonPressed(Buttons.LEFT)) {

            if (Gdx.input.isKeyPressed(Keys.V)) {
                if (!vKeyDown) upgradePanel.toggle();

                vKeyDown = true;
			} else {
                vKeyDown = false;
            }

            if (Gdx.input.isKeyPressed(Keys.B)) {
                if (!bKeyDown) buildPanel.toggle();

                bKeyDown = true;
            } else {
                bKeyDown = false;
            }

            if (Gdx.input.isKeyPressed(Keys.A) && isCurrentPlayer() && game.getPlayer().canBuild("marine-base", gController)) {
                setBuildMode(Prototypes.getProto("marine-base"));
            } else if (Gdx.input.isKeyPressed(Keys.S) && isCurrentPlayer() && game.getPlayer().canBuild("saboteur-base", gController)) {
                setBuildMode(Prototypes.getProto("saboteur-base"));
            } else if (Gdx.input.isKeyPressed(Keys.D) && isCurrentPlayer() && game.getPlayer().canBuild("tank-base", gController)) {
                setBuildMode(Prototypes.getProto("tank-base"));
            } else if (Gdx.input.isKeyPressed(Keys.F) && isCurrentPlayer() && game.getPlayer().canBuild("sniper-base", gController)) {
                setBuildMode(Prototypes.getProto("sniper-base"));
            }
		}
		
		
		// TODO make a list of flags or something rather than creating fields for every key that might get pressed...
		// TODO reset keys 'n' things
		if (vKeyDown && !Gdx.input.isKeyPressed(Keys.V)) {
			vKeyDown = false;
		}

        if (Gdx.input.isKeyPressed(Keys.ESCAPE)) {
            if (buildMode) unsetBuildMode();
        }

        if (Gdx.input.isKeyPressed(Keys.FORWARD_DEL)) {
            if (!delKeyDown && gController.getSelectedObject() != null && gController.getSelectedObject() instanceof Unit) {
                refundUnit((Unit)gController.getSelectedObject());
            }
            delKeyDown = true;
        } else {
            delKeyDown = false;
        }

        if (Gdx.input.isKeyPressed(Keys.SPACE)) {
            if (!spaceKeyDown && gController.getSelectedObject() != null && gController.getSelectedObject() instanceof Unit) {
                ActivateAbility ab = new ActivateAbility();
                ab.unit = gController.getSelectedObject().getObjId();
                ab.owner = game.getPlayer().getPlayerId();
                ab.turn = gController.getGameTurn();
                game.sendCommand(ab);
            }
            spaceKeyDown = true;
        } else {
            spaceKeyDown = false;
        }
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
            buildImage = new Image(TextureManager.getSpriteFromAtlas("assets", proto.image));
            buildImage.setColor(1, 1, 1, 0.5f);
        }
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
		return new Vector2(x + (posCamera.x - stage.getWidth() * 0.5f), y + (posCamera.y - stage.getHeight() * 0.5f));  
	}
	
	public Vector2 mapToScreenCoords(float x, float y) {
		return new Vector2(x - (posCamera.x - stage.getWidth() * 0.5f), y - (posCamera.y - stage.getHeight() * 0.5f));
	}
	
	public void showCommandPreviews() {
		// TODO optimize this
		grpPreviews.clear();
		grpPreviews.remove();
		grpLevel.addActor(grpPreviews);
		
		
		for (Command c: gController.getCommandQueue()) {
			if (c.owner == game.getPlayer().getPlayerId()) {
				
				if (c instanceof Build || c instanceof Move) {
					Image img = null;
					Vector2 pos = new Vector2();
					if (c instanceof Build) {
						img = new Image(TextureManager.getSpriteFromAtlas("assets", Prototypes.getProto(((Build)c).building).image));
						pos = ((Build)c).location;
					} else if (c instanceof Move) {
						img = new Image(gController.getObjById(((Move)c).unit).getImage());
						pos = ((Move)c).toLocation;
					}
					Vector2 sPos = gController.getMap().boardToMapCoords(pos.x, pos.y);
					img.setPosition(sPos.x, sPos.y);
					img.setColor(1, 1, 1, 0.5f);
					grpPreviews.addActor(img);
				}
			}
		}
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

        buildPanel = new BuildPanel(game, this, "gear-hammer", getSkin());
        upgradePanel = new Panel(game, this, "tinker", getSkin());
        selectionPanel = new InfoPanel(game, this, "invisible", getSkin());
        scorePanel = new ScorePanel(game, this, "scores", getSkin(), gController.getPlayers());

        playerScore = new ScorePanel.PlayerScore(getSkin(), game.getPlayer(), true);

        surrenderPanel = new SurrenderPanel(game, this, getSkin());
        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(0.5f, 0.5f, 0.5f, 0.6f);
        pixmap.fill();
        greyBg = new Texture(pixmap);
	}
	
	@Override
	public void resize(int width, int height) {
		super.resize(width, height);

        Image img = new Image(greyBg);
        img.setScaleX(stage.getWidth() * 0.25f);
        img.setScaleY(stage.getHeight());
        stage.addActor(img);

        mapCamera.setToOrtho(false, 800, 480);  // TODO
        mapScale = new Vector2(800f / width, 480f / height);
		
//		stage.addActor(grpLevel);     // -21fps

		// generate the turn info
		grpTurnPane = new Group();
		lblTurnTimer = new Label(" ", new LabelStyle(getMedFont(), new Color(1,1,1,1)));

		btnTurnDone = new TextButton("END TURN", getSkin(), "medium");
		btnTurnDone.setSize(200, 60);

		lblTurnTimer.setY(btnTurnDone.getHeight());

		grpTurnPane.addActor(btnTurnDone);
		grpTurnPane.addActor(lblTurnTimer);

		grpTurnPane.setSize(btnTurnDone.getWidth(), lblTurnTimer.getTop());

		stage.addActor(grpTurnPane);  // -8fps

		// panels
        // TODO add back upgrade panel
//        stage.addActor(upgradePanel);
//        upgradePanel.setAnchor(0, 64);

        stage.addActor(buildPanel);
        buildPanel.setAnchor(0, 0);

        stage.addActor(selectionPanel);
        selectionPanel.setAnchor(0, buildPanel.getTop() + selectionPanel.getHeight());


        stage.addActor(scorePanel);
        scorePanel.update(0);
        scorePanel.setAnchor(0, stage.getHeight() - grpTurnPane.getHeight() - scorePanel.getHeight());


        stage.addActor(playerScore);
        playerScore.update();
        playerScore.setPosition(0, scorePanel.getY() - playerScore.getHeight());

//        stage.addActor(surrenderPanel);
        surrenderPanel.setAnchor(0, stage.getHeight() - 64);


		btnTurnDone.addListener(new ClickListener() {

			@Override
			public void clicked(InputEvent event, float x, float y) {
				super.clicked(event, x, y);
				Gdx.app.log("x", Float.toString(x));
				EndTurn et = new EndTurn();
				et.owner = game.getPlayer().getPlayerId();
				et.turn = gController.getGameTurn();
				
				game.sendCommand(et);
			}
			
		});
	}

	public void updateTurnPane() {

        if ((int)lastTurnTick > (int)gController.getTurnTimer()) {
            lblTurnTimer.setText(String.format("TIMER %02d:%02d", (int)Math.floor(gController.getTurnTimer() / 60), (int)gController.getTurnTimer() % 60));
            if (gController.getTurnTimer() <= 5 && isCurrentPlayer()) {
                lblTurnTimer.addAction(Actions.sequence(Actions.color(new Color(1.0f, 0, 0, 1.0f)), Actions.delay(0.2f), Actions.color(new Color(1.0f, 1f, 1f, 1f))));
            }
        }

        if (gController.getTurnTimer() > 59 && isCurrentPlayer() && lastTurnTick < gController.getTurnTimer()) {
            popupMessage("YOUR TURN!", "hazard-sign", 1);
        }
        lastTurnTick = gController.getTurnTimer();
		grpTurnPane.setX(0);
        grpTurnPane.setSize(btnTurnDone.getWidth(), grpTurnPane.getHeight());
        grpTurnPane.setY(stage.getHeight() - grpTurnPane.getHeight());

        btnTurnDone.setDisabled(!isCurrentPlayer());
        btnTurnDone.setColor((isCurrentPlayer() ? new Color(1, 1, 1, 1) : new Color(0.5f, 0.5f, 0.5f, 1)));
        btnTurnDone.setText((isCurrentPlayer() ? "End Turn" : "Please Wait"));
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
        gController.getMap().draw(mapSpriteBatch, -posCamera.x + stage.getWidth() * 0.5f, -posCamera.y + stage.getHeight() * 0.5f, 1, gController.getUnits());
        mapSpriteBatch.end();
        stage.draw();

        if (buildMode && !isCurrentPlayer()) unsetBuildMode();
        if (gController.getSelectedObject() != null && gController.getSelectedObject().isRemove()) gController.clearSelection();

        // clear highlighting if necessary
        gController.getMap().clearHighlightAndDim();    // 0fps
        lastSelected = gController.getSelectedObject();

        selectionPanel.setSelected(null);
        if (lastSelected != null && lastSelected instanceof Unit) {
            gController.getMap().highlightArea(lastSelected.getBoardPosition(), ((Unit)lastSelected).getMovesLeft(), true);
            selectionPanel.setSelected((Unit)lastSelected);
            mapDirty = true;
        }

        if (mapDirty) {
            gController.getMap().update(game.getPlayer()); // -22fps
            Gdx.app.log("mapDirty", "yes?");
            mapDirty = false;
        }

		// DRAW BOXES
		drawUnitBoxes();

		// move the camera around
		doCameraMovement(delta);
		
		// get input
		doProcessInput(delta);
		
		// update level position
        if (VOBGame.DEBUG_LOCK_SCREEN)
            posCamera.set(grpLevel.getWidth() * 0.5f, grpLevel.getHeight() * 0.5f - stage.getHeight() * 0.125f);

            grpLevel.setPosition(-1 * posCamera.x + stage.getWidth() * 0.5f, -1 * posCamera.y + stage.getHeight() * 0.5f);

		updateTurnPane();
        buildPanel.update(delta);
        selectionPanel.update(delta);
        scorePanel.update(delta, gController.getCurrentPlayer());
        playerScore.update(); // -7fps
        playerScore.setScale(stage.getWidth() * 0.25f / playerScore.getWidth());
        surrenderPanel.update(delta); // 0

//		showCommandPreviews();

        if (buildImage != null) buildImage.remove();
        if (buildMode) {
            grpLevel.addActor(buildImage);

            Vector2 loc = screenToMapCoords(Gdx.input.getX(), stage.getHeight() - Gdx.input.getY());
            loc = gController.getMap().mapToBoardCoords(loc.x, loc.y);
            if (loc.x < 0) loc.x = 0; else if (loc.x >= gController.getMap().getWidth()) loc.x = gController.getMap().getWidth() - 1;
            if (loc.y < 0) loc.y = 0; else if (loc.y >= gController.getMap().getHeight()) loc.y = gController.getMap().getHeight() - 1;
            loc = gController.getMap().boardToMapCoords(loc.x, loc.y);

            buildImage.setPosition(loc.x, loc.y);

            if (!isCurrentPlayer()) unsetBuildMode();
        }

	}

    public void renderGroup(Group group, SpriteBatch batch, float x, float y) {
        for (Actor child : group.getChildren()) {
            if (child instanceof Group) {
                renderGroup((Group)child, batch, x + child.getX(), y + child.getY());
            } else if (child instanceof GameObject) {
                ((GameObject)child).draw(batch, x, y, 1);

            } else if (child instanceof Image) {
//                Gdx.app.log("renderGroup", "image");

                if (((Image)child).getDrawable().getClass() == TextureRegionDrawable.class) {
//                    Gdx.app.log("renderGroup", "TextureRegionDrawable");
                    TextureRegion region = ((TextureRegionDrawable)((Image)child).getDrawable()).getRegion();
                    batch.draw(region, x + child.getX() + ((Image) child).getImageX(), y + child.getY() + ((Image) child).getImageY());
                } else {
                    ((Image)child).getDrawable().draw(batch, x + child.getX(), y + child.getY(), ((Image) child).getImageWidth(), ((Image) child).getImageHeight());
                }
            }
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
        Image img = new Image(TextureManager.getSpriteFromAtlas("assets", icon));
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
}
