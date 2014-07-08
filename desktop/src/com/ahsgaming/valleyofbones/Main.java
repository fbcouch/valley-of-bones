package com.ahsgaming.valleyofbones;

import com.badlogic.gdx.Files;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.gdx.graphics.Texture;

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
        cfg.width = 1280;     // TODO load from config?
        cfg.height = 768;
        cfg.fullscreen = false;
        cfg.resizable = true;
//        cfg.addIcon("icon_128.png", Files.FileType.Internal);
//        cfg.addIcon("icon_64.png", Files.FileType.Internal);
//        cfg.addIcon("icon_32.png", Files.FileType.Internal);
//        cfg.addIcon("icon_16.png", Files.FileType.Internal);

        TextureManager.defaultMaxFilter = Texture.TextureFilter.Linear;
        TextureManager.defaultMinFilter = Texture.TextureFilter.Linear;

        VOBGame.SCALE = 4.0f; // TODO load this from config?

        new LwjglApplication(new VOBGame(), cfg);
    }

}
