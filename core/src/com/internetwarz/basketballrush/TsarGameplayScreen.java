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
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.internetwarz.basketballrush.utils.Score;

public class TsarGameplayScreen implements Screen,InputProcessor
{
    private Tsar game;
    private boolean isGameBegan = false;
    SpriteBatch batch;
    OrthographicCamera camera;
    private static int VIEWPORT_SCALE = 1;
    Box2DDebugRenderer debugRenderer = new Box2DDebugRenderer();

    GlyphLayout layoutScore;
    GlyphLayout layoutTries;

    private float WIDTH;
    private float HEIGHT;
    private float RADIUS;

   // http://www.coding-daddy.xyz/node/23
    ShapeRenderer shapeRenderer;

    //Line
    Texture lineTexture;
    Sprite lineSprite;
    Image line;

    //Circle
    Pixmap pixmap;//for circle
    Texture circleTexture;
    Image imageCircle;


    //Labels
    Label scoreLabel;
    Label.LabelStyle textStyle;
    Label triesLabel;
    Label rightWrongLabel;
    Label.LabelStyle rightStyle;
    Label.LabelStyle wrongStyle;

    //actual circle to check collisions with
    Circle playerCircle;

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
    //private ImageButton easyButton,mediumButton,hardButton;
    private Skin buttonSkin;
    private TextureAtlas buttonAtlas;

    Sound clickSound;
    Preferences prefs;


    TextButton easyButton;
    TextButton mediumButton;
    TextButton hardButton;
    TextButton.TextButtonStyle textButtonStyle;

    boolean isDrawLines = false;

    //Filling sector
    private boolean isShow = false;
    private Color fillColor = new Color();
    private float degrees = 0;
    private float startFilling = 0;

    public TsarGameplayScreen(Tsar game, final int numAttempts) {
        this.game = game;
        this.numAttempts = numAttempts;
        curNumAttempts = numAttempts;
        score = new Score(0);

        WIDTH = (float) Gdx.graphics.getWidth();
        HEIGHT = (float) Gdx.graphics.getHeight();
        RADIUS = heightPercent(30);

        stage = new Stage(new FitViewport(WIDTH, HEIGHT));
        stage.clear();

        InputMultiplexer plex = new InputMultiplexer();
        plex.addProcessor(stage);
        plex.addProcessor(this);

        Gdx.input.setInputProcessor(plex);


        //Preferences init
        prefs = Gdx.app.getPreferences("My Preferences");
        clickSound = game.assets.getSound();

        buttonsInit();

        camera = new OrthographicCamera();
        camera.setToOrtho(false, WIDTH/ VIEWPORT_SCALE, HEIGHT / VIEWPORT_SCALE);

        playerCircle = new Circle(WIDTH/2, HEIGHT /2, RADIUS);

        shapeRenderer = new ShapeRenderer(15000); //increase smoothness of circle

        batch = new SpriteBatch();
        batch.setProjectionMatrix(camera.combined);

        labelsInit();

        //Line init
        lineTexture = new Texture(Gdx.files.internal("images/line_txtr.png"));
        lineSprite = new Sprite(lineTexture);
        lineSprite.setSize(1, RADIUS);

        //Line init
        line = new Image(lineTexture);
        line.setSize(1, RADIUS);

        pixmap = new Pixmap((int)RADIUS*2 + 1, (int)RADIUS*2 + 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(Color.valueOf("#7b7b7b"));
        pixmap.drawCircle((int)RADIUS, (int)RADIUS, (int)RADIUS);
        circleTexture = new Texture(pixmap);
        pixmap.dispose();
        imageCircle = new Image(circleTexture);
        imageCircle.setPosition(WIDTH/2 - RADIUS, (HEIGHT - easyButton.getHeight())/2 - RADIUS);
        stage.addActor(imageCircle);


        drawLines(numSectors);

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
                if(!isGameBegan) {
                    if (prefs.getBoolean("soundOn", true))
                        clickSound.play();
                    System.out.println("easy clicked!");
                    mediumButton.setChecked(false);
                    hardButton.setChecked(false);
                }
                else {
                    restartGame(3);
                }
            }

        });
        stage.addActor(easyButton);

        mediumButton = new TextButton("Medium", textButtonStyle);
        mediumButton.setSize(widthPercent(30), heightPercent(10));
        mediumButton.setPosition(widthPercent(5) + easyButton.getWidth(), HEIGHT - mediumButton.getHeight() - 10);
        mediumButton.addListener(new ClickListener(){
            public void clicked(InputEvent event, float x, float y){
                if(!isGameBegan) {
                    if (prefs.getBoolean("soundOn", true))
                        clickSound.play();
                    easyButton.setChecked(false);
                    hardButton.setChecked(false);
                }
                else {
                   restartGame(2);
                }
            }
        });
        stage.addActor(mediumButton);

        hardButton = new TextButton("Hard", textButtonStyle);
        hardButton.setSize(widthPercent(30), heightPercent(10));
        hardButton.setPosition(mediumButton.getX() + mediumButton.getWidth(), HEIGHT - hardButton.getHeight() - 10);
        hardButton.addListener(new ClickListener(){
            public void clicked(InputEvent event, float x, float y){
                if(!isGameBegan) {
                    if (prefs.getBoolean("soundOn", true))
                        clickSound.play();
                    mediumButton.setChecked(false);
                    easyButton.setChecked(false);
                }
                else {
                    restartGame(1);
                }
            }
        });
        stage.addActor(hardButton);

        if(numAttempts == 3)
            easyButton.setChecked(true);
        else if(numAttempts == 2)
            mediumButton.setChecked(true);
        else if(numAttempts == 1)
            hardButton.setChecked(true);
    }

    private void restartGame(final int numAttempts) {
        Dialog restartConfirmDialog = new Dialog("Restart dialog", new Skin(Gdx.files.internal("skins/uiskin.json")));
        TextButton.TextButtonStyle textButtonStyle1 = textButtonStyle;
        textButtonStyle1.down = buttonSkin.getDrawable("Button checked");
        textButtonStyle1.checked = null;
        TextButton yes = new TextButton("Yes", textButtonStyle1);
        yes.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new TsarGameplayScreen(game, numAttempts));
            }
        });
        TextButton no = new TextButton("No", textButtonStyle1);
        restartConfirmDialog.text("Do you really want to restart the game?");
        restartConfirmDialog.button(yes);
        restartConfirmDialog.button(no);

        restartConfirmDialog.show(stage);
        restartConfirmDialog.setSize(widthPercent(40),heightPercent(30));
        restartConfirmDialog.setPosition(WIDTH/2 - restartConfirmDialog.getWidth()/2, HEIGHT/2 - restartConfirmDialog.getHeight()/2);
    }

    private void labelsInit() {

        layoutScore = new GlyphLayout();
        layoutTries = new GlyphLayout();

        //Labels init
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/Quicksand-Bold.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 30;
        parameter.color= Color.GREEN;
        BitmapFont font = generator.generateFont(parameter);
        rightStyle = new Label.LabelStyle(font, Color.GREEN);
        parameter.color = Color.RED;
        font = generator.generateFont(parameter);
        wrongStyle = new Label.LabelStyle(font, Color.RED);

        parameter.size = 30;
        parameter.color = Color.BLACK;
        font = generator.generateFont(parameter);
        textStyle = new Label.LabelStyle();
        textStyle.font = font;

        layoutScore.setText(font, "Score: " + score.getStringScore());
        layoutTries.setText(font, "Tries : " + curNumAttempts);

        scoreLabel = new Label("Score: 0", textStyle);
        scoreLabel.setPosition(widthPercent(5), easyButton.getY() - layoutScore.height - widthPercent(5));
        scoreLabel.setSize(layoutScore.width, layoutScore.height);
        scoreLabel.setFontScale(1f, 1f);

        triesLabel = new Label("Tries: " + numAttempts, textStyle);
        triesLabel.setPosition(WIDTH - layoutTries.width - widthPercent(5), hardButton.getY() - layoutTries.height - widthPercent(5));
        triesLabel.setSize(layoutTries.width, layoutTries.height);

        rightWrongLabel = new Label("Right!", rightStyle);
        rightWrongLabel = new Label("Wrong!", wrongStyle);
        rightWrongLabel.setVisible(false);
        rightWrongLabel.setPosition(WIDTH/2 - rightWrongLabel.getWidth()/2, HEIGHT/2 - RADIUS - rightWrongLabel.getHeight() - 4);

        stage.addActor(scoreLabel);
        stage.addActor(triesLabel);
        stage.addActor(rightWrongLabel);
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

        playerCircle = new Circle(imageCircle.getX() + imageCircle.getWidth()/2, imageCircle.getY() + imageCircle.getHeight()/2, imageCircle.getHeight()/2);

       if(Intersector.overlaps(playerCircle, new Rectangle(coord.x, coord.y, 1,1)))
       {
           if(isGuessed) {
               randomSector = (int)(Math.random()*(numSectors)+1);
               isGuessed = false;
           }
           isGameBegan = true;

           System.out.println("Overlaps!");
           Vector2 vector1 = new Vector2(coord.y - playerCircle.y, coord.x - playerCircle.x);
           int pickedSector = getCircleSector(numSectors, vector1.angle());
           //double angle = Math. atan2(yAxis.y, yAxis.x) - Math. atan2(vector1.y, vector1.x);
           System.out.println("angle " + vector1.angle());
           System.out.println("sector for " + numSectors + " is " + pickedSector);
           System.out.println("Score: " + score.getScore());
           System.out.println("RIGHT: " + randomSector);
           if(randomSector == pickedSector) {
               isShow = true;
               setFillingParametrs(Color.GREEN, pickedSector);
               System.out.println("WON");
               //stage.getActors().removeRange(stage.getActors().size - numSectors, stage.getActors().size - 1 );
               curNumAttempts = numAttempts;
               isGuessed = true;
               score.setScore(score.getScore() + points);
               scoreLabel.setText("Score: "  + score.getScore());
               triesLabel.setText("Tries: " + numAttempts);
               rightWrongLabel.setText("Right!");
               rightWrongLabel.setStyle(rightStyle);
               rightWrongLabel.setVisible(true);
               //rightWrongLabel.setPosition(WIDTH/2 - rightWrongLabel.getWidth()/2, HEIGHT/2 - RADIUS - rightWrongLabel.getHeight() - 4);
               rightWrongLabel.setPosition(imageCircle.getX() + imageCircle.getWidth()/2 - rightWrongLabel.getWidth()/2 + 8, imageCircle.getY() - rightWrongLabel.getHeight() - 4);
               //numSectors++;
               isDrawLines = true;
           }
           else {
               isShow = true;
               setFillingParametrs(Color.RED, pickedSector);
               System.out.println("LOSE");
               curNumAttempts--;
               triesLabel.setText("Tries: " + curNumAttempts);
               rightWrongLabel.setText("Wrong!");
               rightWrongLabel.setStyle(wrongStyle);
               rightWrongLabel.setVisible(true);
               //rightWrongLabel.setPosition(WIDTH/2 - rightWrongLabel.getWidth()/2, HEIGHT/2 - RADIUS - rightWrongLabel.getHeight() - 4);
               rightWrongLabel.setPosition(imageCircle.getX() + imageCircle.getWidth()/2 - rightWrongLabel.getWidth()/2, imageCircle.getY() - rightWrongLabel.getHeight() - 4);
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
               game.setScreen(new GameEndScreen(game, score, gameType, numAttempts));
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

    private void setFillingParametrs(Color color, int pickedSector) {
        isShow = true;
        fillColor = color;
        float angle = 360.0f / numSectors;
        startFilling = pickedSector * angle - 90;
        degrees = angle;

    }


    private int getCircleSector(int sectorNumbers, float angle)
    {
        float deegreesPerSector = 360/sectorNumbers;

        return (int) (angle / deegreesPerSector)  + 1;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        isShow = false;
        if(isDrawLines) {
            stage.getActors().removeRange(stage.getActors().size - numSectors, stage.getActors().size - 1 );
            numSectors++;
            drawLines(numSectors);
            isDrawLines = false;
        }
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

        if(!isGameBegan) {
            if (easyButton.isChecked()) {
                curNumAttempts = numAttempts = 3;
            } else if (mediumButton.isChecked())
                curNumAttempts = numAttempts = 2;
            else if (hardButton.isChecked())
                curNumAttempts = numAttempts = 1;
            scoreLabel.setText("Score: " + score.getScore());
            triesLabel.setText("Tries: " + numAttempts);
        }
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

        camera.update();
        batch.setProjectionMatrix(camera.combined);

        if(isShow) {
            shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
            shapeRenderer.setColor(fillColor);
            shapeRenderer.arc(imageCircle.getX() + imageCircle.getWidth() / 2, imageCircle.getY() + imageCircle.getHeight() / 2, RADIUS + 0.2f, -startFilling, degrees);
            shapeRenderer.end();
        }

        stage.act();
        batch.begin();
        stage.draw();
        batch.end();
    }

    //Drawing sectors
    private void drawLines(int count) {
        float angle = 360.0f / count;
        for (int i = 0; i < count; i++) {
            line = new Image(lineTexture);
            line.setSize(2, RADIUS);
            //line.setPosition((WIDTH - line.getWidth()) / 2.0f, (HEIGHT - lineSprite.getHeight()) / 2.0f + line.getHeight()/2.0f);
            line.setPosition(imageCircle.getX() + imageCircle.getWidth()/2, imageCircle.getY() + imageCircle.getHeight()/2);
            line.setOrigin(line.getWidth()/2, 0);
            line.setRotation(-i*angle);
            stage.addActor(line);
        }
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
        batch.dispose();
        lineTexture.dispose();
        buttonSkin.dispose();
    }
}
