package com.avogine.game;

import com.avogine.io.Window;

/**
 *
 */
public interface Game {
	
	/**
	 * Initialize all game logic to start the game loop.
	 * @param window
	 */
	public void init(Window window);
	
	/**
	 * Free up any allocated memory defined from this object.
	 * </p>
	 * Typically this just consists of calling {@code cleanup()} on any objects that were created
	 * in the {@code init()} method like meshes, shaders, etc.
	 */
	public void cleanup();
	
	/**
	 * Process user input.
	 * </p>
	 * Stored window input state, such as mouse movement, is cleared after this call.
	 * @param window 
	 */
	public void input(Window window);
	
	/**
	 * Advance game logic at a fixed time step determined by {@code interval}.
	 * </p>
	 * This frequency of this operation may not match the frame render frequency and is generally constrained by {@link #getTargetUps()}.
	 * @param interval The time elapsed between updates in seconds
	 */
	public void update(float interval);
	
	/**
	 * Draw a single frame.
	 * </p>
	 * The frequency of this operation is independent from logic updates and is generally constrained by {@link Window#getTargetFps()}.
	 * @param window The window to render to.
	 */
	public void render(Window window);
	
	/**
	 * @param width
	 * @param height
	 */
	public void resize(int width, int height);
	
	/**
	 * Return the maximum number of updates to attempt to perform per second.
	 * @return the maximum number of updates to attempt to perform per second.
	 */
	public default int getTargetUps() {
		return 30;
	}
}
