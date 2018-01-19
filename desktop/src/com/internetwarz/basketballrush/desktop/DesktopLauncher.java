package com.internetwarz.basketballrush.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.internetwarz.basketballrush.Xintuition;


public class DesktopLauncher {

	public static void main (String[] arg) {
		System.setProperty("user.name","CorrectUserName");
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();

		config.title = "MyGame";
		config.useGL30   = false;
		config.width = 360;//800
		config.height = 640;//600

		new LwjglApplication(new Xintuition(new PlayServicesStub(), new TurnbasedServiceStub()), config);
	}
}
