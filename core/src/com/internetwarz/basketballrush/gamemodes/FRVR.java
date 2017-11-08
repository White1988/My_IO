package com.internetwarz.basketballrush.gamemodes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.ChainShape;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.MassData;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.internetwarz.basketballrush.BasketBallRush;
import com.internetwarz.basketballrush.GameModeSelect;
import com.internetwarz.basketballrush.input.swipe.SwipeHandler;
import com.internetwarz.basketballrush.input.swipe.mesh.SwipeTriStrip;
import com.internetwarz.basketballrush.utils.GameUtils;
import com.internetwarz.basketballrush.utils.Score;

public class FRVR implements Screen,InputProcessor{
    final BasketBallRush game;
    //final float appWidth = 768;
    //final float appHeight = 1280;
    SpriteBatch batch;
    OrthographicCamera camera;
    private static int VIEWPORT_SCALE = 25;


    // http://box2d.org/2011/12/pixels/
    private float METERS_TO_PIXELS_RATIO = 50f; // 50 pixels per meter

    Box2DDebugRenderer debugRenderer = new Box2DDebugRenderer();

    GlyphLayout layoutScore;

    World world;
    Body ballBody;
    Body groundBody;

    private int VELOCITY_ITERATIONS = 8;
    private int POSITION_ITERATIONS = 3;
    //setting values on touch
    private static int gameSpeed;
    private static int touchCounter;

    Score score;
    private GameUtils gameutils;


    private SwipeHandler swiper;
    SwipeTriStrip tris;
    ShapeRenderer shapes;
    Texture swipeTexture;

    //setting variables to store two different color balls and the net
    private Texture targetNet;     //Contains net image resource
    private Texture playerBallTexture;       // Contains ball texture
    private Sprite ballSprite;


    public FRVR(final BasketBallRush gam)
    {
        this.game = gam;
        float w = (float) Gdx.graphics.getWidth();
        float h = (float) Gdx.graphics.getHeight();

        camera = new OrthographicCamera();
        camera.setToOrtho(false, w/ VIEWPORT_SCALE, h/ VIEWPORT_SCALE);
        //camera.setToOrtho(false, w, h);
        batch = new SpriteBatch();
        batch.setProjectionMatrix(camera.combined);
        layoutScore = new GlyphLayout();

        swiper = new SwipeHandler(3);
        //swiper.initialDistance
        //we will use a texture for the smooth edge, and also for stroke effects
        swipeTexture = new Texture("data/gradient.png");
        swipeTexture.setFilter(TextureFilter.Linear, TextureFilter.Linear);

        //the triangle strip renderer
        tris = new SwipeTriStrip();
        tris.thickness = 30;
        shapes = new ShapeRenderer();


        InputMultiplexer plex = new InputMultiplexer();
        plex.addProcessor(this);
        plex.addProcessor(swiper);
        plex.addProcessor(new InputAdapter() {
            @Override
            public boolean scrolled(int amount) {
                camera.zoom += amount / 25f;
                camera.update(); // because of zooming
                return false;
            }
        });
        Gdx.input.setInputProcessor(plex);

        gameSpeed = 600;
        touchCounter =0;
        gameutils = new GameUtils();
        score = new Score(0);

        //loading the images in the variables
        targetNet = game.assets.getTexture("net_frvr");


        // Center the sprite in the top/middle of the screen


        playerBallTexture = game.assets.getTexture("ball_frvr");
        ballSprite = new Sprite(playerBallTexture);
        ballSprite.setPosition(Gdx.graphics.getWidth() / 2 - ballSprite.getWidth() / 2,
                Gdx.graphics.getHeight() / 2);




        // Create a physics world, the heart of the simulation.  The Vector
        //passed in is gravity
        world = new World(new Vector2(0, -98f), true);


        initPlayerBall();
        initGround();
    }

    private void initGround()
    {

        BodyDef  bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.StaticBody;
        bodyDef.position.set(0,0);

        groundBody = world.createBody(bodyDef);

        ChainShape groundShape = new ChainShape();
        groundShape.createChain(new Vector2[] {new Vector2(-500, 0), new Vector2(500, 0)});

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = groundShape;
        fixtureDef.density = 1f;
        fixtureDef.restitution = 0.8f;

        Fixture fixture = groundBody.createFixture(fixtureDef);

        groundShape.dispose();
    }


    private void initPlayerBall()
    {

        // BALL
        // Now create a BodyDefinition.  This defines the physics objects type
        //and position in the simulation
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;

        bodyDef.position.set(0, 5);

        // Create a body in the world using our definition
        ballBody = world.createBody(bodyDef);
        MassData m = new MassData();
        //m.mass = 10000f;
        ballBody.setMassData(m);
        // Now define the dimensions of the physics shape
        CircleShape ballShape = new CircleShape();
        ballShape.setRadius(ballSprite.getHeight()/(2*METERS_TO_PIXELS_RATIO));


        // FixtureDef is a confusing expression for physical properties
        // Basically this is where you, in addition to defining the shape of the
        // body
        // you also define it's properties like density, restitution and others
        //we will see shortly
        // If you are wondering, density and area are used to calculate overall mass

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = ballShape;
        fixtureDef.density = 1f;
        fixtureDef.restitution = 0.75f;


        Fixture fixture = ballBody.createFixture(fixtureDef);

        // Shape is the only disposable of the lot, so get rid of it
        ballShape.dispose();
        // BALL-END
    }



    private void   logs()
    {
        System.out.println("delta time = " + Gdx.graphics.getDeltaTime() * 1000);
        System.out.println("zoom = " + camera.zoom);
        System.out.println("ballBody pos = " + ballBody.getPosition());
        System.out.println("ballBody velocity = " + ballBody.getLinearVelocity());
    }

    @Override
    public void render(float delta) {

        logs();
        world.step(Gdx.graphics.getDeltaTime(), VELOCITY_ITERATIONS, POSITION_ITERATIONS);
        ballSprite.setPosition(ballBody.getPosition().x, ballBody.getPosition().y);

        Gdx.gl.glClearColor(0, 0, 0, 0);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        debugRenderer.render(world, camera.combined);

        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        swipeTexture.bind();

        //generate the triangle strip from our path
        tris.update(swiper.path());
        //the vertex color for tinting, i.e. for opacity
        tris.color = Color.WHITE;
        //render the triangles to the screen
        tris.draw(camera);

        //uncomment to see debug lines
        drawSwipeDebug();


        /*batch.begin();


        layoutScore.setText(game.font,""+score.getStringScore());
        if(touchCounter >= 1) {

        }

        batch.end();*/


    }

    //optional debug drawing..
    void drawSwipeDebug() {
        Array<Vector2> input = swiper.input();

        //draw the raw input
        shapes.begin(ShapeType.Line);
        shapes.setColor(Color.GRAY);
        for (int i=0; i<input.size-1; i++) {
            Vector2 p = input.get(i);
            Vector2 p2 = input.get(i+1);
            shapes.line(p.x, p.y, p2.x, p2.y);
        }
        shapes.end();

        //draw the smoothed and simplified path
        shapes.begin(ShapeType.Line);
        shapes.setColor(Color.RED);
        Array<Vector2> out = swiper.path();
        for (int i=0; i<out.size-1; i++) {
            Vector2 p = out.get(i);
            Vector2 p2 = out.get(i+1);
            shapes.line(p.x, p.y, p2.x, p2.y);
        }
        shapes.end();


        //render our perpendiculars
        shapes.begin(ShapeType.Line);
        Vector2 perp = new Vector2();

        for (int i=1; i<input.size-1; i++) {
            Vector2 p = input.get(i);
            Vector2 p2 = input.get(i+1);

            shapes.setColor(Color.LIGHT_GRAY);
            perp.set(p).sub(p2).nor();
            perp.set(perp.y, -perp.x);
            perp.scl(10f);
            shapes.line(p.x, p.y, p.x+perp.x, p.y+perp.y);
            perp.scl(-1f);
            shapes.setColor(Color.BLUE);
            shapes.line(p.x, p.y, p.x+perp.x, p.y+perp.y);
        }
        shapes.end();
    }

    @Override
    public void dispose() {
        // Hey, I actually did some clean up in a code sample!
        batch.dispose();
        game.dispose();
        targetNet.dispose();
        playerBallTexture.dispose();
        world.dispose();
    }

    @Override
    public void show() {

    }

    @Override
    public void resize(int width, int height) {
        camera.viewportWidth = width/ VIEWPORT_SCALE;
        camera.viewportHeight = height/ VIEWPORT_SCALE;
        camera.update();
        //camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
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
    public boolean keyDown(int keycode) {

       switch (keycode){
           case Input.Keys.BACK:
               game.setScreen(new GameModeSelect(game));
               break;
           case Input.Keys.W:
               ballBody.applyForceToCenter(0,1000, true);
           case Input.Keys.A:
               ballBody.applyForceToCenter(-1000,0, true);
           case Input.Keys.S:
               ballBody.applyForceToCenter(0,-100, true);
           case Input.Keys.D:
               ballBody.applyForceToCenter(1000,0, true);
       }

        return false;
    }


    @Override
    public boolean keyUp(int keycode) {

        // On right or left arrow set the velocity at a fixed rate in that
        //direction
        if(keycode == Input.Keys.RIGHT)
            ballBody.setLinearVelocity(1f, 0f);
        if(keycode == Input.Keys.LEFT)
            ballBody.setLinearVelocity(-1f,0f);

        if(keycode == Input.Keys.UP)
            ballBody.applyForceToCenter(0f,10f,true);
        if(keycode == Input.Keys.DOWN)
            ballBody.applyForceToCenter(0f, -10f, true);



        // If user hits spacebar, reset everything back to normal
        if(keycode == Input.Keys.SPACE) {
            ballBody.setLinearVelocity(110f, 110f);
            ballBody.setAngularVelocity(0f);

            ballSprite.setPosition(0f,0f);
            ballBody.setTransform(0f,0f,0f);
        }



        return true;
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
}

