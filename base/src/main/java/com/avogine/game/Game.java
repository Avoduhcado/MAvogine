package com.avogine.game;

import org.lwjgl.opengl.GL;

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
	 * Initialize any render API specific configurations prior to launching the game loop.
	 * <p>
	 * Presently, this just calls {@link GL#createCapabilities()} to set up an OpenGL capable environment. Exercise caution when 
	 * overriding this method.
	 */
	public default void initRender() {
		GL.createCapabilities();
	}
	
	/**
	 * @return
	 */
	public default GLFWConfig getGLFWConfig() {
		return GLFWConfig.defaultConfig();
	}
	
	/**
	 * @return
	 */
	public default InputConfig getInputConfig() {
		return InputConfig.defaultConfig();
	}
	
}
