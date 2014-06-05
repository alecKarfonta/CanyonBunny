package com.libgdxbook.canyonbunny.models;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

public class Ground extends Entity {

	private TextureRegion regEdge, regMiddle;
	private int length;

	private final float FLOAT_CYCLE_TIME = 2.0f;
	private final float FLOAT_AMPLITUDE = 0.25f;
	private float floatCycleTimeLeft;
	private boolean floatingDownwards;
	private Vector2 floatTargetPosition;

	public Ground() {
		init();
	}

	public void init() {
		dimension.set(1, 1.5f);
		regEdge = Assets.instance.rock.edge;
		regMiddle = Assets.instance.rock.middle;
		setLength(1);

		floatingDownwards = false;
		floatCycleTimeLeft = MathUtils.random(0, FLOAT_CYCLE_TIME / 2); // randomize
																		// movement
		floatTargetPosition = null;
	}

	@Override
	public void update(float deltaTime) {
		super.update(deltaTime);
		floatCycleTimeLeft -= deltaTime; // decrement time left
		if (floatTargetPosition == null) // if there is no target position
			floatTargetPosition = new Vector2(position); // create one
		if (floatCycleTimeLeft <= 0) { // if the cycle is over
			floatCycleTimeLeft = FLOAT_CYCLE_TIME; // reset cycle time
			floatingDownwards = !floatingDownwards; // reverse direction
			body.setLinearVelocity(0, FLOAT_AMPLITUDE
					* (floatingDownwards ? -1 : 1));
		} else {
			body.setLinearVelocity(body.getLinearVelocity().scl(0.98f));
		}
		// new y = the amplitude * direction
		floatTargetPosition.y += FLOAT_AMPLITUDE * (floatingDownwards ? -1 : 1);

		// lerp to new position
		// position.lerp(floatTargetPosition, deltaTime);
	}

	public void setLength(int length) {
		this.length = length;
		bounds.set(0, 0, dimension.x * length, dimension.y);
	}

	public void increaseLength(int amount) {
		setLength(length + amount);
	}

	@Override
	public void render(SpriteBatch batch) {
		TextureRegion reg = null;
		float relX = 0;
		float relY = 0;
		reg = regEdge;
		relX -= dimension.x / 4;
		batch.draw(reg.getTexture(), position.x + relX, position.y + relY,
				origin.x, origin.y, dimension.x / 4, dimension.y, scale.x,
				scale.y, rotation, reg.getRegionX(), reg.getRegionY(),
				reg.getRegionWidth(), reg.getRegionHeight(), false, false);

		relX = 0;
		reg = regMiddle;
		for (int index = 0; index < length; index++) {
			batch.draw(reg.getTexture(), position.x + relX, position.y + relY,
					origin.x, origin.y, dimension.x, dimension.y, scale.x,
					scale.y, rotation, reg.getRegionX(), reg.getRegionY(),
					reg.getRegionWidth(), reg.getRegionHeight(), false, false);
			relX += dimension.x;
		}

		// draw right edge
		reg = regEdge;
		batch.draw(reg.getTexture(), position.x + relX, position.y + relY,
				origin.x + dimension.x / 8, origin.y, dimension.x / 4,
				dimension.y, scale.x, scale.y, rotation, reg.getRegionX(),
				reg.getRegionY(), reg.getRegionWidth(), reg.getRegionHeight(),
				true, false);
	}

}
