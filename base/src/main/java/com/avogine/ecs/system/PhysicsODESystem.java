package com.avogine.ecs.system;

import org.ode4j.ode.*;

import com.avogine.ecs.EntitySystem;
import com.avogine.game.Game;
import com.avogine.game.scene.ECSScene;
import com.avogine.game.util.*;

/**
 *
 */
public class PhysicsODESystem extends EntitySystem implements Updateable, Cleanupable {

	private DWorld world;
	
	/**
	 * 
	 */
	public PhysicsODESystem() {
	}

	@Override
	public void onRegister(Game game) {
		OdeHelper.initODE();
		
		world = OdeHelper.createWorld();
	}

	@Override
	public void onUpdate(GameState gameState) {
		if (gameState.scene() instanceof ECSScene scene) {
			stepWorld(gameState.delta(), scene);
		}
	}
	
	private void stepWorld(float delta, ECSScene scene) {
		world.step(delta);
	}

	@Override
	public void onCleanup() {
		OdeHelper.closeODE();
	}
	
}
