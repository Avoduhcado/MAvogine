package com.avogine.render.opengl.model.material;

import java.util.List;

import com.avogine.render.opengl.model.mesh.Mesh;

/**
 *
 */
public abstract non-sealed class CustomMaterial extends Material {

	protected CustomMaterial() {
		super();
	}
	
	/**
	 * @param meshes
	 */
	protected CustomMaterial(List<Mesh> meshes) {
		super(meshes);
	}

}
