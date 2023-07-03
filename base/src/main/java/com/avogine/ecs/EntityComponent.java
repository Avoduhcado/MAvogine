package com.avogine.ecs;

/**
 *
 */
public interface EntityComponent {

	/**
	 * Optional cleanup method.
	 * </p>
	 * This is a no-op by default but can be overridden by subclasses to perform unique
	 * cleanup operations for components.
	 */
	public default void cleanup() {
		
	}
	
}
