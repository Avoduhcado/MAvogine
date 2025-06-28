package com.avogine.render.data.model;

import java.util.*;

import com.avogine.render.data.Material;
import com.avogine.render.data.mesh.StaticInstancedMesh;

/**
 *
 */
public class StaticInstancedModel extends Model<StaticInstancedMesh> {

	/**
	 * @param id
	 * @param materialMeshMap
	 */
	public StaticInstancedModel(String id, Map<Material, List<StaticInstancedMesh>> materialMeshMap) {
		super(id, materialMeshMap);
	}
	
	/**
	 * @param id
	 * @param mesh
	 * @param material
	 */
	public StaticInstancedModel(String id, StaticInstancedMesh mesh, Material material) {
		super(id, mesh, material);
	}

}
