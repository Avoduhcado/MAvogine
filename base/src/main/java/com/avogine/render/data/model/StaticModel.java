package com.avogine.render.data.model;

import java.util.List;

import com.avogine.render.data.*;
import com.avogine.render.data.mesh.StaticMesh;
import com.avogine.render.data.vertices.array.IndexedVertexArray;

/**
 * @param <T> 
 * 
 */
public class StaticModel<T extends StaticMesh<? extends IndexedVertexArray>> extends Model<T> {
	
	/**
	 * @param id 
	 * @param meshes
	 * @param materials 
	 */
	public StaticModel(String id, List<T> meshes, List<Material> materials) {
		super(id, meshes, materials);
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
