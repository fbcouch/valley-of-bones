package com.ahsgaming.valleyofbones.ai;

import com.ahsgaming.valleyofbones.units.AbstractUnit;
import com.badlogic.gdx.math.Vector2;

/**
 * valley-of-bones
 * (c) 2013 Jami Couch
 * User: jami
 * Date: 1/12/14
 * Time: 9:06 AM
 */
public class Directive {
    AbstractUnit target;
    Vector2 location;
    DirectiveType type;

    public Directive(DirectiveType type, AbstractUnit target) {
        this.type = type;
        this.target = target;
    }

    public Directive(DirectiveType type, Vector2 location) {
        this.type = type;
        this.location = new Vector2(location);
    }

    public static enum DirectiveType {
        FLEE, HUNT, DEFEND, CAPTURE
    }
}

