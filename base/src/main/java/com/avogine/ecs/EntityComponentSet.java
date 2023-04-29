package com.avogine.ecs;

import java.util.*;

/**
 * TODO
 */
public class EntityComponentSet extends HashSet<Class<? extends EntityComponent>> {
	private static final long serialVersionUID = 1L;
	
	private static final Map<Integer, EntityComponentSet> ARCHETYPE_MAP = new HashMap<>();
	
	/**
	 * @param classes 
	 */
	@SafeVarargs
	private EntityComponentSet(Class<? extends EntityComponent>...classes) {
		for (Class<? extends EntityComponent> clazz : classes) {
			add(clazz);
		}
	}
	
	/**
	 * Get an {@link EntityComponentSet} that matches the given array of {@code Classes}.
	 * <p>
	 * This will compute the hashcode of the the given array of {@code Classes} and attempt to return a cached {@code EntityArchetype}.
	 * If no {@code EntityArchetype} exists already, one will be created and returned. Order of the array does not matter.
	 * @param classes an array of {@code Classes} that extend {@link EntityComponent}
	 * @return an {@code EntityArchetype} that contains the given input classes
	 */
	@SafeVarargs
	public static EntityComponentSet of(Class<? extends EntityComponent>...classes) {
		return ARCHETYPE_MAP.computeIfAbsent(Set.of(classes).hashCode(), value -> new EntityComponentSet(classes));
	}
	
	/**
	 * Get an {@link EntityComponentSet} that matches the given array of {@link EntityComponent EntityComponents}
	 * </p>
	 * Usage of this should be discouraged in favor of {@link EntityComponentSet#of(Class...)} since this method requires converting
	 * the supplied {@code EntityComponents} array into an array of {@code Classes} and then just directly calling the other
	 * {@code EntityArchetype.of(Class...)} method.
	 * @param components an array of {@code EntityComponent}s
	 * @return an {@code EntityArchetype} that contains the given input component types
	 */
	@SuppressWarnings("unchecked")
	public static EntityComponentSet of(EntityComponent...components) {
		return of(Arrays.stream(components)
				.map(EntityComponent::getClass)
				.toArray(Class[]::new));
	}
	
}
