package com.ahsgaming.valleyofbones;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

public class ServerMain {

	public ServerMain() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		LwjglApplicationConfiguration cfg = new LwjglApplicationConfiguration();
		cfg.title = "Valley of Bones SERVER";
		cfg.useGL20 = false;
		cfg.width = 400;
		cfg.height = 300;
		cfg.fullscreen = false;
		cfg.resizable = false;
		
		new LwjglApplication(new VOBGame(true), cfg);
	}

}
