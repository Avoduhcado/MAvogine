package com.avogine.ecs.queries;

import java.lang.reflect.ParameterizedType;
import java.util.*;
import java.util.function.BiConsumer;

import com.avogine.ecs.*;

/**
 *
 */
public abstract class EntityBiQuery<T extends EntityComponent, U extends EntityComponent> implements EntityQuery, BiConsumer<T, U> {

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
	
	@Override
	public Set<Class<? extends EntityComponent>> getParamTypes() {
		return Set.of(getFirstType(), getSecondType());
	}

}
