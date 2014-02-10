package com.ahsgaming.valleyofbones.screens;

import com.ahsgaming.valleyofbones.network.KryoCommon;
import com.badlogic.gdx.Gdx;

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
    public boolean allowSpectate = true;

    public int baseTimer = 30;
    public int actionBonusTime = 30;
    public int unitBonusTime = 0;

    public enum FirstMoveType {
        MOVE_RANDOM, MOVE_P1, MOVE_P2
    }

    public GameSetupConfig setDetails(KryoCommon.GameDetails details) {
        mapName = details.map;
        ruleSet = details.rules;
        spawnType = details.spawn;
        firstMove = details.firstMove;
        baseTimer = details.baseTimer;
        actionBonusTime = details.actionBonusTime;
        unitBonusTime = details.unitBonusTime;
        allowSpectate = details.allowSpectate;
        return this;
    }

    public KryoCommon.GameDetails getDetails() {
        KryoCommon.GameDetails details = new KryoCommon.GameDetails();
        details.map = mapName;
        details.rules = ruleSet;
        details.spawn = spawnType;
        details.firstMove = firstMove;
        details.baseTimer = baseTimer;
        details.actionBonusTime = actionBonusTime;
        details.unitBonusTime = unitBonusTime;
        details.allowSpectate = allowSpectate;
        return details;
    }

    public enum SpawnTypes {
        SPAWN_NORMAL, SPAWN_INVERTED, SPAWN_RANDOM
    }
}
