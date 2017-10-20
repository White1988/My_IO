package com.internetwarz.basketballrush;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.internetwarz.basketballrush.utils.GameUtils;
import com.internetwarz.basketballrush.utils.Score;
import com.internetwarz.basketballrush.utils.SimpleDirectionGestureDetector;

public class FRVR implements Screen,InputProcessor{
    final BasketBallRush game;
    final float appWidth = 768;
    final float appHeight = 1280;
    SpriteBatch batch;
    OrthographicCamera camera;

    GlyphLayout layoutScore;

    //setting values on touch
    private static int gameSpeed;
    private static int touchCounter;

    //private static int blueballs;
   // private static int greenballs;

    Score score;
    private GameUtils gameutils;

    private String touchImage;
    private String startMessage;
    private String gameType;

    //setting variables to store two different color balls and the net
    private Texture targetNet;     //Contains net image resource
    private Texture playerBallTexture;       //Contains ball 1 resource
 //   private Texture gameDotImage1;

    private Rectangle playerDotRectangle;
  //  private Array<GameBallValues> gameDot;

    //storing the time of last dot in nano seconds
    private long lastDotTime;


    //Code to show the tutorial animation
    private static final int        FRAME_COLS = 6;         // #1
    private static final int        FRAME_ROWS = 4;         // #2

    Animation walkAnimation;          // #3
    Texture walkSheet;              // #4
    TextureRegion[]                 walkFrames;             // #5
    TextureRegion                   currentFrame;           // #7

    float stateTime;                                        // #8

    public FRVR(final BasketBallRush gam){
        this.game = gam;
        camera = new OrthographicCamera();
        camera.setToOrtho(false, appWidth, appHeight);

        batch = new SpriteBatch();
        batch.setProjectionMatrix(camera.combined);

        layoutScore = new GlyphLayout();

      //  blueballs =0;
      //  greenballs =0;

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

        gameType="FRVR";
        gameSpeed = 600;
        touchCounter =0;
        gameutils = new GameUtils();
        score = new Score(0);
        //touchImage = "image1";
        //startMessage = "Swipe";

        //loading the images in the variables
        targetNet = game.assets.getTexture("net_frvr");
        playerBallTexture = game.assets.getTexture("ball_frvr");
        //gameDotImage1 = game.assets.getTexture("ball2");

        //placing the player dot in the middle of the screen
        playerDotRectangle = new Rectangle(appWidth/2 - targetNet.getWidth()/2,20, targetNet.getWidth(), targetNet.getHeight());

       // gameDot = new Array<GameBallValues>();


        //animation code initialization
        walkSheet = game.assets.getTexture("two"); // #9
        TextureRegion[][] tmp = TextureRegion.split(walkSheet, walkSheet.getWidth()/FRAME_COLS, walkSheet.getHeight()/FRAME_ROWS);              // #10
        walkFrames = new TextureRegion[FRAME_COLS * FRAME_ROWS];
        int index = 0;
        for (int i = 0; i < FRAME_ROWS; i++) {
            for (int j = 0; j < FRAME_COLS; j++) {
                walkFrames[index++] = tmp[i][j];
            }
        }
        walkAnimation = new Animation(0.083f, walkFrames);      // #11
        stateTime = 0f;                         // #13
    }

    /*private void populateDots(){
        Rectangle dots = new Rectangle();
        dots.x = gameutils.randomLocation((int)appWidth);
        dots.y = appHeight;
        dots.width = playerBallTexture.getWidth();
        dots.height = playerBallTexture.getHeight();

        Random rand = new Random();
        int  n = rand.nextInt(2) + 1;
        GameBallValues g;
        if(n==1){
            g = new GameBallValues(dots,gameDotImage1,"image1");
        }
        else{
            g = new GameBallValues(dots, playerBallTexture,"image");
        }
        gameDot.add(g);

        lastDotTime = TimeUtils.nanoTime();
    }*/

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(1, 1, 1, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        stateTime += Gdx.graphics.getDeltaTime();           // #15
        currentFrame = walkAnimation.getKeyFrame(stateTime, true);  // #16

        batch.begin();

        //Drawing the net image
        batch.draw(targetNet, playerDotRectangle.x, playerDotRectangle.y, playerDotRectangle.width, playerDotRectangle.height);

        layoutScore.setText(game.font,""+score.getStringScore());
        if(touchCounter >= 1) {
            /*for(GameBallValues gameBallValues : gameDot){
                game.font.draw(batch,score.getStringScore(),appWidth/2-layoutScore.width/2,90*(appHeight/100));
                batch.draw(gameBallValues.getTexture(), gameBallValues.getRectangle().x, gameBallValues.getRectangle().y);
            }*/
        }
        else{
            game.font.draw(batch,startMessage,appWidth/2-startMessage.length()*40/2,appHeight/2);
            batch.draw(currentFrame, appWidth/2-currentFrame.getRegionWidth()/2, 50);             // #17
        }

        batch.end();


    }

    @Override
    public void dispose() {
        batch.dispose();
        game.dispose();
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
    public boolean keyDown(int keycode) {
        if(keycode == Input.Keys.BACK){
            game.setScreen(new GameModeSelect(game));
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
}

