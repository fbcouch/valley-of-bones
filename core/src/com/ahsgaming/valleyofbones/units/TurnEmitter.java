package com.ahsgaming.valleyofbones.units;

/**
 * valley-of-bones
 * (c) 2013 Jami Couch
 * User: jami
 * Date: 3/15/14
 * Time: 9:07 AM
 */
public interface TurnEmitter extends EventEmitter {
    public void emitStartTurn();
    public void emitEndTurn();
}
