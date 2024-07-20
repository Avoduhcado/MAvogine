package com.avogine.game.util;

import com.avogine.game.scene.Scene;
import com.avogine.io.Window;

/**
 *
 */
public non-sealed interface Renderable extends GameListener {

	/**
	 * This will be called once per frame refresh and should handle rendering the current state of the {@link Scene}.
	 * @param window
	 * @param sceneState A snapshot of the relevant data from the current game loop.
	 */
	public void onRender(Window window, SceneState sceneState);
	
}
