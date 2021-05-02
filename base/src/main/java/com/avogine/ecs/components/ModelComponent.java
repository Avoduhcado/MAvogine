package com.avogine.ecs.components;

import com.avogine.ecs.EntityComponent;
import com.avogine.render.data.Mesh;

/**
 *
 */
public class ModelComponent extends EntityComponent {

	private Mesh[] meshes;
	
	/**
	 * 
	 */
	public ModelComponent() {
		meshes = new Mesh[1];
	}
	
	/**
	 * 
	 * @param mesh
	 */
	public ModelComponent(Mesh mesh) {
		this.meshes = new Mesh[] {mesh};
	}
	
	/**
	 * @param meshes
	 */
	public ModelComponent(Mesh[] meshes) {
		this.meshes = meshes;
	}

	/**
	 * @return the array of Meshes used by this component
	 */
	public Mesh[] getMeshes() {
		return meshes;
	}
	
	/**
	 * @return the first {@link Mesh} in this component's array
	 */
	public Mesh getMesh() {
		return meshes[0];
	}
	
	/**
	 * @param meshes the Meshes to set
	 */
	public void setMeshes(Mesh[] meshes) {
		this.meshes = meshes;
	}
	
}
