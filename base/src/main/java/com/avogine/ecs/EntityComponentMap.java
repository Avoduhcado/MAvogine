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
	 * @return the component contained in this map of type {@code clazz} automatically cast to that type
	 * or {@code null} if no such component exists in this map
	 */
	public <T extends EntityComponent> T getAs(Class<T> clazz) {
		return clazz.cast(get(clazz));
	}
	
}
