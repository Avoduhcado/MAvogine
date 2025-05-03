package com.avogine.render.data.vertices.vertex;

import java.nio.FloatBuffer;

import org.lwjgl.system.MemoryUtil;

import com.avogine.render.data.vertices.VertexData;

/**
 * @param positions 
 */
public record PositionVertex(FloatBuffer positions) implements VertexData {

	@Override
	public void close() {
		MemoryUtil.memFree(positions);
	}

}
