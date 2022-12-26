package com.avogine.ecs.components;

import com.avogine.ecs.EntityComponent;

/**
 * TODO This should likely use a hash or an ID as its key value for retrieving from the cache, the cache can handle reloading saved models.
 */
public class ModelComponent extends EntityComponent {

	private String model;
	
	/**
	 * @param model
	 */
	public ModelComponent(String model) {
		this.model = model;
	}
	
	/**
	 * @return the model
	 */
	public String getModel() {
		return model;
	}
	
}
