package com.avogine.ecs.queries;

import java.util.*;

import com.avogine.ecs.*;
import com.avogine.util.TriConsumer;

/**
 * @param <T> 
 * @param <U> 
 * @param <V> 
 *
 */
public abstract class EntityTriQuery<T extends EntityComponent, U extends EntityComponent, V extends EntityComponent> implements EntityQuery, TriConsumer<T, U, V> {

	private final Class<T> first;
	private final Class<U> second;
	private final Class<V> third;
	
	/**
	 * 
	 */
	@SuppressWarnings("unchecked")
	protected EntityTriQuery() {
		first = (Class<T>) getTypeParam(0);
		second = (Class<U>) getTypeParam(1);
		third = (Class<V>) getTypeParam(2);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void process(Map<Class<? extends EntityComponent>, EntityComponent[]> chunk, int index) {
		accept((T) chunk.get(getFirstType())[index],
				(U) chunk.get(getSecondType())[index],
				(V) chunk.get(getThirdType())[index]);
	}
	
	public Class<T> getFirstType() {
		return first;
	}
	
	public Class<U> getSecondType() {
		return second;
	}
	
	public Class<V> getThirdType() {
		return third;
	}

	@Override
	public Set<Class<? extends EntityComponent>> getParamTypes() {
		return Set.of(getFirstType(), getSecondType(), getThirdType());
	}

}
