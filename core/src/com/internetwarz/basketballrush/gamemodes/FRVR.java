package com.internetwarz.basketballrush.gamemodes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
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
import com.internetwarz.basketballrush.BasketBallRush;
import com.internetwarz.basketballrush.GameModeSelect;
import com.internetwarz.basketballrush.utils.GameUtils;
import com.internetwarz.basketballrush.utils.Score;
import com.internetwarz.basketballrush.utils.SimpleDirectionGestureDetector;

public class FRVR implements Screen,InputProcessor{
    final BasketBallRush game;
    //final float appWidth = 768;
    //final float appHeight = 1280;
    SpriteBatch batch;
    OrthographicCamera camera;

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


    //setting variables to store two different color balls and the net
    private Texture targetNet;     //Contains net image resource
    private Texture playerBallTexture;       // Contains ball texture
    private Sprite ballSprite;


    public FRVR(final BasketBallRush gam)
    {
        this.game = gam;
        camera = new OrthographicCamera();
        //camera.setToOrtho(false, appWidth, appHeight);
        batch = new SpriteBatch();
        batch.setProjectionMatrix(camera.combined);
        layoutScore = new GlyphLayout();

        InputMultiplexer plex = new InputMultiplexer();
        plex.addProcessor(this);
        plex.addProcessor(new SimpleDirectionGestureDetector(new SimpleDirectionGestureDetector.DirectionListener() {

            //Chainging the ball color according to swipe action of the user.
            @Override
            public void onUp() {
            }

            @Override
            public void onRight() {
                touchCounter++;
                //todo make ball move
               // targetNet = game.assets.getTexture("net1");
               // touchImage="image";
            }

            @Override
            public void onLeft() {
                touchCounter++;
               // targetNet = game.assets.getTexture("net2");
               // touchImage="image1";
            }

            @Override
            public void onDown() {

            }
        }));
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
        // We are going to use 1 to 1 dimensions.  Meaning 1 in physics engine
        //is 1 pixel
        // Set our body to the same position as our sprite
        bodyDef.position.set(ballSprite.getX(), ballSprite.getY());

        // Create a body in the world using our definition
        ballBody = world.createBody(bodyDef);
        MassData m = new MassData();
        m.mass = 10000f;
        ballBody.setMassData(m);
        // Now define the dimensions of the physics shape
        CircleShape ballShape = new CircleShape();
        ballShape.setRadius(ballSprite.getHeight()/2);
        // We are a box, so this makes sense, no?
        // Basically set the physics polygon to a box with the same dimensions
        //as our sprite
       // ballShape.setAsBox(ballSprite.getWidth()/2, ballSprite.getHeight()/2);

        // FixtureDef is a confusing expression for physical properties
        // Basically this is where you, in addition to defining the shape of the
        // body
        // you also define it's properties like density, restitution and others
        //we will see shortly
        // If you are wondering, density and area are used to calculate over all
        //mass
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = ballShape;
        fixtureDef.density = 1f;
        fixtureDef.restitution = 0.95f;


        Fixture fixture = ballBody.createFixture(fixtureDef);

        // Shape is the only disposable of the lot, so get rid of it
        ballShape.dispose();
        // BALL-END
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
    public void render(float delta) {


        world.step(Gdx.graphics.getDeltaTime(), VELOCITY_ITERATIONS, POSITION_ITERATIONS);
        ballSprite.setPosition(ballBody.getPosition().x, ballBody.getPosition().y);

        Gdx.gl.glClearColor(1, 1, 1, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        debugRenderer.render(world, camera.combined);


        /*batch.begin();


        layoutScore.setText(game.font,""+score.getStringScore());
        if(touchCounter >= 1) {

        }

        batch.end();*/


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
        camera.viewportWidth = width;
        camera.viewportHeight = height;
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

