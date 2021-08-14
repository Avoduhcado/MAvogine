package com.avogine.ecs.components;

import com.avogine.ecs.*;
import com.avogine.render.data.mesh.*;

/**
 *
 */
public class ModelComponent extends EntityComponent {

	private Model model;
	
	/**
	 * @param model
	 */
	public ModelComponent(Model model) {
		this.model = model;
	}
	
	/**
	 * @return the model
	 */
	public Model getModel() {
		return model;
	}
	
}
