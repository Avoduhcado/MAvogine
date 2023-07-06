package com.avogine.ecs;

import java.lang.reflect.*;
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
	
	public default Type getTypeParam(int index) {
		return ((ParameterizedType)getClass().getGenericSuperclass())
				.getActualTypeArguments()[index];
	}
	
	/**
	 * @return a Set of all generic parameter types that this query should search for.
	 */
	public Set<Class<? extends EntityComponent>> getParamTypes();
	
}
