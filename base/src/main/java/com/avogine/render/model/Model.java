package com.avogine.render.model;

import java.util.*;

import org.joml.primitives.AABBf;

import com.avogine.render.model.material.Material;
import com.avogine.render.model.mesh.*;

/**
 * @param <E> 
 * @param <T> 
 */
public abstract class Model<E extends Material, T extends Renderable & Boundable> {

	protected final String id;
	protected final Map<E, List<T>> materialMap;
	protected AABBf boundingBox;
	
	/**
	 * @param id
	 * @param materialMap
	 */
	protected Model(String id, Map<E, List<T>> materialMap) {
		this.id = id;
		this.materialMap = materialMap;
		computeBoundingBox();
	}
	
	/**
	 * 
	 */
	public void cleanup() {
		materialMap.values().forEach(meshList -> meshList.forEach(Renderable::cleanup));
	}
	
	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}
	
	/**
	 * @return the materialMap
	 */
	public Map<E, List<T>> getMaterialMap() {
		return materialMap;
	}

	/**
	 * @return the boundingBox
	 */
	public AABBf getBoundingBox() {
		return boundingBox;
	}
	
	private void computeBoundingBox() {
		boundingBox = materialMap.values().stream()
				.flatMap(List::stream)
				.map(Boundable::getBoundingBox)
				.reduce(AABBf::union)
				.orElseGet(AABBf::new);
	}
}
