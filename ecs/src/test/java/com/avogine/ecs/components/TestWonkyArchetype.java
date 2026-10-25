package com.avogine.ecs.components;

import java.util.UUID;

import com.avogine.ecs.EntityArchetype;

/**
 * @param transform 
 * @param id 
 * @param model 
 */
public record TestWonkyArchetype(TransformComponent transform, UUID id, ModelComponent model) implements EntityArchetype {

}
