package com.ahsgaming.spacetactics;

import com.ahsgaming.spacetactics.screens.GameLoadingScreen;
import com.ahsgaming.spacetactics.screens.GameOverScreen;
import com.ahsgaming.spacetactics.screens.GameSetupScreen;
import com.ahsgaming.spacetactics.screens.LevelScreen;
import com.ahsgaming.spacetactics.screens.MainMenuScreen;
import com.ahsgaming.spacetactics.screens.OptionsScreen;
import com.ahsgaming.spacetactics.screens.SplashScreen;
import com.ahsgaming.spacetactics.units.Prototypes;
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
	
	public void startGame() {
		//TODO this should accept input and then pass it to the GameController
		gController = new GameController("");
		setScreen(getLevelScreen());
	}

	public void quitGame() {
		Gdx.app.exit();
	}
	
	/**
	 * Implemented methods
	 */
	
	@Override
	public void create() {		
		Prototypes.loadUnits("units.json");
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
		cfg.width = 1000;
		cfg.height = 550;
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
