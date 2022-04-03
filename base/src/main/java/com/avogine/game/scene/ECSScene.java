package com.avogine.game.scene;

import org.joml.*;

import com.avogine.ecs.*;

/**
 *
 */
public abstract class ECSScene extends Scene {

	protected final EntityWorld entityWorld;
	
	protected ECSScene(Matrix4f projection) {
		super(projection);
		entityWorld = new EntityWorld();
	}
	
	/**
	 * @return the entityWorld
	 */
	public EntityWorld getEntityWorld() {
		return entityWorld;
	}

}
