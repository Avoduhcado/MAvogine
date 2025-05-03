package com.avogine.render.data.vertices.vertex;

import java.nio.FloatBuffer;

import org.lwjgl.system.MemoryUtil;

import com.avogine.render.data.vertices.VertexData;

/**
 * @param textureCoordinates 
 */
public record TextureCoordinateVertex(FloatBuffer textureCoordinates) implements VertexData {

	@Override
	public void close() {
		MemoryUtil.memFree(textureCoordinates);
	}

}
