package com.avogine.game.state;

import com.avogine.game.scene.Scene;
import com.avogine.io.Window;
import com.avogine.render.Render;

/**
 * @param <S> 
 * @param <R> 
 */
public interface GameState<S extends Scene, R extends Render<S>> {
	
	/**
	 * @param window
	 */
	public default void init(Window window) {
		scene().init(window);
		render().init(window);
	}
	
	/**
	 * 
	 */
	public default void cleanup() {
		scene().cleanup();
		render().cleanup();
	}
	
	/**
	 * @return the scene
	 */
	public S scene();
	
	/**
	 * @return the render
	 */
	public R render();
	
}
