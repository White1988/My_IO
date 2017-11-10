package com.internetwarz.basketballrush.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;

public class Assets {

    //Image Resources
    Texture gameName,logo, line;
    TextureAtlas buttonAtlas;

    //Sound Resources
    Sound clickSound;

    public void load(){

        gameName = new Texture(Gdx.files.internal("images/gameName.png"));
        logo = new Texture(Gdx.files.internal("images/internetwarz.png"));
        line = new Texture(Gdx.files.internal("images/line_txtr"));


        buttonAtlas = new TextureAtlas(Gdx.files.internal("buttons.pack"));

        clickSound = Gdx.audio.newSound(Gdx.files.internal("sounds/clickSound.mp3"));
    }

    public Texture getTexture(String file){

         if(file.equals("gameName")){
            return  gameName;
        }
        else if(file.equals("internetwarz")){
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

        gameName.dispose();
        logo.dispose();
        line.dispose();


        buttonAtlas.dispose();
        clickSound.dispose();
    }
}
