package com.avogine.render.data.model;

import java.util.*;

import com.avogine.render.data.Material;
import com.avogine.render.data.mesh.Mesh;
import com.avogine.render.data.vertices.VertexArrayData;

/**
 * @param <T> 
 */
public class Model<T extends Mesh<? extends VertexArrayData>> {

	protected final String id;
	protected final List<T> meshes;
	protected final List<Material> materials;
	

	/**
	 * @param id 
	 * @param meshes 
	 * @param materials 
	 */
	public Model(String id, List<T> meshes, List<Material> materials) {
		this.id = id;
		this.meshes = meshes;
		this.materials = materials;
	}
	
	/**
	 * @param id
	 * @param mesh
	 * @param material
	 */
	public Model(String id, T mesh, Material material) {
		this(id, List.of(mesh), List.of(material));
	}
	
	/**
	 * @param id
	 * @param meshes
	 */
	public Model(String id, List<T> meshes) {
		this(id, meshes, new ArrayList<>());
	}
	
	/**
	 * @param id
	 * @param mesh
	 */
	public Model(String id, T mesh) {
		this(id, List.of(mesh));
	}
	
	/**
	 * Free all of the meshes contained in this model.
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
	public List<T> getMeshes() {
		return meshes;
	}
	
	/**
	 * @return the materials
	 */
	public List<Material> getMaterials() {
		return materials;
	}
}