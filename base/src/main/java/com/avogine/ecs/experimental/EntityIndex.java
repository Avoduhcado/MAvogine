package com.avogine.ecs.experimental;

import java.util.*;

/**
 * @param id 
 * @param archetype 
 *
 */
public record EntityIndex(UUID id, Map<Class<? extends EntityComponent>, Integer> archetype) {

}
