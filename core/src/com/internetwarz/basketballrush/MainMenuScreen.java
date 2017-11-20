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
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.internetwarz.basketballrush.utils.LanguagesManager;

import static com.internetwarz.basketballrush.Constants.EASY_MODE;

public class MainMenuScreen implements Screen,InputProcessor {
    final Tsar game;
    final float appWidth;
    final float appHeight;
    SpriteBatch batch;
    OrthographicCamera camera;
    Sound clickSound;
    Preferences prefs;

    //TODO: font
    private Stage stage;
    private Skin buttonSkin;
    private TextureAtlas buttonAtlas;
    private Texture gameName, background;
    private ImageButton leaderboardButton,achievementsButton,soundButton,rateButton,info;
    private TextButton playButton, statisticButton, hallButton, rulesButton, settingsButton;

    public MainMenuScreen(final Tsar gam){
        this.game=gam;
        game.getPlayServices().signIn();
        System.out.println(appWidth = Gdx.graphics.getWidth());
        System.out.println(appHeight = Gdx.graphics.getHeight());
        camera = new OrthographicCamera();
        camera.setToOrtho(false, appWidth, appHeight);
        batch = new SpriteBatch();
        batch.setProjectionMatrix(camera.combined);

        stage = new Stage(new FitViewport(appWidth,appHeight));
        stage.clear();

        //Add text xIntuition and it's background
        Texture topImageTexture = new Texture("skins/topImage.png");
        Image topImage = new Image(topImageTexture);
        topImage.setSize(appWidth + widthPercent(20), appHeight/8 + heightPercent(5));
        topImage.setPosition(0 - widthPercent(10), appHeight - appHeight/8);
        stage.addActor(topImage);

        Texture topText = new Texture("skins/topText.png");
        Image topTextImage = new Image(topText);
        topTextImage.setSize(appWidth - (appWidth/10)*2, appHeight/10 - 10);
        topTextImage.setPosition(appWidth/2 - topTextImage.getWidth()/2, topImage.getY() + appHeight/8/8);
        stage.addActor(topTextImage);



        buttonAtlas = game.assets.getButtonAtlas();
        Pixmap rect = new Pixmap((int)widthPercent(33),20,Pixmap.Format.RGBA8888);
        rect.setColor(Color.LIGHT_GRAY);
        rect.fillRectangle(0, 0, (int)widthPercent(33), 20);
        Texture buttonChecked = new Texture(rect);
        rect.setColor(Color.DARK_GRAY);
        rect.drawRectangle(0,0,(int)widthPercent(33),20);
        buttonChecked.draw(rect, 0, 0);
        rect.dispose();

        Texture buttonTexture = new Texture("skins/buttonPlay.png");
        buttonAtlas.addRegion("Button", buttonChecked, 0, 0, (int)widthPercent(33), 20);
        buttonAtlas.addRegion("buttonPlay", buttonTexture, 0, 0, buttonTexture.getWidth(), buttonTexture.getHeight());
        buttonTexture = new Texture("skins/buttonPlayClick.png");
        buttonAtlas.addRegion("buttonPlayClick", buttonTexture, 0, 0, buttonTexture.getWidth(), buttonTexture.getHeight());
        buttonTexture = new Texture("skins/buttonStatistics.png");
        buttonAtlas.addRegion("buttonStatistics", buttonTexture, 0, 0, buttonTexture.getWidth(), buttonTexture.getHeight());
        buttonTexture = new Texture("skins/buttonStatisticsClick.png");
        buttonAtlas.addRegion("buttonStatisticsClick", buttonTexture, 0, 0, buttonTexture.getWidth(), buttonTexture.getHeight());
        buttonTexture = new Texture("skins/buttonHallOfFame.png");
        buttonAtlas.addRegion("buttonHallOfFame", buttonTexture, 0, 0, buttonTexture.getWidth(), buttonTexture.getHeight());
        buttonTexture = new Texture("skins/buttonHallOfFameClick.png");
        buttonAtlas.addRegion("buttonHallOfFameClick", buttonTexture, 0, 0, buttonTexture.getWidth(), buttonTexture.getHeight());
        buttonTexture = new Texture("skins/buttonRules.png");
        buttonAtlas.addRegion("buttonRules", buttonTexture, 0, 0, buttonTexture.getWidth(), buttonTexture.getHeight());
        buttonTexture = new Texture("skins/buttonRulesClick.png");
        buttonAtlas.addRegion("buttonRulesClick", buttonTexture, 0, 0, buttonTexture.getWidth(), buttonTexture.getHeight());
        buttonTexture = new Texture("skins/buttonSettings.png");
        buttonAtlas.addRegion("buttonSettings", buttonTexture, 0, 0, buttonTexture.getWidth(), buttonTexture.getHeight());
        buttonTexture = new Texture("skins/buttonSettingsClick.png");
        buttonAtlas.addRegion("buttonSettingsClick", buttonTexture, 0, 0, buttonTexture.getWidth(), buttonTexture.getHeight());


        rect = new Pixmap((int)appWidth, (int)appHeight, Pixmap.Format.RGBA8888);
        rect.setColor(Color.valueOf("#15091e"));
        rect.fillRectangle(0, 0, (int)appWidth, (int) appHeight);
        background = new Texture(rect);
        rect.dispose();

        Skin playBtnSkin = new Skin();
        playBtnSkin.add("playButton", buttonTexture);


        buttonSkin = new Skin();
        buttonSkin.addRegions(buttonAtlas);
       // buttonSkin.add("buttonPlay", new Image(buttonTexture));
        TextButton.TextButtonStyle textButtonStyle = new TextButton.TextButtonStyle();
        textButtonStyle.font = game.font;
        textButtonStyle.up = buttonSkin.getDrawable("buttonPlay");
        textButtonStyle.down = buttonSkin.getDrawable("buttonPlayClick");



        InputMultiplexer plex = new InputMultiplexer();
        plex.addProcessor(this);
        plex.addProcessor(stage);
        Gdx.input.setInputProcessor(plex);

        prefs = Gdx.app.getPreferences("My Preferences");
        clickSound = game.assets.getSound();
        //gameName = game.assets.getTexture("gameName");



        playButton = new TextButton(LanguagesManager.getInstance().getString("play"), textButtonStyle);
        //playButton.setSize(widthPercent(33), heightPercent(10));
        playButton.setSize(appWidth - appWidth/10*2, appHeight/9);
        playButton.setPosition(appWidth/10, topImage.getY() - playButton.getHeight() - appHeight/17);
        playButton.addListener(new ClickListener(){
            public void clicked(InputEvent event, float x, float y){
                if(prefs.getBoolean("soundOn",true))
                    clickSound.play();
                //System.out.println("Play clicked!");
               /* if(prefs.getBoolean("first",true)) could be annoying for player
                    game.setScreen(new RulesScreen(game));
                else*/
                    game.setScreen(new TsarGameplayScreen(game, Constants.ATTEMPTS_IN_GAMEMODE.get(EASY_MODE)));

            }
        });
        //playButton.getLabelCell().padLeft(120);
        playButton.getLabel().setAlignment(Align.left);
        playButton.getLabelCell().padLeft(playButton.getWidth()/2 - widthPercent(10));
        stage.addActor(playButton);

        textButtonStyle = new TextButton.TextButtonStyle();
        textButtonStyle.font = game.font;
        textButtonStyle.up = buttonSkin.getDrawable("buttonStatistics");
        textButtonStyle.down = buttonSkin.getDrawable("buttonStatisticsClick");

        statisticButton = new TextButton(LanguagesManager.getInstance().getString("statistics"), textButtonStyle);
        statisticButton.setSize(playButton.getWidth(), playButton.getHeight());
        statisticButton.setPosition(playButton.getX(), playButton.getY() - playButton.getHeight() - appHeight/22);
        statisticButton.addListener(new ClickListener(){
            public void clicked(InputEvent event, float x, float y){
                if(prefs.getBoolean("soundOn",true))
                    clickSound.play();
                game.setScreen(new StatisticsScreen(game));
            }
        });
        statisticButton.getLabel().setAlignment(Align.left);
        statisticButton.getLabelCell().padLeft(playButton.getWidth()/2 - widthPercent(10));
        stage.addActor(statisticButton);

        textButtonStyle = new TextButton.TextButtonStyle();
        textButtonStyle.font = game.font;
        textButtonStyle.up = buttonSkin.getDrawable("buttonHallOfFame");
        textButtonStyle.down = buttonSkin.getDrawable("buttonHallOfFameClick");

        hallButton = new TextButton(LanguagesManager.getInstance().getString("hall_of_fame"), textButtonStyle);
        hallButton.setSize(playButton.getWidth(), playButton.getHeight());
        hallButton.setPosition(playButton.getX(), statisticButton.getY() - statisticButton.getHeight() - appHeight/22);
        hallButton.addListener(new ClickListener(){
            public void clicked(InputEvent event, float x, float y){
                if(prefs.getBoolean("soundOn",true))
                    clickSound.play();
                game.setScreen(new HallOfFameScreen(game));

            }
        });
        hallButton.getLabel().setAlignment(Align.left);
        hallButton.getLabelCell().padLeft(playButton.getWidth()/2 - widthPercent(10));
        stage.addActor(hallButton);

        textButtonStyle = new TextButton.TextButtonStyle();
        textButtonStyle.font = game.font;
        textButtonStyle.up = buttonSkin.getDrawable("buttonRules");
        textButtonStyle.down = buttonSkin.getDrawable("buttonRulesClick");

        rulesButton = new TextButton(LanguagesManager.getInstance().getString("rules"), textButtonStyle);
        rulesButton.setSize(playButton.getWidth(), playButton.getHeight());
        rulesButton.setPosition(playButton.getX(), hallButton.getY() - hallButton.getHeight() - appHeight/22);
        rulesButton.addListener(new ClickListener(){
            public void clicked(InputEvent event, float x, float y){
                if(prefs.getBoolean("soundOn",true))
                    clickSound.play();
                game.setScreen(new RulesScreen(game));
            }
        });
        rulesButton.getLabel().setAlignment(Align.left);
        rulesButton.getLabelCell().padLeft(playButton.getWidth()/2 - widthPercent(10));
        stage.addActor(rulesButton);

        textButtonStyle = new TextButton.TextButtonStyle();
        textButtonStyle.font = game.font;
        textButtonStyle.up = buttonSkin.getDrawable("buttonSettings");
        textButtonStyle.down = buttonSkin.getDrawable("buttonSettingsClick");

        settingsButton = new TextButton(LanguagesManager.getInstance().getString("settings"), textButtonStyle);
        settingsButton.setSize(playButton.getWidth(), playButton.getHeight());
        settingsButton.setPosition(playButton.getX(), rulesButton.getY() - rulesButton.getHeight() - appHeight/22);
        settingsButton.addListener(new ClickListener(){
            public void clicked(InputEvent event, float x, float y){
                if(prefs.getBoolean("soundOn",true))
                    clickSound.play();
                game.setScreen(new SettingsScreen(game));
            }
        });
        settingsButton.getLabel().setAlignment(Align.left);
        settingsButton.getLabelCell().padLeft(playButton.getWidth()/2 - widthPercent(10));
        stage.addActor(settingsButton);
        //Play Button resources
        /*playButton = new ImageButton(buttonSkin.getDrawable("play"),buttonSkin.getDrawable("playClicked"));
        playButton.setPosition(appWidth/2-playButton.getWidth()/2,appHeight/2-playButton.getHeight()/2);
        playButton.addListener(new ClickListener(){
            public void clicked(InputEvent event, float x, float y){
                if(prefs.getBoolean("first",true))
                    game.setScreen(new HowToPlay(game));
                else
                    //game.setScreen(new GameModeSelect(game));
                    game.setScreen(new TsarGameplayScreen(game, 3));
            }
        });
        stage.addActor(playButton);

        //Leaderboard Button resources
        leaderboardButton = new ImageButton(buttonSkin.getDrawable("leaderboard"),buttonSkin.getDrawable("leaderboardClicked"));
        leaderboardButton.setPosition(widthPercent(30)-leaderboardButton.getWidth()/2,heightPercent(35));
        leaderboardButton.addListener(new ClickListener(){
            public void clicked(InputEvent event, float x, float y){
                if(prefs.getBoolean("soundOn",true))
                    clickSound.play();
                game.getPlayServices().showScore();
            }
        });
        stage.addActor(leaderboardButton);

        //Achievements Button resources
        achievementsButton = new ImageButton(buttonSkin.getDrawable("achievements"),buttonSkin.getDrawable("achievementsClicked"));
        achievementsButton.setPosition(widthPercent(70)-achievementsButton.getWidth()/2,heightPercent(35));
        achievementsButton.addListener(new ClickListener(){
            public void clicked(InputEvent event, float x, float y){
                if(prefs.getBoolean("soundOn",true))
                    clickSound.play();
                game.getPlayServices().showAchievement();
            }
        });
        stage.addActor(achievementsButton);

        //Rate Button Resource
        rateButton = new ImageButton(buttonSkin.getDrawable("rate"),buttonSkin.getDrawable("rate"));
        rateButton.setPosition(widthPercent(50),heightPercent(18));
        rateButton.addListener(new ClickListener(){
            public void clicked(InputEvent event, float x, float y){
                if(prefs.getBoolean("soundOn",true))
                    clickSound.play();
                game.getPlayServices().rateGame();
            }
        });
        stage.addActor(rateButton);
        */

        //Sound Button resources
        /*if(prefs.getBoolean("soundOn",true))
            soundButton = new ImageButton(buttonSkin.getDrawable("soundEnable"),buttonSkin.getDrawable("soundDisable"),
                buttonSkin.getDrawable("soundDisable"));
        else
            soundButton = new ImageButton(buttonSkin.getDrawable("soundDisable"),buttonSkin.getDrawable("soundEnable"),
                    buttonSkin.getDrawable("soundEnable"));

        soundButton.setPosition(widthPercent(40),heightPercent(18));
        soundButton.addListener(new ClickListener(){
            public void clicked(InputEvent event, float x, float y){
                if(prefs.getBoolean("soundOn",true)){
                    clickSound.play();
                    prefs.putBoolean("soundOn",false);
                    soundButton.setChecked(true);
                    //soundButton.setBackground(buttonSkin.getDrawable("soundDisable"));
                }
                else{
                    prefs.putBoolean("soundOn",true);
                    soundButton.setChecked(false);
                    //soundButton.setBackground(buttonSkin.getDrawable("soundEnable"));
                }
                prefs.flush();
            }
        });
        stage.addActor(soundButton);*/

        //Info Button resources
        /*info = new ImageButton(buttonSkin.getDrawable("info"),buttonSkin.getDrawable("info"));
        info.setPosition(widthPercent(50)-info.getWidth()/2,heightPercent(25));
        info.addListener(new ClickListener(){
            public void clicked(InputEvent event, float x, float y){
                if(prefs.getBoolean("soundOn",true))
                    clickSound.play();
                game.setScreen(new HowToPlay(game));
            }
        });
        stage.addActor(info);*/
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(1, 1, 1, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        stage.act();
        batch.begin();
        stage.getBatch().begin();
        stage.getBatch().draw(background, 0, 0);//drawing background
        stage.getBatch().end();
        stage.draw();
        batch.end();

        /*batch.begin();
        batch.draw(gameName,appWidth/2-gameName.getWidth()/2,heightPercent(65));
        batch.end();*/
    }

    @Override
    public void dispose() {
        batch.dispose();
        stage.dispose();
        buttonSkin.dispose();
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
        if(keycode == Input.Keys.BACK){
            Gdx.app.exit();
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
