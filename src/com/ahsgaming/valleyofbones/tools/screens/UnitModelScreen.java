package com.ahsgaming.valleyofbones.tools.screens;

import com.ahsgaming.valleyofbones.tools.JsonUnitModelTool;
import com.ahsgaming.valleyofbones.units.Prototypes;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;

/**
 * Created with IntelliJ IDEA.
 * User: jami
 * Date: 5/17/13
 * Time: 8:22 AM
 * To change this template use File | Settings | File Templates.
 */
public class UnitModelScreen implements Screen {
    public static final String LOG = "UnitModelScreen";

    Skin skin;
    Stage stage;

    Table mainTable, protoTable;
    List protoList;
    Button btnAdd, btnRemove;

    Button btnSave, btnCancel;

    Prototypes.JsonProto selectedProto;

    final JsonUnitModelTool parent;

    String[] attributes = {
            "id", "type", "image", "title", "desc"
    };

    TextField[] attributeFields;

    String[] properties = {
            "curhp", "maxhp", "armor", "food",
            "requires", "attackdamage", "attackspeed", "attackrange", "movespeed",
            "cost", "subtype", "bonus" };
    PropertyType[] propertyTypes = {
            PropertyType.INT, PropertyType.INT, PropertyType.INT, PropertyType.INT,
            PropertyType.LIST_ID, PropertyType.INT, PropertyType.FLOAT, PropertyType.FLOAT, PropertyType.INT,
            PropertyType.INT, PropertyType.STRING, PropertyType.OBJECT_STR_INT
    };

    Array<Property> propertyArray;

    /**
     * Constructor
     *
     * @param parent
     */
    public UnitModelScreen(JsonUnitModelTool parent) {
        super();
        this.parent = parent;
        this.stage = new Stage();
    }

    public String[] getProtoIds() {
        String[] returnVal = new String[Prototypes.getProtos().size];
        int i = 0;
        for (String id: Prototypes.getProtos().keys()) {
            returnVal[i] = id;
            i++;
        }
        return returnVal;
    }

    public Skin getSkin() {
        if (skin == null)
            skin = new Skin(Gdx.files.internal("newui/uiskin.json"));

        return skin;
    }

    public void setSelectedProto(Prototypes.JsonProto proto) {
        selectedProto = proto;

        for (int a=0; a<attributes.length; a++) {
            if (attributes[a].equals("id"))
                attributeFields[a].setText(proto.id);
            else if (attributes[a].equals("type"))
                attributeFields[a].setText(proto.type);
            else if (attributes[a].equals("image"))
                attributeFields[a].setText(proto.image);
            else if (attributes[a].equals("title"))
                attributeFields[a].setText(proto.title);
            else if (attributes[a].equals("desc"))
                attributeFields[a].setText(proto.desc);
            else
                attributeFields[a].setText("");

        }

        for (int a=0; a<properties.length; a++) {
            if (proto.hasProperty(properties[a]))
                propertyArray.get(a).setValue(proto.getProperty(properties[a]).toString());
            else
                propertyArray.get(a).setValue("");
        }
    }

    @Override
    public void show() {
        stage.clear();
        Gdx.input.setInputProcessor(stage);
        getSkin();

        mainTable = new Table(getSkin());
        protoTable = new Table(getSkin());

        protoList = new List(getProtoIds(), getSkin());

        btnAdd = new TextButton("ADD", getSkin());
        btnRemove = new TextButton("REMOVE", getSkin(), "cancel");

        mainTable.setFillParent(true);

        mainTable.add(protoList).expandY().width(stage.getWidth() * 0.3f).top();

        //protoTable.setFillParent(true);

        mainTable.add(protoTable).expand();

        mainTable.row();

        Group g = new Group();
        g.addActor(btnAdd);
        g.addActor(btnRemove);
        btnAdd.setPosition(0, 0);
        btnRemove.setPosition(btnAdd.getRight(), 0);

        mainTable.add(g);

        mainTable.row();

        // proto table
        attributeFields = new TextField[attributes.length];

        propertyArray = new Array<Property>();

        for (int i=0; i<properties.length; i++) {
            if (i < attributes.length) {
                protoTable.add(attributes[i]).left().pad(4);
                attributeFields[i] = new TextField("", getSkin());
                protoTable.add(attributeFields[i]).pad(4).padRight(30);
            } else {
                protoTable.add().colspan(2).pad(4).padRight(30);
            }

            protoTable.add(properties[i]).pad(4).left();

            Property p = new Property(properties[i], propertyTypes[i], "", getSkin());
            propertyArray.add(p);

            protoTable.add(p.widget).pad(4);

            protoTable.row();
        }

        btnSave = new TextButton("SAVE", getSkin(), "medium");
        btnCancel = new TextButton("CANCEL", getSkin(), "medium-cancel");

        protoTable.add(btnCancel).pad(4).minSize(150, 50);
        protoTable.add().colspan(2);
        protoTable.add(btnSave).pad(4).minSize(150, 50);


        btnSave.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);

                Prototypes.saveUnits(Prototypes.UNIT_FILE);
            }
        });


        setSelectedProto(Prototypes.getProto(protoList.getSelection()));
        Prototypes.saveUnits(Prototypes.UNIT_FILE);
    }

    @Override
    public void resize(int width, int height) {
        stage.setViewport(width, height, true);
        stage.clear();




        stage.addActor(mainTable);


    }

    @Override
    public void render(float delta) {
        stage.act(delta);

        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        stage.draw();

        if (selectedProto == null || !selectedProto.id.equals(protoList.getSelection())) {
            setSelectedProto(Prototypes.getProto(protoList.getSelection()));
        }
    }

    @Override
    public void hide() {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void dispose() {
        //To change body of implemented methods use File | Settings | File Templates.
        stage.dispose();
        if (skin != null) skin.dispose();
    }

    public enum PropertyType {
        INT, FLOAT, STRING, OBJECT_STR_INT, LIST_ID;
    }

    public static class Property {
        String name;

        PropertyType type;

        Widget widget;

        public Property(String name, PropertyType type, String value, Skin skin) {
            this.name = name;
            this.type = type;

            switch(this.type) {
                case LIST_ID:

                    //break;
                case OBJECT_STR_INT:

                    //break;
                case INT:
                case FLOAT:
                case STRING:
                default:
                    widget = new TextField("", skin);
            }

            setValue(value);
        }

        public void setValue(String value) {
            switch(this.type) {
                case LIST_ID:

                    //break;
                case OBJECT_STR_INT:

                    //break;
                case INT:
                case FLOAT:
                case STRING:
                default:
                    ((TextField)widget).setText(value);
            }
        }

        public String getValue() {
            switch(this.type) {
                case LIST_ID:

                    //break;
                case OBJECT_STR_INT:

                    //break;
                case INT:
                case FLOAT:
                case STRING:
                default:
                    return ((TextField)widget).getText();
            }
            //return "";
        }
    }
}
