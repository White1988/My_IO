package com.internetwarz.basketballrush.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.internetwarz.basketballrush.BasketBallRush;


public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();

		config.title = "MyGame";
		config.useGL30   = false;
		config.width = 480;
		config.height = 320;
		new LwjglApplication(new BasketBallRush(new PlayServicesStub()), config);
	}
}
