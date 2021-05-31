package com.avogine.ecs.components;

import com.avogine.ecs.*;
import com.avogine.render.data.mesh.*;

/**
 *
 */
public class ModelComponentPro extends EntityComponent {

	private Model model;
	
	/**
	 * @param model
	 */
	public ModelComponentPro(Model model) {
		this.model = model;
	}
	
	/**
	 * @return the model
	 */
	public Model getModel() {
		return model;
	}
	
}
