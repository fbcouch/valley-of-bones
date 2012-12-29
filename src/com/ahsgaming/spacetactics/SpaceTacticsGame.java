package com.ahsgaming.spacetactics;

import java.io.IOException;

import com.ahsgaming.spacetactics.network.Command;
import com.ahsgaming.spacetactics.network.KryoCommon;
import com.ahsgaming.spacetactics.network.Unpause;
import com.ahsgaming.spacetactics.screens.GameLoadingScreen;
import com.ahsgaming.spacetactics.screens.GameOverScreen;
import com.ahsgaming.spacetactics.screens.GameSetupScreen;
import com.ahsgaming.spacetactics.screens.LevelScreen;
import com.ahsgaming.spacetactics.screens.MainMenuScreen;
import com.ahsgaming.spacetactics.screens.OptionsScreen;
import com.ahsgaming.spacetactics.screens.SplashScreen;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.gdx.graphics.FPSLogger;
import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;

public class SpaceTacticsGame extends Game {
	public static final boolean DEBUG = true;

	public static final String LOG = "SpaceTactics";
	
	FPSLogger fpsLogger = new FPSLogger();
	
	private GameController gController = null;
	
	private float keyScrollSpeed = 500;
	private float mouseScrollSpeed = 500;
	private float mouseScrollSize = 15;
	
	// SERVER
	private GameServer localServer;
	
	// CLIENT
	private Client client;
	private String host;
	private Connection clientConn;
	private String playerName = "NetPlayer";
	private int playerId = -1;
	
	
	public void startGame() {
		//TODO this should accept input and then pass it to the GameController
		
		// TODO for now, assuming single player, so we need to start and manage the server
		localServer = new GameServer();
		new Thread() {
			public void run() {
				while(localServer.update()) { }
			}
		}.start();
		
		// TODO refactor this
		client = new Client();
		client.start();
		
		KryoCommon.register(client);
		
		client.addListener(new Listener() {
			public void connected (Connection c) {
				
			}
			
			public void received (Connection c, Object obj) {
				if (obj instanceof Command) {
					Command cmd = (Command)obj;
					if (cmd instanceof Unpause) System.out.println("Unpause " + Integer.toString(cmd.tick));
					gController.queueCommand(cmd);
				}
			}
			
			public void disconnected (Connection c) {
				
			}
		});
		
		host = "localhost";
		try {
			client.connect(5000, host, KryoCommon.tcpPort);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			Gdx.app.log(LOG, "Client connection failed: " + e.getMessage());
			e.printStackTrace();
		}
		
		gController = new GameController("");
		gController.LOG = gController.LOG + "#Client";
		setScreen(getLevelScreen());
		
		Command cmd = new Unpause();
		cmd.tick = 0;
		sendCommand(cmd);
	}

	public void quitGame() {
		Gdx.app.exit();
	}
	
	public void sendCommand(Command cmd) {
		client.sendTCP(cmd);
	}
	
	/**
	 * Implemented methods
	 */
	
	@Override
	public void create() {		
		if (DEBUG) {
			startGame();
		}
		else {
			setScreen((DEBUG ? getMainMenuScreen() : getSplashScreen()));
		}
	}

	@Override
	public void dispose() {
		super.dispose();
	}

	@Override
	public void render() {	
		super.render();
		if (DEBUG) fpsLogger.log();
		
	}

	@Override
	public void resize(int width, int height) {
		super.resize(width, height);
	}

	@Override
	public void pause() {
		super.pause();
	}

	@Override
	public void resume() {
		super.resume();
	}
	
	/**
	 * getters and setters
	 */
	
	public SplashScreen getSplashScreen() {
		return new SplashScreen(this);
	}
	
	public MainMenuScreen getMainMenuScreen() {
		return new MainMenuScreen(this);
	}
	
	public OptionsScreen getOptionsScreen() {
		return new OptionsScreen(this);
	}
	
	public GameSetupScreen getGameSetupScreen() {
		return new GameSetupScreen(this);
	}
	
	public GameLoadingScreen getGameLoadingScreen() {
		return new GameLoadingScreen(this);
	}
	
	public LevelScreen getLevelScreen() {
		return new LevelScreen(this, gController);
	}
	
	public GameOverScreen getGameOverScreen() {
		return new GameOverScreen(this); // TODO pass in data here
	}
	
	
	/**
	 * Program entry point
	 * @param args
	 */
	
	public static void main(String[] args) {
		LwjglApplicationConfiguration cfg = new LwjglApplicationConfiguration();
		cfg.title = "Space Tactics";
		cfg.useGL20 = true;
		cfg.width = 1440;
		cfg.height = 900;
		cfg.fullscreen = false;
		cfg.resizable = false;
		
		new LwjglApplication(new SpaceTacticsGame(), cfg);
	}

	/**
	 * @return the keyScrollSpeed
	 */
	public float getKeyScrollSpeed() {
		return keyScrollSpeed;
	}

	/**
	 * @param keyScrollSpeed the keyScrollSpeed to set
	 */
	public void setKeyScrollSpeed(float keyScrollSpeed) {
		this.keyScrollSpeed = keyScrollSpeed;
	}

	/**
	 * @return the mouseScrollSpeed
	 */
	public float getMouseScrollSpeed() {
		return mouseScrollSpeed;
	}

	/**
	 * @param mouseScrollSpeed the mouseScrollSpeed to set
	 */
	public void setMouseScrollSpeed(float mouseScrollSpeed) {
		this.mouseScrollSpeed = mouseScrollSpeed;
	}

	/**
	 * @return the mouseScrollSize
	 */
	public float getMouseScrollSize() {
		return mouseScrollSize;
	}

	/**
	 * @param mouseScrollSize the mouseScrollSize to set
	 */
	public void setMouseScrollSize(float mouseScrollSize) {
		this.mouseScrollSize = mouseScrollSize;
	}
}
