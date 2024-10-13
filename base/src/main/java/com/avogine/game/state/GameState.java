package com.avogine.game.state;

import com.avogine.game.scene.Scene;
import com.avogine.io.Window;
import com.avogine.render.SceneRender;

/**
 * @param <T> 
 * @param <U> 
 */
public abstract class GameState<T extends Scene, U extends SceneRender<T>> {

	protected T scene;
	protected U render;

	/**
	 * @param window
	 */
	public abstract void init(Window window);
	
	/**
	 * 
	 */
	public abstract void cleanup();
	
	/**
	 * @return the scene
	 */
	public T getScene() {
		return scene;
	}
	
	/**
	 * @return the render
	 */
	public U getRender() {
		return render;
	}
	
}
