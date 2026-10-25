package com.avogine.render.opengl.model;

import java.util.*;

import com.avogine.render.model.Model;
import com.avogine.render.model.material.SimpleMaterial;
import com.avogine.render.opengl.model.mesh.StaticMesh;

/**
 *
 */
public class StaticModel extends Model<SimpleMaterial, StaticMesh> {

	/**
	 * @param id
	 * @param materialMap
	 */
	public StaticModel(String id, Map<SimpleMaterial, List<StaticMesh>> materialMap) {
		super(id, materialMap);
	}
	
	/**
	 * @param id
	 */
	public StaticModel(String id) {
		this(id, new HashMap<>());
	}
	
}
