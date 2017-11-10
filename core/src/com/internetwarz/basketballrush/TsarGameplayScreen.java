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
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
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
    Circle playerCircle;
    // todo  how to determine is screen touch coordinates within certain shape?
    // todo  find out is there any clickable stuff like buttons
    // todo  fill in touched sector with color


    public TsarGameplayScreen(Tsar game) {
        this.game = game;

        WIDTH = (float) Gdx.graphics.getWidth();
        HEIGTH  = (float) Gdx.graphics.getHeight();

        camera = new OrthographicCamera();
        camera.setToOrtho(false, WIDTH/ VIEWPORT_SCALE, HEIGTH/ VIEWPORT_SCALE);

        playerCircle = new Circle(WIDTH/2, HEIGTH/2, RADIUS);

        batch = new SpriteBatch();
        batch.setProjectionMatrix(camera.combined);
        layoutScore = new GlyphLayout();
        shapeRenderer = new ShapeRenderer(15000); //increase smoothness of circle


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

       if(Intersector.overlaps(playerCircle, new Rectangle(coord.x, coord.y, 1,1)))
       {
           System.out.println("Overlaps!");
           Vector2 yAxis = new Vector2(0,1);
           Vector2 vector1 = new Vector2(coord.x - playerCircle.x,coord.y - playerCircle.y);
           //double angle = Math. atan2(yAxis.y, yAxis.x) - Math. atan2(vector1.y, vector1.x);
           System.out.println("angle " + vector1.angle());

           System.out.println("sector for 2 is " + getCircleSector(2, vector1.angle()));
           System.out.println("sector for 3 is " + getCircleSector(3, vector1.angle()));
           System.out.println("sector for 4 is " + getCircleSector(4, vector1.angle()));
           System.out.println("sector for 5 is " + getCircleSector(5, vector1.angle()));
           System.out.println("sector for 6 is " + getCircleSector(6, vector1.angle()));
           System.out.println("sector for 7 is " + getCircleSector(7, vector1.angle()));



           //vector1.angle()
       }




        return false;
    }




    private int getCircleSector(int sectorNumbers, float angle)
    {
        // angle which passed is related to x-axis, need to convert it first
        angle -= 90f;

        if(angle < 0) angle+= 90;

        float deegreesPerSector = 360/sectorNumbers;



        return (int) (angle / deegreesPerSector)  + 1;
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

        playerCircle = new Circle(WIDTH/2, HEIGTH/2, RADIUS);


       // camera.viewportWidth = width / VIEWPORT_SCALE;
       // camera.viewportHeight = height / VIEWPORT_SCALE;
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
