package com.avogine.ecs;

import java.io.*;
import java.util.*;

/**
 *
 */
public class EntityChunk implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

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
		componentMap.setEntityId(entityID);
		componentsMap.put(entityID, componentMap);
	}
	
	/**
	 * 
	 * @param entityID
	 * @return
	 */
	public EntityComponentMap removeComponentMap(long entityID) {
		return componentsMap.remove(entityID);
	}
	
	/**
	 * @param archetype
	 * @return true if the {@link EntityArchetype} for this chunk contains all of the {@link EntityComponent}s in {@code archetype}
	 */
	public boolean containsAll(EntityArchetype archetype) {
		return this.archetype.containsAll(archetype);
	}
	
	/**
	 * @param entityID the Long ID of an entity
	 * @return true if this chunk contains the given entity ID
	 */
	public boolean containsEntity(long entityID) {
		return componentsMap.containsKey(entityID);
	}
	
	/**
	 * @return the archetype
	 */
	public EntityArchetype getArchetype() {
		return archetype;
	}
	
}
