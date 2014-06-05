package com.libgdxbook.canyonbunny.models;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class Goal extends Entity {
	private TextureRegion regGoal;

	public Goal() {
		init();
	}

	private void init() {
		dimension.set(3.0f, 3.0f);
		regGoal = Assets.instance.levelDecoration.goal;
		if (regGoal == null) {
			System.out.println("goal image not loaded");
		}
		// Set bounding box for collision detection
		// make the box as tall as possible so the player always hits it
		bounds.set(0, Float.MIN_VALUE, 10, Float.MAX_VALUE); 
		origin.set(dimension.x / 2.0f, 0.0f);
	}

	public void render(SpriteBatch batch) {
		TextureRegion reg = null;
		reg = regGoal;
		/**/
		batch.draw(reg.getTexture(), position.x - origin.x, position.y
				- origin.y, origin.x, origin.y, dimension.x, dimension.y,
				scale.x, scale.y, rotation, reg.getRegionX(), reg.getRegionY(),
				reg.getRegionWidth(), reg.getRegionHeight(), false, false);
				/**/
	}
}
