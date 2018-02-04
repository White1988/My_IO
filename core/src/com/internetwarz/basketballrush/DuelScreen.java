package com.internetwarz.basketballrush;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.FillViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.internetwarz.basketballrush.model.PlayerTurn;
import com.internetwarz.basketballrush.utils.LanguagesManager;
import com.internetwarz.basketballrush.utils.Score;


public class DuelScreen implements Screen, InputProcessor{


    private static final String TAG = "DuelScreen";
    private SpriteBatch batch;
    private Texture background;
    private final Xintuition game;
    private static int VIEWPORT_SCALE = 1;
    BitmapFont font;

    private Label label;
    private Label debugLabel1;
    private Label debugLabel5;
    private Label debugLabel2;
    private Label debugLabel3;
    private Label debugLabel4;

    private PlayerTurn lastTurnData ;

    private Label.LabelStyle rightStyle;
    private Label.LabelStyle textStyle;
    private GlyphLayout layout;
    private Stage stage;

    private ShapeRenderer shapeRenderer;
    private OrthographicCamera camera;

    private float WIDTH;
    private float HEIGHT;
    private final float RADIUS;
    private float startFilling = 0;
    private float degrees = 0;

    private boolean isGuessed = true;
    private boolean isGameBegan = false;
    private boolean isShow = false;

    private int randomSector;
    private int numSectors = 10;
    private int curNumAttempts;
    public int numAttempts;
    private int points = 1;

    private Color fillColor = new Color();
    private final Score score;

    private Rectangle rectangle;
    private Rectangle rectangle2;
    private Rectangle rectangle3;

    //for circle
    private Circle playerCircle;
    private Circle innerCircle;
    Pixmap pixmap;
    Texture circleTexture;
    Image imageCircle;
    // todo  check http://www.gamefromscratch.com/post/2014/12/09/LibGDX-Tutorial-Part-17-Viewports.aspx
    Viewport viewport;

    private boolean myTurn = true; // true by default

    public DuelScreen(Xintuition game, PlayerTurn initialData ) {

        this.game = game;
        score = new Score(1);

        camera = new OrthographicCamera();
        viewport = new FillViewport(Gdx.graphics.getWidth(),Gdx.graphics.getHeight(),camera);
        viewport.apply();

        camera.position.set(camera.viewportWidth/2,camera.viewportHeight/2,0);

        WIDTH = camera.viewportWidth;
        HEIGHT = camera.viewportHeight;
        RADIUS = (WIDTH-WIDTH/10*2 - WIDTH/20)/3;

        stage = new Stage(viewport);
        stage.clear();

        InputMultiplexer plex = new InputMultiplexer();
        plex.addProcessor(stage);
        plex.addProcessor(this);

        shapeRenderer = new ShapeRenderer(15000);//increase smoothness of circle

        batch = new SpriteBatch();
        batch.setProjectionMatrix(camera.combined);

      /* pixmap = new Pixmap((int)RADIUS*2 + 1, (int)RADIUS*2 + 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(Color.valueOf("#abcdff"));
        pixmap.fillCircle((int) (WIDTH/2), (int) (HEIGHT/2), (int)RADIUS);
        circleTexture = new Texture(pixmap);
        pixmap.dispose();*/
       // todo we actually dont work with imageCircle now, its used just for coordinates, maybe we'll use it later
        imageCircle = new Image(new Texture(new Pixmap((int)RADIUS*2 + 1, (int)RADIUS*2 + 1, Pixmap.Format.RGBA8888)));
        imageCircle.setPosition(camera.viewportWidth/2,camera.viewportHeight/2);
        //stage.addActor(imageCircle);

        buttonsInit();

        InitLabels();
        Xintuition.getTurnBasedService().coreGameplayCallBacks.addEnemyTurnFinishedCallback(new TurnBasedService.PlayerDataAction() {
            @Override
            public void Action(PlayerTurn param) {
                lastTurnData = param;
                myTurn = true;
                debugLabel5.setText("TOUR TURN");
            }
        });

        if(initialData != null)
        {
            myTurn = initialData.matchStatus == PlayerTurn.MATCH_TURN_STATUS_MY_TURN;
            debugLabel5.setText(myTurn ? "MY TURN" : "Enemy turn");
        }
        else
        {
            Gdx.app.log(TAG, "initialData == null!");
        }

    }
    private void buttonsInit(){
        Pixmap rect = new Pixmap(40,20,Pixmap.Format.RGBA8888);
        rect.setColor(Color.LIGHT_GRAY);
        rect.fillRectangle(0, 0, 40, 20);
        Texture buttonChecked = new Texture(rect);
        rect.setColor(Color.DARK_GRAY);
        rect.drawRectangle(0,0,40,20);
        buttonChecked.draw(rect, 0, 0);
        rect.dispose();
        Gdx.input.setInputProcessor(this);
        rect = new Pixmap((int)WIDTH, (int)HEIGHT, Pixmap.Format.RGBA8888);
        rect.setColor(Color.valueOf("#15091e"));
        rect.fillRectangle(0, 0, (int)WIDTH, (int) HEIGHT);
        background = new Texture(rect);
        rect.dispose();
    }
    private void InitLabels(){


        layout = new GlyphLayout();

        //Labels init
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/Magistral Bold.TTF"));
        if(LanguagesManager.getInstance().getLanguage().equals("KO") || LanguagesManager.getInstance().getLanguage().equals("JA") || LanguagesManager.getInstance().getLanguage().equals("ZH"))
            generator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/DroidSansFallback.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 30;
        parameter.color= Color.GREEN;
        parameter.characters = FreeTypeFontGenerator.DEFAULT_CHARS + "абвгдеёжзийклмнопрстуфхцчшщъыьэюяАБВГДЕЁЖЗИЙКЛМНОПРСТУФХЦЧШЩЪЫЬЭЮЯ";
        if(LanguagesManager.getInstance().getLanguage().equals("KO") || LanguagesManager.getInstance().getLanguage().equals("JA") || LanguagesManager.getInstance().getLanguage().equals("ZH"))
            parameter.characters = FreeTypeFontGenerator.DEFAULT_CHARS + "ただ手段ハード栄誉の殿堂統計遊びますゲームは終わった結果設定ただ手段ハードレベル実験右！間違いました！再起動はいいいえあなたは本当にゲームを再開しますか？ベストプレーヤーゲームは終わった！統計選手栄誉の殿堂ルールログアウト言語"
                    +"명예의 전당통계놀이게임이결과설정다만방법단단한수평실험오른쪽잘못된다시 시작예아니오게임을 다시 시작 하시겠습니까?최고의 선수게임은끝났어!통계(플레이어)명예의 전당규칙로그 아웃언어"
                    +"名人堂統計玩遊戲結束了結果設置只是手段硬水平實驗對錯了重新開始是的沒有你真的想重新啟動遊戲嗎？最好的球員遊戲結束了統計玩家名人堂規則註銷語言";
         font = generator.generateFont(parameter);
        rightStyle = new Label.LabelStyle(font, Color.GREEN);

        parameter.size = 18;
        parameter.color = Color.valueOf("#bed5f6");
        parameter.shadowColor = Color.valueOf("#202123");
        parameter.shadowOffsetX = 1;
        parameter.shadowOffsetY = 1;
        font = generator.generateFont(parameter);
        textStyle = new Label.LabelStyle();
        textStyle.font = font;

        //font for numbers
        generator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/Magistral Bold.TTF"));
        BitmapFont fontForNumbers = generator.generateFont(parameter);
        Label.LabelStyle numbersStyle = new Label.LabelStyle();
        numbersStyle.font = fontForNumbers;

        layout.setText(font, "layout");


        label = new Label( "label ", textStyle);
        label.setPosition(40, 40);
        label.setSize(55, 55);
        label.setFontScale(1f, 1f);



        debugLabel1 = new Label( "label ", textStyle);
        debugLabel1.setPosition(140, 140);
        debugLabel1.setSize(55, 55);
        debugLabel1.setFontScale(1f, 1f);

        debugLabel2 = new Label( "label ", textStyle);
        debugLabel2.setPosition(140, 200);
        debugLabel2.setSize(55, 55);
        debugLabel2.setFontScale(1f, 1f);


        debugLabel3 = new Label( "label ", textStyle);
        debugLabel3.setPosition(140, 250);
        debugLabel3.setSize(55, 55);
        debugLabel3.setFontScale(1f, 1f);

        debugLabel4 = new Label( "label ", textStyle);
        debugLabel4.setPosition(140, 300);
        debugLabel4.setSize(55, 55);
        debugLabel4.setFontScale(1f, 1f);


        debugLabel5 = new Label( "label ", textStyle);
        debugLabel5.setPosition(140, 350);
        debugLabel5.setSize(55, 55);
        debugLabel5.setFontScale(1f, 1f);

        stage.addActor(label);
        stage.addActor(debugLabel1);
        stage.addActor(debugLabel2);
        stage.addActor(debugLabel3);
        stage.addActor(debugLabel4);
        stage.addActor(debugLabel5);


        TextButton.TextButtonStyle textButtonStyle = new TextButton.TextButtonStyle();
        textButtonStyle.font = font;


        TextButton b2 = new TextButton("Leave", textButtonStyle);
        b2.setSize(100, 50);
        b2.setPosition(WIDTH - 100, HEIGHT - 100);
        b2.addListener(new ClickListener(){
            public void clicked(InputEvent event, float x, float y){
                Xintuition.getTurnBasedService().onLeaveClicked();
            }
        });

        TextButton b1 = new TextButton("Finish", textButtonStyle);
        b1.setSize(100, 50);
        b1.setPosition(WIDTH - 100, HEIGHT - 200);
        b1.addListener(new ClickListener(){
            public void clicked(InputEvent event, float x, float y){
                Xintuition.getTurnBasedService().onFinishClicked();
            }
        });


        TextButton b3 = new TextButton("Cancel", textButtonStyle);
        b3.setSize(100, 50);
        b3.setPosition(WIDTH - 100, HEIGHT - 300);
        b3.addListener(new ClickListener(){
            public void clicked(InputEvent event, float x, float y){
                Xintuition.getTurnBasedService().onCancelClicked();
            }
        });

        stage.addActor(b1);
        stage.addActor(b2);
        stage.addActor(b3);


    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        Gdx.gl.glClearColor(0,0,0,1);

        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

        camera.update();
        batch.setProjectionMatrix(camera.combined);

        if(lastTurnData != null)
        {
            debugLabel1.setText("Turn count: " + lastTurnData.turnCounter);
            debugLabel2.setText("Selected number: " + lastTurnData.selectedNumber);
            debugLabel3.setText("player1Score: " + lastTurnData.player1Score);
            debugLabel4.setText("player2Score: " + lastTurnData.player2Score);
        }

        stage.act();
        batch.begin();
        stage.getBatch().begin();
        stage.getBatch().draw(background, 0, 0);//drawing background
        stage.getBatch().end();
        stage.draw();
        batch.end();

        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);//drawing rectangle
        shapeRenderer.setColor(Color.WHITE);
        shapeRenderer.rect(550,10,20,20);
        shapeRenderer.rect(550,40,80,80);
        shapeRenderer.rect(550,100,80,80);
        shapeRenderer.end();

        // drawing zoned circle
        float x= 54;
        float y = 36;

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(Color.BLUE);
        for(int i=0;i<10; i++){
            shapeRenderer.arc(imageCircle.getX() , imageCircle.getY(), imageCircle.getHeight()/2,x,y);
            x+=36;
            y+=36;
        }

        shapeRenderer.end();

        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(Color.RED);
        for(int i=0;i<10; i++){
            shapeRenderer.arc(imageCircle.getX() , imageCircle.getY() , imageCircle.getHeight()/2,x,y);
            x+=36;
            y+=36;
        }
        shapeRenderer.end();

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(Color.PURPLE);
        shapeRenderer.circle(imageCircle.getX() , imageCircle.getY() , imageCircle.getHeight()/3);
        shapeRenderer.end();

        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(Color.RED);
        shapeRenderer.circle(imageCircle.getX() , imageCircle.getY(), imageCircle.getHeight()/3);
        shapeRenderer.end();

        //drawing sectors numbers
        batch.begin();
        font.draw(batch, "1", imageCircle.getX()  +20, imageCircle.getY()  +75);
        font.draw(batch, "2", imageCircle.getX() + 50, imageCircle.getY() +50);
        font.draw(batch, "3", imageCircle.getX() + 70 , imageCircle.getY() + 10 );
        font.draw(batch, "4", imageCircle.getX() + 50 , imageCircle.getY() -40);
        font.draw(batch, "5", imageCircle.getX() +20, imageCircle.getY() -65);
        font.draw(batch, "6", imageCircle.getX() -20, imageCircle.getY() -65);
        font.draw(batch, "7", imageCircle.getX() -60, imageCircle.getY() -40);
        font.draw(batch, "8", imageCircle.getX() - 80, imageCircle.getY() +10);
        font.draw(batch, "9", imageCircle.getX() -60, imageCircle.getY() + 50);
        font.draw(batch, "10", imageCircle.getX() -30, imageCircle.getY() + 70);
        batch.end();


    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width,height);
        camera.position.set(camera.viewportWidth/2,camera.viewportHeight/2,0);

        WIDTH = camera.viewportWidth;
        HEIGHT = camera.viewportHeight;
        imageCircle.setPosition((int) (camera.viewportWidth/2), (int) (camera.viewportHeight/2));
        stage.getViewport().update(width,height,false);
        playerCircle = new Circle(imageCircle.getX() , imageCircle.getY() , imageCircle.getHeight()/2);
        innerCircle = new Circle(imageCircle.getX() , imageCircle.getY() , 60);

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

        Vector3 coord =  camera.unproject(new Vector3(screenX, screenY, 0));

        System.out.println("worldX " +coord.x);
        System.out.println("worldY " +coord.y);
//        rectangle= new Rectangle(10,10,80,80);
//        rectangle2= new Rectangle(100,10,80,80);
//        rectangle3= new Rectangle(10,100,80,80);
//
//        if(Intersector.overlaps(rectangle, new Rectangle(coord.x, coord.y, 1,1)))
//        {
//            System.out.println("Click on Done");
//            Xintuition.getTurnBasedService().onDoneClicked(7);
//        }
//
//        if(Intersector.overlaps(rectangle2, new Rectangle(coord.x, coord.y, 1,1)))
//        {
//            System.out.println("Click on Leave");
//            Xintuition.getTurnBasedService().onLeaveClicked();
//        }
//        if(Intersector.overlaps(rectangle3, new Rectangle(coord.x, coord.y, 1,1)))
//        {
//            System.out.println("Click on Finish");
//            Xintuition.getTurnBasedService().onFinishClicked();
//        }

        playerCircle = new Circle(imageCircle.getX() , imageCircle.getY() , imageCircle.getHeight()/2);
        innerCircle = new Circle(imageCircle.getX() , imageCircle.getY() , 60);

        if(!myTurn) return false;
        if(Intersector.overlaps(playerCircle, new Rectangle(coord.x, coord.y, 1,1))) {
            if (!Intersector.overlaps(innerCircle, new Rectangle(coord.x, coord.y, 1, 1))) {
                if (isGuessed) {
                    randomSector = (int) (Math.random() * (numSectors) + 1);
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
                System.out.println("------------------------------------");
                Xintuition.getTurnBasedService().onDoneClicked(pickedSector);
                myTurn = false;
                debugLabel5.setText("ENEMY TURN");
                /*if (randomSector == pickedSector) {
                    isShow = true;
                    setFillingParametrs(Color.GREEN, pickedSector);
                    System.out.println("WON");
                    //stage.getActors().removeRange(stage.getActors().size - numSectors, stage.getActors().size - 1 );
                    curNumAttempts = numAttempts;
                    isGuessed = true;
                    score.setScore(score.getScore() + points);

                    Xintuition.getTurnBasedService().onDoneClicked(pickedSector);

                } else {
                    isShow = true;
                    setFillingParametrs(Color.RED, pickedSector);
                    System.out.println("LOSE");
                    curNumAttempts--;

                  //  Xintuition.getTurnBasedService().onFinishClicked();
                }*/
            }

        }

        return false;
    }
    private int getCircleSector(int sectorNumbers, float angle)
    {
        float deegreesPerSector = 360/sectorNumbers;

        return (int) (angle / deegreesPerSector)  + 1;
    }
    private void setFillingParametrs(Color color, int pickedSector) {
        isShow = true;
        fillColor = color;
        float angle = 360.0f / numSectors;
        startFilling = pickedSector * angle - 90;
        degrees = angle;

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
