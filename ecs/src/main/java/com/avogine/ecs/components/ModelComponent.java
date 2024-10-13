package com.avogine.ecs.components;

import com.avogine.ecs.EntityComponent;
import com.avogine.render.data.Model;

/**
 * TODO This should likely use a hash or an ID as its key value for retrieving from the cache, the cache can handle reloading saved models.
 * @param model The name of a {@link Model} to use when rendering this component.
 */
public record ModelComponent(String model) implements EntityComponent {
	
}
