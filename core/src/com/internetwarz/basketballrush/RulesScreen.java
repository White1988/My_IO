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
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.internetwarz.basketballrush.utils.LanguagesManager;

/**
 * Created by dell on 16.11.2017.
 */

public class RulesScreen implements Screen, InputProcessor {
    private final Image topImage;
    private final Image topTextImage;
    Xintuition game;

    private int WIDTH;
    private int HEIGHT;

    private OrthographicCamera camera;
    private SpriteBatch batch;
    private int VIEWPORT_SCALE = 1;

    private Stage stage;
    private Table table;
    private Preferences prefs;
    private Sound clickSound;

    //Labels
    Label titleLable;
    Label textLabel;
    private Texture background;

    public RulesScreen(Xintuition game) {
        this.game = game;
        WIDTH = Gdx.graphics.getWidth();
        HEIGHT = Gdx.graphics.getHeight();

        stage = new Stage(new FitViewport(WIDTH, HEIGHT));
        stage.clear();

        InputMultiplexer plex = new InputMultiplexer();
        plex.addProcessor(stage);
        plex.addProcessor(this);

        Gdx.input.setInputProcessor(plex);
        Gdx.input.setCatchBackKey(true);

        camera = new OrthographicCamera();
        camera.setToOrtho(false, WIDTH/ VIEWPORT_SCALE, HEIGHT / VIEWPORT_SCALE);

        batch = new SpriteBatch();
        batch.setProjectionMatrix(camera.combined);


        //Preferences init
        prefs = Gdx.app.getPreferences("My Preferences");
        clickSound = game.assets.getSound();

        //Background init
        Pixmap rect = new Pixmap((int) WIDTH, (int) HEIGHT, Pixmap.Format.RGBA8888);
        rect.setColor(Color.valueOf("#15091e"));
        rect.fillRectangle(0, 0, (int)WIDTH, (int) HEIGHT);
        background = new Texture(rect);

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

        labelsInit();
    }


    private void labelsInit() {

        //Labels init
        table = new Table();
        table.setFillParent(true);
        //table.setDebug(true);
        table.top();

        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/Magistral Bold.TTF"));
        if(LanguagesManager.getInstance().getLanguage().equals("KO") || LanguagesManager.getInstance().getLanguage().equals("JA") || LanguagesManager.getInstance().getLanguage().equals("ZH"))
            generator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/DroidSansFallback.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 25;
        parameter.color= Color.valueOf("#bed5f6");
        parameter.characters = FreeTypeFontGenerator.DEFAULT_CHARS + "абвгдеёжзийклмнопрстуфхцчшщъыьэюяАБВГДЕЁЖЗИЙКЛМНОПРСТУФХЦЧШЩЪЫЬЭЮЯ";
        if(LanguagesManager.getInstance().getLanguage().equals("KO") || LanguagesManager.getInstance().getLanguage().equals("JA") || LanguagesManager.getInstance().getLanguage().equals("ZH"))
            parameter.characters = FreeTypeFontGenerator.DEFAULT_CHARS + "ただ手段ハード栄誉の殿堂統計遊びますゲームは終わった結果設定ただ手段ハードレベル実験右！間違いました！再起動はいいいえあなたは本当にゲームを再開しますか？ベストプレーヤーゲームは終わった！統計選手栄誉の殿堂ルールログアウト言語"
                    +"명예의 전당통계놀이게임이결과설정다만방법단단한수평실험오른쪽잘못된다시 시작예아니오게임을 다시 시작 하시겠습니까?최고의 선수게임은끝났어!통계(플레이어)명예의 전당규칙로그 아웃언어"
                    +"名人堂統計玩遊戲結束了結果設置只是手段硬水平實驗對錯了重新開始是的沒有你真的想重新啟動遊戲嗎？最好的球員遊戲結束了統計玩家名人堂規則註銷語言"
                    +"以圓圈形式的遊戲場被分成扇區，每個級別只有一個扇區隨機定位。玩家的任務，依靠直覺找到這個忠實的“綠色”部門。行業數量逐年遞增。通過確定正確的部門，你正在從一級到另一級。你的結果是你在比賽中達到的最高水平。遊戲中有三種類型的複雜性：“初學者”，“業餘”和“Profi”。初學者每級有三次嘗試。對於“業餘”級別 - 每級只有兩次嘗試。玩“Profi”難度級別時，玩家必須在每個級別的第一次嘗試中確定正確的扇區。"
                    +"원 형태의 게임 필드는 섹터로 나뉘며, 각 레벨마다 하나의 섹터 만 임의로 배치됩니다. 이 충실한 '녹색'부문을 찾기 위해 직감에 의존하는 플레이어의 임무. 섹터의 수는 레벨마다 증가합니다. 올바른 분야를 결정함으로써, 당신은 레벨에서 레벨로 이동하고 있습니다. 결과는 게임 도중 도달 한 최대 레벨입니다. 게임에는 'Beginner', 'Amateur'및 'Profi'의 세 가지 유형의 복잡성이 있습니다. 초급자는 레벨 당 3 번 시도됩니다. '아마추어'레벨 - 레벨 당 단지 두 번의 시도. 'Profi'난이도에서 플레이 할 때, 플레이어는 각 레벨의 첫 번째 시도에서 올바른 섹터를 결정해야합니다."
                    +"円の形のゲームフィールドはセクタに分割されており、各レベルごとにランダムに1つのセクタしか配置されていません。この忠実な 'グリーン'セクターを見つける直感に頼る、プレーヤーの仕事。セクター数はレベルごとに増加します。正しいセクターを決定することによって、あなたはレベルからレベルに移動しています。結果はゲーム中に到達した最大レベルです。ゲームには、 'Beginner'、 'Amateur'、 'Profi'の3種類の複雑さがあります。初心者にはレベルごとに3回の試行が与えられます。 「アマチュア」のレベルでは、レベルごとに2回の試行しかありません。 'Profi'難易度でプレイする場合、プレイヤーは各レベルの最初の試行で正しいセクターを決定する必要があります。";

        BitmapFont font = generator.generateFont(parameter);
        Label.LabelStyle labelStyle = new Label.LabelStyle(font, Color.valueOf("#bed5f6"));

        titleLable = new Label(LanguagesManager.getInstance().getString("rules"), labelStyle);
        titleLable.setWrap(true);
        table.add(titleLable)
                .expandX()
                .left()
                .padLeft(WIDTH/2 - titleLable.getWidth()/2)
                .padTop(HEIGHT/8 + HEIGHT/40);




        parameter.size = 15;
        font = generator.generateFont(parameter);
        labelStyle = new Label.LabelStyle(font, Color.valueOf("#bed5f6"));
        /*String text = "The standard Lorem Ipsum passage, used since the 1500s\"Lorem  ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod  tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim  veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea  commodo consequat.\n" +
                "\n" +
                "Duis aute irure dolor in reprehenderit in voluptate  velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint  occaecat cupidatat non proident, sunt in culpa qui officia deserunt  mollit anim id est laborum.\"  ";
*/
        String text = LanguagesManager.getInstance().getString("rulesText");
        textLabel= new Label(text, labelStyle);
        textLabel.setWrap(true);
        textLabel.setWidth(WIDTH - widthPercent(20));
        table.row();
        table.add(textLabel)
                .left()
                .width(WIDTH - widthPercent(20))
                .padLeft(widthPercent(10))
                .padTop(heightPercent(5));

        stage.addActor(table);
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
    public boolean keyDown(int keycode) {
        if(keycode == Input.Keys.BACKSPACE || keycode == Input.Keys.BACK){
            game.setScreen(new MainMenuScreen(game));
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

        stage.act();
        stage.getBatch().begin();
        stage.getBatch().draw(background, 0, 0);//drawing background
        stage.getBatch().end();
        batch.begin();
        stage.draw();
        batch.end();

    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width,height,true);
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
