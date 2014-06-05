package com.libgdxbook.controllers;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.libgdxbook.canyonbunny.models.Entity;

public class CameraHelper {
	private static final String TAG = CameraHelper.class.getName();
	
	// settings
	private final float MAX_ZOOM_IN = .25f;
	private final float MAX_ZOOM_OUT = 10.0f;
	private final float FOLLOW_SPEED = 4.0f;

	private Vector2 position;
	private float zoom;
	private Entity target;

	public CameraHelper() {
		position = new Vector2();
		zoom = 1.0f;
	}

	public void update(float deltaTime) {
		if (!hasTarget())
			return;
		// smooth the camera's movement with linear interpolation
		position.lerp(target.position, FOLLOW_SPEED * deltaTime); 
		position.y = Math.max(-1f, position.y);	// dont move the camera below -1
	}

	public void applyTo(OrthographicCamera camera) {
		camera.position.set(position.x, position.y, 0);
		camera.zoom = zoom;
		camera.update();
	}

	public boolean hasTarget() {
		return target != null;
	}

	public boolean hasTarget(Entity target) {
		return hasTarget() && this.target.equals(target);
	}

	public void setTarget(Entity target) {
		this.target = target;
	}

	public void setPosition(float x, float y) {
		this.position.set(x, y);
	}

	public Vector2 getPosition() {
		return position;
	}

	public void addZoom(float amount) {
		this.zoom += amount;
	}

	public float getZoom() {
		return zoom;
	}

	public void setZoom(float zoom) {
		this.zoom = zoom;
	}

	public void setPosition(Vector2 position) {
		this.position = position;
	}

}
