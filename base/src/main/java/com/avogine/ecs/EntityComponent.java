package com.avogine.ecs;

/**
 * TODO
 */
public interface EntityComponent {
	/**
	 * Optional cleanup method.
	 * </p>
	 * This is a no-op by default but can be overriden by subclasses to perform unique
	 * cleanup operations for components.
	 */
	public default void cleanup() {
		
	}
}
