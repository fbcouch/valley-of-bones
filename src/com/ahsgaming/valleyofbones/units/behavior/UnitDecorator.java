package com.ahsgaming.valleyofbones.units.behavior;

import com.ahsgaming.valleyofbones.GameController;
import com.ahsgaming.valleyofbones.Player;
import com.ahsgaming.valleyofbones.units.*;
import com.badlogic.gdx.math.Vector2;

/**
 * valley-of-bones
 * (c) 2013 Jami Couch
 * User: jami
 * Date: 3/15/14
 * Time: 4:23 PM
 */
public abstract class UnitDecorator extends AbstractUnit {
    AbstractUnit unit;

    public UnitDecorator(AbstractUnit unit) {
        this.unit = unit;
    }

    @Override
    public boolean attack(AbstractUnit target) {
        return unit.attack(target);    //To change body of overridden methods use File | Settings | File Templates.
    }

    @Override
    public void move(GameController gameController, Vector2 boardPosition) {
        unit.move(gameController, boardPosition);    //To change body of overridden methods use File | Settings | File Templates.
    }

    @Override
    public void defend(float damage) {
        unit.defend(damage);    //To change body of overridden methods use File | Settings | File Templates.
    }

    @Override
    public void activateAbility() {
        unit.activateAbility();    //To change body of overridden methods use File | Settings | File Templates.
    }

    @Override
    public void startTurn() {
        unit.startTurn();    //To change body of overridden methods use File | Settings | File Templates.
    }

    @Override
    public void endTurn() {
        unit.endTurn();    //To change body of overridden methods use File | Settings | File Templates.
    }

    @Override
    public void update() {
        unit.update();    //To change body of overridden methods use File | Settings | File Templates.
    }

    @Override
    public int getId() {
        return unit.getId();    //To change body of overridden methods use File | Settings | File Templates.
    }

    @Override
    public void setId(int id) {
        unit.setId(id);    //To change body of overridden methods use File | Settings | File Templates.
    }

    @Override
    public Prototypes.JsonProto getProto() {
        return unit.getProto();    //To change body of overridden methods use File | Settings | File Templates.
    }

    @Override
    public UnitData getData() {
        return unit.getData();    //To change body of overridden methods use File | Settings | File Templates.
    }

    @Override
    public UnitView getView() {
        return unit.getView();    //To change body of overridden methods use File | Settings | File Templates.
    }

    @Override
    public Player getOwner() {
        return unit.getOwner();    //To change body of overridden methods use File | Settings | File Templates.
    }

    @Override
    public Player getOriginalOwner() {
        return unit.getOriginalOwner();    //To change body of overridden methods use File | Settings | File Templates.
    }

    @Override
    public void register(EventListener listener) {
        unit.register(listener);    //To change body of overridden methods use File | Settings | File Templates.
    }

    @Override
    public void remove(EventListener listener) {
        unit.remove(listener);    //To change body of overridden methods use File | Settings | File Templates.
    }

    @Override
    public void emit() {
        unit.emit();    //To change body of overridden methods use File | Settings | File Templates.
    }
}
