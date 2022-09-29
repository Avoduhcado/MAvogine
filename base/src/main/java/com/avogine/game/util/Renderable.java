package com.avogine.game.util;

import com.avogine.game.*;

/**
 *
 */
public interface Renderable {

	/**
	 * @param sceneState
	 */
	public void onRender(SceneState sceneState);
	
	/**
	 * Register this {@link Renderable} with the given {@link Game}.
	 * @param game The {@code Game} to register this {@code Renderable} with.
	 */
	public default void register(Game game) {
		game.addRenderable(this);
	}
	
}
