package com.avogine.render.data;

import java.util.List;

/**
 *
 */
public class Model {

	private final String id;
	private List<Mesh> meshes;
	private List<Material> materials;
	
	/**
	 * @param id 
	 * @param meshes
	 * @param materials 
	 */
	public Model(String id, List<Mesh> meshes, List<Material> materials) {
		this.id = id;
		this.meshes = meshes;
		this.materials = materials;
	}
	
	/**
	 * @param id 
	 * @param mesh
	 * @param material
	 */
	public Model(String id, Mesh mesh, Material material) {
		this(id, List.of(mesh), List.of(material));
	}
	
	/**
	 * 
	 */
	public void cleanup() {
		meshes.forEach(Mesh::cleanup);
	}
	
	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}
	
	/**
	 * @return the meshes
	 */
	public List<Mesh> getMeshes() {
		return meshes;
	}
	
	/**
	 * @return the materials
	 */
	public List<Material> getMaterials() {
		return materials;
	}

}
