package com.avogine.ecs;

import java.util.*;

/**
 *
 */
public interface EntityQuery {

	/**
	 * @param chunk
	 * @param index
	 */
	public void process(Map<Class<? extends EntityComponent>, EntityComponent[]> chunk, int index);
	
	/**
	 * @return a Set of all generic parameter types that this query should search for.
	 */
	public Set<Class<? extends EntityComponent>> getParamTypes();
	
}
