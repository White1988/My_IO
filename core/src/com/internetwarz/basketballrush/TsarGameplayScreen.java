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
import com.internetwarz.basketballrush.utils.LanguagesManager;
import com.internetwarz.basketballrush.utils.Score;

import static com.internetwarz.basketballrush.Constants.EASY_MODE;
import static com.internetwarz.basketballrush.Constants.HARD_MODE;
import static com.internetwarz.basketballrush.Constants.MEDIUM_MODE;

public class TsarGameplayScreen implements Screen,InputProcessor
{
    private Image topImage;
    private Image topTextImage;
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
    private Texture background;
    private BitmapFont font;
    private Image curLevelImage;
    private Image curAttemptsImage;

    public TsarGameplayScreen(Tsar game, final int numAttempts) {
        this.game = game;
        this.numAttempts = numAttempts;
        curNumAttempts = numAttempts;
        score = new Score(1);

        WIDTH = (float) Gdx.graphics.getWidth();
        HEIGHT = (float) Gdx.graphics.getHeight();
        RADIUS = (WIDTH-WIDTH/10*2)/2;

        stage = new Stage(new FitViewport(WIDTH, HEIGHT));
        stage.clear();

        InputMultiplexer plex = new InputMultiplexer();
        plex.addProcessor(stage);
        plex.addProcessor(this);

        Gdx.input.setInputProcessor(plex);

        //font init
        fontInit();


        //Preferences init
        prefs = Gdx.app.getPreferences("My Preferences");
        clickSound = game.assets.getSound();

        //Add text xIntuition and it's background
        Texture topImageTexture = new Texture("skins/topImage.png");
        topImage = new Image(topImageTexture);
        topImage.setSize(WIDTH + widthPercent(20), HEIGHT/8 + heightPercent(7));
        topImage.setPosition(0 - widthPercent(10), HEIGHT - HEIGHT/8);
        stage.addActor(topImage);

        Texture topText = new Texture("skins/topText.png");
        topTextImage = new Image(topText);
        topTextImage.setSize(WIDTH - (WIDTH/10)*2, HEIGHT/10 - 10);
        topTextImage.setPosition(WIDTH/2 - topTextImage.getWidth()/2, topImage.getY() + HEIGHT/8/8);
        stage.addActor(topTextImage);

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
        pixmap.setColor(Color.valueOf("#abcdff"));
        pixmap.fillCircle((int)RADIUS, (int)RADIUS, (int)RADIUS);
        circleTexture = new Texture(pixmap);
        pixmap.dispose();
        imageCircle = new Image(circleTexture);
        imageCircle.setPosition(WIDTH/2 - RADIUS, curLevelImage.getY()/2 - RADIUS);
        stage.addActor(imageCircle);


        drawLines(numSectors);

    }

    private void fontInit() {
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/Attractive-Regular.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 20;
        parameter.color= Color.valueOf("#4f6676");
        //parameter.shadowColor = Color.valueOf("#141a1e");
        //parameter.shadowOffsetX = -1;
        //parameter.shadowOffsetY = -2;
        parameter.characters = FreeTypeFontGenerator.DEFAULT_CHARS + "абвгдеёжзийклмнопрстуфхцчшщъыьэюяАБВГДЕЁЖЗИЙКЛМНОПРСТУФХЦЧШЩЪЫЬЭЮЯ";
        font=generator.generateFont(parameter);
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

        rect = new Pixmap((int)WIDTH, (int)HEIGHT, Pixmap.Format.RGBA8888);
        rect.setColor(Color.valueOf("#15091e"));
        rect.fillRectangle(0, 0, (int)WIDTH, (int) HEIGHT);
        background = new Texture(rect);
        rect.dispose();


        //Buttons init
        Texture buttonTexture = new Texture("skins/buttonPlay.png");
        buttonAtlas = game.assets.getButtonAtlas();
        buttonAtlas.addRegion("Button", button, 0, 0, 40, 20);
        buttonAtlas.addRegion("Button checked", buttonChecked, 0, 0, 40, 20);
        buttonTexture = new Texture("skins/easyLevel.png");
        buttonAtlas.addRegion("easyLevel", buttonTexture, 0, 0, buttonTexture.getWidth(), buttonTexture.getHeight());
        buttonTexture = new Texture("skins/easyLevelClick.png");
        buttonAtlas.addRegion("easyLevelClick", buttonTexture, 0, 0, buttonTexture.getWidth(), buttonTexture.getHeight());
        buttonTexture = new Texture("skins/mediumLevel.png");
        buttonAtlas.addRegion("mediumLevel", buttonTexture, 0, 0, buttonTexture.getWidth(), buttonTexture.getHeight());
        buttonTexture = new Texture("skins/mediumLevelClick.png");
        buttonAtlas.addRegion("mediumLevelClick", buttonTexture, 0, 0, buttonTexture.getWidth(), buttonTexture.getHeight());
        buttonTexture = new Texture("skins/hardLevel.png");
        buttonAtlas.addRegion("hardLevel", buttonTexture, 0, 0, buttonTexture.getWidth(), buttonTexture.getHeight());
        buttonTexture = new Texture("skins/hardLevelClick.png");
        buttonAtlas.addRegion("hardLevelClick", buttonTexture, 0, 0, buttonTexture.getWidth(), buttonTexture.getHeight());

        buttonSkin = new Skin();
        buttonSkin.addRegions(buttonAtlas);

        textButtonStyle = new TextButton.TextButtonStyle();
        textButtonStyle.font = font;
        textButtonStyle.up = buttonSkin.getDrawable("easyLevel");
        textButtonStyle.checked = buttonSkin.getDrawable("easyLevelClick");

        easyButton = new TextButton(LanguagesManager.getInstance().getString("easy"), textButtonStyle);
        easyButton.setSize((WIDTH - WIDTH/10*2)/3, heightPercent(7));
        easyButton.setPosition(WIDTH/10,topImage.getY()  - easyButton.getHeight() - HEIGHT/17);
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
                    game.setScreen(new TsarGameplayScreen(game, Constants.ATTEMPTS_IN_GAMEMODE.get(EASY_MODE)));
                    //restartGame(Constants.ATTEMPTS_IN_GAMEMODE.get(EASY_MODE));
                }
            }

        });
        stage.addActor(easyButton);

        textButtonStyle = new TextButton.TextButtonStyle();
        textButtonStyle.font = font;
        textButtonStyle.up = buttonSkin.getDrawable("mediumLevel");
        textButtonStyle.checked = buttonSkin.getDrawable("mediumLevelClick");

        mediumButton = new TextButton(LanguagesManager.getInstance().getString("medium"), textButtonStyle);
        mediumButton.setSize(easyButton.getWidth(), heightPercent(7));
        mediumButton.setPosition(easyButton.getX() + easyButton.getWidth(), topImage.getY()  - easyButton.getHeight() - HEIGHT/17);
        mediumButton.addListener(new ClickListener(){
            public void clicked(InputEvent event, float x, float y){
                if(!isGameBegan) {
                    if (prefs.getBoolean("soundOn", true))
                        clickSound.play();
                    easyButton.setChecked(false);
                    hardButton.setChecked(false);
                }
                else {
                   //restartGame(Constants.ATTEMPTS_IN_GAMEMODE.get(Constants.MEDIUM_MODE));
                    game.setScreen(new TsarGameplayScreen(game, Constants.ATTEMPTS_IN_GAMEMODE.get(MEDIUM_MODE)));

                }
            }
        });
        stage.addActor(mediumButton);

        textButtonStyle = new TextButton.TextButtonStyle();
        textButtonStyle.font = font;
        textButtonStyle.up = buttonSkin.getDrawable("hardLevel");
        textButtonStyle.checked = buttonSkin.getDrawable("hardLevelClick");

        hardButton = new TextButton(LanguagesManager.getInstance().getString("hard"), textButtonStyle);
        hardButton.setSize(easyButton.getWidth(), heightPercent(7));
        hardButton.setPosition(mediumButton.getX() + mediumButton.getWidth(), topImage.getY()  - easyButton.getHeight() - HEIGHT/17);
        hardButton.addListener(new ClickListener(){
            public void clicked(InputEvent event, float x, float y){
                if(!isGameBegan) {
                    if (prefs.getBoolean("soundOn", true))
                        clickSound.play();
                    mediumButton.setChecked(false);
                    easyButton.setChecked(false);
                }
                else {
                    //restartGame(Constants.ATTEMPTS_IN_GAMEMODE.get(Constants.HARD_MODE));
                    game.setScreen(new TsarGameplayScreen(game, Constants.ATTEMPTS_IN_GAMEMODE.get(HARD_MODE)));
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
        //Generate font
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/Attractive-Regular.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 15;
        parameter.characters = FreeTypeFontGenerator.DEFAULT_CHARS + "абвгдеёжзийклмнопрстуфхцчшщъыьэюяАБВГДЕЁЖЗИЙКЛМНОПРСТУФХЦЧШЩЪЫЬЭЮЯ";
        BitmapFont font = generator.generateFont(parameter);
        Label.LabelStyle textStyle = new Label.LabelStyle(font, Color.WHITE);

        Dialog restartConfirmDialog = new Dialog("Restart", new Skin(Gdx.files.internal("skins/uiskin.json")));
        TextButton.TextButtonStyle textButtonStyle1 = textButtonStyle;
        textButtonStyle1.down = buttonSkin.getDrawable("Button checked");
        textButtonStyle1.checked = null;
        TextButton yes = new TextButton(LanguagesManager.getInstance().getString("yes"), textButtonStyle1);
        yes.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new TsarGameplayScreen(game, numAttempts));
            }
        });
        TextButton no = new TextButton(LanguagesManager.getInstance().getString("no"), textButtonStyle1);




        restartConfirmDialog.text(LanguagesManager.getInstance().getString("restartText"), textStyle);
        restartConfirmDialog.button(yes);
        restartConfirmDialog.button(no);

        restartConfirmDialog.show(stage);
        restartConfirmDialog.setSize(widthPercent(40),heightPercent(30));
        restartConfirmDialog.setPosition(WIDTH/2 - restartConfirmDialog.getWidth()/2, HEIGHT/2 - restartConfirmDialog.getHeight()/2);
    }

    private void labelsInit() {
        Texture labelBackgroundTexture = new Texture("skins/currentLevel.png");
        curLevelImage = new Image(labelBackgroundTexture);
        curLevelImage.setSize(WIDTH/10 * 4 - WIDTH/20, HEIGHT/17);
        curLevelImage.setPosition(WIDTH/10, easyButton.getY() - easyButton.getHeight() - HEIGHT/18);
        stage.addActor(curLevelImage);

        labelBackgroundTexture = new Texture("skins/currentLevel.png");
        curAttemptsImage = new Image(labelBackgroundTexture);
        curAttemptsImage.setSize(WIDTH/10 * 4 - WIDTH/20, HEIGHT/17);
        curAttemptsImage.setPosition(WIDTH - curAttemptsImage.getWidth() - WIDTH/10, curLevelImage.getY());
        stage.addActor(curAttemptsImage);

        layoutScore = new GlyphLayout();
        layoutTries = new GlyphLayout();

        //Labels init
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/Attractive-Regular.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 30;
        parameter.color= Color.GREEN;
        parameter.characters = FreeTypeFontGenerator.DEFAULT_CHARS + "абвгдеёжзийклмнопрстуфхцчшщъыьэюяАБВГДЕЁЖЗИЙКЛМНОПРСТУФХЦЧШЩЪЫЬЭЮЯ";
        BitmapFont font = generator.generateFont(parameter);
        rightStyle = new Label.LabelStyle(font, Color.GREEN);
        parameter.color = Color.RED;
        font = generator.generateFont(parameter);
        wrongStyle = new Label.LabelStyle(font, Color.RED);

        parameter.size = 18;
        parameter.color = Color.valueOf("#bed5f6");
        font = generator.generateFont(parameter);
        textStyle = new Label.LabelStyle();
        textStyle.font = font;

        layoutScore.setText(font, LanguagesManager.getInstance().getString("level") + ": " + score.getStringScore());
        layoutTries.setText(font, LanguagesManager.getInstance().getString("tries") + ": " + curNumAttempts);

        scoreLabel = new Label(LanguagesManager.getInstance().getString("level") + ": 0", textStyle);
        scoreLabel.setPosition(curLevelImage.getX() + WIDTH/40, curLevelImage.getY() + curLevelImage.getHeight()/3);
        scoreLabel.setSize(layoutScore.width, layoutScore.height);
        scoreLabel.setFontScale(1f, 1f);


        triesLabel = new Label(LanguagesManager.getInstance().getString("tries") + ": " + numAttempts, textStyle);
        triesLabel.setPosition(curAttemptsImage.getX() + WIDTH/40, scoreLabel.getY());
        triesLabel.setSize(layoutTries.width, layoutTries.height);

        rightWrongLabel = new Label(LanguagesManager.getInstance().getString("right"), rightStyle);
        rightWrongLabel = new Label(LanguagesManager.getInstance().getString("wrong"), wrongStyle);
        rightWrongLabel.setVisible(false);
        rightWrongLabel.setPosition(WIDTH/2 - rightWrongLabel.getWidth()/2, HEIGHT/2 - RADIUS - rightWrongLabel.getHeight() - 4);

        stage.addActor(scoreLabel);
        stage.addActor(triesLabel);
        stage.addActor(rightWrongLabel);
    }


    @Override
    public boolean keyDown(int keycode) {
        if(keycode == Input.Keys.BACKSPACE || keycode == Input.Keys.BACK){
            game.setScreen(new MainMenuScreen(game));
        }
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
               scoreLabel.setText(LanguagesManager.getInstance().getString("level") + ": "  + score.getScore());
               triesLabel.setText(LanguagesManager.getInstance().getString("tries") + ": " + numAttempts);
               rightWrongLabel.setText(LanguagesManager.getInstance().getString("right"));
               rightWrongLabel.setStyle(rightStyle);
               rightWrongLabel.setVisible(true);
               //rightWrongLabel.setPosition(WIDTH/2 - rightWrongLabel.getWidth()/2, HEIGHT/2 - RADIUS - rightWrongLabel.getHeight() - 4);
               rightWrongLabel.setPosition(imageCircle.getX() + imageCircle.getWidth()/2 - rightWrongLabel.getPrefWidth()/2 + 8, imageCircle.getY() - rightWrongLabel.getHeight() - 4);
               //numSectors++;
               isDrawLines = true;
           }
           else {
               isShow = true;
               setFillingParametrs(Color.RED, pickedSector);
               System.out.println("LOSE");
               curNumAttempts--;
               triesLabel.setText(LanguagesManager.getInstance().getString("tries") + ": " + curNumAttempts);
               rightWrongLabel.setText(LanguagesManager.getInstance().getString("wrong"));
               rightWrongLabel.setStyle(wrongStyle);
               rightWrongLabel.setVisible(true);
               //rightWrongLabel.setPosition(WIDTH/2 - rightWrongLabel.getWidth()/2, HEIGHT/2 - RADIUS - rightWrongLabel.getHeight() - 4);
               rightWrongLabel.setPosition(imageCircle.getX() + imageCircle.getWidth()/2 - rightWrongLabel.getWidth()/2, imageCircle.getY() - rightWrongLabel.getHeight() - 4);
               isGuessed = false;
           }

           if(curNumAttempts == 0) {
               String gameType;
               if(numAttempts == 1)
                   gameType = EASY_MODE;
               else if(numAttempts == 2)
                   gameType = MEDIUM_MODE;
               else
                   gameType = HARD_MODE;
               gameOver(game, score, gameType, numAttempts);
           }

       }
       return false;
    }

    private void gameOver(Tsar game, Score score, String gameType, int numAttempts) {
        isGameBegan = false;
        rightWrongLabel.setText(LanguagesManager.getInstance().getString("gameOver"));
        rightWrongLabel.setX(WIDTH/2 - rightWrongLabel.getPrefWidth()/2);
        saveScore(score, gameType);

        //Updating the circle
        stage.getActors().removeRange(stage.getActors().size - numSectors, stage.getActors().size - 1 );
        numSectors = 2;
        isGuessed = true;
        drawLines(numSectors);
        isDrawLines = false;
        this.score = new Score(1);

        if (easyButton.isChecked()) {
            curNumAttempts = numAttempts = 3;
        } else if (mediumButton.isChecked())
            curNumAttempts = numAttempts = 2;
        else if (hardButton.isChecked())
            curNumAttempts = numAttempts = 1;
        scoreLabel.setText(LanguagesManager.getInstance().getString("level") + ": " + this.score.getScore());
        triesLabel.setText(LanguagesManager.getInstance().getString("tries") + ": " + numAttempts);
    }

    private void saveScore(Score score, String gameType) {
        //TODO: add saving data
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

        //update data
        if(!isGameBegan) {
            if (easyButton.isChecked()) {
                curNumAttempts = numAttempts = 3;
            } else if (mediumButton.isChecked())
                curNumAttempts = numAttempts = 2;
            else if (hardButton.isChecked())
                curNumAttempts = numAttempts = 1;
            scoreLabel.setText(LanguagesManager.getInstance().getString("level") + ": " + score.getScore());
            triesLabel.setText(LanguagesManager.getInstance().getString("tries") + ": " + numAttempts);
        }
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

        camera.update();
        batch.setProjectionMatrix(camera.combined);

        stage.act();
        batch.begin();
        stage.getBatch().begin();
        stage.getBatch().draw(background, 0, 0);//drawing background
        stage.getBatch().end();
        stage.draw();
        batch.end();

        if(isShow) {
            shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
            shapeRenderer.setColor(fillColor);
            shapeRenderer.arc(imageCircle.getX() + imageCircle.getWidth() / 2, imageCircle.getY() + imageCircle.getHeight() / 2, RADIUS + 0.2f, -startFilling, degrees);
            shapeRenderer.end();
        }


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
        game.font.draw(batch,LanguagesManager.getInstance().getString("level") + ": " + score.getStringScore(),WIDTH-layoutScore.width - 4, HEIGHT - layoutScore.height);
        game.font.draw(batch,LanguagesManager.getInstance().getString("tries") + ": " + curNumAttempts,4 , HEIGHT - layoutTries.height);
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
