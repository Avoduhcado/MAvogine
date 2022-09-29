package com.avogine.game.util;

import com.avogine.game.*;
import com.avogine.game.scene.*;

/**
 *
 */
public interface Renderable {

	/**
	 * This will be called once per frame refresh and should handle rendering the current state of the {@link Scene}.
	 * @param sceneState A snapshot of the relevant data from the current game loop.
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
