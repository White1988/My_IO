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
import com.badlogic.gdx.utils.viewport.FitViewport;

/**
 * Created by dell on 16.11.2017.
 */

public class StatisticsScreen implements Screen,InputProcessor {
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
    Label titleLable;

    public StatisticsScreen(Tsar game) {
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

        easyButton = new TextButton("Easy", textButtonStyle);
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

        mediumButton = new TextButton("Medium", textButtonStyle);
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

        hardButton = new TextButton("Hard", textButtonStyle);
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
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/Quicksand-Bold.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 30;
        parameter.color= Color.GREEN;
        BitmapFont font = generator.generateFont(parameter);
        Label.LabelStyle labelStyle = new Label.LabelStyle(font, Color.BLACK);
        titleLable = new Label("Statistics (player)", labelStyle);
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
        if(keycode == Input.Keys.BACKSPACE){
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
