package com.libgdxbook.canyonbunny.models;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

public class Clouds extends Entity {
	private float length;
	private Array<TextureRegion> regClouds;
	private Array<Cloud> clouds;

	public class Cloud extends Entity {
		private TextureRegion regCloud;

		public Cloud() {
		}

		public void setRegion(TextureRegion region) {
			regCloud = region;
		}

		@Override
		public void render(SpriteBatch batch) {
			TextureRegion reg = regCloud;
			batch.draw(reg.getTexture(), position.x + origin.x, position.y
					+ origin.y, origin.x, origin.y, dimension.x, dimension.y,
					scale.x, scale.y, rotation, reg.getRegionX(),
					reg.getRegionY(), reg.getRegionWidth(),
					reg.getRegionHeight(), false, false);
		}
	}

	public Clouds(float length) {
		this.length = length;
		init();
	}

	public void init() {
		dimension.set(3.0f, 1.5f);
		regClouds = new Array<TextureRegion>();
		regClouds.add(Assets.instance.levelDecoration.cloud01);
		regClouds.add(Assets.instance.levelDecoration.cloud02);
		regClouds.add(Assets.instance.levelDecoration.cloud03);
		int distFac = 5; // distance factor
		int numClouds = (int) (length / distFac);
		clouds = new Array<Cloud>(2 * numClouds);
		for (int index = 0; index < numClouds; index++) {
			Cloud cloud = spawnCloud();
			cloud.position.x = index * distFac;
			clouds.add(cloud);
		}

	}

	private Cloud spawnCloud() {
		Cloud cloud = new Cloud();
		cloud.dimension.set(dimension);
		cloud.setRegion(regClouds.random());
		Vector2 pos = new Vector2();
		pos.x = length + 10;
		pos.y += 1.75;
		pos.y += MathUtils.random(0.0f, 0.2f)
				* (MathUtils.randomBoolean() ? 1 : -1); // randomize height
		cloud.position.set(pos);
		Vector2 velocity = new Vector2();
		velocity.x += .5f; // base speed
		velocity.x += MathUtils.random(0.0f, .75f); // plus a random speed
		velocity.x *= -1; // move left
		cloud.velocity.set(velocity);
		return cloud;
	}

	@Override
	public void render(SpriteBatch batch) {
		for (Cloud cloud : clouds) {
			cloud.render(batch);
		}
	}

	@Override
	public void update(float deltaTime) {
		// for each cloud in clouds, with index's to modify the original list
		for (int i = clouds.size - 1; i >= 0; i--) {
			Cloud cloud = clouds.get(i);
			cloud.update(deltaTime);
			// if the cloud moves out of the game world
			if (cloud.position.x < -10) {
				clouds.removeIndex(i);	// destroy the cloud
				clouds.add(spawnCloud());	// create a new one at the right edge of the screen
			}
		}
	}
}
