package com.avogine.ecs.components;

import com.avogine.ecs.EntityComponent;
import com.avogine.render.data.Mesh;

/**
 *
 */
public class MeshRender extends EntityComponent {

	private Mesh[] meshes;
	
	/**
	 * 
	 */
	public MeshRender() {
		meshes = new Mesh[1];
	}
	
	public MeshRender(Mesh[] meshes) {
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
