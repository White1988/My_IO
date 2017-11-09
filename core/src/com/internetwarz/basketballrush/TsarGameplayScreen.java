package com.internetwarz.basketballrush;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;

public class TsarGameplayScreen implements Screen,InputProcessor
{
    private Tsar game;
    SpriteBatch batch;
    OrthographicCamera camera;
    private static int VIEWPORT_SCALE = 25;
    Box2DDebugRenderer debugRenderer = new Box2DDebugRenderer();

    GlyphLayout layoutScore;

   // http://www.coding-daddy.xyz/node/23
    ShapeRenderer shapeRenderer;


    public TsarGameplayScreen(Tsar game) {
        this.game = game;

        float w = (float) Gdx.graphics.getWidth();
        float h = (float) Gdx.graphics.getHeight();

        camera = new OrthographicCamera();
        camera.setToOrtho(false, w/ VIEWPORT_SCALE, h/ VIEWPORT_SCALE);


        batch = new SpriteBatch();
        batch.setProjectionMatrix(camera.combined);
        layoutScore = new GlyphLayout();


    }






























    @Override
    public boolean keyDown(int keycode) {
        return false;
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

    }
}
