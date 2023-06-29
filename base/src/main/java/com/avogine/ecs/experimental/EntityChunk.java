package com.avogine.ecs.experimental;

import java.util.*;
import java.util.function.BiConsumer;

/**
 *
 */
public class EntityChunk {

	private int chunkSize;
	private final Map<Class<? extends EntityComponent>, EntityComponent[]> components;
	
	/**
	 * @param archetype 
	 * 
	 */
	public EntityChunk(Set<Class<? extends EntityComponent>> archetype) {
		components = new HashMap<>();
		for (Class<? extends EntityComponent> clazz : archetype) {
			components.put(clazz, new EntityComponent[512]);
		}
	}
	
	public int getChunkSize() {
		return chunkSize;
	}
	
	/**
	 * @param chunkSize the chunkSize to set
	 */
	public void setChunkSize(int chunkSize) {
		this.chunkSize = chunkSize;
	}
	
	/**
	 * @param <T>
	 * @param clazz
	 * @param index 
	 * @return
	 */
	public <T extends EntityComponent> T getAs(Class<T> clazz, int index) {
		var component = components.get(clazz)[index];
		return clazz.cast(component);
	}
	
	public <T extends EntityComponent> Optional<T> getAsOpt(Class<T> clazz, int index) {
		if (components.containsKey(clazz)) {
			return Optional.ofNullable(clazz.cast(components.get(clazz)[index]));
		}
		return Optional.empty();
	}
	
	/**
	 * @param clazz
	 */
	public <T extends EntityComponent> T doChunkStuff(Class<T> clazz, int index) {
		return clazz.cast(components.get(clazz)[index]);
	}
	
	/**
	 * @param <T>
	 * @param <U>
	 * @param tClass 
	 * @param uClass 
	 * @param consumer
	 * @param index
	 */
	public <T extends EntityComponent, U extends EntityComponent> void getAsArch(Class<T> tClass, Class<U> uClass, BiConsumer<T, U> consumer, int index) {
		consumer.accept(tClass.cast(components.get(tClass)[index]), uClass.cast(components.get(uClass)[index]));
	}
	
	/**
	 * This is slow for some reason but {@code getAsArch} is fast, like 65% faster
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void processQuery(EntityBiQuery query, int index) {
		query.accept(
				query.getFirstType().cast(components.get(query.getFirstType())[index]),
				query.getSecondType().cast(components.get(query.getSecondType())[index]));
	}
	
	public void processGeneric(EntityQuery query, int index) {
		query.process(components, index);
	}

	/**
	 * @param query
	 * @return
	 */
	public void processWholeChunk(EntityQuery query) {
		for (int i = 0; i < chunkSize; i++) {
			query.process(components, i);
		}
	}
	
	/**
	 * @param <T>
	 * @param <U>
	 * @param <V>
	 * @param tClass
	 * @param uClass
	 * @param vClass
	 * @param consumer
	 * @param index
	 */
	public <T extends EntityComponent, U extends EntityComponent, V extends EntityComponent> void getAsArch(Class<T> tClass, Class<U> uClass, Class<V> vClass, TriConsumer<T, U, V> consumer, int index) {
//		consumer.accept(
//				tClass.cast(components.get(tClass)[index]),
//				uClass.cast(components.get(uClass)[index]),
//				vClass.cast(components.get(vClass)[index]));
		consumer.accept(
				(T) components.get(tClass)[index],
				(U) components.get(uClass)[index],
				(V) components.get(vClass)[index]);
	}
	
	@FunctionalInterface
	public interface TriConsumer<T, U, V>  {
		void accept(T t, U u, V v);
	}
	
	public Map<Class<? extends EntityComponent>, EntityComponent[]> getComponents() {
		return components;
	}
	
	public Set<Class<? extends EntityComponent>> getArchetype() {
		return components.keySet();
	}

}
