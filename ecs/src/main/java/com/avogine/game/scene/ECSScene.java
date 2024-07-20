package com.avogine.game.scene;

import org.joml.Matrix4f;

import com.avogine.ecs.EntityManager;

/**
 *
 */
public abstract class ECSScene extends SwappableScene {

	protected final EntityManager entityManager;
	
	protected ECSScene() {
		this(new Matrix4f(), new Matrix4f());
	}
	
	protected ECSScene(Matrix4f projection, Matrix4f view) {
		super(projection, view);
		entityManager = new EntityManager();
	}
	
	/**
	 * @return the entityManager
	 */
	public EntityManager getEntityManager() {
		return entityManager;
	}

}
