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
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.internetwarz.basketballrush.utils.LanguagesManager;
import com.internetwarz.basketballrush.utils.Score;

public class GameEndScreen implements Screen,InputProcessor {
    final Tsar game;
    private final TextButton.TextButtonStyle textButtonStyle;
    Score score;
    final float appWidth = 768;
    final float appHeight = 1280;
    SpriteBatch batch;
    OrthographicCamera camera;
    Sound clickSound;
    Preferences prefs;

    private String gameType;
    private String scoreString;
    private String highScoreString;

    GlyphLayout layoutGameOver,layoutYourScore,layoutHighScore;

    private Stage stage;
    private Skin buttonSkin;
    private TextureAtlas buttonAtlas;
    private TextButton playButton,leaderboardButton,achievementsButton,homeButton;
    private int numAttempts;
    private BitmapFont font;

    public GameEndScreen(final Tsar gam, Score scor, String gt, final int numAttempts){
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/Attractive-Regular.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = (int) (40 * 2.5);
        parameter.color = Color.BLACK;
        parameter.characters = FreeTypeFontGenerator.DEFAULT_CHARS + "абвгдеёжзийклмнопрстуфхцчшщъыьэюяАБВГДЕЁЖЗИЙКЛМНОПРСТУФХЦЧШЩЪЫЬЭЮЯ";
        font = generator.generateFont(parameter);

        this.game=gam;
        this.score = scor;
        this.numAttempts = numAttempts;
        camera = new OrthographicCamera();
        camera.setToOrtho(false, appWidth, appHeight);
        batch = new SpriteBatch();
        batch.setProjectionMatrix(camera.combined);


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

        buttonAtlas = new TextureAtlas("buttons.pack");
        buttonAtlas.addRegion("Button", button, 0, 0, 40, 20);
        buttonAtlas.addRegion("Button checked", buttonChecked, 0, 0, 40, 20);
        buttonSkin = new Skin();
        buttonSkin.addRegions(buttonAtlas);
        buttonSkin = new Skin();
        buttonSkin.addRegions(buttonAtlas);

        textButtonStyle = new TextButton.TextButtonStyle();
        textButtonStyle.font = font;
        textButtonStyle.up = buttonSkin.getDrawable("Button");
        textButtonStyle.down = buttonSkin.getDrawable("Button checked");


        stage = new Stage(new FitViewport(appWidth,appHeight));
        stage.clear();
        InputMultiplexer plex = new InputMultiplexer();
        plex.addProcessor(stage);
        plex.addProcessor(this);
        Gdx.input.setInputProcessor(plex);
        prefs = Gdx.app.getPreferences("My Preferences");
        clickSound = Gdx.audio.newSound(Gdx.files.internal("sounds/clickSound.mp3"));

        scoreString = LanguagesManager.getInstance().getString("level") + " : "+score.getStringScore();
        gameType = gt;

        //setting score preferences
        if(score.getScore()>prefs.getInteger(gameType,0)){
            prefs.putInteger(gameType,score.getScore());
        }
        prefs.flush();
        highScoreString = LanguagesManager.getInstance().getString("best") + " : "+prefs.getInteger(gameType,0);
        game.getPlayServices().submitScore(score.getScore(),gameType);

        //setting games Played preferences
        prefs.putInteger(gameType+" played",prefs.getInteger(gameType+" played",0)+1);

        prefs.flush();

        game.getPlayServices().gamesPlayedAchievements(gameType,prefs.getInteger(gameType+" played",0));


        layoutGameOver = new GlyphLayout();
        layoutGameOver.setText(font,LanguagesManager.getInstance().getString("gameOver"));

        layoutYourScore = new GlyphLayout();
        layoutYourScore.setText(font,scoreString);

        layoutHighScore = new GlyphLayout();
        layoutHighScore.setText(font,highScoreString);




        //Play Button resources
        playButton = new TextButton("Play again",textButtonStyle);
        playButton.setPosition(appWidth/2-playButton.getWidth()/2,appHeight/2-playButton.getHeight()/2);
        playButton.addListener(new ClickListener(){
            public void clicked(InputEvent event, float x, float y){
                game.setScreen(new TsarGameplayScreen(game, numAttempts));
            }
        });
        stage.addActor(playButton);

        //Leaderboard Button resources
        leaderboardButton = new TextButton("Leaderboard",textButtonStyle);
        leaderboardButton.setPosition(widthPercent(30)-leaderboardButton.getWidth()/2,heightPercent(35));
        leaderboardButton.addListener(new ClickListener(){
            public void clicked(InputEvent event, float x, float y){
                if(prefs.getBoolean("soundOn",true))
                    clickSound.play();
                game.getPlayServices().showScore();
            }
        });
        //stage.addActor(leaderboardButton);

       /* //Achievements Button resources
        achievementsButton = new ImageButton(buttonSkin.getDrawable("achievements"),buttonSkin.getDrawable("achievementsClicked"));
        achievementsButton.setPosition(widthPercent(70)-achievementsButton.getWidth()/2,heightPercent(35));
        achievementsButton.addListener(new ClickListener(){
            public void clicked(InputEvent event, float x, float y){
                if(prefs.getBoolean("soundOn",true))
                    clickSound.play();
                game.getPlayServices().showAchievement();
            }
        });
        stage.addActor(achievementsButton);*/

        //Home Button resources
        homeButton = new TextButton("Exit", textButtonStyle);
        homeButton.setPosition(appWidth/2-homeButton.getWidth()/2, heightPercent(35));
        homeButton.addListener(new ClickListener(){
            public void clicked(InputEvent event, float x, float y){
                if(prefs.getBoolean("soundOn",true))
                    clickSound.play();
                game.setScreen(new MainMenuScreen(game));
            }
        });
        stage.addActor(homeButton);


    }

    private void buttonsInit() {

    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(1, 1, 1, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        stage.act();
        batch.begin();
        stage.draw();
        batch.end();

        batch.begin();
        font.draw(batch,LanguagesManager.getInstance().getString("gameOver"),appWidth/2-layoutGameOver.width/2,heightPercent(80));
        font.draw(batch,scoreString,appWidth/2-layoutYourScore.width/2,heightPercent(70));
        font.draw(batch,highScoreString,appWidth/2-layoutHighScore.width/2,heightPercent(60));
        batch.end();
    }

    @Override
    public void dispose() {
        batch.dispose();
        stage.dispose();
        buttonSkin.dispose();
        buttonAtlas.dispose();
    }

    @Override
    public void show() {

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

    public float widthPercent(int w){
        float result;
        result = (appWidth*w)/100;
        return result;
    }

    public float heightPercent(int h){
        float result;
        result = (appHeight*h)/100;
        return result;
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
