package com.avogine.render.data.model;

import java.util.*;

import com.avogine.render.data.Material;
import com.avogine.render.data.mesh.StaticMesh;

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
