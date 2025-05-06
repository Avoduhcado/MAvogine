package com.avogine.render.data.simple;

import java.util.*;

import com.avogine.render.data.Material;
import com.avogine.render.data.model.StaticModel;

/**
 *
 */
public class SimpleModel extends StaticModel<SimpleMesh> {

	/**
	 * @param id
	 * @param materialMeshMap 
	 */
	public SimpleModel(String id, Map<Material, List<SimpleMesh>> materialMeshMap) {
		super(id, materialMeshMap);
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
