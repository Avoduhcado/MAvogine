package com.avogine.ecs.components;

import com.avogine.ecs.*;

/**
 *
 */
public class MeshComponent extends EntityComponent {

	private int[] meshIndices;
	
	/**
	 * 
	 */
	public MeshComponent() {
		meshIndices = new int[0];
	}
	
	/**
	 * 
	 * @param meshIndex
	 */
	public MeshComponent(int meshIndex) {
		meshIndices = new int[] {meshIndex};
	}
	
	/**
	 * @param meshIndices
	 */
	public MeshComponent(int[] meshIndices) {
		this.meshIndices = meshIndices;
	}
	
	/**
	 * @return the array of Meshes used by this component
	 */
	public int[] getMeshes() {
		return meshIndices;
	}
	
	/**
	 * @param meshIndices the Meshes to set
	 */
	public void setMeshes(int[] meshIndices) {
		this.meshIndices = meshIndices;
	}
	
}
