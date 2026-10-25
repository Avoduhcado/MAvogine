package com.avogine.ecs.components;

import java.util.UUID;

import com.avogine.ecs.EntityArchetype;

/**
 *
 * @param id
 * @param transform
 */
public record TestTransformable(UUID id, TransformComponent transform) implements EntityArchetype {
	
}