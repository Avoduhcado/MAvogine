package com.avogine.game;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL;

import com.avogine.Avogine;
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
	 * Initialize all game related logic and potential API resources prior to launching the game loop.
	 * </p>
	 * This method is intended to only be called directly from {@link Avogine#start()}, actual implementations of {@link Game} should
	 * override {@link #init(Window)} to handle {@code Game} specific initialization. Exercise caution when overriding this method.
	 * @param window
	 */
	public default void baseInit(Window window) {
		initRender(window);
		init(window);
	}
	
	/**
	 * TODO#12 This could likely be abstracted into some sort of EnginePrefs that are loaded via config file rather than hardcoded into the Game interface.
	 * Initialize any render API specific configurations prior to launching the game loop.
	 * <p>
	 * Presently, this calls {@link GL#createCapabilities()} to set up an OpenGL capable environment. This could be moved to something like a {@code GameOpenGL} sub type
	 * if multiple render API vendors should be supported, i.e., Vulkan.
	 * @param window 
	 */
	public default void initRender(Window window) {
		GL.createCapabilities();
		if (window.isVsync()) {
			GLFW.glfwSwapInterval(1);
		} else {
			GLFW.glfwSwapInterval(0);
		}
	}

	/**
	 * Return the maximum number of updates to attempt to perform per second.
	 * @return the maximum number of updates to attempt to perform per second.
	 */
	public default int getTargetUps() {
		return 30;
	}

	/**
	 * @return the {@link GLFWConfig}
	 */
	public default GLFWConfig getGLFWConfig() {
		return GLFWConfig.defaultConfig();
	}
	
	/**
	 * @return the {@link InputConfig}
	 */
	public default InputConfig getInputConfig() {
		return InputConfig.defaultConfig();
	}
	
}
