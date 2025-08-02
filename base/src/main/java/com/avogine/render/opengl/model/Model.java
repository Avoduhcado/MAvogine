package com.avogine.render.opengl.model;

import java.util.*;

import org.joml.primitives.AABBf;

import com.avogine.render.model.animation.Animation;
import com.avogine.render.model.mesh.Boundable;

/**
 * 
 */
public class Model {

	private final String id;
	private final List<Material> materials;

	private final List<Animation> animations;
	
	private final AABBf aabb;
	
	/**
	 * @param id 
	 * @param materials 
	 * @param animations 
	 */
	public Model(String id, List<Material> materials, List<Animation> animations) {
		this.id = id;
		this.materials = materials;
		this.animations = animations;
		aabb = materials.stream().flatMap(material -> material.getBoundableMeshes().stream())
				.map(Boundable::getAABB)
				.reduce(AABBf::union)
				.orElseGet(AABBf::new);
	}
	
	/**
	 * @param id
	 * @param material
	 * @param animations 
	 */
	public Model(String id, Material material, List<Animation> animations) {
		this(id, List.of(material), animations);
	}
	
	/**
	 * @param id
	 * @param materials
	 */
	public Model(String id, List<Material> materials) {
		this(id, materials, new ArrayList<>());
	}
	
	/**
	 * @param id
	 * @param material
	 */
	public Model(String id, Material material) {
		this(id, List.of(material), new ArrayList<>());
	}
	
	/**
	 * Free all of the meshes contained in this model.
	 */
	public void cleanup() {
		materials.forEach(Material::cleanup);
	}

	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}
	
	/**
	 * @return the materials
	 */
	public List<Material> getMaterials() {
		return materials;
	}
	
	/**
	 * @return the animations
	 */
	public List<Animation> getAnimations() {
		return animations;
	}
	
	/**
	 * @return the aabb
	 */
	public AABBf getAabb() {
		return aabb;
	}
}
