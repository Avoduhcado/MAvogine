package com.avogine.ecs.components;

import com.avogine.ecs.*;

/**
 *
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
