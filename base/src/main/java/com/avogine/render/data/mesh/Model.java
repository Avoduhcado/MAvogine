package com.avogine.render.data.mesh;

import java.util.*;

import com.avogine.render.data.*;
import com.avogine.render.shader.uniform.*;

/**
 * XXX Should Model contain its own name for proper cache indexing?
 */
public class Model {

	private List<Mesh> meshes;
	private List<Material> materials;
	
	/**
	 * @param meshes
	 * @param materials 
	 */
	public Model(List<Mesh> meshes, List<Material> materials) {
		this.meshes = meshes;
		this.materials = materials;
	}
	
	/**
	 * TODO I don't know how to feel about passing the uniform in here, kinda uggo
	 * @param material 
	 * 
	 */
	public void render(UniformMaterial material) {
		meshes.forEach(mesh -> {
			material.loadMaterial(materials.get(mesh.getMaterialIndex()));
			mesh.render();
		});
	}
	
	/**
	 * @return the meshes
	 */
	public List<Mesh> getMeshes() {
		return meshes;
	}
	
}
