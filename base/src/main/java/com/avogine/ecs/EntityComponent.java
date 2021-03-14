package com.avogine.ecs;

/**
 * XXX Implementers of this type MUST include a public no-args constructor in order to satisfy {@link EntityWorld#createEntityWith(EntityArchetype)}.
 * It's also worth noting that no assumptions should be made about default field values if they're
 * not strictly defined inside of that constructor.
 */
public abstract class EntityComponent {
	
	/**
	 * Zero-arg constructor for use in creating instances of a given component through reflection.
	 * <p>
	 * This will only be executed if the subclass does not define any constructors of its own.
	 */
	public EntityComponent() {
		
	}
	
}
