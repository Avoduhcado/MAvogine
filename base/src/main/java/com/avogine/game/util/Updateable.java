package com.avogine.game.util;

import com.avogine.game.*;

/**
 *
 */
public interface Updateable {

	/**
	 * @param frameState
	 */
	public void onUpdate(FrameState frameState);
	
	/**
	 * @param game
	 */
	public default void register(Game game) {
		game.addUpdateable(this);
	}
	
}
