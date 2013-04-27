package com.ahsgaming.valleyofbones;

import com.ahsgaming.valleyofbones.network.Command;
import com.ahsgaming.valleyofbones.network.GameClient;
import com.ahsgaming.valleyofbones.network.GameServer;
import com.ahsgaming.valleyofbones.screens.GameJoinScreen;
import com.ahsgaming.valleyofbones.screens.GameLoadingScreen;
import com.ahsgaming.valleyofbones.screens.GameOverScreen;
import com.ahsgaming.valleyofbones.screens.GameSetupScreen;
import com.ahsgaming.valleyofbones.screens.GameSetupScreen.GameSetupConfig;
import com.ahsgaming.valleyofbones.screens.LevelScreen;
import com.ahsgaming.valleyofbones.screens.MainMenuScreen;
import com.ahsgaming.valleyofbones.screens.OptionsScreen;
import com.ahsgaming.valleyofbones.screens.ServerScreen;
import com.ahsgaming.valleyofbones.screens.SplashScreen;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.gdx.graphics.FPSLogger;
import com.badlogic.gdx.utils.Array;

public class VOBGame extends Game {
	public static final boolean DEBUG = true;

	public static final String LOG = "SpaceTactics";
	
	FPSLogger fpsLogger = new FPSLogger();
	
	private GameController gController = null;
	
	private float keyScrollSpeed = 500;
	private float mouseScrollSpeed = 500;
	private float mouseScrollSize = 15;
	
	// SERVER
	GameServer localServer;
	
	// CLIENT
	GameClient localClient;
	Player player;
	
	boolean started = false;
	
	boolean isServer = false;

	boolean loadGame = false;
	
	GameResult gameResult = null; // client sets this when a game ends
	
	/*
	 * Constructors
	 */
	
	public VOBGame(boolean isServer) {
		this.isServer = isServer;
	}
	
	/*
	 * Methods
	 */
	
	public void createGame(GameSetupConfig cfg) {
		if (isServer) {
			localServer = new GameServer(this, cfg);
		} else {
			if (cfg.isMulti) { 
				localClient = new GameClient(this, cfg);
			} else {
				// TODO implement local SP
			}
		}
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
		if (!isServer) {
            if (localClient != null) {
                localClient.startGame();
                gController = localClient.getGameController();
            }
            setScreen(getLevelScreen());
        } else {
            if (localServer != null) {
                localServer.startGame();
                gController = localServer.getGameController();
            }
        }
		

	}
	
	public void sendStartGame() {
		if (localClient != null) localClient.sendStartGame();
	}

	public void quitGame() {
		Gdx.app.exit();
	}
	
	public void sendCommand(Command cmd) {
		// TODO fix this
		gController.queueCommand(cmd);
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
		
		if (isServer) {
			setScreen(getServerScreen());
			
			GameSetupConfig cfg = new GameSetupConfig();
			cfg.isMulti = true;
			createGame(cfg);
			
		} else {
			setScreen((DEBUG ? getMainMenuScreen() : getSplashScreen()));
			
		}
		
	}

	@Override
	public void dispose() {
		super.dispose();
		
		closeGame();
	}

	@Override
	public void render() {	
		super.render();
		if (DEBUG && !isServer) fpsLogger.log();

		if (loadGame) {
			startGame();
			loadGame = false;
		}
		
		if (!isServer) {
			if (localClient != null && localClient.isConnected() && loadGame && !started) {
				started = true;
				startGame();
			}
			
			if (gController != null) {
				if (gameResult != null) {
					this.setScreen(this.getGameOverScreen(gameResult));
					gameResult = null;
				}
			}
		} else {
            if (localServer != null && loadGame && !started) {
                started = true;
                startGame();
            }
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
		return new LevelScreen(this, localClient.getGameController());
	}
	
	public GameOverScreen getGameOverScreen(GameResult result) {
		return new GameOverScreen(this, result);
	}
	
	public Player getPlayer() {
		if (localClient != null) {
			player = localClient.getPlayer();
		}
		return player;
	}
	
	public Array<Player> getPlayers() {
		if (localClient != null) {
			Array<Player> ret = new Array<Player>();
			ret.addAll(localClient.getPlayers());
			return ret;
		}
		return new Array<Player>();
	}
	
	public void setLoadGame() {
		// TODO Auto-generated method stub
		loadGame = true;
	}
	
	public boolean isConnected() {
		if (localClient == null) return false;
		
		return localClient.isConnected();
	}
	
	public boolean isConnecting() {
		if (localClient == null) return false;
		
		return localClient.isConnecting();
	}
	
	public GameClient getClient() {
		return localClient;
	}
	
	
	/**
	 * Program entry point
	 * @param args
	 */
	
	public static void main(String[] args) {
		
		LwjglApplicationConfiguration cfg = new LwjglApplicationConfiguration();
		cfg.title = "Valley of Bones";
		cfg.useGL20 = true;
		cfg.width = 1440;
		cfg.height = 900;
		cfg.fullscreen = false;
		cfg.resizable = false;
		
		new LwjglApplication(new VOBGame(false), cfg);
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
