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
	protected EntitySystem(EntityWorld entityWorld) {
		this.entityWorld = entityWorld;
	}
	
	/**
	 * TODO Remove?
	 * Alternatively, some form of generic game state record would need to be passed in here for systems that need additional data to process components
	 */
	public abstract void process();
	
	/**
	 * Free up any allocated memory if necessary
	 */
	public abstract void cleanup();
	
}
