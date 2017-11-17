package com.internetwarz.basketballrush;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.internetwarz.basketballrush.utils.LanguagesManager;

import java.util.ArrayList;

/**
 * Created by dell on 16.11.2017.
 */

public class HallOfFameScreen implements Screen, InputProcessor{
    Tsar game;

    private int WIDTH;
    private int HEIGHT;

    private OrthographicCamera camera;
    private SpriteBatch batch;
    private int VIEWPORT_SCALE = 1;

    private Stage stage;
    private Preferences prefs;
    private Sound clickSound;

    //Buttons
    TextureAtlas buttonAtlas;
    Skin buttonSkin;
    TextButton.TextButtonStyle textButtonStyle;
    TextButton easyButton, mediumButton, hardButton;

    //Labels
    private Label titleLable;
    private Label.LabelStyle labelStyle;
    private Label.LabelStyle styleTitle;
    private Label titleLvl;
    private Label titlePlayer;
    private int amountLines;
    private ArrayList<ArrayList<Label>> rows = new ArrayList<ArrayList<Label>>();
    private Label titleDate;

    public HallOfFameScreen(Tsar game) {
        this.game = game;
        WIDTH = Gdx.graphics.getWidth();
        HEIGHT = Gdx.graphics.getHeight();

        stage = new Stage(new FitViewport(WIDTH, HEIGHT));
        stage.clear();

        InputMultiplexer plex = new InputMultiplexer();
        plex.addProcessor(stage);
        plex.addProcessor(this);

        Gdx.input.setInputProcessor(plex);
        Gdx.input.setCatchBackKey(true);

        camera = new OrthographicCamera();
        camera.setToOrtho(false, WIDTH/ VIEWPORT_SCALE, HEIGHT / VIEWPORT_SCALE);

        batch = new SpriteBatch();
        batch.setProjectionMatrix(camera.combined);


        //Preferences init
        prefs = Gdx.app.getPreferences("My Preferences");
        clickSound = game.assets.getSound();

        buttonsInit();
        labelsInit();
        tableInit();
    }

    private void tableInit() {
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/Attractive-Regular.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 20;
        parameter.color= Color.BLACK;
        parameter.characters = FreeTypeFontGenerator.DEFAULT_CHARS + "абвгдеёжзийклмнопрстуфхцчшщъыьэюяАБВГДЕЁЖЗИЙКЛМНОПРСТУФХЦЧШЩЪЫЬЭЮЯ";
        BitmapFont font = generator.generateFont(parameter);

        styleTitle = new Label.LabelStyle();
        styleTitle.font = font;
        styleTitle.background = buttonSkin.getDrawable("Button");
        titleLvl = new Label("Level", styleTitle);
        titleLvl.setPosition(widthPercent(10), titleLable.getY() - titleLable.getHeight() - heightPercent(10));
        titleLvl.setWidth(WIDTH/2 - widthPercent(10 + 20));
        titleLvl.setHeight((titleLable.getY() - titleLable.getHeight() - heightPercent(10))/6);
        titleLvl.setAlignment(Align.center);
        titleLvl.setDebug(true);
        stage.addActor(titleLvl);

        titlePlayer = new Label("Player", styleTitle);
        titlePlayer.setPosition(titleLvl.getX() + titleLvl.getWidth(), titleLvl.getY());
        titlePlayer.setWidth(WIDTH/2 - widthPercent(20));
        titlePlayer.setHeight(titleLvl.getHeight());
        titlePlayer.setAlignment(Align.center);
        titlePlayer.setEllipsis(false);
        titlePlayer.setDebug(true);
        stage.addActor(titlePlayer);

        titleDate = new Label("Date", styleTitle);
        titleDate.setPosition(titlePlayer.getX() + titlePlayer.getWidth(), titlePlayer.getY());
        titleDate.setWidth(WIDTH/2 - widthPercent(20));
        titleDate.setHeight(titleLvl.getHeight());
        titleDate.setAlignment(Align.center);
        titleDate.setDebug(true);
        stage.addActor(titleDate);


        amountLines = 6;

        for (int i = amountLines-1; i > 0; i--) {
            addLineToTable(i, 10, "01.02.1998");
        }


    }

    private void addLineToTable(int level, int countGames, String date ) {
        ArrayList<Label> row = new ArrayList<Label>();
        Label levelLabel = new Label("Level" + level, styleTitle);
        Label playersLabel = new Label("Games" + countGames, styleTitle);
        Label dateLabel = new Label("Date" + date, styleTitle);
        row.add(levelLabel);
        row.add(playersLabel);
        row.add(dateLabel);
        rows.add(row);
        if(rows.size() == 1) {
            levelLabel.setPosition(titleLvl.getX(), titleLvl.getY() - titleLvl.getHeight());
            playersLabel.setPosition(titlePlayer.getX(), titlePlayer.getY() - titlePlayer.getHeight());
            dateLabel.setPosition(titleDate.getX(), titleDate.getY() - titleDate.getHeight());
        }
        else {
            Label prevLevel = rows.get(rows.size()-2).get(0);
            Label prevPlayer = rows.get(rows.size()-2).get(1);
            Label prevDate = rows.get(rows.size()-2).get(2);

            levelLabel.setPosition(prevLevel.getX(), prevLevel.getY() - prevLevel.getHeight());
            playersLabel.setPosition(prevPlayer.getX(), prevPlayer.getY() - prevPlayer.getHeight());
            dateLabel.setPosition(prevDate.getX(), prevDate.getY() - prevDate.getHeight());
        }
        levelLabel.setWidth(titleLvl.getWidth());
        levelLabel.setHeight(titleLvl.getHeight());
        levelLabel.setAlignment(Align.center);
        levelLabel.setDebug(true);

        playersLabel.setWidth(titlePlayer.getWidth());
        playersLabel.setHeight(titlePlayer.getHeight());
        playersLabel.setAlignment(Align.center);
        playersLabel.setDebug(true);

        dateLabel.setWidth(titleDate.getWidth());
        dateLabel.setHeight(titleDate.getHeight());
        dateLabel.setAlignment(Align.center);
        dateLabel.setDebug(true);

        stage.addActor(levelLabel);
        stage.addActor(playersLabel);
        stage.addActor(dateLabel);


    }

    private void buttonsInit() {
        //Button checked texture
        Pixmap rect = new Pixmap(40,20,Pixmap.Format.RGBA8888);
        rect.setColor(Color.LIGHT_GRAY);
        rect.fillRectangle(0, 0, 40, 20);
        Texture buttonChecked = new Texture(rect);
        rect.setColor(Color.DARK_GRAY);
        rect.drawRectangle(0,0,40,20);
        buttonChecked.draw(rect, 0, 0);
        rect.dispose();

        //Simple button texture
        Pixmap rect2 = new Pixmap(40,20,Pixmap.Format.RGBA8888);
        rect2.setColor(Color.DARK_GRAY);
        rect2.drawRectangle(0,0,40,20);
        Texture button = new Texture(rect2);
        rect2.dispose();


        //Buttons init
        buttonAtlas = game.assets.getButtonAtlas();
        buttonAtlas.addRegion("Button", button, 0, 0, 40, 20);
        buttonAtlas.addRegion("Button checked", buttonChecked, 0, 0, 40, 20);
        buttonSkin = new Skin();
        buttonSkin.addRegions(buttonAtlas);

        textButtonStyle = new TextButton.TextButtonStyle();
        textButtonStyle.font = game.font;
        textButtonStyle.up = buttonSkin.getDrawable("Button");
        //textButtonStyle.down = buttonSkin.getDrawable("Simple button");
        textButtonStyle.checked = buttonSkin.getDrawable("Button checked");

        easyButton = new TextButton(LanguagesManager.getInstance().getString("easy"), textButtonStyle);
        easyButton.setSize(widthPercent(30), heightPercent(10));
        easyButton.setPosition(widthPercent(5), HEIGHT - easyButton.getHeight() - 10);
        easyButton.addListener(new ClickListener(){
            public void clicked(InputEvent event, float x, float y){
                if (prefs.getBoolean("soundOn", true))
                    clickSound.play();
                System.out.println("easy clicked!");
                mediumButton.setChecked(false);
                hardButton.setChecked(false);

            }

        });
        stage.addActor(easyButton);

        mediumButton = new TextButton(LanguagesManager.getInstance().getString("medium"), textButtonStyle);
        mediumButton.setSize(widthPercent(30), heightPercent(10));
        mediumButton.setPosition(widthPercent(5) + easyButton.getWidth(), HEIGHT - mediumButton.getHeight() - 10);
        mediumButton.addListener(new ClickListener(){
            public void clicked(InputEvent event, float x, float y){
                if (prefs.getBoolean("soundOn", true))
                    clickSound.play();
                easyButton.setChecked(false);
                hardButton.setChecked(false);

            }
        });
        stage.addActor(mediumButton);

        hardButton = new TextButton(LanguagesManager.getInstance().getString("hard"), textButtonStyle);
        hardButton.setSize(widthPercent(30), heightPercent(10));
        hardButton.setPosition(mediumButton.getX() + mediumButton.getWidth(), HEIGHT - hardButton.getHeight() - 10);
        hardButton.addListener(new ClickListener(){
            public void clicked(InputEvent event, float x, float y){
                if (prefs.getBoolean("soundOn", true))
                    clickSound.play();
                mediumButton.setChecked(false);
                easyButton.setChecked(false);


            }
        });
        stage.addActor(hardButton);

        easyButton.setChecked(true);
    }

    private void labelsInit() {

        //Labels init
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/Attractive-Regular.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 30;
        parameter.color= Color.GREEN;
        parameter.characters = FreeTypeFontGenerator.DEFAULT_CHARS + "абвгдеёжзийклмнопрстуфхцчшщъыьэюяАБВГДЕЁЖЗИЙКЛМНОПРСТУФХЦЧШЩЪЫЬЭЮЯ";
        BitmapFont font = generator.generateFont(parameter);
        labelStyle = new Label.LabelStyle(font, Color.BLACK);
        titleLable = new Label(LanguagesManager.getInstance().getString("hallOfFame"), labelStyle);
        titleLable.setPosition(WIDTH/2 - titleLable.getWidth()/2, easyButton.getY() - heightPercent(10));

        stage.addActor(titleLable);
    }


    public float widthPercent(int w){
        float result;
        result = (WIDTH*w)/100;
        return result;
    }

    public float heightPercent(int h){
        float result;
        result = (HEIGHT*h)/100;
        return result;
    }

    @Override
    public boolean keyDown(int keycode) {
        if(keycode == Input.Keys.BACKSPACE || keycode == Input.Keys.BACK){
            game.setScreen(new MainMenuScreen(game));
        }
        return true;
    }

    @Override
    public boolean keyUp(int keycode) {
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(int amount) {
        return false;
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

        camera.update();
        batch.setProjectionMatrix(camera.combined);

        stage.act();
        batch.begin();
        stage.draw();
        batch.end();

    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width,height,false);
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {

    }
}
