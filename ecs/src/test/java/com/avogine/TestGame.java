package com.avogine;

import com.avogine.ecs.TestECSScene;
import com.avogine.game.Game;
import com.avogine.io.Window;

/**
 *
 */
public class TestGame implements Game {

	private final TestECSScene scene;
	
	/**
	 * 
	 */
	public TestGame() {
		scene = new TestECSScene();
	}
	
	@Override
	public void init(Window window) {
		// Not implemented
	}

	@Override
	public void cleanup() {
		// Not implemented
	}

	@Override
	public void input(Window window) {
		// Not implemented
	}

	@Override
	public void update(float interval) {
		// Not implemented
	}

	@Override
	public void render(Window window) {
		// Not implemented
	}

	@Override
	public void resize(int width, int height) {
		// Not implemented
	}

	/**
	 * @return the scene
	 */
	public TestECSScene getScene() {
		return scene;
	}
}
