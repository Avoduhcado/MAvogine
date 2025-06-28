package com.avogine.render.data;

import static org.lwjgl.system.MemoryUtil.memFree;

import java.nio.FloatBuffer;

/**
 * @param instanceMatrices 
 * @param instanceNormals 
 */
public record InstancedBuffers(FloatBuffer instanceMatrices, FloatBuffer instanceNormals) implements AutoCloseable {

	@Override
	public void close() {
		memFree(instanceMatrices);
		memFree(instanceNormals);
	}

}
