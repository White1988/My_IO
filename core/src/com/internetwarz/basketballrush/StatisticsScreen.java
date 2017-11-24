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
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.internetwarz.basketballrush.utils.LanguagesManager;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by dell on 16.11.2017.
 */

public class StatisticsScreen implements Screen,InputProcessor {
    private final Image topImage;
    private final Image topTextImage;
    Tsar game;

    private int WIDTH;
    private int HEIGHT;

    private OrthographicCamera camera;
    private SpriteBatch batch;
    private int VIEWPORT_SCALE = 1;

    private Stage stage;
    private Preferences prefs;
    private Sound clickSound;

    //Buttons
    TextureAtlas buttonAtlas;
    Skin buttonSkin;
    TextButton.TextButtonStyle textButtonStyle;
    TextButton easyButton, mediumButton, hardButton;

    //Labels
    Label titleLable;
    private ArrayList<ArrayList<Label>> rows = new ArrayList<ArrayList<Label>>();
    private Label.LabelStyle styleTitle;
    private int amountLines;
    private Label titleLvl;
    private Label titleGames;
    private Texture background;
    private BitmapFont font;
    private Label.LabelStyle lineFontStyle;
    private BitmapFont fontLines;


    public StatisticsScreen(Tsar game) {
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

        getData();

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


        fontInit();
        buttonsInit();
        labelsInit();
        tableInit();
    }

    private void fontInit() {
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/Magistral Bold.TTF"));
        if(LanguagesManager.getInstance().getLanguage().equals("KO") || LanguagesManager.getInstance().getLanguage().equals("JA") || LanguagesManager.getInstance().getLanguage().equals("ZH"))
            generator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/DroidSansFallback.ttf"));

        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 14;
        parameter.color= Color.valueOf("#506878");
        parameter.borderStraight = false;
        parameter.borderWidth = 1;
        parameter.borderColor = Color.valueOf("#e2e3e7");
        parameter.shadowColor = Color.valueOf("#141a1e");

        parameter.characters = FreeTypeFontGenerator.DEFAULT_CHARS + "абвгдеёжзийклмнопрстуфхцчшщъыьэюяАБВГДЕЁЖЗИЙКЛМНОПРСТУФХЦЧШЩЪЫЬЭЮЯ";
        if(LanguagesManager.getInstance().getLanguage().equals("KO") || LanguagesManager.getInstance().getLanguage().equals("JA") || LanguagesManager.getInstance().getLanguage().equals("ZH"))
            parameter.characters = FreeTypeFontGenerator.DEFAULT_CHARS + "ただ手段ハード栄誉の殿堂統計遊びますゲームは終わった結果設定ただ手段ハードレベル実験右！間違いました！再起動はいいいえあなたは本当にゲームを再開しますか？ベストプレーヤーゲームは終わった！統計選手栄誉の殿堂ルールログアウト言語"
                    +"명예의 전당통계놀이게임이결과설정다만방법단단한수평실험오른쪽잘못된다시 시작예아니오게임을 다시 시작 하시겠습니까?최고의 선수게임은끝났어!통계(플레이어)명예의 전당규칙로그 아웃언어"
                    +"名人堂統計玩遊戲結束了結果設置只是手段硬水平實驗對錯了重新開始是的沒有你真的想重新啟動遊戲嗎？最好的球員遊戲結束了統計玩家名人堂規則註銷語言";
        font=generator.generateFont(parameter);
    }

    private void getData() {
        //TODO: add getting data
    }

    private void tableInit() {
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/Magistral Bold.TTF"));
        if(LanguagesManager.getInstance().getLanguage().equals("KO") || LanguagesManager.getInstance().getLanguage().equals("JA") || LanguagesManager.getInstance().getLanguage().equals("ZH"))
            generator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/DroidSansFallback.ttf"));

        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 17;
        parameter.color= Color.valueOf("#506878");
        parameter.characters = FreeTypeFontGenerator.DEFAULT_CHARS + "абвгдеёжзийклмнопрстуфхцчшщъыьэюяАБВГДЕЁЖЗИЙКЛМНОПРСТУФХЦЧШЩЪЫЬЭЮЯ";
        if(LanguagesManager.getInstance().getLanguage().equals("KO") || LanguagesManager.getInstance().getLanguage().equals("JA") || LanguagesManager.getInstance().getLanguage().equals("ZH"))
            parameter.characters = FreeTypeFontGenerator.DEFAULT_CHARS + "ただ手段ハード栄誉の殿堂統計遊びますゲームは終わった結果設定ただ手段ハードレベル実験右！間違いました！再起動はいいいえあなたは本当にゲームを再開しますか？ベストプレーヤーゲームは終わった！統計選手栄誉の殿堂ルールログアウト言語"
                    +"명예의 전당통계놀이게임이결과설정다만방법단단한수평실험오른쪽잘못된다시 시작예아니오게임을 다시 시작 하시겠습니까?최고의 선수게임은끝났어!통계(플레이어)명예의 전당규칙로그 아웃언어"
                    +"名人堂統計玩遊戲結束了結果設置只是手段硬水平實驗對錯了重新開始是的沒有你真的想重新啟動遊戲嗎？最好的球員遊戲結束了統計玩家名人堂規則註銷語言";

        BitmapFont font = generator.generateFont(parameter);

        styleTitle = new Label.LabelStyle();
        styleTitle.font = font;
        styleTitle.background = buttonSkin.getDrawable("panelTitle");

        parameter.color = Color.valueOf("ffffff");
        parameter.shadowColor = Color.valueOf("#186dbb");
        parameter.shadowOffsetX = 1;
        parameter.shadowOffsetY = 1;
        parameter.size = 16;
        fontLines = generator.generateFont(parameter);
        lineFontStyle = new Label.LabelStyle();
        lineFontStyle.font = fontLines;

        titleLvl = new Label(LanguagesManager.getInstance().getString("level"), styleTitle);
        titleLvl.setPosition(WIDTH/20, titleLable.getY() - titleLable.getHeight() - heightPercent(10));
        titleLvl.setWidth(WIDTH/2 - WIDTH/20);
        titleLvl.setHeight((titleLable.getY() - titleLable.getHeight() - heightPercent(10))/6);
        titleLvl.setAlignment(Align.center);
        stage.addActor(titleLvl);

        titleGames = new Label(LanguagesManager.getInstance().getString("countGames"), styleTitle);
        titleGames.setPosition(titleLvl.getX() + titleLvl.getWidth() - 1, titleLvl.getY());
        titleGames.setWidth(WIDTH/2 - WIDTH/20);
        titleGames.setHeight(titleLvl.getHeight());
        titleGames.setAlignment(Align.center);
        stage.addActor(titleGames);
        amountLines = 6;

        /*for (int i = amountLines-1; i > 0; i--) {
            addLineToTable(i, 10);
        }*/
        ArrayList<HashMap> list = game.firebaseHelper.getListEasy();
        System.out.println(amountLines = list.size());
        for (HashMap map: list) {
            if(((Number)map.get("gamesCount")).longValue() == 0)
                amountLines--;
        }
        int i = 1;
        for (HashMap map: list) {
            if(((Number)map.get("gamesCount")).longValue() != 0)
                addLineToTable(((Number)map.get("level")).longValue(), ((Number)map.get("gamesCount")).longValue(),i++);
        }

    }

    private void addLineToTable(long level, long countGames, int lineNumber ) {
        ArrayList<Label> row = new ArrayList<Label>();
        if(lineNumber%2 == 0) {
            lineFontStyle = new Label.LabelStyle();
            lineFontStyle.font = fontLines;
            lineFontStyle.background = buttonSkin.getDrawable("line");
        }
        else {
            lineFontStyle = new Label.LabelStyle();
            lineFontStyle.font = fontLines;
            lineFontStyle.background = buttonSkin.getDrawable("lineDark");
        }
        Label levelLabel = new Label(level + " " + LanguagesManager.getInstance().getString("lowcaseLevel"), lineFontStyle);
        Label gamesLabel = new Label(String.valueOf(countGames), lineFontStyle);
        row.add(levelLabel);
        row.add(gamesLabel);
        rows.add(row);
        if(rows.size() == 1) {
            levelLabel.setPosition(titleLvl.getX() + 1, titleLvl.getY() - titleLvl.getHeight());
            gamesLabel.setPosition(titleGames.getX(), titleGames.getY() - titleGames.getHeight());
        }
        else {
            Label prevLevel = rows.get(rows.size()-2).get(0);
            Label prevGame = rows.get(rows.size()-2).get(1);
            levelLabel.setPosition(prevLevel.getX(), prevLevel.getY() - prevLevel.getHeight());
            System.out.println("Prev label: " + prevLevel);
            gamesLabel.setPosition(prevGame.getX(), prevGame.getY() - prevGame.getHeight());
        }
        levelLabel.setWidth(titleLvl.getWidth() - 1);
        levelLabel.setHeight(titleLvl.getHeight());
        levelLabel.setAlignment(Align.center);
        //levelLabel.setDebug(true);

        gamesLabel.setWidth(titleGames.getWidth() - 1);
        gamesLabel.setHeight(titleGames.getHeight());
        gamesLabel.setAlignment(Align.center);
        //gamesLabel.setDebug(true);

        stage.addActor(levelLabel);
        stage.addActor(gamesLabel);


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
        Texture labelTexture = new Texture("skins/panelTitle.png");
        buttonAtlas.addRegion("panelTitle", labelTexture, 30, 10, labelTexture.getWidth()-30, labelTexture.getHeight()-30);
        labelTexture = new Texture("skins/line.png");
        buttonAtlas.addRegion("line", labelTexture, 20, 5, labelTexture.getWidth()-20, labelTexture.getHeight()-10);
        labelTexture = new Texture("skins/lineDark.png");
        buttonAtlas.addRegion("lineDark", labelTexture, 20, 5, labelTexture.getWidth()-20, labelTexture.getHeight()-10);

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
                if (prefs.getBoolean("soundOn", true))
                    clickSound.play();
                System.out.println("easy clicked!");
                mediumButton.setChecked(false);
                hardButton.setChecked(false);
                rows = new ArrayList<ArrayList<Label>>();
                if(amountLines != 0) {
                    stage.getActors().removeRange(stage.getActors().size - amountLines * 2, stage.getActors().size - 1);
                }
                ArrayList<HashMap> list = game.firebaseHelper.getListEasy();
                System.out.println(amountLines = list.size());
                for (HashMap map: list) {
                    if(((Number)map.get("gamesCount")).longValue() == 0)
                        amountLines--;
                }
                int i = 1;
                for (HashMap map: list) {
                    if(((Number)map.get("gamesCount")).longValue() != 0)
                        addLineToTable(((Number)map.get("level")).longValue(), ((Number)map.get("gamesCount")).longValue(),i++);
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
                if (prefs.getBoolean("soundOn", true))
                    clickSound.play();
                easyButton.setChecked(false);
                hardButton.setChecked(false);
                rows = new ArrayList<ArrayList<Label>>();
                if(amountLines != 0) {
                    stage.getActors().removeRange(stage.getActors().size - amountLines * 2, stage.getActors().size - 1);
                }
                ArrayList<HashMap> list = game.firebaseHelper.getListMedium();
                System.out.println(amountLines = list.size());
                for (HashMap map: list) {
                    if(((Number)map.get("gamesCount")).longValue() == 0)
                        amountLines--;
                }
                int i = 1;
                for (HashMap map: list) {
                    if(((Number)map.get("gamesCount")).longValue() != 0)
                        addLineToTable(((Number)map.get("level")).longValue(), ((Number)map.get("gamesCount")).longValue(),i++);
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
                if (prefs.getBoolean("soundOn", true))
                    clickSound.play();
                mediumButton.setChecked(false);
                easyButton.setChecked(false);
                rows = new ArrayList<ArrayList<Label>>();
                if(amountLines != 0) {
                    stage.getActors().removeRange(stage.getActors().size - amountLines * 2, stage.getActors().size - 1);
                }
                ArrayList<HashMap> list = game.firebaseHelper.getListHard();
                System.out.println(amountLines = list.size());
                for (HashMap map: list) {
                    if(((Number)map.get("gamesCount")).longValue() == 0)
                        amountLines--;
                }
                int i = 1;
                for (HashMap map: list) {
                    if(((Number)map.get("gamesCount")).longValue() != 0)
                        addLineToTable(((Number)map.get("level")).longValue(), ((Number)map.get("gamesCount")).longValue(),i++);
                }
            }
        });
        stage.addActor(hardButton);

        easyButton.setChecked(true);
    }

    private void labelsInit() {

        //Labels init
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/Magistral Bold.TTF"));
        if(LanguagesManager.getInstance().getLanguage().equals("KO") || LanguagesManager.getInstance().getLanguage().equals("JA") || LanguagesManager.getInstance().getLanguage().equals("ZH"))
            generator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/DroidSansFallback.ttf"));

        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 24;
        parameter.color= Color.valueOf("#f4f8fb");
        parameter.characters = FreeTypeFontGenerator.DEFAULT_CHARS + "абвгдеёжзийклмнопрстуфхцчшщъыьэюяАБВГДЕЁЖЗИЙКЛМНОПРСТУФХЦЧШЩЪЫЬЭЮЯ";
        if(LanguagesManager.getInstance().getLanguage().equals("KO") || LanguagesManager.getInstance().getLanguage().equals("JA") || LanguagesManager.getInstance().getLanguage().equals("ZH"))
            parameter.characters = FreeTypeFontGenerator.DEFAULT_CHARS + "ただ手段ハード栄誉の殿堂統計遊びますゲームは終わった結果設定ただ手段ハードレベル実験右！間違いました！再起動はいいいえあなたは本当にゲームを再開しますか？ベストプレーヤーゲームは終わった！統計選手栄誉の殿堂ルールログアウト言語"
                    +"명예의 전당통계놀이게임이결과설정다만방법단단한수평실험오른쪽잘못된다시 시작예아니오게임을 다시 시작 하시겠습니까?최고의 선수게임은끝났어!통계(플레이어)명예의 전당규칙로그 아웃언어"
                    +"名人堂統計玩遊戲結束了結果設置只是手段硬水平實驗對錯了重新開始是的沒有你真的想重新啟動遊戲嗎？最好的球員遊戲結束了統計玩家名人堂規則註銷語言";

        BitmapFont font = generator.generateFont(parameter);
        Label.LabelStyle labelStyle = new Label.LabelStyle(font, Color.valueOf("#f4f8fb"));
        titleLable = new Label(LanguagesManager.getInstance().getString("statisticsPlayer"), labelStyle);
        titleLable.setPosition(WIDTH/2 - titleLable.getWidth()/2, easyButton.getY() - heightPercent(10));

        stage.addActor(titleLable);
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

    }
}
