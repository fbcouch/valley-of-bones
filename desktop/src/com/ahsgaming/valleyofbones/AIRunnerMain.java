package com.ahsgaming.valleyofbones;

import com.badlogic.gdx.Files;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

public class AIRunnerMain {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		LwjglApplicationConfiguration cfg = new LwjglApplicationConfiguration();
		cfg.title = "Valley of Bones AI Runner";
		cfg.useGL20 = false;
		cfg.width = 1280;
		cfg.height = 768;
		cfg.fullscreen = false;
		cfg.resizable = false;

        cfg.addIcon("icon_128.png", Files.FileType.Internal);
        cfg.addIcon("icon_64.png", Files.FileType.Internal);
        cfg.addIcon("icon_32.png", Files.FileType.Internal);
        cfg.addIcon("icon_16.png", Files.FileType.Internal);
		
		new LwjglApplication(new VOBAIRunner(), cfg);
	}

}
