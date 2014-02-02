package com.ahsgaming.valleyofbones.screens;

import com.ahsgaming.valleyofbones.network.KryoCommon;

/**
* valley-of-bones
* (c) 2013 Jami Couch
* Created on 1/24/14 by jami
* ahsgaming.com
*/
public class GameSetupConfig {
    public String mapName = "";
    public int ruleSet = 0;
    public int spawnType = 0;
    public int firstMove = 0;
    public boolean isMulti = false;
    public boolean isHost = true;
    public boolean isSpectator = false;
    public boolean isPublic = false;
    public String hostName = "localhost";
    public int hostPort = KryoCommon.tcpPort;
    public String playerName = "New Player";
    public int maxPauses = 3;

    public enum FirstMoveType {
        MOVE_RANDOM, MOVE_P1, MOVE_P2
    }
}
