package com.avogine.ecs;

import java.util.HashMap;

/**
 *
 */
public class EntityComponentMap extends HashMap<Class<? extends EntityComponent>, EntityComponent> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * @param <T>
	 * @param clazz
	 * @return
	 */
	public <T extends EntityComponent> T getAs(Class<T> clazz) {
		return (T) get(clazz);
	}
}
