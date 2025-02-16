package com.avogine.render;

import static org.lwjgl.opengl.GL11C.glClearColor;

import com.avogine.game.scene.Scene;
import com.avogine.io.Window;

/**
 * @param <T> The {@link Scene} implementation this renderer should be capable of drawing.
 *
 */
public interface SceneRender<T extends Scene> {

	/**
	 * Initialize render specific state.
	 * @param window 
	 */
	public default void init(Window window) {
		// Set a nice Avocado green default color
		glClearColor(177f / 255f, 193f / 255f, 71f / 255f, 1.0f);
	}
	
	/**
	 * @param window
	 * @param scene
	 */
	public void render(Window window, T scene);
	
	/**
	 * 
	 */
	public void cleanup();
	
}
