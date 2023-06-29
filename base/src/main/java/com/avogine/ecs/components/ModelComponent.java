package com.avogine.ecs.components;

import com.avogine.ecs.EntityComponent;

/**
 * TODO Maybe store an integer hash instead?
 * @param modelName 
 */
public record ModelComponent(String modelName) implements EntityComponent {

}
