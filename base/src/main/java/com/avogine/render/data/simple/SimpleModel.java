package com.avogine.render.data.simple;

import java.util.List;

import com.avogine.render.data.Material;
import com.avogine.render.data.model.StaticModel;

/**
 *
 */
public class SimpleModel extends StaticModel<SimpleMesh> {

	/**
	 * @param id
	 * @param meshes
	 * @param materials
	 */
	public SimpleModel(String id, List<SimpleMesh> meshes, List<Material> materials) {
		super(id, meshes, materials);
	}
	
	/**
	 * @param id 
	 * @param mesh 
	 * @param material 
	 */
	public SimpleModel(String id, SimpleMesh mesh, Material material) {
		super(id, mesh, material);
	}

}
