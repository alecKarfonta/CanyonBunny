package com.libgdxbook.canyonbunny.views;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.libgdxbook.canyonbunny.Constants;
import com.libgdxbook.canyonbunny.GamePreferences;
import com.libgdxbook.canyonbunny.models.Assets;
import com.libgdxbook.controllers.WorldController;

// this class draws the current state of the game

public class WorldRenderer implements Disposable {
	private OrthographicCamera camera, cameraGUI;
	private SpriteBatch batch;
	private WorldController worldController;
	private static final boolean DEBUG_DRAW_BOX2D_WORLD = true;
	private Box2DDebugRenderer b2debugRenderer;

	private ShaderProgram shaderMonochrome;

	public WorldRenderer(WorldController worldController) {
		this.worldController = worldController;
		init();
	}

	public void init() {
		batch = new SpriteBatch();
		camera = new OrthographicCamera(Constants.VIEWPORT_WIDTH,
				Constants.VIEWPORT_HEIGHT);
		camera.position.set(0, 0, 0);
		camera.update();

		cameraGUI = new OrthographicCamera(Constants.VIEWPORT_GUI_WIDTH,
				Constants.VIEWPORT_GUI_HEIGHT);
		cameraGUI.position.set(0, 0, 0);
		cameraGUI.setToOrtho(true); // flip y
		cameraGUI.update();
		b2debugRenderer = new Box2DDebugRenderer();
		shaderMonochrome = new ShaderProgram(
				Gdx.files.internal(Constants.shaderMonochromeVertex),
				Gdx.files.internal(Constants.shaderMonochromeFragment));
		if (!shaderMonochrome.isCompiled()) {
			String msg = "Could not compile shader program: "
					+ shaderMonochrome.getLog();
			throw new GdxRuntimeException(msg);
		}
	}

	public void render() {
		renderWorld(batch);
		renderGUI(batch);
	}

	public void renderWorld(SpriteBatch batch) {
		worldController.cameraHelper.applyTo(camera);
		batch.setProjectionMatrix(camera.combined);
		batch.begin();
		if (GamePreferences.instance.useMonochromeShader) {
		       batch.setShader(shaderMonochrome);
		       // set the varying variable u_amount to 1 so the gray scale effect is 100%
		       shaderMonochrome.setUniformf("u_amount", 1.0f);
		       }
		worldController.level.render(batch); // render the world using the shader
		batch.setShader(null);	// reset the shader to libgdx default
		batch.end();
		if (DEBUG_DRAW_BOX2D_WORLD) {
			b2debugRenderer.render(worldController.world, camera.combined);
		}
	}

	public void renderGUI(SpriteBatch batch) {
		batch.setProjectionMatrix(cameraGUI.combined);
		batch.begin();
		renderGUIScore(batch);
		renderGUIFeatherPowerup(batch);
		renderGUIExtraLives(batch);
		if (GamePreferences.instance.showFpsCounter) {
			renderGUIFpsCounter(batch);
		}
		renderGUIGameOverMessage(batch);
		batch.end();
	}

	public void renderGUIScore(SpriteBatch batch) {
		float x = -15;
		float y = -15;
		float offsetX = 50;
		float offsetY = 50;
		// slowly increment the visual score to the actual score
		if (worldController.scoreVisual < worldController.score) {
			// while the visual score is changing shake the sprite
			long shakeAlpha = System.currentTimeMillis() % 360;
			float shakeDist = 1.5f;
			offsetX += MathUtils.sinDeg(shakeAlpha * 2.2f) * shakeDist;
			offsetY += MathUtils.sinDeg(shakeAlpha * 2.9f) * shakeDist;
		}
		batch.draw(Assets.instance.goldCoin.goldCoin, x, y, // position
				offsetX, offsetY, // origin
				100, 100, // dimensions
				0.35f, -0.35f, // scale
				0); // rotation
		// using the default big font, draw the visual score
		Assets.instance.fonts.defaultBig.draw(batch, ""
				+ (int) worldController.scoreVisual, x + 75, y + 37);
	}

	public void renderGUIExtraLives(SpriteBatch batch) {
		float x = cameraGUI.viewportWidth - 50 - Constants.LIVES_START * 50;
		float y = -15;
		for (int index = 0; index < Constants.LIVES_START; index++) {
			if (worldController.lives <= index) {
				batch.setColor(.5f, .5f, .5f, .5f);
			}
			batch.draw(Assets.instance.bunny.head, x + (index * 50), y, 50, 50,
					120, 100, .25f, -.35f, 0);
			batch.setColor(1, 1, 1, 1); // reset batch color

		}

		if (worldController.lives >= 0
				&& worldController.livesVisual > worldController.lives) {
			int i = worldController.lives;
			float alphaColor = Math.max(0, worldController.livesVisual
					- worldController.lives - 0.5f);
			float alphaScale = 0.35f * (2 + worldController.lives - worldController.livesVisual) * 2;
			float alphaRotate = -45 * alphaColor;
			batch.setColor(1.0f, 0.7f, 0.7f, alphaColor);
			batch.draw(Assets.instance.bunny.head, x + i * 50, y, 50, 50, 120,
					100, alphaScale, -alphaScale, alphaRotate);
			batch.setColor(1, 1, 1, 1);
		}

	}

	private void renderGUIGameOverMessage(SpriteBatch batch) {
		float x = cameraGUI.viewportWidth / 2;
		float y = cameraGUI.viewportHeight / 2;
		if (worldController.isGameOver()) {
			BitmapFont fontGameOver = Assets.instance.fonts.defaultBig;
			fontGameOver.setColor(1, 0.75f, 0.25f, 1);
			fontGameOver.drawMultiLine(batch, "GAME OVER", x, y, 0,
					BitmapFont.HAlignment.CENTER);
			fontGameOver.setColor(1, 1, 1, 1);
		}
	}

	private void renderGUIFeatherPowerup(SpriteBatch batch) {
		float x = -15;
		float y = 30;
		float timeLeftFeatherPowerup = worldController.level.bunnyHead.timeLeftFeatherPowerup;
		if (timeLeftFeatherPowerup > 0) {
			// Start icon fade in/out if the left power-up time
			// is less than 4 seconds. The fade interval is set
			// to 5 changes per second.
			if (timeLeftFeatherPowerup < 4) {
				if (((int) (timeLeftFeatherPowerup * 5) % 2) != 0) {
					batch.setColor(1, 1, 1, 0.5f);
				}
			}
			batch.draw(Assets.instance.feather.feather, x, y, 50, 50, 100, 100,
					0.35f, -0.35f, 0);
			batch.setColor(1, 1, 1, 1);
			Assets.instance.fonts.defaultSmall.draw(batch, ""
					+ (int) timeLeftFeatherPowerup, x + 60, y + 57);
		}
	}

	public void renderGUIFpsCounter(SpriteBatch batch) {
		float x = cameraGUI.viewportWidth - 55;
		float y = cameraGUI.viewportHeight - 15;
		int fps = Gdx.graphics.getFramesPerSecond();
		BitmapFont fpsFont = Assets.instance.fonts.defaultNormal;
		if (fps >= 45) {
			fpsFont.setColor(Color.GREEN);
		} else if (fps >= 30) {
			fpsFont.setColor(Color.YELLOW);
		} else {
			fpsFont.setColor(Color.RED);
		}
		fpsFont.draw(batch, "FPS: " + fps, x, y);
		fpsFont.setColor(Color.WHITE);
	}

	public void resize(int width, int height) {
		camera.viewportWidth = (Constants.VIEWPORT_HEIGHT / height) * width;
		camera.update();

		cameraGUI.viewportHeight = Constants.VIEWPORT_GUI_HEIGHT;
		cameraGUI.viewportWidth = Constants.VIEWPORT_GUI_WIDTH;
		cameraGUI.position.set(cameraGUI.viewportWidth / 2,
				cameraGUI.viewportHeight / 2, 0);
		camera.update();

	}

	@Override
	public void dispose() {
		batch.dispose();

	     shaderMonochrome.dispose();
	}
}
