package com.avogine.render.model.mesh.data;

import java.util.*;

import org.joml.primitives.AABBf;

/**
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
public record MeshData(
		float[] vertices, float[] normals, float[] tangents, float[] bitangents, float[] textureCoordinates,
		AnimMeshData skeletonData,
		int[] indices,
		AABBf aabb,
		int materialIndex) {
	
	/**
	 * @param vertices
	 * @param normals
	 * @param tangents
	 * @param bitangents
	 * @param textureCoordinates
	 * @param indices
	 * @param aabb
	 */
	public MeshData(float[] vertices, float[] normals, float[] tangents, float[] bitangents, float[] textureCoordinates, int[] indices, AABBf aabb) {
		this(vertices, normals, tangents, bitangents, textureCoordinates, null, indices, aabb, 0);
	}
	
	/**
	 * @param vertices
	 * @param normals
	 * @param textureCoordinates
	 * @param indices
	 * @param aabb
	 */
	public MeshData(float[] vertices, float[] normals, float[] textureCoordinates, int[] indices, AABBf aabb) {
		this(vertices, normals, new float[normals.length], new float[normals.length], textureCoordinates, null, indices, aabb, 0);
	}
	
	/**
	 * @param boneIds
	 * @param weights
	 */
	public record AnimMeshData(int[] boneIds, float[] weights) {
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + Arrays.hashCode(boneIds);
			result = prime * result + Arrays.hashCode(weights);
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (!(obj instanceof AnimMeshData))
				return false;
			AnimMeshData other = (AnimMeshData) obj;
			return Arrays.equals(boneIds, other.boneIds) && Arrays.equals(weights, other.weights);
		}
		
		@Override
		public String toString() {
			return "AnimMeshData [boneIds=" + boneIds + ", weights=" + weights + "]";
		}
		
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Arrays.hashCode(bitangents);
		result = prime * result + Arrays.hashCode(indices);
		result = prime * result + Arrays.hashCode(normals);
		result = prime * result + Arrays.hashCode(tangents);
		result = prime * result + Arrays.hashCode(textureCoordinates);
		result = prime * result + Arrays.hashCode(vertices);
		result = prime * result + Objects.hash(aabb, materialIndex, skeletonData);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!(obj instanceof MeshData))
			return false;
		MeshData other = (MeshData) obj;
		return Objects.equals(aabb, other.aabb) && Arrays.equals(bitangents, other.bitangents)
				&& Arrays.equals(indices, other.indices) && materialIndex == other.materialIndex
				&& Arrays.equals(normals, other.normals) && Objects.equals(skeletonData, other.skeletonData)
				&& Arrays.equals(tangents, other.tangents)
				&& Arrays.equals(textureCoordinates, other.textureCoordinates)
				&& Arrays.equals(vertices, other.vertices);
	}

	@Override
	public String toString() {
		return "MeshData [vertices=" + vertices + ", normals=" + normals + ", tangents=" + tangents + ", bitangents="
				+ bitangents + ", textureCoordinates=" + textureCoordinates + ", skeletonData=" + skeletonData
				+ ", indices=" + indices + ", aabb=" + aabb + ", materialIndex=" + materialIndex + "]";
	}
	
}
