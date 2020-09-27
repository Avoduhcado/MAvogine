package com.avogine.ecs;

/**
 *
 */
public abstract class EntitySystem {

	protected EntityWorld entityWorld;
	
	/**
	 * @param entityWorld 
	 * 
	 */
	public EntitySystem(EntityWorld entityWorld) {
		this.entityWorld = entityWorld;
	}
	
	/**
	 * 
	 */
	public abstract void process();
	
}
