package com.avogine.ecs.components;

import com.avogine.ecs.EntityComponent;
import com.avogine.render.data.mesh.Model;

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
	 * Pass a model and assign this component to that model's name.
	 * </p>
	 * <b>This does not store the given model, it is only a convenience method for retrieving a model's name.</b>
	 * @param model The {@link Model} to set the name to.
	 */
	public ModelComponent(Model model) {
		this.model = model.getName();
	}
	
	/**
	 * @return the model
	 */
	public String getModel() {
		return model;
	}
	
}
