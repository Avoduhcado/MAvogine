package com.avogine.game;

/**
 *
 */
public interface Updateable {

	/**
	 * @param interval The time passed in the current frame in milliseconds.
	 */
	public void onUpdate(float interval);
	
}
