package com.avogine.ecs.components;

import java.util.UUID;

import com.avogine.ecs.EntityArchetype;

/**
 *
 * @param id
 * @param transform
 * @param model
 */
public record TestRenderable(UUID id, TransformComponent transform, ModelComponent model) implements EntityArchetype {
	
}