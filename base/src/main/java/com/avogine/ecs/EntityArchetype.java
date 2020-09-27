package com.avogine.ecs;

import java.util.HashSet;

/**
 *
 */
public class EntityArchetype extends HashSet<Class<? extends EntityComponent>> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * @param classes 
	 */
	@SafeVarargs
	public EntityArchetype(Class<? extends EntityComponent>...classes) {
		for (Class<? extends EntityComponent> clazz : classes) {
			add(clazz);
		}
	}
	
	/**
	 * @param components
	 */
	public EntityArchetype(EntityComponent[] components) {
		for (EntityComponent component : components) {
			add(component.getClass());
		}
	}
	
	// TODO
	public static EntityArchetype of(EntityComponent[] components) {
		// TODO Store a cache of archetypes so that when checking new entities we don't have to allocate a new archetype each time
		return null;
	}
	
}
