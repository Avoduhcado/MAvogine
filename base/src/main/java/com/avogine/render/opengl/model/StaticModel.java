package com.avogine.render.opengl.model;

import java.util.*;

import com.avogine.render.model.Material;
import com.avogine.render.opengl.model.mesh.StaticMesh;

/**
 * 
 */
public class StaticModel extends Model<StaticMesh> {
	
	/**
	 * @param id 
	 * @param materialMeshMap 
	 */
	public StaticModel(String id, Map<Material, List<StaticMesh>> materialMeshMap) {
		super(id, materialMeshMap);
	}
	
	/**
	 * @param id 
	 * @param mesh
	 * @param material
	 */
	public StaticModel(String id, StaticMesh mesh, Material material) {
		super(id, mesh, material);
	}
	
}
