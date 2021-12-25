package com.avogine.ecs;

import java.util.*;

/**
 * TODO
 */
public class EntityArchetype extends HashSet<Class<? extends EntityComponent>> {
	private static final long serialVersionUID = 1L;
	
	private static final Map<Integer, EntityArchetype> ARCHETYPE_MAP = new HashMap<>();
		
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

	/**
	 * Get an {@link EntityArchetype} that matches the given array of {@code Classes}.
	 * <p>
	 * This will compute the hashcode of the the given array of {@code Classes} and attempt to return a cached {@code EntityArchetype}.
	 * If no {@code EntityArchetype} exists already, one will be created and returned. Order of the array does not matter.
	 * @param classes an array of {@code Classes} that extend {@link EntityComponent}
	 * @return an {@code EntityArchetype} that contains the given input classes
	 */
	@SafeVarargs
	public static EntityArchetype of(Class<? extends EntityComponent>...classes) {
		return ARCHETYPE_MAP.computeIfAbsent(Set.of(classes).hashCode(), value -> new EntityArchetype(classes));
	}
	
	/**
	 * Get an {@link EntityArchetype} that matches the given array of {@link EntityComponent EntityComponents}
	 * </p>
	 * Usage of this should be discouraged in favor of {@link #of(Class...)} since this method requires converting
	 * the supplied {@code EntityComponents} array into an array of {@code Classes} and then just directly calling the other
	 * {@code EntityArchetype.of(Class...)} method.
	 * @param components an array of {@code EntityComponent}s
	 * @return an {@code EntityArchetype} that contains the given input component types
	 */
	@SuppressWarnings("unchecked")
	public static EntityArchetype of(EntityComponent...components) {
		return of(Arrays.stream(components)
				.map(EntityComponent::getClass)
				.toArray(Class[]::new));
	}
	
}
