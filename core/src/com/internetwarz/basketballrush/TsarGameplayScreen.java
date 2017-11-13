package com.internetwarz.basketballrush;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.internetwarz.basketballrush.utils.Score;

public class TsarGameplayScreen implements Screen,InputProcessor
{
    private Tsar game;
    SpriteBatch batch;
    OrthographicCamera camera;
    private static int VIEWPORT_SCALE = 1;
    Box2DDebugRenderer debugRenderer = new Box2DDebugRenderer();

    GlyphLayout layoutScore;
    GlyphLayout layoutTries;

    private float WIDTH;
    private float HEIGHT;
    private float RADIUS = 100;

   // http://www.coding-daddy.xyz/node/23
    ShapeRenderer shapeRenderer;

    //Line
    Texture lineTexture;
    Sprite lineSprite;
    Image line;

    //actual circle to check collisions with
    Circle playerCircle;
    // todo  how to determine is screen touch coordinates within certain shape?
    // todo  find out is there any clickable stuff like buttons
    // todo  fill in touched sector with color

    //Game parameters
    public int numAttempts;
    private int curNumAttempts;
    private int numSectors = 2;
    private boolean isGuessed = true;
    private int randomSector;
    private Score score;
    private int points = 1;

    //Buttons
    private Stage stage;
    private ImageButton easyButton,mediumButton,hardButton;
    private Skin buttonSkin;
    private TextureAtlas buttonAtlas;

    Sound clickSound;
    Preferences prefs;


    TextButton textButton;
    TextButton.TextButtonStyle textButtonStyle;

    public TsarGameplayScreen(Tsar game, final int numAttempts) {
        this.game = game;
        this.numAttempts = numAttempts;
        curNumAttempts = numAttempts;
        score = new Score(0);

        WIDTH = (float) Gdx.graphics.getWidth();
        HEIGHT = (float) Gdx.graphics.getHeight();

        stage = new Stage(new FitViewport(WIDTH, HEIGHT));
        stage.clear();

        InputMultiplexer plex = new InputMultiplexer();
        plex.addProcessor(stage);
        plex.addProcessor(this);

        Gdx.input.setInputProcessor(plex);
        Gdx.input.setInputProcessor(this);


        //init stage
        //stage = new Stage(new FitViewport(WIDTH,HEIGHT));
       // stage.clear();


        //Preferences init
        prefs = Gdx.app.getPreferences("My Preferences");
        clickSound = game.assets.getSound();

        //Buttons init
        buttonAtlas = game.assets.getButtonAtlas();
        buttonSkin = new Skin();
        buttonSkin.addRegions(buttonAtlas);

        textButtonStyle = new TextButton.TextButtonStyle();
        textButtonStyle.font = game.font;
        textButtonStyle.up = buttonSkin.getDrawable("easyClicked");
        textButtonStyle.down = buttonSkin.getDrawable("easy");
        textButton = new TextButton("Button1", textButtonStyle);
        textButton.setPosition(WIDTH/2, textButton.getHeight());
        //stage.addActor(textButton);

        easyButton = new ImageButton(buttonSkin.getDrawable("easy"),buttonSkin.getDrawable("easyClicked"));
        mediumButton = new ImageButton(buttonSkin.getDrawable("medium"),buttonSkin.getDrawable("mediumClicked"));
        hardButton = new ImageButton(buttonSkin.getDrawable("hard"),buttonSkin.getDrawable("hardClicked"));


        easyButton.setPosition(WIDTH/2 - mediumButton.getWidth()/2 - easyButton.getWidth(), HEIGHT - easyButton.getHeight());
        easyButton.addListener(new ClickListener(){
            public void clicked(InputEvent event, float x, float y){
                if(prefs.getBoolean("soundOn",true))
                    clickSound.play();
                System.out.println("easy clicked!");

            }
        });
       // stage.addActor(easyButton);


        //Medium Button resources

        mediumButton.setPosition(WIDTH/2 - mediumButton.getWidth()/2, HEIGHT - mediumButton.getHeight());
        mediumButton.addListener(new ClickListener(){
            public void clicked(InputEvent event, float x, float y){
                if(prefs.getBoolean("soundOn",true))
                    clickSound.play();
            }
        });
        //stage.addActor(mediumButton);

        //Hard Button resources

        hardButton.setPosition(WIDTH/2 + mediumButton.getWidth()/2, HEIGHT - hardButton.getHeight());
        hardButton.addListener(new ClickListener(){
            public void clicked(InputEvent event, float x, float y){
                if(prefs.getBoolean("soundOn",true))
                    clickSound.play();
            }
        });
        //stage.addActor(hardButton);

        //


        camera = new OrthographicCamera();
        camera.setToOrtho(false, WIDTH/ VIEWPORT_SCALE, HEIGHT / VIEWPORT_SCALE);

        playerCircle = new Circle(WIDTH/2, HEIGHT /2, RADIUS);

        batch = new SpriteBatch();
        batch.setProjectionMatrix(camera.combined);
        layoutScore = new GlyphLayout();
        layoutTries = new GlyphLayout();
        shapeRenderer = new ShapeRenderer(15000); //increase smoothness of circle

        //Line init
        lineTexture = new Texture(Gdx.files.internal("images/line_txtr.png"));
        lineSprite = new Sprite(lineTexture);
        lineSprite.setSize(1, RADIUS);

        line = new Image(lineTexture);
        line.setSize(1, RADIUS);



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
           if(isGuessed) {
               randomSector = (int)(Math.random()*(numSectors)+1);
               isGuessed = false;
           }

           System.out.println("Overlaps!");
           Vector2 vector1 = new Vector2(coord.y - playerCircle.y, coord.x - playerCircle.x);
           int pickedSector = getCircleSector(numSectors, vector1.angle());
           //double angle = Math. atan2(yAxis.y, yAxis.x) - Math. atan2(vector1.y, vector1.x);
           System.out.println("angle " + vector1.angle());
           System.out.println("sector for " + numSectors + " is " + pickedSector);
           System.out.println("Score: " + score.getScore());
           if(randomSector == pickedSector) {
               System.out.println("WON");
               curNumAttempts = numAttempts;
               isGuessed = true;
               score.setScore(score.getScore() + points);
               numSectors++;
           }
           else {
               System.out.println("LOSE");
               curNumAttempts--;
               isGuessed = false;
           }

           if(curNumAttempts == 0) {
               String gameType;
               if(numSectors == 1)
                   gameType = "Easy";
               else if(numSectors == 2)
                   gameType = "Medium";
               else
                   gameType = "Hard";
               game.setScreen(new GameEndScreen(game, score, gameType));
           }
           /*System.out.println("sector for 2 is " + getCircleSector(2, vector1.angle()));
           System.out.println("sector for 3 is " + getCircleSector(3, vector1.angle()));
           System.out.println("sector for 4 is " + getCircleSector(4, vector1.angle()));
           System.out.println("sector for 5 is " + getCircleSector(5, vector1.angle()));
           System.out.println("sector for 6 is " + getCircleSector(6, vector1.angle()));
           System.out.println("sector for 7 is " + getCircleSector(7, vector1.angle()));
           System.out.println("sector for 8 is " + getCircleSector(8, vector1.angle()));
           System.out.println("sector for 9 is " + getCircleSector(9, vector1.angle()));*/
           //vector1.angle()
       }




        return false;
    }




    private int getCircleSector(int sectorNumbers, float angle)
    {
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

        //Drawing lines
        drawLines(numSectors);

        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(Color.RED);
        shapeRenderer.circle(WIDTH/2, HEIGHT /2,RADIUS);
        shapeRenderer.end();


        layoutScore.setText(game.font, "Score: " + score.getStringScore());
        layoutTries.setText(game.font, "Tries left: " + curNumAttempts);
        batch.begin();
        game.font.draw(batch,"Score: " + score.getStringScore(),WIDTH-layoutScore.width - 4, HEIGHT - layoutScore.height);
        game.font.draw(batch,"Tries left: " + curNumAttempts,4 , HEIGHT - layoutTries.height);
        batch.end();

        //draw buttons
        batch.begin();
        easyButton.draw(batch, 1);
        mediumButton.draw(batch, 1);
        hardButton.draw(batch, 1);
        //textButton.draw(batch, 1);
        batch.end();


        /*stage.act();
        batch.begin();
        stage.draw();
        batch.end();
        stage.getActors().removeRange(stage.getActors().size - numSectors, stage.getActors().size);*/

    }

    //Drawing sectors
    private void drawLines(int count) {
        float angle = 360.0f / count;
        for (int i = 0; i < count; i++) {
            batch.begin();
            batch.draw(lineSprite,
                    (WIDTH - lineSprite.getWidth()) / 2.0f, (HEIGHT - lineSprite.getHeight()) / 2.0f + lineSprite.getHeight()/2.0f,
                    0, 0,
                    lineSprite.getWidth(), lineSprite.getHeight(),
                    1f, 1f,-i * angle, true);
            batch.end();

            /*line = new Image(lineTexture);
            line.setPosition((WIDTH - line.getWidth()) / 2.0f, (HEIGHT - lineSprite.getHeight()) / 2.0f + line.getHeight()/2.0f);
            line.setOrigin(0, 0);
            line.setRotation(-i*angle);
            stage.addActor(line);*/
        }
    }

    @Override
    public void resize(int width, int height) {
        WIDTH=width;
        HEIGHT = height;

        playerCircle = new Circle(WIDTH/2, HEIGHT /2, RADIUS);
        batch.begin();
        game.font.draw(batch,"Score: " + score.getStringScore(),WIDTH-layoutScore.width - 4, HEIGHT - layoutScore.height);
        game.font.draw(batch,"Tries left: " + curNumAttempts,4 , HEIGHT - layoutTries.height);
        batch.end();

        //camera.viewportWidth = width / VIEWPORT_SCALE;
        //camera.viewportHeight = height / VIEWPORT_SCALE;
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
        batch.dispose();
        lineTexture.dispose();
    }
}
