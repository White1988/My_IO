package com.internetwarz.basketballrush;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.internetwarz.basketballrush.utils.Assets;

public class Tsar extends Game {
	public SpriteBatch batch;
	public BitmapFont font;
	public FreeTypeFontGenerator generator;
    private static PlayServices playServices;
	public Assets assets;

    public Tsar(PlayServices playServices)
    {
        this.playServices = playServices;
    }


	public static PlayServices getPlayServices() {
		return playServices;
	}

	@Override
	public void create () {
		batch = new SpriteBatch();
		generator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/Magistral Bold.TTF"));
		FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
		parameter.size = 25;
		parameter.color= Color.valueOf("#506878");
		parameter.borderStraight = false;
		parameter.borderWidth = 1;
		parameter.borderColor = Color.valueOf("#e2e3e7");
		parameter.shadowColor = Color.valueOf("#141a1e");
		//parameter.shadowOffsetX = -1;
		//parameter.shadowOffsetY = -1;
		parameter.characters = FreeTypeFontGenerator.DEFAULT_CHARS + "абвгдеёжзийклмнопрстуфхцчшщъыьэюяАБВГДЕЁЖЗИЙКЛМНОПРСТУФХЦЧШЩЪЫЬЭЮЯ";
		font=generator.generateFont(parameter);

		//Loading all the assets in the assets class
		assets = new Assets();

        //Changing the screen to display splash screen
		this.setScreen(new SplashScreen(this)); //todo uncomment
		//this.setScreen(new TsarGameplayScreen(this));
	}

	@Override
	public void render () {
		super.render();
	}

	@Override
	public void dispose () {
		batch.dispose();
		generator.dispose();
		font.dispose();
        assets.dispose();
	}

}

