package com.avogine.render.data.mesh;

import java.util.*;

/**
 *
 */
public class Model {

	private List<Mesh> meshes;
	
	/**
	 * @param meshes
	 */
	public Model(List<Mesh> meshes) {
		this.meshes = meshes;
	}
	
	/**
	 * 
	 */
	public void render() {
		meshes.forEach(Mesh::render);
	}
	
	/**
	 * @return the meshes
	 */
	public List<Mesh> getMeshes() {
		return meshes;
	}
	
}
