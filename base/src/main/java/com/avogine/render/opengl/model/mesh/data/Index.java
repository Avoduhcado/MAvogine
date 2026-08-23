package com.avogine.render.opengl.model.mesh.data;

import java.nio.IntBuffer;

/**
 * 
 * @param data 
 * @param vertexCount 
 */
public record Index(IntBuffer data, int vertexCount) {

	/**
	 * @param data
	 */
	public Index(IntBuffer data) {
		this(data, data.limit());
	}
	
}
