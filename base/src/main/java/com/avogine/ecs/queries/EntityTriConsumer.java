package com.avogine.ecs.queries;

import java.lang.reflect.ParameterizedType;
import java.util.*;

import com.avogine.ecs.*;
import com.avogine.util.TriConsumer;

/**
 * @param <T> 
 * @param <U> 
 * @param <V> 
 *
 */
public abstract class EntityTriConsumer<T extends EntityComponent, U extends EntityComponent, V extends EntityComponent> implements EntityQuery, TriConsumer<T, U, V> {

	@SuppressWarnings("unchecked")
	@Override
	public void process(Map<Class<? extends EntityComponent>, EntityComponent[]> chunk, int index) {
		accept((T) chunk.get(getFirstType())[index],
				(U) chunk.get(getSecondType())[index],
				(V) chunk.get(getThirdType())[index]);
	}
	
	@SuppressWarnings("unchecked")
	public Class<T> getFirstType() {
		return (Class<T>) ((ParameterizedType)getClass().getGenericSuperclass())
				.getActualTypeArguments()[0];
	}
	
	@SuppressWarnings("unchecked")
	public Class<U> getSecondType() {
		return (Class<U>) ((ParameterizedType)getClass().getGenericSuperclass())
				.getActualTypeArguments()[1];
	}
	
	@SuppressWarnings("unchecked")
	public Class<V> getThirdType() {
		return (Class<V>) ((ParameterizedType)getClass().getGenericSuperclass())
				.getActualTypeArguments()[2];
	}

	@Override
	public Set<Class<? extends EntityComponent>> getParamTypes() {
		return Set.of(getFirstType(), getSecondType(), getThirdType());
	}

}
