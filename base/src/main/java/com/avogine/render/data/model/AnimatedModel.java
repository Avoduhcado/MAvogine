package com.avogine.render.data.model;

import java.util.*;

import com.avogine.render.data.*;
import com.avogine.render.data.mesh.AnimatedMesh;

/**
 * 
 */
public class AnimatedModel extends Model<AnimatedMesh> {

	private final List<Animation> animations;
	
	/**
	 * @param id
	 * @param materialMeshMap
	 * @param animations 
	 */
	public AnimatedModel(String id, Map<Material, List<AnimatedMesh>> materialMeshMap, List<Animation> animations) {
		super(id, materialMeshMap);
		this.animations = animations;
	}
	
	/**
	 * @param id
	 * @param mesh
	 * @param material
	 * @param animations
	 */
	public AnimatedModel(String id, AnimatedMesh mesh, Material material, List<Animation> animations) {
		this(id, Map.of(material, List.of(mesh)), animations);
	}
	
	/**
	 * @return the animations
	 */
	public List<Animation> getAnimations() {
		return animations;
	}
	
}
