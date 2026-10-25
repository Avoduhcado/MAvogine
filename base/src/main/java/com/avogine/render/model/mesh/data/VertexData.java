package com.avogine.render.model.mesh.data;

import java.nio.FloatBuffer;

import org.lwjgl.system.MemoryUtil;

/**
 * TODO Constructors for no tangent/bitangent (Maybe optional color parameter?)
 * @param normals
 * @param tangents
 * @param bitangents
 * @param textureCoordinates
 */
public record VertexData(FloatBuffer normals, FloatBuffer tangents, FloatBuffer bitangents, FloatBuffer textureCoordinates) {
	
	/**
	 * @return the size of all attributes combined.
	 */
	public int vertexSize() {
		return 3 + 3 + 3 + 2;
	}
	
	/**
	 * @return all vertex data interleaved into a single {@link FloatBuffer}.
	 */
	public FloatBuffer interleave() {
		int vertexCount = normals.limit();
		FloatBuffer interleavedVertices = MemoryUtil.memAllocFloat(vertexSize() * vertexCount);
		
		for (int i = 0; i < vertexCount / 3; i++) {
			interleavedVertices
			.put(i * vertexSize(), normals, i * 3, 3)
			.put((i * vertexSize()) + 3, tangents, i * 3, 3)
			.put((i * vertexSize()) + 3 + 3, bitangents, i * 3, 3)
			.put((i * vertexSize()) + 3 + 3 + 3, textureCoordinates, i * 2, 2);
		}
		
		return interleavedVertices;
	}
	
}
