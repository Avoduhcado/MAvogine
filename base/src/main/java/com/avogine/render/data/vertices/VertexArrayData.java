package com.avogine.render.data.vertices;

import com.avogine.render.data.gl.VAO;

/**
 *
 */
public interface VertexArrayData {

	/**
	 * @return binds the data to a new {@link VAO}.
	 */
	public VAO bind();
	
}
