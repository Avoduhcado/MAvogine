package com.avogine.game.scene;

import org.joml.Matrix4f;

import com.avogine.ecs.EntityWorld;

/**
 *
 */
public abstract class ECSScene extends Scene {

	protected final EntityWorld entityWorld;
	
	protected ECSScene() {
		super();
		entityWorld = new EntityWorld();
	}
	
	protected ECSScene(Matrix4f projection, Matrix4f view) {
		super(projection, view);
		entityWorld = new EntityWorld();
	}
	
	/**
	 * @return the entityWorld
	 */
	public EntityWorld getEntityWorld() {
		return entityWorld;
	}

}
