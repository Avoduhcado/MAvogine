package com.avogine.game.util;

import com.avogine.game.scene.Scene;

/**
 * Interface for marking an object as something that should be updated once per game loop.
 */
public non-sealed interface Updateable extends GameListener {

	/**
	 * This will be called once per game loop and should be where any recurring logic is performed.
	 * @param scene The currently loaded Scene.
	 * @param delta The amount of time that has passed since the last frame was updated in fractions of a second.
	 */
	public void onUpdate(Scene scene, float delta);
	
}
