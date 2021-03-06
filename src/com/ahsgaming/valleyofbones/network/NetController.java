package com.ahsgaming.valleyofbones.network;

import com.ahsgaming.valleyofbones.GameController;
import com.ahsgaming.valleyofbones.Player;
import com.badlogic.gdx.utils.Array;

import java.util.HashMap;

public interface NetController {
	public void setGameController(GameController controller);
	public GameController getGameController();
	
	public void startGame();
	public void sendStartGame();
	
	public void endGame();
	
	public void stop();
	
	public boolean update(float delta);
	
	public void addAIPlayer();
	public void removePlayer(int playerId);

	public Array<Player> getPlayers();
    public HashMap<Integer, String> getSpectators();
    public Player getPlayer();
	
	public void sendCommand(Command cmd);
    public void sendAICommand(Command cmd);
	
	public boolean isConnected();
	public boolean isConnecting();

    public void sendChat(String message);
    public Array<String> getChatLog();
}
