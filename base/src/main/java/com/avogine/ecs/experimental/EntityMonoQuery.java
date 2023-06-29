package com.avogine.ecs.experimental;

import java.lang.reflect.ParameterizedType;
import java.util.Set;
import java.util.function.Consumer;

/**
 *
 */
public abstract class EntityMonoQuery<T> implements EntityQuery {
	public abstract Consumer<T> process();
	
	/**
	 * @see <a href="https://stackoverflow.com/questions/1901164/get-type-of-a-generic-parameter-in-java-with-reflection">Parameter Reflection</a>
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public Class<T> getFirstType() {
		return (Class<T>) ((ParameterizedType)getClass().getGenericSuperclass())
				.getActualTypeArguments()[0];
	}
	
	@Override
	public Set<Class<?>> getParamTypes() {
		return Set.of(getFirstType());
	}
}
