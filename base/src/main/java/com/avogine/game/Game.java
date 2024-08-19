package com.avogine.game;

import com.avogine.io.Window;
import com.avogine.io.config.*;

/**
 *
 */
public interface Game {
	
	/**
	 * Initialize all relevant game logic to start the game loop.
	 * @param window
	 */
	public void init(Window window);
	
	/**
	 * @param window
	 */
	public void input(Window window);
	
	/**
	 * Update any relevant entities or systems at a fixed time step determined by {@code interval}.
	 * @param interval The time elapsed between updates in seconds
	 */
	public void update(float interval);
	
	/**
	 * Draw the current scene.
	 * <p>
	 * This should be called exactly once per frame.
	 * <p>
	 * <b>XXX Should explicit calls to GL methods be avoided from this implementation? And be dependent on something deeper? I don't
	 * particularly care about abstract rendering methods at the moment.</b>
	 * @param window The window to render the scene to.
	 */
	public void render(Window window);
	
	/**
	 * @param width
	 * @param height
	 */
	public default void resize(int width, int height) {
		
	}
	
	/**
	 * Free up any allocated memory defined from this object.
	 * <p>
	 * Typically this just consists of calling {@code cleanup()} on any objects that were created
	 * in the {@code init()} method like meshes, shaders, etc.
	 */
	public void cleanup();
	
	/**
	 * Return the maximum number of updates to attempt to perform per second.
	 * @return the maximum number of updates to attempt to perform per second.
	 */
	public default int getTargetUps() {
		return 30;
	}
	
	/**
	 * Return the {@link GLFWConfig} to apply when running this {@link Game}.
	 * <p>
	 * By default this returns {@link GLFWConfig#defaultConfig()} to setup a standardly acceptable configuration.
	 * @return the {@link GLFWConfig} to apply when running this {@link Game}.
	 */
	public default GLFWConfig getGLFWConfig() {
		return GLFWConfig.defaultConfig();
	}
	
	/**
	 * Return the {@link InputConfig} to apply to the {@link Window} this {@link Game} is running in.
	 * <p>
	 * By default this returns {@link InputConfig#defaultConfig()} to setup a standardly acceptable configuration.
	 * @return the {@link InputConfig} to apply to the {@link Window} this {@link Game} is running in.
	 */
	public default InputConfig getInputConfig() {
		return InputConfig.defaultConfig();
	}
	
}
