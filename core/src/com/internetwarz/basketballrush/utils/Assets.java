package com.internetwarz.basketballrush.utils;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;

public class Assets {

    //Image Resources
    TextureAtlas buttonAtlas;

    //Sound Resources
    Sound clickSound;

    public void load(){

    }

    public Texture getTexture(String file){
        return null;
    }

    public Sound getSound(){
        return clickSound;
    }

    public TextureAtlas getButtonAtlas(){
        return buttonAtlas;
    }

    public void dispose(){

    }
}
