package com.avogine.ecs.queries;

import java.util.*;
import java.util.function.BiConsumer;

import com.avogine.ecs.*;

/**
 * @param <T> 
 * @param <U> 
 */
public abstract class EntityBiQuery<T extends EntityComponent, U extends EntityComponent> implements EntityQuery, BiConsumer<T, U> {

	protected final Class<T> first;
	protected final Class<U> second;
	
	@SuppressWarnings("unchecked")
	protected EntityBiQuery() {
		this.first = (Class<T>) getTypeParam(0);
		this.second = (Class<U>) getTypeParam(1);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void process(Map<Class<? extends EntityComponent>, EntityComponent[]> chunk, int index) {
		accept((T) chunk.get(getFirstType())[index],
				(U) chunk.get(getSecondType())[index]);
	}
	
	/**
	 * @see <a href="https://stackoverflow.com/questions/1901164/get-type-of-a-generic-parameter-in-java-with-reflection">Parameter Reflection</a>
	 * @return
	 */
	public Class<T> getFirstType() {
		return first;
	}
	
	/**
	 * @return
	 */
	public Class<U> getSecondType() {
		return second;
	}
	
	@Override
	public Set<Class<? extends EntityComponent>> getParamTypes() {
		return Set.of(getFirstType(), getSecondType());
	}

}
