package com.ahsgaming.valleyofbones.units;

import com.ahsgaming.valleyofbones.GameController;
import com.ahsgaming.valleyofbones.Player;
import com.ahsgaming.valleyofbones.units.behavior.*;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

/**
 * valley-of-bones
 * (c) 2013 Jami Couch
 * User: jami
 * Date: 3/15/14
 * Time: 8:54 AM
 */
public class AbstractUnit implements EventEmitter {
    public String LOG = "Unit";

    Prototypes.JsonProto proto;
    UnitData data;
    UnitView view;
    Player owner;
    Player originalOwner;
    int id;

    MoveBehavior moveBehavior;
    AttackBehavior attackBehavior;
    DefendBehavior defendBehavior;
    AbilityBehavior abilityBehavior;
    UnitTurnListener turnBehavior;

    Array<EventListener> listeners;
    TurnEmitter turnEmitter;

    public AbstractUnit() {
        listeners = new Array<EventListener>();
    }

    public boolean attack(UnitManager unitManager, AbstractUnit target) {
        return attackBehavior.attack(unitManager, target);
    }

    public void move(GameController gameController, Vector2 boardPosition) {
        moveBehavior.move(gameController, boardPosition);
    }

    public void defend(float damage) {
        defendBehavior.defend(damage);
    }

    public void activateAbility() {
        abilityBehavior.activateAbility();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Prototypes.JsonProto getProto() {
        return proto;
    }

    public UnitData getData() {
        return data;
    }

    public UnitView getView() {
        return view;
    }

    public Player getOwner() {
        return owner;
    }

    public void setOwner(Player owner) {
        this.owner = owner;
    }

    public void setOriginalOwner(Player originalOwner) {
        this.originalOwner = originalOwner;
    }

    public Player getOriginalOwner() {
        return originalOwner;
    }

    @Override
    public void register(EventListener listener) {
        if (listeners.indexOf(listener, true) == -1) {
            listeners.add(listener);
        }
    }

    @Override
    public void remove(EventListener listener) {
        listeners.removeValue(listener, true);
    }

    @Override
    public void emit() {
        for (EventListener listener: listeners) {
            listener.update();
        }
    }

    public void startTurn(int turn) {
        turnBehavior.startTurn(turn);
    }

    public void endTurn(int turn) {
        turnBehavior.endTurn(turn);
    }

    public void update(UnitManager unitManager, float delta) {
       turnBehavior.update(unitManager, delta);
    }
}
