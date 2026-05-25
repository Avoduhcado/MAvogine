package com.avogine.render.model.mesh.data;

import org.joml.primitives.AABBf;

/**
 *
 * @param vertices
 * @param normals
 * @param tangents
 * @param bitangents
 * @param textureCoordinates
 * @param skeletonData
 * @param indices
 * @param aabb
 * @param materialIndex
 */
public record MeshData(float[] vertices, float[] normals, float[] tangents, float[] bitangents, float[] textureCoordinates,
		AnimMeshData skeletonData,
		int[] indices,
		AABBf aabb,
		int materialIndex) {

	/**
	 * @param boneIds
	 * @param weights
	 */
	public record AnimMeshData(int[] boneIds, float[] weights) {}
}
