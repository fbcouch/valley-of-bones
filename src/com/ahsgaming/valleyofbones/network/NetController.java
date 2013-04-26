package com.ahsgaming.valleyofbones.network;

import com.ahsgaming.valleyofbones.GameController;
import com.ahsgaming.valleyofbones.Player;
import com.badlogic.gdx.utils.Array;

public interface NetController {
	public void setGameController(GameController controller);
	public GameController getGameController();
	
	public void startGame();
	public void sendStartGame();
	
	public void endGame();
	
	public void stop();
	
	public boolean update();
	
	public void addAIPlayer(int team);
	public void removePlayer(int playerId);
	public Array<Player> getPlayers();
	
	public void sendCommand(Command cmd);
	
	public boolean isConnected();
	public boolean isConnecting();
}