package com.ahsgaming.valleyofbones.screens;

import com.ahsgaming.valleyofbones.GameObject;
import com.ahsgaming.valleyofbones.network.Build;
import com.ahsgaming.valleyofbones.units.Unit;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ActorGestureListener;

/**
 * valley-of-bones
 * (c) 2013 Jami Couch
 * User: jami
 * Date: 12/3/13
 * Time: 7:28 AM
 */
public class LevelScreenInputListener extends ActorGestureListener {
    public static String LOG = "LevelScreenInputListener";

    LevelScreen levelScreen;

    boolean mapInterrupt;

    public LevelScreenInputListener(LevelScreen levelScreen) {
        this.levelScreen = levelScreen;
    }

    @Override
    public void touchDown(InputEvent event, float x, float y, int pointer, int button) {
        super.touchDown(event, x, y, pointer, button);    //To change body of overridden methods use File | Settings | File Templates.
        if (x < levelScreen.stage.getWidth() * 0.25f) mapInterrupt = true;

    }

    @Override
    public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
        super.touchUp(event, x, y, pointer, button);    //To change body of overridden methods use File | Settings | File Templates.
        mapInterrupt = false;
    }

    @Override
    public void tap(InputEvent event, float x, float y, int count, int button) {
        super.tap(event, x, y, count, button);    //To change body of overridden methods use File | Settings | File Templates.
        Vector2 mapPos = levelScreen.screenToMapCoords(x , y);
        Vector2 boardPos = levelScreen.gController.getMap().mapToBoardCoords(mapPos.x, mapPos.y);
        if (mapInterrupt) return;


        Gdx.app.log(LOG, String.format("Select obj at %s", boardPos.toString()));
        levelScreen.gController.selectObjAtBoardPos(boardPos, levelScreen.game.getPlayer());
        if (levelScreen.gController.getSelectedObject() != null && levelScreen.gController.getSelectedObject() instanceof Unit) {
            Unit u = (Unit)levelScreen.gController.getSelectedObject();
            Gdx.app.log(LOG, String.format("Selected: %s (%d/%d)", u.getProtoId(), u.getCurHP(), u.getMaxHP()));
            levelScreen.unsetBuildMode();
        }

    }

    @Override
    public boolean longPress(Actor actor, float x, float y) {
        Gdx.input.vibrate(200);
        if (mapInterrupt) return super.longPress(actor, x, y);

        Vector2 mapPos = levelScreen.screenToMapCoords(x , y);
        Vector2 boardPos = levelScreen.gController.getMap().mapToBoardCoords(mapPos.x, mapPos.y);

        if (levelScreen.isBuildMode()) {
            Gdx.app.log(LOG, String.format("Build at %s", boardPos.toString()));
            levelScreen.build(boardPos);
        }

        if (levelScreen.gController.getSelectedObject() != null &&
            levelScreen.isCurrentPlayer() &&
            levelScreen.gController.getSelectedObject().getOwner().getPlayerId() == levelScreen.game.getPlayer().getPlayerId())
        {
            GameObject target = levelScreen.gController.getObjAtBoardPos(boardPos);
            Unit unit = (Unit)levelScreen.gController.getSelectedObject();

            if (target != null && target instanceof Unit && (!((Unit)target).getInvisible() || unit.isDetector())) {
                if (target.getOwner().getPlayerId() != levelScreen.game.getPlayer().getPlayerId()) {
                    levelScreen.attack(unit.getObjId(), target.getObjId());
                }

            } else {
                levelScreen.move(unit.getObjId(), boardPos);
            }

        }

        return true;
    }

    @Override
    public void pan(InputEvent event, float x, float y, float deltaX, float deltaY) {
        super.pan(event, x, y, deltaX, deltaY);    //To change body of overridden methods use File | Settings | File Templates.
        if (!mapInterrupt) {
            levelScreen.posCamera.sub(new Vector2(deltaX, deltaY));
            levelScreen.clampCamera();
        }
    }
}
