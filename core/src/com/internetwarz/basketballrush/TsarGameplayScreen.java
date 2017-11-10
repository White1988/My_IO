package com.internetwarz.basketballrush;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;

public class TsarGameplayScreen implements Screen,InputProcessor
{
    private Tsar game;
    SpriteBatch batch;
    OrthographicCamera camera;
    private static int VIEWPORT_SCALE = 1;
    Box2DDebugRenderer debugRenderer = new Box2DDebugRenderer();

    GlyphLayout layoutScore;

    private float WIDTH;
    private float HEIGTH;
    private float RADIUS = 100;

   // http://www.coding-daddy.xyz/node/23
    ShapeRenderer shapeRenderer;

    //actual circle to check collisions with
    Circle circle;
    // todo  how to determine is screen touch coordinates within certain shape?
    // todo  find out is there any clickable stuff like buttons
    // todo  fill in touched sector with color


    public TsarGameplayScreen(Tsar game) {
        this.game = game;

        WIDTH = (float) Gdx.graphics.getWidth();
        HEIGTH  = (float) Gdx.graphics.getHeight();

        camera = new OrthographicCamera();
        camera.setToOrtho(false, WIDTH/ VIEWPORT_SCALE, HEIGTH/ VIEWPORT_SCALE);

        circle = new Circle(WIDTH/2, HEIGTH/2, RADIUS);

        batch = new SpriteBatch();
        batch.setProjectionMatrix(camera.combined);
        layoutScore = new GlyphLayout();
        shapeRenderer = new ShapeRenderer();


        Gdx.input.setInputProcessor(this);

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

        System.out.println("screenX " +screenX);
        System.out.println("screenY " +screenY);

        Vector3 coord =  camera.unproject(new Vector3(screenX, screenY, 0));

        System.out.println("worldX " +coord.x);
        System.out.println("worldY " +coord.y);

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

        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(Color.WHITE);
        shapeRenderer.circle(WIDTH/2,HEIGTH/2,RADIUS);
        shapeRenderer.end();

        /*for (int i = 0 ; i < 9; i ++){
            shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
            shapeRenderer.setColor(Color.WHITE);
            shapeRenderer.identity();
            // todo (Nikita) find out how to rotate
            // line so we will have circle divided by sectors
            shapeRenderer.rotate(0, 0, 1, 20*i);

            shapeRenderer.line(50,50, 100, 100);
            shapeRenderer.end();
        }*/

    }

    @Override
    public void resize(int width, int height) {
        WIDTH=width;
        HEIGTH = height;

        circle = new Circle(WIDTH/2, HEIGTH/2, RADIUS);


        camera.viewportWidth = width / VIEWPORT_SCALE;
        camera.viewportHeight = height / VIEWPORT_SCALE;
        camera.update();
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
