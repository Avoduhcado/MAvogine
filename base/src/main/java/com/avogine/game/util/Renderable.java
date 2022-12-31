package com.avogine.game.util;

import com.avogine.game.scene.Scene;

/**
 *
 */
public non-sealed interface Renderable extends Registerable {

	/**
	 * This will be called once per frame refresh and should handle rendering the current state of the {@link Scene}.
	 * @param sceneState A snapshot of the relevant data from the current game loop.
	 */
	public void onRender(SceneState sceneState);
	
}
