package com.avogine.render.data;

import com.avogine.render.data.gl.VAO;

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

