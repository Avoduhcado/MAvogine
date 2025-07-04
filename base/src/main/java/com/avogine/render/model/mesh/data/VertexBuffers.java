package com.avogine.render.model.mesh.data;

import static org.lwjgl.system.MemoryUtil.memFree;

import java.nio.*;


/**
 * @param positions 
 * @param normals 
 * @param tangents 
 * @param bitangents 
 * @param textureCoordinates 
 * @param colors 
 * @param weights 
 * @param boneIds 
 * @param indices 
 */
public record VertexBuffers(
		FloatBuffer positions,
		FloatBuffer normals,
		FloatBuffer tangents,
		FloatBuffer bitangents,
		FloatBuffer textureCoordinates,
		FloatBuffer colors,
		FloatBuffer weights,
		IntBuffer boneIds,
		IntBuffer indices) implements AutoCloseable {

	/**
	 * @param positions
	 * @param normals
	 * @param tangents
	 * @param bitangents
	 * @param textureCoordinates
	 * @param weights
	 * @param boneIds
	 * @param indices
	 */
	public VertexBuffers(
			FloatBuffer positions,
			FloatBuffer normals,
			FloatBuffer tangents,
			FloatBuffer bitangents,
			FloatBuffer textureCoordinates,
			FloatBuffer weights,
			IntBuffer boneIds,
			IntBuffer indices) {
		this(positions, normals, tangents, bitangents, textureCoordinates, null, weights, boneIds, indices);
	}

	/**
	 * @param positions
	 * @param normals
	 * @param tangents
	 * @param bitangents
	 * @param textureCoordinates
	 * @param indices
	 */
	public VertexBuffers(
			FloatBuffer positions,
			FloatBuffer normals,
			FloatBuffer tangents,
			FloatBuffer bitangents,
			FloatBuffer textureCoordinates,
			IntBuffer indices) {
		this(positions, normals, tangents, bitangents, textureCoordinates, null, null, indices);
	}
	
	/**
	 * @param positions
	 */
	public VertexBuffers(FloatBuffer positions) {
		this(positions, null, null, null, null, null);
	}
	
	@Override
	public void close() {
		memFree(positions);
		memFree(normals);
		memFree(tangents);
		memFree(bitangents);
		memFree(textureCoordinates);
		memFree(colors);
		memFree(weights);
		memFree(boneIds);
		memFree(indices);
	}
	
}
