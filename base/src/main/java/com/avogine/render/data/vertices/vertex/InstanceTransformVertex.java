package com.avogine.render.data.vertices.vertex;

import java.nio.FloatBuffer;

import org.lwjgl.system.MemoryUtil;

import com.avogine.render.data.vertices.VertexData;

/**
 * @param instanceMatrices 
 * @param instanceNormals 
 */
public record InstanceTransformVertex(FloatBuffer instanceMatrices, FloatBuffer instanceNormals) implements VertexData {
	
	@Override
	public void close() {
		MemoryUtil.memFree(instanceMatrices);
		MemoryUtil.memFree(instanceNormals);
	}
	
}
