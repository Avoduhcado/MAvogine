package com.avogine.game.util;

/**
 * Sub-interface of {@link Registerable} for game components that need additional termination that must occur on the main thread.
 */
public interface Terminable extends Registerable {
	/**
	 * @return the {@link Registerable} action to perform during termination
	 */
	public Registerable terminate();
}
