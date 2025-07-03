package com.avogine.render.opengl.model;

import java.util.*;

import com.avogine.render.model.Material;
import com.avogine.render.model.mesh.MeshData;
import com.avogine.render.opengl.model.mesh.Mesh;

/**
 * @param <T> 
 */
public class Model<T extends Mesh<? extends MeshData>> {

	protected final String id;
	protected final Map<Material, List<T>> materialMeshMap;
	

	/**
	 * @param id 
	 * @param materialMeshMap 
	 */
	public Model(String id, Map<Material, List<T>> materialMeshMap) {
		this.id = id;
		this.materialMeshMap = materialMeshMap;
	}
	
	/**
	 * @param id
	 * @param mesh
	 * @param material
	 */
	public Model(String id, T mesh, Material material) {
		this(id, Map.of(material, List.of(mesh)));
	}
	
	/**
	 * Free all of the meshes contained in this model.
	 */
	public void cleanup() {
		materialMeshMap.values().forEach(meshList -> meshList.forEach(Mesh::cleanup));
	}

	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}
	
	/**
	 * @return the materialMeshMap
	 */
	public Map<Material, List<T>> getMaterialMeshMap() {
		return materialMeshMap;
	}
	
	/**
	 * @return the meshes
	 */
	public List<T> getMeshes() {
		return materialMeshMap.values().stream()
				.flatMap(List::stream)
				.distinct()
				.toList();
	}
	
	/**
	 * @return the materials
	 */
	public List<Material> getMaterials() {
		return materialMeshMap.keySet().stream().toList();
	}
}