package com.avogine.game;

import java.util.*;

import com.avogine.io.*;

/**
 *
 */
public abstract class Game {

	protected List<Updateable> updateables;
	
	protected Game() {
		updateables = new ArrayList<>();
	}
	
	/**
	 * Initialize all relevant game logic to start the game loop.
	 * @param window
	 */
	public abstract void init(Window window);
	
	/**
	 * Update any relevant entities or systems at a fixed time step determined by {@code interval}.
	 * @param interval The time elapsed between updates in seconds
	 */
	public void update(float interval) {
		updateables.forEach(update -> update.onUpdate(interval));
	}
	
	/**
	 * Draw the current scene.
	 * <p>
	 * This should be called exactly once per frame.
	 * <p>
	 * <b>XXX Should explicit calls to GL methods be avoided from this implementation? And be dependent on something deeper? I don't
	 * particularly care about abstract rendering methods at the moment.</b>
	 */
	public abstract void render();
	
	/**
	 * Free up any allocated memory defined from this object.
	 * <p>
	 * Typically this just consists of calling {@code cleanup()} on any objects that were created
	 * in the {@code init()} method like meshes, shaders, etc.
	 */
	public abstract void cleanup();
	
}
