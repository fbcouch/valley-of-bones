package com.ahsgaming.valleyofbones.screens.panels;

import com.ahsgaming.valleyofbones.VOBGame;
import com.ahsgaming.valleyofbones.screens.AbstractScreen;
import com.ahsgaming.valleyofbones.screens.LevelScreen;
import com.ahsgaming.valleyofbones.units.*;
import com.ahsgaming.valleyofbones.units.ProgressBar;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.TimeUtils;

/**
 * Created with IntelliJ IDEA.
 * User: jami
 * Date: 5/1/13
 * Time: 11:30 PM
 * To change this template use File | Settings | File Templates.
 */
public class InfoPanel extends Group implements EventListener {
    public static final String LOG = "InfoPanel";

    final String HEALTH = "%d/%d";
    final String ATTACK = "%d";
    final String RANGE = "%d";
    final String ARMOR = "%d";
    final String MOVE = "%d";
    final String ATTACK_LEFT = "%d";
    final String MOVE_LEFT = "%d";
    final String REFUND = "$%d";
    final String SPLASH = "%.0f%%";

    Label lblTitle, lblHealth, lblAttack, lblRange, lblArmor, lblMove, lblAttacksLeft, lblMovesLeft, lblRefund;
    Image imgBackground, iconHealth, iconAttack, iconRange, iconArmor, iconMove, iconAttacksLeft, iconMovesLeft, iconRefund, imgUnit;
    Image iconSubtype;
    Skin skin;
    VOBGame game;
    LevelScreen levelScreen;
    Group grpUnit;
    AbstractUnit selected, lastSelected;
    Prototypes.JsonProto lastBuildProto, buildProto;

    Table bonusTable, mainTable, statTable;

    AbilityIndicator abilityIndicator;
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
        imgUnit = new Image(VOBGame.instance.getTextureManager().getSpriteFromAtlas("assets", "ability-bg"));

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

        abilityIndicator = new AbilityIndicator(skin, levelScreen);

        mainTable.add(abilityIndicator).padRight(20 * VOBGame.SCALE);


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
            abilityIndicator.update(buildProto);

            healthBar.setSize(iconMovesLeft.getX() - iconHealth.getRight(), 4);
            healthBar.setCurrent(buildProto.getProperty("curhp").asFloat() / buildProto.getProperty("maxhp").asFloat());

            layout();

        } else if (selected != null) {
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
//            lblRefund.setText(String.format(REFUND, selected.getData().getRefund()));

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

            abilityIndicator.update(selected);

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


        healthBar.setSize(iconMovesLeft.getX() - iconHealth.getX(), 4 * VOBGame.SCALE);
    }

    public void setSelected(AbstractUnit unit) {
        selected = unit;
        buildProto = null;
    }

    public void setBuildProto(Prototypes.JsonProto proto) {
        selected = null;
        buildProto = proto;
    }

    public static class AbilityIndicator extends Table {
        Group grpAbilityIcon, grpAbilityText;
        Image imgAbilityBg, imgCheckOn, imgCheckOff, imgCheckBg, iconMoney, iconAbility, iconSplash;
        Label lblIncome, lblSplash;

        LevelScreen levelScreen;

        public AbilityIndicator(Skin skin, LevelScreen levelScreen) {
            super(skin);

            this.levelScreen = levelScreen;

            imgAbilityBg = new Image(VOBGame.instance.getTextureManager().getSpriteFromAtlas("assets", "ability-bg"));
            imgCheckOff = new Image(VOBGame.instance.getTextureManager().getSpriteFromAtlas("assets", "check-off"));
            imgCheckOn = new Image(VOBGame.instance.getTextureManager().getSpriteFromAtlas("assets", "check-on"));
            imgCheckBg = new Image(VOBGame.instance.getTextureManager().getSpriteFromAtlas("assets", "check-bg"));
            iconMoney = new Image(VOBGame.instance.getTextureManager().getSpriteFromAtlas("assets", "money-small"));
            iconSplash = new Image(VOBGame.instance.getTextureManager().getSpriteFromAtlas("assets", "splash-small"));

            iconMoney.setScale(0.65f);
            lblIncome = new Label("+0", skin, "small");
            lblIncome.setFontScale(VOBGame.SCALE * 0.75f);

            grpAbilityIcon = new Group();
            grpAbilityIcon.addActor(imgAbilityBg);
            grpAbilityIcon.setSize(imgAbilityBg.getWidth(), imgAbilityBg.getHeight());

            grpAbilityText = new Group();
            grpAbilityText.addActor(imgCheckBg);
            grpAbilityText.setSize(imgCheckBg.getWidth(), imgCheckBg.getHeight());

            add(grpAbilityIcon).fillX().padBottom(5 * VOBGame.SCALE).row();
            add(grpAbilityText).fillX().row();
        }

        public void update(final AbstractUnit unit) {
            removeAll();

            if (!unit.getData().getAbility().equals("")) {
                iconAbility = new Image(VOBGame.instance.getTextureManager().getSpriteFromAtlas("assets", unit.getData().getAbility() + "-small"));
                grpAbilityIcon.addActor(iconAbility);
                iconAbility.setPosition((grpAbilityIcon.getWidth() - iconAbility.getWidth()) * 0.5f, (grpAbilityIcon.getHeight() - iconAbility.getHeight()) * 0.5f);
                if (unit.getData().isAbilityActive()) {
                    iconAbility.setColor(0.0f, 0.8f, 1.0f, 1.0f);

                    if (unit.getData().getAbility().equals("increasing-returns") || unit.getData().getAbility().equals("splash")) {
                        if (unit.getData().getAbility().equals("splash")) {
                            lblIncome.setText(String.format("%.2f", unit.getData().getSplashDamage()));
                            lblIncome.setColor(0, 0.8f, 1.0f, 1.0f);
                            lblIncome.setPosition(5 * VOBGame.SCALE, 6 * VOBGame.SCALE);

                        } else {
                            lblIncome.setText("" + (- unit.getData().getUpkeep().get(0)));
                            lblIncome.setPosition(15 * VOBGame.SCALE, 6 * VOBGame.SCALE);

                            grpAbilityText.addActor(iconMoney);
                            iconMoney.setPosition(2 * VOBGame.SCALE, 1 * VOBGame.SCALE);
                            if (unit.getData().getUpkeep().get(0) < unit.getProto().getProperty("upkeep").asInt()) {
                                lblIncome.setColor(0, 0.8f, 1.0f, 1.0f);
                                iconMoney.setColor(0, 0.8f, 1.0f, 1.0f);
                            } else {
                                lblIncome.setColor(1, 1, 1, 1);
                                iconMoney.setColor(1, 1, 1, 1);
                            }
                        }

                        grpAbilityText.addActor(lblIncome);


                    } else {
                        grpAbilityText.addActor(imgCheckOn);

                        if (unit.getData().getAbility().equals("stealth") || unit.getData().getAbility().equals("mind-control")) {
                            if (imgCheckOn.getListeners().size > 0)
                                imgCheckOn.removeListener(imgCheckOn.getListeners().first());
                            imgCheckOn.addListener(new ClickListener(){
                                @Override
                                public void clicked(InputEvent event, float x, float y) {
                                    super.clicked(event, x, y);

                                    levelScreen.activateAbility(unit.getId());
                                }
                            });
                        }
                    }
                } else {
                    grpAbilityText.addActor(imgCheckOff);
                    if (imgCheckOff.getListeners().size > 0)
                        imgCheckOff.removeListener(imgCheckOff.getListeners().first());

                    if (unit.getData().getAbility().equals("stealth")) {
                        imgCheckOff.addListener(new ClickListener(){
                            @Override
                            public void clicked(InputEvent event, float x, float y) {
                                super.clicked(event, x, y);

                                levelScreen.activateAbility(unit.getId());
                            }
                        });
                    }
                }
            }


        }

        public void update(Prototypes.JsonProto proto) {
            removeAll();

            String ability = (proto.hasProperty("ability") && !proto.getProperty("ability").asString().equals("") ? proto.getProperty("ability").asString() : (proto.hasProperty("splashdamage") && proto.getProperty("splashdamage").asFloat() > 0 ? "splash" : null));

            if (ability != null) {
                iconAbility = new Image(VOBGame.instance.getTextureManager().getSpriteFromAtlas("assets", ability + "-small"));
                grpAbilityIcon.addActor(iconAbility);
                iconAbility.setPosition((grpAbilityIcon.getWidth() - iconAbility.getWidth()) * 0.5f, (grpAbilityIcon.getHeight() - iconAbility.getHeight()) * 0.5f);

            }
        }

        void removeAll() {
            if (iconAbility != null) {
                iconAbility.remove();
            }
            imgCheckOn.remove();
            imgCheckOff.remove();
            iconMoney.remove();
            lblIncome.remove();
        }
    }
}
