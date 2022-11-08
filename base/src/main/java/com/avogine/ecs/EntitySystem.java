package com.avogine.ecs;

import com.avogine.game.Game;
import com.avogine.game.util.Registerable;

/**
 * 
 */
public abstract class EntitySystem implements Registerable {

	protected EntitySystem(Game game) {
		game.addToRegisterQueue(this);
	}
	
}
