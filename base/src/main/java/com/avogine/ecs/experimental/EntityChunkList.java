package com.avogine.ecs.experimental;

import java.util.*;

/**
 *
 */
public class EntityChunkList {

	private final Set<Class<? extends EntityComponent>> archetype;
	private final Map<UUID, Map<Class<? extends EntityComponent>, EntityComponent>> components;
	
	/**
	 * 
	 */
	public EntityChunkList(Set<Class<? extends EntityComponent>> archetype) {
		this.archetype = archetype;
		components = new HashMap<>();
	}
	
	public void putAll(EntityComponent...components) {
		var id = UUID.randomUUID();
		this.components.put(id, new HashMap<>());
		for (EntityComponent component : components) {
			this.components.get(id).put(component.getClass(), component);
		}
	}
	
	public Set<Class<? extends EntityComponent>> getArchetype() {
		return archetype;
	}
	
	public Map<UUID, Map<Class<? extends EntityComponent>, EntityComponent>> getComponents() {
		return components;
	}
	
}
