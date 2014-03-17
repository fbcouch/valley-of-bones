package com.ahsgaming.valleyofbones.units;

import java.util.ArrayList;

/**
 * valley-of-bones
 * (c) 2013 Jami Couch
 * User: jami
 * Date: 3/15/14
 * Time: 8:55 AM
 */
public interface EventEmitter {
    public void register(EventListener listener);
    public void remove(EventListener listener);
    public void emit();
}
