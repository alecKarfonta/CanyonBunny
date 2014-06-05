package com.libgdxbook.canyonbunny;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.math.Interpolation;
import com.libgdxbook.canyonbunny.models.Assets;
import com.libgdxbook.canyonbunny.views.DirectedGame;
import com.libgdxbook.canyonbunny.views.MenuScreen;
import com.libgdxbook.canyonbunny.views.transitions.ScreenTransition;
import com.libgdxbook.canyonbunny.views.transitions.ScreenTransitionSlice;
import com.libgdxbook.controllers.AudioManager;

public class CanyonBunny extends DirectedGame {
	@Override
	public void create() {
		// Set Libgdx log level
		Gdx.app.setLogLevel(Application.LOG_DEBUG);
		// Load assets
		Assets.instance.init(new AssetManager());
		GamePreferences.instance.load();
		AudioManager.instance.play(Assets.instance.music.song01);
		// at the start of the application, slide the menu screen in
		ScreenTransition transition = ScreenTransitionSlice.init(2,
				ScreenTransitionSlice.UP_DOWN, 10, Interpolation.pow5Out);
		setScreen(new MenuScreen(this), transition);
		
	}
}