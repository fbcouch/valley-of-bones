package com.ahsgaming.valleyofbones;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

/**
 * valley-of-bones
 * (c) 2013 Jami Couch
 * Created on 11/29/13 by jami
 * ahsgaming.com
 */
public class Main {
    /**
     * Program entry point
     * @param args
     */

    public static void main(String[] args) {

        LwjglApplicationConfiguration cfg = new LwjglApplicationConfiguration();
        cfg.title = "Valley of Bones | ahsgaming.com | (c) 2013 Jami Couch";
        cfg.useGL20 = false;
        cfg.width = 1280;
        cfg.height = 768;
        cfg.fullscreen = false;
        cfg.resizable = true;

        new LwjglApplication(new VOBGame(false), cfg);
    }

}
