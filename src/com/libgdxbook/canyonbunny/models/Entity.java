package com.libgdxbook.canyonbunny.models;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;

public abstract class Entity {
	public Vector2 position, velocity, terminalVelocity, friction,
			acceleration, dimension, origin, scale;
	public Rectangle bounds;
	public Body body;
	public FixtureDef fixtureDef;
	public BodyDef bodyDef;
	public float rotation;
	public float stateTime;
	public Animation animation;

	public Entity() {
		position = new Vector2();
		dimension = new Vector2(1, 1);
		origin = new Vector2();
		scale = new Vector2(1, 1);
		rotation = 0;
		velocity = new Vector2();
		terminalVelocity = new Vector2(1, 1);
		friction = new Vector2();
		acceleration = new Vector2();
		bounds = new Rectangle();

	}

	protected void updateMotionX(float deltaTime) {
		// if is moving
		if (velocity.x != 0) {
			// apply friction
			if (velocity.x > 0) {
				velocity.x = Math.max(velocity.x - friction.x * deltaTime, 0);
			} else {
				velocity.x = Math.min(velocity.x + friction.x * deltaTime, 0);
			}
		}
		// apply acceleration
		velocity.x += acceleration.x * deltaTime;
		// always less than the terminal velocity
		velocity.x = MathUtils.clamp(velocity.x, -terminalVelocity.x,
				terminalVelocity.x);
	}

	protected void updateMotionY(float deltaTime) {
		if (velocity.y != 0) {
			// Apply friction
			if (velocity.y > 0) {
				velocity.y = Math.max(velocity.y - friction.y * deltaTime, 0);
			} else {
				velocity.y = Math.min(velocity.y + friction.y * deltaTime, 0);
			}
		}
		// Apply acceleration
		velocity.y += acceleration.y * deltaTime;
		// Make sure the object's velocity does not exceed the
		// positive or negative terminal velocity
		velocity.y = MathUtils.clamp(velocity.y, -terminalVelocity.y,
				terminalVelocity.y);
	}

	public void update(float deltaTime) {
		stateTime += deltaTime;
		// if the entity does not have a box2d body, handle the physics
		if (body == null) {
			updateMotionX(deltaTime);
			updateMotionY(deltaTime);
			// Move to the new position
			position.x += velocity.x * deltaTime;
			position.y += velocity.y * deltaTime;
			// else update the entity position based on the body's position
		} else {
			position.set(body.getPosition());
			rotation = body.getAngle() * MathUtils.radiansToDegrees;
		}
	}
	
	// changes the entity's animation and starts it at 0
	public void setAnimation(Animation animation) {
		this.animation = animation;
		stateTime = 0;
	}

	public abstract void render(SpriteBatch batch);

}
