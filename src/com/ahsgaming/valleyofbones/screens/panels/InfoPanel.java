package com.ahsgaming.valleyofbones.screens.panels;

import com.ahsgaming.valleyofbones.TextureManager;
import com.ahsgaming.valleyofbones.VOBGame;
import com.ahsgaming.valleyofbones.screens.AbstractScreen;
import com.ahsgaming.valleyofbones.screens.LevelScreen;
import com.ahsgaming.valleyofbones.units.ProgressBar;
import com.ahsgaming.valleyofbones.units.Prototypes;
import com.ahsgaming.valleyofbones.units.Unit;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.TimeUtils;

/**
 * Created with IntelliJ IDEA.
 * User: jami
 * Date: 5/1/13
 * Time: 11:30 PM
 * To change this template use File | Settings | File Templates.
 */
public class InfoPanel extends Group {
    public static final String LOG = "InfoPanel";

    final String HEALTH = "%d/%d";
    final String ATTACK = "%d";
    final String RANGE = "%d";
    final String ARMOR = "%d";
    final String MOVE = "%d";
    final String ATTACK_LEFT = "%d";
    final String MOVE_LEFT = "%d";
    final String REFUND = "$%d";

    Label lblTitle, lblHealth, lblAttack, lblRange, lblArmor, lblMove, lblAttacksLeft, lblMovesLeft, lblRefund;
    Image imgBackground, iconHealth, iconAttack, iconRange, iconArmor, iconMove, iconAttacksLeft, iconMovesLeft, iconRefund, imgUnit, iconAbility, imgAbilityBg;
    Image iconSubtype;
    Skin skin;
    VOBGame game;
    LevelScreen levelScreen;
    Group grpAbility, grpUnit;
    Image imgCheckOn, imgCheckOff;
    Unit selected, lastSelected;
    Prototypes.JsonProto lastBuildProto, buildProto;

    Table bonusTable, mainTable, statTable;

    ProgressBar healthBar;

    long lastUpdated = 0;

    public InfoPanel(VOBGame game, LevelScreen lvlScreen, Skin skin) {
        super();
        this.game = game;
        this.levelScreen = lvlScreen;
        this.skin = skin;

        iconHealth = new Image(VOBGame.instance.getTextureManager().getSpriteFromAtlas("assets", "hospital-cross-small"));
        iconAttack = new Image(VOBGame.instance.getTextureManager().getSpriteFromAtlas("assets", "crossed-swords-small"));
        iconRange = new Image(VOBGame.instance.getTextureManager().getSpriteFromAtlas("assets", "archery-target-small"));
        iconArmor = new Image(VOBGame.instance.getTextureManager().getSpriteFromAtlas("assets", "checked-shield-small"));
        iconMove = new Image(VOBGame.instance.getTextureManager().getSpriteFromAtlas("assets", "radial-balance-small"));
        iconMovesLeft = new Image(VOBGame.instance.getTextureManager().getSpriteFromAtlas("assets", "walking-boot-small"));
        iconAttacksLeft = new Image(VOBGame.instance.getTextureManager().getSpriteFromAtlas("assets", "rune-sword-small"));
        iconRefund = new Image(VOBGame.instance.getTextureManager().getSpriteFromAtlas("assets", "skull-crossed-bones-small"));
        imgBackground = new Image(VOBGame.instance.getTextureManager().getSpriteFromAtlas("assets", "selection-hud-bg"));
        imgAbilityBg = new Image(VOBGame.instance.getTextureManager().getSpriteFromAtlas("assets", "ability-bg"));
        imgCheckOff = new Image(VOBGame.instance.getTextureManager().getSpriteFromAtlas("assets", "check-off"));
        imgCheckOn = new Image(VOBGame.instance.getTextureManager().getSpriteFromAtlas("assets", "check-on"));
        imgUnit = new Image(VOBGame.instance.getTextureManager().getSpriteFromAtlas("assets", "ability-bg"));

        grpAbility = new Group();
        grpAbility.addActor(imgAbilityBg);

        grpUnit = new Group();
        grpUnit.addActor(imgUnit);
        grpUnit.setSize(imgUnit.getImageWidth(), imgUnit.getImageHeight());

        bonusTable = new Table(skin);

        lblTitle = new Label("NOTHING", skin, "small");
        lblTitle.setFontScale(VOBGame.SCALE * 0.75f);
        lblHealth = new Label(String.format(HEALTH, 0, 0), skin, "small");
        lblHealth.setFontScale(0.75f * VOBGame.SCALE);
        lblAttack = new Label(String.format(ATTACK, 0), skin, "small");
        lblAttack.setFontScale(VOBGame.SCALE);
        lblRange = new Label(String.format(RANGE, 0), skin, "small");
        lblRange.setFontScale(VOBGame.SCALE);
        lblArmor = new Label(String.format(ARMOR, 0), skin, "small");
        lblArmor.setFontScale(VOBGame.SCALE);
        lblMove = new Label(String.format(MOVE, 0), skin, "small");
        lblMove.setFontScale(VOBGame.SCALE);
        lblAttacksLeft = new Label(String.format(ATTACK_LEFT, 0), skin, "small");
        lblAttacksLeft.setFontScale(VOBGame.SCALE);
        lblMovesLeft = new Label(String.format(MOVE_LEFT, 0), skin, "small");
        lblMovesLeft.setFontScale(VOBGame.SCALE);
        lblRefund = new Label(String.format(REFUND, 0), skin, "small");
        lblRefund.setFontScale(VOBGame.SCALE);

        healthBar = new ProgressBar();
        healthBar.setSize(lblHealth.getWidth(), 4);

        addActor(imgBackground);
        setSize(imgBackground.getWidth(), imgBackground.getHeight());

        mainTable = new Table(skin);
        mainTable.setFillParent(true);
        addActor(mainTable);

        mainTable.add(grpUnit).padLeft(20 * VOBGame.SCALE);

        statTable = new Table(skin);
        mainTable.add(statTable).expandX();

        statTable.add(lblTitle).colspan(8).pad(5 * VOBGame.SCALE);
        statTable.row().padBottom(5 * VOBGame.SCALE);
        statTable.add(iconHealth);
        statTable.add(lblHealth).bottom().left().colspan(3);
        statTable.add(iconMovesLeft);
        statTable.add(lblMovesLeft);
        statTable.add(iconAttacksLeft);
        statTable.add(lblAttacksLeft);
        statTable.row().padBottom(3 * VOBGame.SCALE);
        statTable.add(iconAttack);
        statTable.add(lblAttack);
        statTable.add(iconRange);
        statTable.add(lblRange);
        statTable.add(iconMove);
        statTable.add(lblMove);
        statTable.add(iconArmor);
        statTable.add(lblArmor);
        statTable.row();

        statTable.add(bonusTable).colspan(8).expandX();

        mainTable.add(grpAbility).padRight(20 * VOBGame.SCALE);


        layout();

    }

    public void update() {

        if (buildProto != null && buildProto != lastBuildProto) {
            selected = null;
            lastSelected = null;
            lastBuildProto = buildProto;

            System.out.println("BuildProto: " + buildProto.title);
            lblTitle.setText(buildProto.title);
            lblHealth.setText(String.format(HEALTH, buildProto.getProperty("curhp").asInt(), buildProto.getProperty("maxhp").asInt()));
            lblAttack.setText(String.format(ATTACK, buildProto.getProperty("attackdamage").asInt()));
            lblRange.setText(String.format(RANGE, buildProto.getProperty("attackrange").asInt()));
            lblArmor.setText(String.format(ARMOR, buildProto.getProperty("armor").asInt()));
            lblMove.setText(String.format(MOVE, (int) buildProto.getProperty("movespeed").asFloat()));
            lblAttacksLeft.setText(String.format(ATTACK_LEFT, (int)buildProto.getProperty("attackspeed").asFloat()));
            lblMovesLeft.setText(String.format(MOVE_LEFT, (int)buildProto.getProperty("movespeed").asFloat()));

            bonusTable.clearChildren();
            if (buildProto.hasProperty("bonus")) {
                for (JsonValue val: buildProto.getProperty("bonus")) {
                    String subtype = val.name();
                    bonusTable.add(new Image(VOBGame.instance.getTextureManager().getSpriteFromAtlas("assets", subtype + "-small")));
                    Label times = new Label("x", skin, "small");
                    times.setFontScale(VOBGame.SCALE * 0.75f);
                    bonusTable.add(times).bottom();
                    Label label = new Label(String.format("%.1f", val.asFloat()), skin, "small");
                    label.setFontScale(VOBGame.SCALE);
                    bonusTable.add(label).bottom();
                }
            }


            grpAbility.removeActor(imgCheckOn);
            grpAbility.removeActor(imgCheckOff);

            if (iconAbility != null) {
                grpAbility.removeActor(iconAbility);
            }

            if (buildProto.hasProperty("ability") && !buildProto.getProperty("ability").asString().equals("")) {
                iconAbility = new Image(VOBGame.instance.getTextureManager().getSpriteFromAtlas("assets", buildProto.getProperty("ability").asString() + "-small"));
                grpAbility.addActor(iconAbility);
            } else {
                iconAbility = null;
            }

            healthBar.setSize(iconMovesLeft.getX() - iconHealth.getRight(), 4);
            healthBar.setCurrent(buildProto.getProperty("curhp").asFloat() / buildProto.getProperty("maxhp").asFloat());

            layout();

        } else if (selected != null && (selected != lastSelected || selected.getData().getModified() > lastUpdated)) {
            lastSelected = selected;
            buildProto = null;
            lastBuildProto = null;
            lastUpdated = TimeUtils.millis();

            lblTitle.setText(selected.getProto().title);
            lblHealth.setText(String.format(HEALTH, selected.getData().getCurHP(), selected.getData().getMaxHP()));
            lblAttack.setText(String.format(ATTACK, selected.getData().getAttackDamage()));
            lblRange.setText(String.format(RANGE, selected.getData().getAttackRange()));
            lblArmor.setText(String.format(ARMOR, selected.getData().getArmor()));
            lblMove.setText(String.format(MOVE, (int) selected.getData().getMoveSpeed()));
            lblAttacksLeft.setText(String.format(ATTACK_LEFT, (int)selected.getData().getAttacksLeft()));
            lblMovesLeft.setText(String.format(MOVE_LEFT, (int)selected.getData().getMovesLeft()));
            lblRefund.setText(String.format(REFUND, selected.getData().getRefund()));

            bonusTable.clearChildren();

            for (String subtype: selected.getData().getBonus().keySet()) {
                bonusTable.add(new Image(VOBGame.instance.getTextureManager().getSpriteFromAtlas("assets", subtype + "-small")));
                Label times = new Label("x", skin, "small");
                times.setFontScale(VOBGame.SCALE * 0.75f);
                bonusTable.add(times).bottom();
                Label label = new Label(String.format("%.1f", selected.getData().getBonus(subtype)), skin, "small");
                label.setFontScale(VOBGame.SCALE);
                bonusTable.add(label).bottom();
            }

            if (iconAbility != null) {
                grpAbility.removeActor(iconAbility);
                grpAbility.removeActor(imgCheckOn);
            }

            grpAbility.addActor(imgCheckOff);

            if (!selected.getData().getAbility().equals("")) {
                Gdx.app.log(LOG, selected.getData().getAbility());
                iconAbility = new Image(VOBGame.instance.getTextureManager().getSpriteFromAtlas("assets", selected.getData().getAbility() + "-small"));
                if (selected.getData().isAbilityActive()) {
                    iconAbility.setColor(0.0f, 0.8f, 1.0f, 1.0f);
                    grpAbility.removeActor(imgCheckOff);
                    grpAbility.addActor(imgCheckOn);

                    if (imgCheckOn.getListeners().size > 0)
                        imgCheckOn.removeListener(imgCheckOn.getListeners().first());
                    imgCheckOn.addListener(new ClickListener(){
                        @Override
                        public void clicked(InputEvent event, float x, float y) {
                            super.clicked(event, x, y);

                            levelScreen.activateAbility(selected.getId());
                        }
                    });
                } else {
                    if (imgCheckOff.getListeners().size > 0)
                        imgCheckOff.removeListener(imgCheckOff.getListeners().first());
                    imgCheckOff.addListener(new ClickListener(){
                        @Override
                        public void clicked(InputEvent event, float x, float y) {
                            super.clicked(event, x, y);

                            levelScreen.activateAbility(selected.getId());
                        }
                    });
                }

                grpAbility.addActor(iconAbility);


            } else {
                iconAbility = null;
            }

            healthBar.setSize(iconMovesLeft.getX() - iconHealth.getRight(), 4);
            healthBar.setCurrent((float)selected.getData().getCurHP() / (float)selected.getData().getMaxHP());

            layout();
        }
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);
        Vector2 coords = AbstractScreen.localToGlobal(new Vector2(0, 0), lblHealth);
        healthBar.setSize(iconMovesLeft.getX() - iconHealth.getX() - iconHealth.getWidth(), 4 * VOBGame.SCALE);
        healthBar.draw((SpriteBatch)batch, coords.x, coords.y + iconHealth.getHeight() * iconHealth.getScaleY() - healthBar.getHeight(), parentAlpha);
    }

    public void layout() {
        setSize(imgBackground.getWidth(), imgBackground.getHeight());

        int y = 0;
        float x = 0;

        if (imgUnit != null) imgUnit.remove();
        if (iconSubtype != null) iconSubtype.remove();

        if (buildProto != null) {
            imgUnit = new Image(VOBGame.instance.getTextureManager().getSpriteFromAtlas("assets", buildProto.image));
            iconSubtype = new Image(VOBGame.instance.getTextureManager().getSpriteFromAtlas("assets", buildProto.getProperty("subtype").asString() + "-small"));
        } else if (selected != null) {
            imgUnit = new Image(selected.getView().getImage());
            iconSubtype = new Image(VOBGame.instance.getTextureManager().getSpriteFromAtlas("assets", selected.getData().getSubtype() + "-small"));
        }

        if (imgUnit != null && iconSubtype != null) {
            grpUnit.addActor(imgUnit);
            grpUnit.addActor(iconSubtype);
            iconSubtype.setPosition(imgUnit.getRight() - iconSubtype.getWidth(), imgUnit.getY());
            grpUnit.setSize(imgUnit.getWidth(), imgUnit.getHeight());
        }

        imgCheckOff.setPosition(0, 0);
        imgCheckOn.setPosition(0, 0);
        imgAbilityBg.setPosition(0, imgCheckOff.getTop() + 5 * VOBGame.SCALE);
        grpAbility.setSize(imgAbilityBg.getRight(), imgAbilityBg.getTop());
//        grpAbility.setPosition(Math.max(lblTitle.getRight(), Math.max(lblAttacksLeft.getRight(), lblArmor.getRight())) + 5 * VOBGame.SCALE, lblArmor.getY());
        if (iconAbility != null) {
            iconAbility.setPosition(
                    (imgAbilityBg.getWidth() - iconAbility.getWidth()) * 0.5f,
                    imgAbilityBg.getY() + (imgAbilityBg.getHeight() - iconAbility.getHeight()) * 0.5f
            );
        }

        healthBar.setSize( iconMovesLeft.getX() - iconHealth.getX()
//                AbstractScreen.localToGlobal(new Vector2(0, 0), iconMovesLeft).x
//                        - AbstractScreen.localToGlobal(new Vector2(0, 0), iconHealth).x
                , 4 * VOBGame.SCALE
        );
    }

    public void setSelected(Unit unit) {
        selected = unit;
        buildProto = null;
    }

    public void setBuildProto(Prototypes.JsonProto proto) {
        buildProto = proto;
    }
}
