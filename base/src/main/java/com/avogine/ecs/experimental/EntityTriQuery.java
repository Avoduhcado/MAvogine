package com.avogine.ecs.experimental;

import java.lang.reflect.ParameterizedType;
import java.util.Set;

import com.avogine.ecs.experimental.EntityChunk.TriConsumer;

/**
 *
 */
public abstract class EntityTriQuery<T, U, V> implements EntityQuery {

	public abstract TriConsumer<T, U, V> process();

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
	public Set<Class<?>> getParamTypes() {
		return Set.of(getFirstType(), getSecondType(), getThirdType());
	}
}
