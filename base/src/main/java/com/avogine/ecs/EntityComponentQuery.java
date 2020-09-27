package com.avogine.ecs;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Stream;

/**
 *
 */
public class EntityComponentQuery {

	private EntityArchetype queryArchetype;
	
	private final Set<EntityComponentMap> resultMap = new HashSet<>();
	
	/**
	 * @param queryArchetype 
	 * 
	 */
	public EntityComponentQuery(EntityArchetype queryArchetype) {
		this.queryArchetype = queryArchetype;
	}
	
	/**
	 * @param set
	 * @return a collection of all {@link EntityComponentMap}s guaranteed to contain all of the necessary components for this system's query
	 */
	public Set<EntityComponentMap> fetch(Set<EntityChunk> set) {
		resultMap.clear();
		for (EntityChunk chunk : set) {
			if (chunk.containsAll(queryArchetype)) {
				resultMap.addAll(chunk.getComponentMaps().values());
			}
		}
		
		return resultMap;
	}
	
	/**
	 * <b>XXX This very well could not be very memory efficient as each call instantiates a new Stream.</b>
	 * @param chunks
	 * @return a Stream of {@link EntityComponentMap}s guaranteed to contain all of the {@link EntityComponent}s determined by this system's query archetype
	 */
	public Stream<EntityComponentMap> fetchStream(Set<EntityChunk> chunks) {
		return chunks.stream()
				.filter(chunk -> chunk.containsAll(queryArchetype))
				.flatMap(chunk -> chunk.getComponentMaps().values().stream());
	}
	
	/**
	 * @param queryArchetype the queryArchetype to set
	 */
	public void setQueryArchetype(EntityArchetype queryArchetype) {
		this.queryArchetype = queryArchetype;
	}
	
	/**
	 * @return the resultMap
	 */
	public Set<EntityComponentMap> getResultMap() {
		return resultMap;
	}
	
}
