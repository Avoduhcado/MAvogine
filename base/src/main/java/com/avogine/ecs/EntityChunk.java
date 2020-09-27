package com.avogine.ecs;

import java.util.HashMap;
import java.util.Map;

/**
 *
 */
public class EntityChunk {

	private EntityArchetype archetype;
	
	private Map<Long, EntityComponentMap> componentsMap = new HashMap<>();
	
	/**
	 * @param archetype 
	 * 
	 */
	public EntityChunk(EntityArchetype archetype) {
		this.archetype = archetype;
	}
	
	/**
	 * @return the componentMaps
	 */
	public Map<Long, EntityComponentMap> getComponentMaps() {
		return componentsMap;
	}
	
	/**
	 * @param entity
	 * @return
	 */
	public EntityComponentMap getComponentMap(long entity) {
		return componentsMap.get(entity);
	}
	
	/**
	 * 
	 * @param entityID 
	 * @param componentMap
	 */
	public void addComponentMap(long entityID, EntityComponentMap componentMap) {
		componentsMap.put(entityID, componentMap);
	}
	
	/**
	 * @param archetype
	 * @return true if the {@link EntityArchetype} for this chunk contains all of the {@link EntityComponent}s in {@code archetype}
	 */
	public boolean containsAll(EntityArchetype archetype) {
		return this.archetype.containsAll(archetype);
	}
	
	/**
	 * @return the archetype
	 */
	public EntityArchetype getArchetype() {
		return archetype;
	}
	
}
