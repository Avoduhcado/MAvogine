package com.avogine.render.opengl.model;

import java.util.*;

import com.avogine.render.model.Model;
import com.avogine.render.model.animation.Animation;
import com.avogine.render.model.material.SimpleMaterial;
import com.avogine.render.opengl.model.mesh.AnimatedMesh;;

/**
 *
 */
public class AnimatedModel extends Model<SimpleMaterial, AnimatedMesh> {
	private final List<Animation> animations;
	
	/**
	 * @param id
	 * @param materialMap
	 * @param animations 
	 */
	public AnimatedModel(String id, Map<SimpleMaterial, List<AnimatedMesh>> materialMap, List<Animation> animations) {
		super(id, materialMap);
		this.animations = animations;
	}
	
	/**
	 * @param id
	 */
	public AnimatedModel(String id) {
		this(id, new HashMap<>(), new ArrayList<>());
	}
	
	/**
	 * @return the animations
	 */
	public List<Animation> getAnimations() {
		return animations;
	}

}
