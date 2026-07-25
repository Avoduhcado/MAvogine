package com.avogine.render.opengl.model.mesh.data;

import java.nio.IntBuffer;

import com.avogine.render.opengl.VBO;

/**
 * 
 * @param buffer 
 * @param vertexCount 
 */
public record Index(VBO buffer, int vertexCount) {

	/**
	 * @param data
	 */
	public Index(IntBuffer data) {
		this(VBO.elementArrayBuffer(data), data.limit());
	}
	
}
