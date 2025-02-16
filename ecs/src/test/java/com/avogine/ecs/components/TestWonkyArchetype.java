package com.avogine.ecs.components;

import java.util.UUID;

import com.avogine.ecs.EntityArchetype;

/**
 *
 */
public record TestWonkyArchetype(TransformComponent transform, UUID id, ModelComponent model) implements EntityArchetype {

}
