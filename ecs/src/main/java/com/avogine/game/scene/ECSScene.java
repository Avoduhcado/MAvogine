package com.avogine.game.scene;

import com.avogine.ecs.EntityManager;

/**
 *
 */
public abstract class ECSScene extends Scene {

	protected final EntityManager entityManager;
	
	protected ECSScene() {
		this(new Projection(640, 480), new Camera());
	}
	
	protected ECSScene(Projection projection, Camera view) {
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
