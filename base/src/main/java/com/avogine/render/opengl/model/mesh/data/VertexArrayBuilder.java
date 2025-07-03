package com.avogine.render.opengl.model.mesh.data;

import com.avogine.render.model.mesh.MeshData;
import com.avogine.render.opengl.VAO;

/**
 * @param <T> 
 *
 */
public interface VertexArrayBuilder<T extends MeshData> {
	
	/**
	 * @param meshData
	 * @return
	 */
	public VAO buildVertexArray(T meshData);

}

