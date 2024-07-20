package com.avogine.render;

import org.lwjgl.opengl.GL;

import com.avogine.game.scene.Scene;
import com.avogine.io.Window;

/**
 * @param <T> The {@link Scene} implementation this renderer should be capable of drawing.
 *
 */
public interface SceneRender<T extends Scene> {

	/**
	 * Configure settings necessary to perform rendering.
	 * 
	 * Take caution that if you override this method you will either need to configure these OpenGL settings yourself
	 * or will at least need to call super.init().
	 */
	public default void init() {
		GL.createCapabilities();
	}
	
	/**
	 * @param window
	 * @param scene
	 */
	public abstract void render(Window window, T scene);
	
	/**
	 * 
	 */
	public abstract void cleanup();
	
}
