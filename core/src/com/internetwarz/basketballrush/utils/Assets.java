package com.internetwarz.basketballrush.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;

public class Assets {

    //Image Resources
    Texture logo, line;
    TextureAtlas buttonAtlas;

    //Sound Resources
    Sound clickSound;

    public void load(){

        logo = new Texture(Gdx.files.internal("images/internetwarz.png"));
        line = new Texture(Gdx.files.internal("images/line_txtr.png"));


        buttonAtlas = new TextureAtlas(Gdx.files.internal("buttons.pack"));

        clickSound = Gdx.audio.newSound(Gdx.files.internal("sounds/clickSound.mp3"));
    }

    public Texture getTexture(String file){


        if(file.equals("internetwarz")){
            return  logo;
        }
        else if(file.equals("line_txtr"))
            return line;
        else return  null;
    }

    public Sound getSound(){
        return clickSound;
    }

    public TextureAtlas getButtonAtlas(){
        return buttonAtlas;
    }

    public void dispose(){
        logo.dispose();
        line.dispose();


        buttonAtlas.dispose();
        clickSound.dispose();
    }
}
