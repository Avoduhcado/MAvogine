package com.avogine.ecs.components;

import java.util.UUID;

import com.avogine.ecs.EntityArchetype;

public record TestTransformable(UUID id, TransformComponent transform) implements EntityArchetype {
	
}