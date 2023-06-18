package com.avogine.render.data.mesh;

import java.util.*;
import java.util.function.Consumer;

import com.avogine.ecs.EntityArchetype;
import com.avogine.render.data.material.Material;

/**
 *
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
	 * @param mesh
	 * @param material
	 */
	public Model(Mesh mesh, Material material) {
		this(List.of(mesh), List.of(material));
	}
	
	public <T extends EntityArchetype> void renderPro(Collection<T> entities, Consumer<T> consumer) {
		meshes.forEach(mesh -> {
			bindMaterial(mesh); // TODO Different entities could be using different materials, we'll need to store unique materials with the component instead of the mesh
			
			mesh.renderPro(entities, consumer);
		});
	}
	
	public <T> void renderBoo(Collection<T> entities, Consumer<T> consumer) {
		meshes.forEach(mesh -> {
			bindMaterial(mesh);
			
			mesh.renderBoo(entities, consumer);
		});
	}
	
	/**
	 * 
	 */
	public void render() {
		render(c -> {});
	}
	
	/**
	 * @param consumer a Consumer function to process additional per mesh operations during rendering like applying materials.
	 * 
	 */
	public void render(Consumer<Mesh> consumer) {
		meshes.forEach(mesh -> {
			consumer.accept(mesh);
			
			bindMaterial(mesh);
			mesh.render();
			unbindMaterial(mesh);
		});
	}
	
	private void bindMaterial(Mesh mesh) {
		var material = materials.get(mesh.getMaterialIndex());
		if (material != null) {
			material.bind();
		}
	}
	
	private void unbindMaterial(Mesh mesh) {
		
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
