package com.internetwarz.basketballrush.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.internetwarz.basketballrush.Tsar;


public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();

		config.title = "MyGame";
		config.useGL30   = false;
		config.width = 800;
		config.height = 600;
		new LwjglApplication(new Tsar(new PlayServicesStub()), config);
	}
}
