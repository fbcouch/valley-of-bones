package com.ahsgaming.valleyofbones;

import com.badlogic.gdx.Files;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

public class ServerMain {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		LwjglApplicationConfiguration cfg = new LwjglApplicationConfiguration();
		cfg.title = "Valley of Bones SERVER";
		cfg.width = 1280;
		cfg.height = 768;
		cfg.fullscreen = false;
		cfg.resizable = false;

        cfg.addIcon("assets/icon_128.png", Files.FileType.Internal);
        cfg.addIcon("assets/icon_64.png", Files.FileType.Internal);
        cfg.addIcon("assets/icon_32.png", Files.FileType.Internal);
        cfg.addIcon("assets/icon_16.png", Files.FileType.Internal);
		
		new LwjglApplication(new VOBServer(), cfg);
	}

}
