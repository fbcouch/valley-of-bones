package com.ahsgaming.valleyofbones.network;

/**
 * Created with IntelliJ IDEA.
 * User: jami
 * Date: 5/23/13
 * Time: 12:29 AM
 * To change this template use File | Settings | File Templates.
 */
public class ActivateAbility extends Command {
    public int unit;

    @Override
    public boolean equals(Object o) {
        return o instanceof ActivateAbility && super.equals(o) && ((ActivateAbility)o).unit == unit;
    }
}
