package com.avogine.ecs;

import java.util.UUID;

/**
 * Parent interface for all {@link EntityComponent} implementations.
 * </p>
 * It is expected and enforced that queryable implementations of {@link EntityArchetype} are record types.
 */
public interface EntityArchetype {
	/**
	 * @return The ID of the entity.
	 */
	public UUID id();
}
