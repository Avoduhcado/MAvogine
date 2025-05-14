package com.avogine.render.data.vertices.vertex;

import static org.lwjgl.system.MemoryUtil.memFree;

import java.nio.IntBuffer;

import com.avogine.render.data.vertices.VertexData;

/**
 * @param indices 
 */
public record ElementVertex(IntBuffer indices) implements VertexData {

	@Override
	public void close() {
		memFree(indices);
	}
	
}
