package com.avogine.ecs.experimental;

import java.lang.reflect.ParameterizedType;
import java.util.*;
import java.util.function.BiConsumer;

/**
 * @param <T> 
 * @param <U> 
 *
 */
public abstract class EntityBiQuery<T, U> implements EntityQuery, BiConsumer<T, U> {

	private final Class<T> firstType;
	private final Class<U> secondType;
	private final Set<Class<?>> paramTypes;
	
	@SuppressWarnings("unchecked")
	protected EntityBiQuery() {
		firstType = (Class<T>) ((ParameterizedType)getClass().getGenericSuperclass())
				.getActualTypeArguments()[0];
		secondType = (Class<U>) ((ParameterizedType)getClass().getGenericSuperclass())
				.getActualTypeArguments()[1];
		paramTypes = Set.of(firstType, secondType);
		System.out.println("Allocating query " + firstType + " " + secondType);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void process(Map<Class<? extends EntityComponent>, EntityComponent[]> components, int index) {
		accept((T) components.get(firstType)[index], (U) components.get(secondType)[index]);
	}
	
	/**
	 * @return
	 */
	public Class<T> getFirstType() {
		return firstType;
	}
	
	/**
	 * @return
	 */
	public Class<U> getSecondType() {
		return secondType;
	}

	@Override
	public Set<Class<?>> getParamTypes() {
		return paramTypes;
	}
}
