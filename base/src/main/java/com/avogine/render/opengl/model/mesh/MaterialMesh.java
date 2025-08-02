package com.avogine.render.opengl.model.mesh;

import com.avogine.render.model.mesh.VertexArrayData;
import com.avogine.render.opengl.VertexArrayObject;

/**
 * 
 */
public abstract sealed class MaterialMesh<T extends VertexArrayData> extends VertexArrayObject<T> permits Mesh, InstancedMesh, AnimatedMesh {

	/**
	 * @param vertexData
	 */
	protected MaterialMesh(T vertexData) {
		super(vertexData);
	}

}
