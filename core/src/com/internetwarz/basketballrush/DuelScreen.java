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
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.internetwarz.basketballrush.model.PlayerTurn;
import com.internetwarz.basketballrush.utils.LanguagesManager;




public class DuelScreen implements Screen, InputProcessor{

    private SpriteBatch batch;
    private Texture background;
    private final Xintuition game;
    private static int VIEWPORT_SCALE = 1;



    private Label label;

    private Label debugLabel1;
    private Label debugLabel2;
    private Label debugLabel3;
    private Label debugLabel4;

    private Label debugLabel5;


    private Label.LabelStyle rightStyle;
    private Label.LabelStyle textStyle;
    private GlyphLayout layout;

    private Stage stage;

    private ShapeRenderer shapeRenderer;
    private OrthographicCamera camera;

    private float WIDTH;
    private float HEIGHT;

    private Rectangle rectangle;
    private Rectangle rectangle2;
    private Rectangle rectangle3;



    private PlayerTurn lastTurnData = null;

    public DuelScreen(final Xintuition game ) {

        this.game = game;

        WIDTH = (float) Gdx.graphics.getWidth();
        HEIGHT = (float) Gdx.graphics.getHeight();

        stage = new Stage(new FitViewport(WIDTH, HEIGHT));
        stage.clear();

        InputMultiplexer plex = new InputMultiplexer();
        plex.addProcessor(stage);
        plex.addProcessor(this);

        camera = new OrthographicCamera();
        camera.setToOrtho(false, WIDTH/ VIEWPORT_SCALE, HEIGHT / VIEWPORT_SCALE);

        shapeRenderer = new ShapeRenderer(15000);//increase smoothness of circle


        batch = new SpriteBatch();
        batch.setProjectionMatrix(camera.combined);

        buttonsInit();

        InitLabels();

        Xintuition.getTurnBasedService().coreGameplayCallBacks.addEnemyTurnFinishedCallback(new TurnBasedService.EnemyTurnAction() {
            @Override
            public void Action(PlayerTurn param) {
                lastTurnData = param;
            }
        });

        Xintuition.getTurnBasedService().coreGameplayCallBacks.addMatchLeftCallback(new TurnBasedService.VoidAction() {
            @Override
            public void Action() {
               Gdx.app.postRunnable(new Runnable() {
                   @Override
                   public void run() {
                       game.setScreen(new MainMenuScreen(game));
                   }
               });
            }
        });

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
        BitmapFont font = generator.generateFont(parameter);
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
        label.setPosition(520, 520);
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
        debugLabel5.setSize(155, 55);
        debugLabel5.setFontScale(1f, 1f);



        stage.addActor(label);
        stage.addActor(debugLabel1);
        stage.addActor(debugLabel2);
        stage.addActor(debugLabel3);
        stage.addActor(debugLabel4);

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

        label.setText("7");


        if(lastTurnData != null)
        {
           debugLabel1.setText("Turn count: " + lastTurnData.turnCounter);
           debugLabel2.setText("Selected number: " + lastTurnData.selectedNumber);
           debugLabel3.setText("player1Score: " + lastTurnData.player1Score);
           debugLabel4.setText("player2Score: " + lastTurnData.player2Score);
           debugLabel5.setText("overall: " + lastTurnData.toString());
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
        shapeRenderer.rect(500,500,80,80);
        shapeRenderer.rect(400,400,80,80);
        shapeRenderer.rect(600,600,80,80);

        /*shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(Color.WHITE);
        shapeRenderer.circle(100, 300, 100);
        shapeRenderer.end();*/

        shapeRenderer.end();

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

        Gdx.app.log("Duel","worldX " +coord.x);
        Gdx.app.log("Duel","worldY " +coord.y);
        rectangle= new Rectangle(500,500,80,80);
        rectangle2= new Rectangle(400,400,80,80);
        rectangle3= new Rectangle(600,600,80,80);

        if(Intersector.overlaps(rectangle, new Rectangle(coord.x, coord.y, 1,1)))
        {

            Gdx.app.log("Duel","Click on Done");
            Xintuition.getTurnBasedService().onDoneClicked(7);

        }

        if(Intersector.overlaps(rectangle2, new Rectangle(coord.x, coord.y, 1,1)))
        {

            Gdx.app.log("Duel","Click on Leave");

            //todo (Dima) add "Are you sure" dialog
            Xintuition.getTurnBasedService().onLeaveClicked();
        }
        if(Intersector.overlaps(rectangle3, new Rectangle(coord.x, coord.y, 1,1)))
        {

            Gdx.app.log("Duel","Click on Finish");
            Xintuition.getTurnBasedService().onFinishClicked();
        }

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
