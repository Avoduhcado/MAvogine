package com.avogine.ecs;

import com.avogine.io.*;

/**
 *
 */
public abstract class EntitySystem {

	protected boolean initialized;
	
	/**
	 * Initialize any relevant parts of this system.
	 * @param window
	 */
	public abstract void init(Window window);
	
	/**
	 * Free up any allocated memory if necessary.
	 */
	public abstract void cleanup();
	
}
