package com.avogine.ecs;

import com.avogine.game.*;
import com.avogine.io.*;

/**
 *
 */
public abstract class EntitySystem {

	protected boolean initialized;
	
	/**
	 * Initialize any relevant parts of this system.
	 * @param game 
	 * @param window
	 */
	public abstract void init(Game game, Window window);
	
}
