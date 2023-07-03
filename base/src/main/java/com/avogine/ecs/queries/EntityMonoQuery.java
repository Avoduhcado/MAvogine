package com.avogine.ecs.queries;

import java.lang.reflect.ParameterizedType;
import java.util.*;
import java.util.function.Consumer;

import com.avogine.ecs.*;

/**
 *
 */
public abstract class EntityMonoQuery<T extends EntityComponent> implements EntityQuery, Consumer<T> {

	@SuppressWarnings("unchecked")
	@Override
	public void process(Map<Class<? extends EntityComponent>, EntityComponent[]> chunk, int index) {
		accept((T) chunk.get(getFirstType())[index]);
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
	
	@Override
	public Set<Class<? extends EntityComponent>> getParamTypes() {
		return Set.of(getFirstType());
	}

}
