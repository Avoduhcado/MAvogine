package com.avogine.ecs.components;

import com.avogine.ecs.EntityComponent;
import com.avogine.render.data.Mesh;

/**
 *
 */
public class MeshComponent extends EntityComponent {

	private Mesh[] meshes;
	
	/**
	 * 
	 */
	public MeshComponent() {
		meshes = new Mesh[1];
	}
	
	public MeshComponent(Mesh[] meshes) {
		this.meshes = meshes;
	}

	public Mesh[] getMeshes() {
		return meshes;
	}
	
	/**
	 * @param meshes the meshes to set
	 */
	public void setMeshes(Mesh[] meshes) {
		this.meshes = meshes;
	}
	
}
