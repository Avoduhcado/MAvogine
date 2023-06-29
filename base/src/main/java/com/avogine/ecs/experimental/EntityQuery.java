package com.avogine.ecs.experimental;

import java.util.*;

/**
 *
 */
public interface EntityQuery {

	/**
	 * @param components
	 * @param index 
	 */
	public void process(Map<Class<? extends EntityComponent>, EntityComponent[]> components, int index);
	
	/**
	 * TODO This should enforce EntityComponent hierarchy
	 * @return
	 */
	public Set<Class<?>> getParamTypes();
	
}
