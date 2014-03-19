package com.ahsgaming.valleyofbones.network;

import com.ahsgaming.valleyofbones.GameController;

/**
 * valley-of-bones
 * (c) 2013 Jami Couch
 * User: jami
 * Date: 3/19/14
 * Time: 6:29 PM
 */
public class ActionResetCommand extends Command {

    @Override
    public void execute(GameController gameController) {
        gameController.actionReset();
    }
}
