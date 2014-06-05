package com.libgdxbook.canyonbunny.models;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

public class Mountains extends Entity {
	private TextureRegion regMountainLeft, regMountainRight;
	private int length;

	public Mountains(int length) {
		this.length = length;
		init();
	}

	public void init() {
		dimension.set(10, 2);

		regMountainLeft = Assets.instance.levelDecoration.mountainLeft;
		regMountainRight = Assets.instance.levelDecoration.mountainRight;

		origin.x = -dimension.x * 2;
		length += dimension.x * 2;
	}

	public void updateScrollPosition(Vector2 camPosition) {
		position.set(camPosition.x, position.y);
	}

	private void drawMountain(SpriteBatch batch, float offsetX, float offsetY,
			float tintColor, float parallaxSpeedX) {
		TextureRegion reg = null;
		batch.setColor(tintColor, tintColor, tintColor, 1);
		float relX = dimension.x * offsetX;
		float relY = dimension.y * offsetY;

		int mountainLength = 0;
		mountainLength += MathUtils.ceil(length / (2 * dimension.x)
				* (1 - parallaxSpeedX));

		for (int index = 0; index < mountainLength; index++) {
			reg = regMountainLeft;
			batch.draw(reg.getTexture(), origin.x + relX + position.x
					* parallaxSpeedX, origin.y + relY + position.y, origin.x,
					origin.y, dimension.x, dimension.y, scale.x, scale.y,
					rotation, reg.getRegionX(), reg.getRegionY(),
					reg.getRegionWidth(), reg.getRegionHeight(), false, false);
			relX += dimension.x;

			reg = regMountainRight;
			batch.draw(reg.getTexture(), origin.x + relX + position.x
					* parallaxSpeedX, origin.y + relY + position.y, origin.x,
					origin.y, dimension.x, dimension.y, scale.x, scale.y,
					rotation, reg.getRegionX(), reg.getRegionY(),
					reg.getRegionWidth(), reg.getRegionHeight(), false, false);
			relX += dimension.x;
		}
		batch.setColor(1, 1, 1, 1); // reset color from tint
	}

	@Override
	public void render(SpriteBatch batch) {
		// parallax scrolling
		// 80% distant mountains (dark gray)
		drawMountain(batch, 0.5f, 0.5f, 0.5f, 0.8f);
		// 50% distant mountains (gray)
		drawMountain(batch, 0.25f, 0.25f, 0.7f, 0.5f);
		// 30% distant mountains (light gray)
		drawMountain(batch, 0.0f, 0.0f, 0.9f, 0.3f);

	}

}
