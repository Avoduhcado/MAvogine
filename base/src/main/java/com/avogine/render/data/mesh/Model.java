package com.avogine.render.data.mesh;

import java.util.List;
import java.util.function.Consumer;

import com.avogine.render.data.Material;
import com.avogine.render.loader.assimp.ModelCache;

/**
 * XXX Should Model contain its own name for proper cache indexing?
 */
public class Model {

	private final String name;
	
	private List<Mesh> meshes;
	private List<Material> materials;
	
	/**
	 * @param name 
	 * @param meshes
	 * @param materials 
	 */
	public Model(String name, List<Mesh> meshes, List<Material> materials) {
		this.name = name;
		this.meshes = meshes;
		this.materials = materials;
	}
	
	/**
	 * @param consumer a Consumer function to process additional per mesh operations during rendering like applying materials.
	 * 
	 */
	public void render(Consumer<Mesh> consumer) {
		meshes.forEach(mesh -> {
			consumer.accept(mesh);
			mesh.render();
		});
	}
	
	/**
	 * The name of the model.
	 * </p>
	 * This should be a unique identifier if you intend to retrieve it from the {@link ModelCache}, but the system will
	 * make no efforts to enforce that the name is unique.
	 * @return the name of the model.
	 */
	public String getName() {
		return name;
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
	
	/**
	 * @param index
	 * @return the material at index
	 */
	public Material getMaterial(int index) {
		return materials.get(index);
	}
	
}
