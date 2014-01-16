package com.ahsgaming.valleyofbones.units;

import com.ahsgaming.valleyofbones.Player;
import com.badlogic.gdx.utils.Array;

import java.util.HashMap;

/**
 * valley-of-bones
 * (c) 2013 Jami Couch
 * User: jami
 * Date: 1/16/14
 * Time: 8:10 AM
 */
public class UnitData {
    // Unit Stats
    String ability = "";
    int armor = 0;
    int attackDamage = 0;
    int attackRange = 0;
    float attackSpeed = 0;
    HashMap<String, Float> bonus = new HashMap<String, Float>();
    int buildTime = 0;
    boolean capturable = false;
    int cost = 0;
    int curHP = 0;
    int food = 0;
    String image = "";
    int maxHP = 0;
    float moveSpeed = 0;
    String protoId = "";
    Array<String> requires = new Array<String>();
    int sightRange = 0;
    float splashDamage = 0;
    String subtype = "";
    String type = "";
    int upkeep = 0;

    // Game Status
    boolean building = true;
    int buildTimeLeft = 0;

    int lastStealthToggleTurn = 0;
    boolean stealthActive = false;

    float movesLeft = 0, attacksLeft = 0;

    Player uncontested = null;
    int capUnitCount = 0;

    // getters & setters

    public String getAbility() {
        return ability;
    }

    public void setAbility(String ability) {
        this.ability = ability;
    }

    public int getArmor() {
        return armor;
    }

    public void setArmor(int armor) {
        this.armor = armor;
    }

    public int getAttackDamage() {
        return attackDamage;
    }

    public void setAttackDamage(int attackDamage) {
        this.attackDamage = attackDamage;
    }

    public int getAttackRange() {
        return attackRange;
    }

    public void setAttackRange(int attackRange) {
        this.attackRange = attackRange;
    }

    public float getAttackSpeed() {
        return attackSpeed;
    }

    public void setAttackSpeed(float attackSpeed) {
        this.attackSpeed = attackSpeed;
    }

    public float getBonus(String type) {
        return (bonus.containsKey(type) ? bonus.get(type) : 1);
    }

    public void setBonus(String type, float bonus) {
        this.bonus.put(type, bonus);
    }

    public int getBuildTime() {
        return buildTime;
    }

    public void setBuildTime(int buildTime) {
        this.buildTime = buildTime;
    }

    public boolean isCapturable() {
        return capturable;
    }

    public void setCapturable(boolean capturable) {
        this.capturable = capturable;
    }

    public int getCost() {
        return cost;
    }

    public void setCost(int cost) {
        this.cost = cost;
    }

    public int getCurHP() {
        return curHP;
    }

    public void setCurHP(int curHP) {
        this.curHP = curHP;
    }

    public int getFood() {
        return food;
    }

    public void setFood(int food) {
        this.food = food;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public int getMaxHP() {
        return maxHP;
    }

    public void setMaxHP(int maxHP) {
        this.maxHP = maxHP;
    }

    public float getMoveSpeed() {
        return moveSpeed;
    }

    public void setMoveSpeed(float moveSpeed) {
        this.moveSpeed = moveSpeed;
    }

    public String getProtoId() {
        return protoId;
    }

    public void setProtoId(String protoId) {
        this.protoId = protoId;
    }

    public Array<String> getRequires() {
        return requires;
    }

    public void setRequires(Array<String> requires) {
        this.requires = requires;
    }

    public int getSightRange() {
        return sightRange;
    }

    public void setSightRange(int sightRange) {
        this.sightRange = sightRange;
    }

    public float getSplashDamage() {
        return splashDamage;
    }

    public void setSplashDamage(float splashDamage) {
        this.splashDamage = splashDamage;
    }

    public String getSubtype() {
        return subtype;
    }

    public void setSubtype(String subtype) {
        this.subtype = subtype;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getUpkeep() {
        return upkeep;
    }

    public void setUpkeep(int upkeep) {
        this.upkeep = upkeep;
    }

    public boolean isBuilding() {
        return building;
    }

    public void setBuilding(boolean building) {
        this.building = building;
    }

    public int getBuildTimeLeft() {
        return buildTimeLeft;
    }

    public void setBuildTimeLeft(int buildTimeLeft) {
        this.buildTimeLeft = buildTimeLeft;
    }

    public int getLastStealthToggleTurn() {
        return lastStealthToggleTurn;
    }

    public void setLastStealthToggleTurn(int lastStealthToggleTurn) {
        this.lastStealthToggleTurn = lastStealthToggleTurn;
    }

    public boolean isStealthActive() {
        return stealthActive;
    }

    public void setStealthActive(boolean stealthActive) {
        this.stealthActive = stealthActive;
    }

    public float getMovesLeft() {
        return movesLeft;
    }

    public void setMovesLeft(float movesLeft) {
        this.movesLeft = movesLeft;
    }

    public float getAttacksLeft() {
        return attacksLeft;
    }

    public void setAttacksLeft(float attacksLeft) {
        this.attacksLeft = attacksLeft;
    }

    public Player getUncontested() {
        return uncontested;
    }

    public void setUncontested(Player uncontested) {
        this.uncontested = uncontested;
    }

    public int getCapUnitCount() {
        return capUnitCount;
    }

    public void setCapUnitCount(int capUnitCount) {
        this.capUnitCount = capUnitCount;
    }
}
