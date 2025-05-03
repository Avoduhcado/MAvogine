package com.avogine.render.data.simple;

import java.util.List;

import com.avogine.render.data.Material;
import com.avogine.render.data.model.StaticModel;

/**
 *
 */
public class SimpleInstanceModel extends StaticModel<SimpleInstanceMesh> {

	/**
	 * @param id
	 * @param meshes
	 * @param materials
	 */
	public SimpleInstanceModel(String id, List<SimpleInstanceMesh> meshes, List<Material> materials) {
		super(id, meshes, materials);
	}
	
	/**
	 * @param id
	 * @param mesh
	 * @param material
	 */
	public SimpleInstanceModel(String id, SimpleInstanceMesh mesh, Material material) {
		super(id, mesh, material);
	}

}
