package com.avogine.render.data.simple;

import java.util.*;

import com.avogine.render.data.Material;
import com.avogine.render.data.model.StaticModel;

/**
 *
 */
public class SimpleInstanceModel extends StaticModel<SimpleInstanceMesh> {

	/**
	 * @param id
	 * @param materialMeshMap 
	 */
	public SimpleInstanceModel(String id, Map<Material, List<SimpleInstanceMesh>> materialMeshMap) {
		super(id, materialMeshMap);
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
