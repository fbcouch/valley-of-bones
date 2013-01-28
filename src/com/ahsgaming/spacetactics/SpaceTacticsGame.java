package com.ahsgaming.spacetactics;

import java.util.ArrayList;

import com.ahsgaming.spacetactics.network.Command;
import com.ahsgaming.spacetactics.network.GameClient;
import com.ahsgaming.spacetactics.network.GameServer;
import com.ahsgaming.spacetactics.screens.GameJoinScreen;
import com.ahsgaming.spacetactics.screens.GameLoadingScreen;
import com.ahsgaming.spacetactics.screens.GameOverScreen;
import com.ahsgaming.spacetactics.screens.GameSetupScreen;
import com.ahsgaming.spacetactics.screens.GameSetupScreen.GameSetupConfig;
import com.ahsgaming.spacetactics.screens.LevelScreen;
import com.ahsgaming.spacetactics.screens.MainMenuScreen;
import com.ahsgaming.spacetactics.screens.OptionsScreen;
import com.ahsgaming.spacetactics.screens.ServerScreen;
import com.ahsgaming.spacetactics.screens.SplashScreen;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.gdx.graphics.FPSLogger;

public class SpaceTacticsGame extends Game {
	public static final boolean DEBUG = true;

	public static final String LOG = "SpaceTactics";
	
	FPSLogger fpsLogger = new FPSLogger();
	
	private GameController gController = null;
	
	private float keyScrollSpeed = 500;
	private float mouseScrollSpeed = 500;
	private float mouseScrollSize = 15;
	
	// SERVER
	GameServer localServer;
	Thread serverThread;
	
	// CLIENT
	GameClient localClient;
	Thread clientThread;
	
	boolean started = false;
	
	boolean isServerOnly = false;

	boolean loadGame = false;
	
	GameResult gameResult = null; // client sets this when a game ends
	
	/*
	 * Constructors
	 */
	
	public SpaceTacticsGame(boolean isServer) {
		isServerOnly = isServer;
	}
	
	/*
	 * Methods
	 */
	
	public void createGame(GameSetupConfig cfg) {
		//TODO this should accept input and then pass it to the GameController
		
		// TODO for now, assuming single player, so we need to start and manage the server
		
		if (!cfg.isMulti || cfg.isHost) { 
			localServer = new GameServer(cfg);
			serverThread = new Thread() {
				public void run() {
					boolean cont = true;
					while(cont) {
						cont = localServer.update();
						try {
							Thread.sleep(0);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					localServer = null;
				}
			};
			serverThread.start();
		}
		
		localClient = new GameClient(this, cfg);
		
		clientThread = new Thread() {
			public void run() {
				boolean cont = true;
				while(cont) {
					cont = localClient.update();
					try {
						Thread.sleep(0);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				localClient = null;
			}
		};
		clientThread.start();
		
		
		
		//gController = new GameController("", new ArrayList<Player>());
		//gController.LOG = gController.LOG + "#Client";
	}
	
	public void closeGame() {
		if (localServer != null) {
			localServer.stop();
		}
		if (localClient != null) {
			localClient.stop();
		}
	}
	
	public void startGame() {
		//if (localServer != null) localServer.startGame();
		if (localClient != null) localClient.startGame();
		
		setScreen(getLevelScreen());
	}
	
	public void sendStartGame() {
		if (localServer != null) localServer.startGame();
	}

	public void quitGame() {
		Gdx.app.exit();
	}
	
	public void sendCommand(Command cmd) {
		localClient.sendCommand(cmd);
	}
	
	public void addAIPlayer(int team) {
		localClient.addAIPlayer(team);
	}
	
	public void removePlayer(int playerId) {
		localClient.removePlayer(playerId);
	}
	
	/**
	 * Implemented methods
	 */
	
	@Override
	public void create() {		
		
		if (isServerOnly) {
			setScreen(getServerScreen());
		} else {
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
		
		/*if (localClient != null && localClient.getPlayers().size() > 0 && !started) {
			started = true;
			startGame();
		}*/
		
		if (loadGame) {
			startGame();
			loadGame = false;
		}
		
		if (gameResult != null) {
			this.setScreen(this.getGameOverScreen(gameResult));
			gameResult = null;
		}
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
		
		GameSetupConfig cfg = new GameSetupConfig();
		cfg.isHost = true;
		cfg.isMulti = false;
		
		return new GameSetupScreen(this, cfg);
	}
	
	public GameSetupScreen getGameSetupScreenMP(boolean isHost) {
		GameSetupConfig cfg = new GameSetupConfig();
		cfg.isMulti = true;
		cfg.isHost = isHost;
		return new GameSetupScreen(this, cfg);
	}
	
	public GameSetupScreen getGameSetupScreenMP(GameSetupConfig cfg) {
		return new GameSetupScreen(this, cfg);
	}
	
	public GameJoinScreen getGameJoinScreen() {
		return new GameJoinScreen(this);
	}
	
	public GameLoadingScreen getGameLoadingScreen() {
		return new GameLoadingScreen(this);
	}
	
	public ServerScreen getServerScreen() {
		return new ServerScreen(this);
	}
	
	public LevelScreen getLevelScreen() {
		return new LevelScreen(this, localClient.getController());
	}
	
	public GameOverScreen getGameOverScreen(GameResult result) {
		return new GameOverScreen(this, result);
	}
	
	public Player getPlayer() {
		if (localClient != null) {
			return localClient.getPlayer();
		}
		return null;
	}
	
	public ArrayList<Player> getPlayers() {
		if (localClient != null) {
			return (ArrayList<Player>) localClient.getPlayers().clone();
		}
		return new ArrayList<Player>();
	}
	
	public void setLoadGame() {
		// TODO Auto-generated method stub
		loadGame  = true;
	}
	
	public boolean isConnected() {
		if (localClient == null) return false;
		
		return localClient.isConnected();
	}
	
	public boolean isConnecting() {
		if (localClient == null) return false;
		
		return localClient.isConnecting();
	}
	
	
	/**
	 * Program entry point
	 * @param args
	 */
	
	public static void main(String[] args) {
		boolean isServer = false;
		
		for (int i=0;i<args.length;i++) {
			if (args[i].equals("server")) isServer = true;
		}
		
		LwjglApplicationConfiguration cfg = new LwjglApplicationConfiguration();
		cfg.title = "Space Tactics";
		cfg.useGL20 = true;
		cfg.width = 1440;
		cfg.height = 900;
		cfg.fullscreen = false;
		cfg.resizable = false;
		
		new LwjglApplication(new SpaceTacticsGame(isServer), cfg);
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

	public void setGameResult(GameResult gameResult) {
		this.gameResult = gameResult;
	}

	

	
}
