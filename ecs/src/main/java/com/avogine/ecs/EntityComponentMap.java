package com.avogine.ecs;

import java.util.*;

/**
 *
 */
public class EntityComponentMap extends HashMap<Class<? extends EntityComponent>, EntityComponent> {
	private static final long serialVersionUID = 1L;
	
	/**
	 * TODO Return an {@link Optional} instead of just the object, most places should not call this regardless.
	 * @param <T>
	 * @param clazz
	 * @return the component contained in this map of type {@code clazz} automatically cast to that type
	 * or {@code null} if no such component exists in this map
	 */
	public <T extends EntityComponent> T getAs(Class<T> clazz) {
		var component = get(clazz);
		if (component.getClass().isAssignableFrom(clazz)) {
			return clazz.cast(component);
		}
		return null;
	}
	
}
