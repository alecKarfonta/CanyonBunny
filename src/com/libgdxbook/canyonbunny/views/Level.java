package com.libgdxbook.canyonbunny.views;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Array;
import com.libgdxbook.canyonbunny.models.BunnyHead;
import com.libgdxbook.canyonbunny.models.Carrot;
import com.libgdxbook.canyonbunny.models.Clouds;
import com.libgdxbook.canyonbunny.models.Entity;
import com.libgdxbook.canyonbunny.models.Feather;
import com.libgdxbook.canyonbunny.models.Goal;
import com.libgdxbook.canyonbunny.models.GoldCoin;
import com.libgdxbook.canyonbunny.models.Mountains;
import com.libgdxbook.canyonbunny.models.Ground;
import com.libgdxbook.canyonbunny.models.WaterOverlay;

public class Level {
	public static final String TAG = Level.class.getName();

	public enum BLOCK_TYPE {
		EMPTY(0, 0, 0), // black
		GOAL(255, 0, 0), // red
		ROCK(0, 255, 0), // green
		PLAYER_SPAWNPOINT(255, 255, 255), // white
		ITEM_FEATHER(255, 0, 255), // purple
		ITEM_COIN_GOLD(255, 255, 0); // yellow

		private int color;

		private BLOCK_TYPE(int r, int g, int b) {
			color = r << 24 | g << 16 | b << 8 | 0xff;
		}

		public boolean sameColor(int color) {
			return this.color == color;
		}

		public int getColor() {
			return color;
		}
	}

	public BunnyHead bunnyHead;
	public Array<GoldCoin> goldCoins;
	public Array<Feather> feathers;
	public Array<Carrot> carrots;
	public Goal goal;
	public Array<Ground> rocks;
	public Clouds clouds;
	public Mountains mountains;
	public WaterOverlay water;

	public Level(String filename) {
		init(filename);
	}

	public void init(String filename) {
		bunnyHead = null;
		rocks = new Array<Ground>();
		goldCoins = new Array<GoldCoin>();
		feathers = new Array<Feather>();
		carrots = new Array<Carrot>();

		Pixmap pixmap = new Pixmap(Gdx.files.internal(filename));
		
		int lastPixel = -1;
		for (int y = 0; y < pixmap.getHeight(); y++) {
			for (int x = 0; x < pixmap.getWidth(); x++) {
				Entity entity = null;
				float offsetHeight = 0;
				float baseHeight = pixmap.getHeight() - y;
				int currentPixel = pixmap.getPixel(x, y);
				// empty
				if (BLOCK_TYPE.EMPTY.sameColor(currentPixel)) {
					// do nothing
					// rock
				} else if (BLOCK_TYPE.ROCK.sameColor(currentPixel)) {
					if (lastPixel != currentPixel) {
						entity = new Ground();
						float heightIncreaseFactor = .5f;
						offsetHeight = -3.0f;
						entity.position.set(x, baseHeight * entity.dimension.y
								* heightIncreaseFactor + offsetHeight);
						rocks.add((Ground) entity);
					} else {
						rocks.get(rocks.size - 1).increaseLength(1);
					}
					// spawn
				} else if (BLOCK_TYPE.PLAYER_SPAWNPOINT.sameColor(currentPixel)) {
					entity = new BunnyHead();
					offsetHeight = -3.0f;
					entity.position.set(x, baseHeight * entity.dimension.y
							+ offsetHeight);
					bunnyHead = (BunnyHead) entity;
					// coin
				} else if (BLOCK_TYPE.ITEM_COIN_GOLD.sameColor(currentPixel)) {
					entity = new GoldCoin();
					offsetHeight = -1.5f;
					entity.position.set(x, baseHeight * entity.dimension.y
							+ offsetHeight);
					goldCoins.add((GoldCoin) entity);
					// feather
				} else if (BLOCK_TYPE.ITEM_FEATHER.sameColor(currentPixel)) {
					entity = new Feather();
					offsetHeight = -1.5f;
					entity.position.set(x, baseHeight * entity.dimension.y
							+ offsetHeight);
					feathers.add((Feather) entity);
					// goal
				} else if (BLOCK_TYPE.GOAL.sameColor(currentPixel)) {
					entity = new Goal();
					offsetHeight = -5.0f;
					entity.position.set(x, baseHeight + offsetHeight);
					goal = (Goal) entity;
					// unknown
				} else {
					int r = 0xff & (currentPixel >>> 24); // red channel
					int g = 0xff & (currentPixel >>> 16); // green channel
					int b = 0xff & (currentPixel >>> 8); // blue channel
					int a = 0xff & currentPixel; // alpha channel
					Gdx.app.error(TAG, "Unknown Object at (" + x + "," + y
							+ ") : " + r + " " + " " + g + " " + b + " " + a);
				}
				lastPixel = currentPixel;
			}
		}
		// decorations
		clouds = new Clouds(pixmap.getWidth());
		clouds.position.set(0, 2);
		mountains = new Mountains(pixmap.getWidth());
		mountains.position.set(-1, -1);
		water = new WaterOverlay(pixmap.getWidth());
		water.position.set(0, -3.75f);

		// free memory
		pixmap.dispose();
		Gdx.app.debug(TAG, "Level '" + filename + "' loaded");

	}

	public void render(SpriteBatch batch) {
		mountains.render(batch);
		for (Ground rock : rocks) {
			rock.render(batch);
		}
		for (GoldCoin goldCoin : goldCoins) {
			goldCoin.render(batch);
		}
		for (Feather feather : feathers) {
			feather.render(batch);
		}
		for (Carrot carrot : carrots) {
			carrot.render(batch);
		}
		
		bunnyHead.render(batch);
		water.render(batch);
		clouds.render(batch);
		goal.render(batch);
	}

	public void update(float deltaTime) {
		bunnyHead.update(deltaTime);
		for (Ground rock : rocks)
			rock.update(deltaTime);
		for (GoldCoin goldCoin : goldCoins)
			goldCoin.update(deltaTime);
		for (Feather feather : feathers)
			feather.update(deltaTime);
		for (Carrot carrot : carrots) 
			carrot.update(deltaTime);
		clouds.update(deltaTime);
	}
}
