package com.avogine.render;

import com.avogine.game.scene.Scene;
import com.avogine.io.Window;

/**
 * @param <T> The {@link Scene} implementation this renderer should be capable of drawing.
 *
 */
public interface Render<T extends Scene> {

	/**
	 * Initialize render specific state.
	 * @param window
	 */
	public void init(Window window);
	
	/**
	 * 
	 */
	public void cleanup();
	
	/**
	 * @param window
	 * @param scene
	 */
	public void render(Window window, T scene);
	
	/**
	 * 
	 * @param width
	 * @param height
	 */
	public void resize(int width, int height);
	
}
