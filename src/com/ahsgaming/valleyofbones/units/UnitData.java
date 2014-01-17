package com.ahsgaming.valleyofbones.units;

import com.ahsgaming.valleyofbones.Player;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;

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
    boolean building = false;
    int buildTimeLeft = 0;

    boolean stealthEntered = false;
    boolean stealthActive = false;

    float movesLeft = 0, attacksLeft = 0;

    Player uncontested = null;
    int capUnitCount = 0;

    public static UnitData createUnitData(String protoId) {
        return createUnitData(Prototypes.getProto(protoId));
    }

    public static UnitData createUnitData(Prototypes.JsonProto proto) {
        UnitData unitData = new UnitData();

        unitData.ability = proto.properties.getString("ability", "");
        unitData.armor = proto.properties.getInt("armor", 0);
        unitData.attackDamage = proto.properties.getInt("attackdamage", 0);
        unitData.attackRange = proto.properties.getInt("attackrange", 0);
        unitData.attackSpeed = proto.properties.getFloat("attackspeed", 0);
        unitData.bonus.clear();
        if (proto.properties.get("bonus") != null)
            for (JsonValue v: proto.properties.get("bonus"))
                unitData.bonus.put(v.name(), v.asFloat());
        unitData.buildTime = proto.properties.getInt("buildtime", 0);
        unitData.capturable = proto.properties.getBoolean("capturable", false);
        unitData.cost = proto.properties.getInt("cost", 0);
        unitData.curHP = proto.properties.getInt("curhp", 0);
        unitData.food = proto.properties.getInt("food", 0);
        unitData.image = proto.image;
        unitData.maxHP = proto.properties.getInt("maxhp", 0);
        unitData.moveSpeed = proto.properties.getFloat("movespeed", 0);
        unitData.protoId = proto.id;
        unitData.requires.clear();
        if (proto.properties.get("requires") != null)
            for (JsonValue v: proto.properties.get("requires"))
                unitData.requires.add(v.asString());
        unitData.sightRange = proto.properties.getInt("sightrange", unitData.attackRange); // sight range defaults to attack range
        unitData.splashDamage = proto.properties.getFloat("splashdamage", 0);
        unitData.subtype = proto.properties.getString("subtype", "");
        unitData.type = proto.type;
        unitData.upkeep = proto.properties.getInt("upkeep", 0);

        return unitData;
    }

    // special things

    public boolean isDetector() {
        return ability.equals("detect");
    }

    public boolean isInvisible() {
        return stealthActive || ability.equals("sabotage");
    }

    public boolean isAbilityActive() {
        return ((ability.equals("stealth") && stealthActive) ||
                ability.equals("detect") || ability.equals("sabotage"));
    }

    public int getRefund() {
        return (int)(cost * 0.5f * (curHP / maxHP));
    }

    public boolean isAlive() {
        return isCapturable() || curHP > 0;
    }

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
        this.curHP = (curHP > maxHP ? maxHP : curHP);
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

    public boolean isStealthActive() {
        return stealthActive;
    }

    public void setStealthActive(boolean stealthActive) {
        this.stealthActive = stealthActive;
    }

    public boolean isStealthEntered() {
        return stealthEntered;
    }

    public void setStealthEntered(boolean stealthEntered) {
        this.stealthEntered = stealthEntered;
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
