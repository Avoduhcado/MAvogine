package com.avogine.ecs.components;

import java.util.UUID;

import com.avogine.ecs.EntityArchetype;

/**
 *
 */
public record TestMeshRenderable(UUID id, TransformComponent transform, MeshComponent mesh) implements EntityArchetype {

}
