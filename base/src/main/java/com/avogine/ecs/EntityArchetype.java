package com.avogine.ecs;

import java.util.*;
import java.util.stream.*;

/**
 * TODO
 */
public class EntityArchetype extends HashSet<Class<? extends EntityComponent>> {
	private static final long serialVersionUID = 1L;
	
	private static final Map<Integer, EntityArchetype> ARCHETYPE_MAP = new HashMap<>();

	private static final Comparator<Class<? extends EntityComponent>> CLASS_NAME_COMPARATOR = (class1, class2) -> class1.getName().compareTo(class2.getName());
	
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
	 * Find an {@code EntityArchetype} that already matches the given set of {@code Class}es.
	 * <p>
	 * This will compute the hashcode of the the given array of {@code Class}es and attempt to return a cached {@code EntityArchetype}.
	 * If no {@code EntityArchetype} exists already, one will be created and returned. Order of the array should not matter as the input
	 * will always be sent through a sort to ensure the hashcodes will match.
	 * @param classes an array of {@code Class}es that extend {@link EntityComponent}
	 * @return a cached {@code EntityArchetype} that contains the given input classes
	 */
	@SafeVarargs
	public static EntityArchetype of(Class<? extends EntityComponent>...classes) {
		Arrays.sort(classes, CLASS_NAME_COMPARATOR);
		return ARCHETYPE_MAP.computeIfAbsent(Arrays.hashCode(classes), value -> new EntityArchetype(classes));
	}
	
	/**
	 * XXX Usage of this should be discouraged in favor of {@link #of(Class...)} since this method requires converting
	 * the supplied {@link EntityComponent}s array into an array of {@code Class}es and then just directly calling the other
	 * {@code #of(Class...)} method.
	 * @param components an array of {@link EntityComponent}s
	 * @return a cached {@code EntityArchetype} that contains the given input classes
	 */
	@SuppressWarnings("unchecked")
	public static EntityArchetype of(EntityComponent...components) {
		return of(Arrays.stream(components)
				.map(EntityComponent::getClass)
				.toArray(Class[]::new));
	}
	
}
