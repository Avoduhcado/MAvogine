package com.avogine.render.data;

import java.nio.*;

import org.lwjgl.system.MemoryUtil;

/**
 * @param positions 
 * @param normals 
 * @param tangents 
 * @param bitangents 
 * @param textureCoordinates 
 * @param indices 
 */
public record VertexData(FloatBuffer positions, FloatBuffer normals, FloatBuffer tangents, FloatBuffer bitangents, FloatBuffer textureCoordinates, IntBuffer indices) implements AutoCloseable {

	@Override
	public void close() {
		MemoryUtil.memFree(positions);
		MemoryUtil.memFree(normals);
		MemoryUtil.memFree(tangents);
		MemoryUtil.memFree(bitangents);
		MemoryUtil.memFree(textureCoordinates);
		MemoryUtil.memFree(indices);
	}
}
