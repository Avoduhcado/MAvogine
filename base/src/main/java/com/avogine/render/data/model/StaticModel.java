package com.avogine.render.data.model;

import java.util.*;

import com.avogine.render.data.Material;
import com.avogine.render.data.mesh.StaticMesh;
import com.avogine.render.data.vertices.array.IndexedVertexArray;

/**
 * @param <T> 
 * 
 */
public class StaticModel<T extends StaticMesh<? extends IndexedVertexArray>> extends Model<T> {
	
	/**
	 * @param id 
	 * @param materialMeshMap 
	 */
	public StaticModel(String id, Map<Material, List<T>> materialMeshMap) {
		super(id, materialMeshMap);
	}
	
	/**
	 * @param id 
	 * @param mesh
	 * @param material
	 */
	public StaticModel(String id, T mesh, Material material) {
		super(id, mesh, material);
	}
	
}
