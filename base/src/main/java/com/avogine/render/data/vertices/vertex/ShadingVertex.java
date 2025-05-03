package com.avogine.render.data.vertices.vertex;

import java.nio.FloatBuffer;

import org.lwjgl.system.MemoryUtil;

import com.avogine.render.data.vertices.VertexData;

/**
 * @param normals 
 * @param tangents 
 * @param bitangents 
 */
public record ShadingVertex(FloatBuffer normals, FloatBuffer tangents, FloatBuffer bitangents) implements VertexData {

	@Override
	public void close() {
		MemoryUtil.memFree(normals);
		MemoryUtil.memFree(tangents);
		MemoryUtil.memFree(bitangents);
	}

}
