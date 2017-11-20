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
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.internetwarz.basketballrush.utils.LanguagesManager;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by dell on 16.11.2017.
 */

public class SettingsScreen implements Screen, InputProcessor {
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
    TextButton signOut;

    Skin skin;

    //Labels
    Label titleLable;
    Label langLabel;
    Label signOutLabel;

    //Select box
    HashMap<String, String> languages2;
    SelectBox languagesSB;

    public SettingsScreen(Tsar game) {
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

        languagesInit();
        labelsInit();
        selectBoxInit();
        buttonInit();
    }

    private void buttonInit() {
        //Font
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/Attractive-Regular.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 23;
        parameter.color= Color.BLACK;
        parameter.characters = FreeTypeFontGenerator.DEFAULT_CHARS + "абвгдеёжзийклмнопрстуфхцчшщъыьэюяАБВГДЕЁЖЗИЙКЛМНОПРСТУФХЦЧШЩЪЫЬЭЮЯ";
        BitmapFont font = generator.generateFont(parameter);

        //Button checked texture
        Pixmap rect = new Pixmap(40,20,Pixmap.Format.RGBA8888);
        rect.setColor(Color.LIGHT_GRAY);
        rect.fillRectangle(0, 0, 40, 20);
        Texture buttonDown = new Texture(rect);
        rect.setColor(Color.DARK_GRAY);
        rect.drawRectangle(0,0,40,20);
        buttonDown.draw(rect, 0, 0);
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
        buttonAtlas.addRegion("Button down", buttonDown, 0, 0, 40, 20);
        buttonSkin = new Skin();
        buttonSkin.addRegions(buttonAtlas);

        textButtonStyle = new TextButton.TextButtonStyle();
        textButtonStyle.font = font;
        textButtonStyle.up = buttonSkin.getDrawable("Button");
        textButtonStyle.down = buttonSkin.getDrawable("Button down");
        //textButtonStyle.checked = buttonSkin.getDrawable("Button checked");

        signOut = new TextButton(LanguagesManager.getInstance().getString("signOut"), textButtonStyle);
        signOut.setSize(widthPercent(30), heightPercent(8));
        signOut.setPosition(WIDTH/2 - signOut.getWidth()/2, HEIGHT/2);
        signOut.addListener(new ClickListener(){
            public void clicked(InputEvent event, float x, float y){
                if (prefs.getBoolean("soundOn", true))
                    clickSound.play();

            }

        });
        stage.addActor(signOut);

        //Add label
        Label.LabelStyle labelStyle = new Label.LabelStyle(font, Color.BLACK);
        signOutLabel = new Label(LanguagesManager.getInstance().getString("signOut"), labelStyle);
        signOutLabel.setPosition(WIDTH/2 - signOutLabel.getWidth()/2, signOut.getY() + signOut.getHeight()/2 + signOutLabel.getHeight() + heightPercent(1));
        stage.addActor(signOutLabel);

    }

    private void languagesInit() {
        languages2 = new HashMap<String, String>();
        languages2.put("Ru", "Russian");
        languages2.put("en_UK", "English");
    }

    private void selectBoxInit() {
        String curLanguage = "";
        for(Map.Entry entry: languages2.entrySet()) {
            if(entry.getKey().equals(prefs.getString("Language"))){
                curLanguage = entry.getValue().toString();
                System.out.println("Language: " + curLanguage);
                break;
            }
        }
        System.out.println("Language: " + curLanguage);
        skin = new Skin(Gdx.files.internal("skins/uiskin.json"));
        languagesSB = new SelectBox(skin);
        languagesSB.setWidth(widthPercent(30));
        languagesSB.setItems(languages2.values().toArray());
        languagesSB.setPosition(WIDTH/2 - languagesSB.getWidth()/2, langLabel.getY() - languagesSB.getHeight() - heightPercent(1));
        languagesSB.setSelected(curLanguage);
        languagesSB.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                clickSound.play();
                System.out.println((languagesSB.getSelected()));

                for(Map.Entry entry : languages2.entrySet()) {
                    if(entry.getValue() == languagesSB.getSelected()) {
                        System.out.println(entry.getKey());
                        if(LanguagesManager.getInstance().loadLanguage(entry.getKey().toString())) {
                            prefs.putString("Language", entry.getKey().toString());
                            prefs.flush();
                            game.setScreen(new SettingsScreen(game));
                        }
                    }
                }
            }
        });
        stage.addActor(languagesSB);
    }


    private void labelsInit() {

        //Labels init
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/Attractive-Regular.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 23;
        parameter.color= Color.BLACK;
        parameter.characters = FreeTypeFontGenerator.DEFAULT_CHARS + "абвгдеёжзийклмнопрстуфхцчшщъыьэюяАБВГДЕЁЖЗИЙКЛМНОПРСТУФХЦЧШЩЪЫЬЭЮЯ";
        BitmapFont font = generator.generateFont(parameter);

        Label.LabelStyle labelStyle = new Label.LabelStyle(font, Color.BLACK);
        titleLable = new Label(LanguagesManager.getInstance().getString("settings"), labelStyle);
        titleLable.setPosition(WIDTH/2 - titleLable.getWidth()/2, HEIGHT - titleLable.getHeight() - heightPercent(2));
        stage.addActor(titleLable);

        parameter.size = 23;
        parameter.color= Color.GREEN;
        font = generator.generateFont(parameter);
        labelStyle = new Label.LabelStyle(font, Color.BLACK);
        langLabel = new Label(LanguagesManager.getInstance().getString("language"), labelStyle);
        langLabel.setPosition(WIDTH/2 - langLabel.getPrefWidth()/2, titleLable.getY() - heightPercent(10));
        stage.addActor(langLabel);
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
