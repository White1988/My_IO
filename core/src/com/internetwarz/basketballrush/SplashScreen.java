package com.internetwarz.basketballrush;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.TimeUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;

public class SplashScreen implements Screen {
    final Tsar game;
    float appWidth;
    float appHeight;
    SpriteBatch batch;
    OrthographicCamera camera;
    Texture splashImage;
    Stage stage;
    long startTime;

    public SplashScreen(final Tsar gam){
        this.game = gam;
        appWidth = Gdx.graphics.getWidth();
        appHeight = Gdx.graphics.getHeight();
        stage = new Stage(new FitViewport(appWidth,appHeight));
        stage.clear();
        startTime = TimeUtils.millis();
        camera = new OrthographicCamera();
        camera.setToOrtho(false, appWidth, appHeight);

        batch = new SpriteBatch();
        batch.setProjectionMatrix(camera.combined);
        splashImage = new Texture(Gdx.files.internal("images/splash.jpg"));
        Image splash = new Image(splashImage);
        splash.setSize(appWidth, appHeight);
        stage.addActor(splash);

        //Calling the load functions to load all the assets on the splash screen
        game.assets.load();
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(1, 1, 1, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);


        stage.act();
        batch.begin();
        stage.draw();
        batch.end();

        if(TimeUtils.millis() - startTime > 2000)
            game.setScreen(new MainMenuScreen(game));
    }


    @Override
    public void show() {

    }

    @Override
    public void resize(int width, int height) {

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
        batch.dispose();
    }
}
