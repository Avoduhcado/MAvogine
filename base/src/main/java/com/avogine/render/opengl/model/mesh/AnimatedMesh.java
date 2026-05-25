package com.avogine.render.opengl.model.mesh;

import java.util.Objects;

import org.joml.primitives.AABBf;

import com.avogine.render.model.mesh.data.MeshData;
import com.avogine.render.opengl.VertexArrayObject;
import com.avogine.render.opengl.model.mesh.data.SkeletonData.BoneID;
import com.avogine.render.opengl.model.mesh.data.VertexData.*;
import com.avogine.render.opengl.model.mesh.data.VertexData.Vertex.*;

/**
 *
 */
@SuppressWarnings("java:S107")
public final class AnimatedMesh extends Mesh {
	/**
	 * Max number of bone weights that can be applied to a single vertex.
	 */
	public static final int MAX_WEIGHTS = 4;

	/**
	 * @param positions
	 * @param normals
	 * @param tangents nullable
	 * @param bitangents nullable
	 * @param textureCoordinates
	 * @param weights 
	 * @param boneIDs 
	 * @param indices
	 * @param aabb
	 */
	public AnimatedMesh(float[] positions, float[] normals, float[] tangents, float[] bitangents, float[] textureCoordinates, int[] boneIDs, float[] weights, int[] indices, AABBf aabb) {
		super(buildVAO(positions, normals, tangents, bitangents, textureCoordinates, boneIDs, weights, indices), indices.length, aabb);
	}
	
	/**
	 * @param positions
	 * @param normals
	 * @param tangents nullable
	 * @param bitangents nullable
	 * @param textureCoordinates
	 * @param indices
	 * @param aabb
	 */
	public AnimatedMesh(float[] positions, float[] normals, float[] tangents, float[] bitangents, float[] textureCoordinates, int[] indices, AABBf aabb) {
		super(buildVAO(positions, normals, tangents, bitangents, textureCoordinates, new int[MAX_WEIGHTS * positions.length / 3], new float[MAX_WEIGHTS * positions.length / 3], indices),
				indices.length, aabb);
	}
	
	/**
	 * @param meshData
	 */
	public AnimatedMesh(MeshData meshData) {
		this(meshData.vertices(), meshData.normals(), meshData.tangents(), meshData.bitangents(), meshData.textureCoordinates(),
				meshData.skeletonData().boneIds(), meshData.skeletonData().weights(),
				meshData.indices(),
				meshData.aabb());
	}
	
	private static VertexArrayObject buildVAO(float[] positions, float[] normals, float[] tangents, float[] bitangents, float[] textureCoordinates, int[] boneIDs, float[] weights, int[] indices) {
		try (Vertex3D positionsVertex = Vertex.wrap3D(positions, 0);
				Vertex3D normalsVertex = Vertex.wrap3D(normals, 1);
				Vertex3D tangentsVertex = Vertex.wrap3D(Objects.isNull(tangents) ? normals.length : tangents, 2);
				Vertex3D bitangentsVertex = Vertex.wrap3D(Objects.isNull(bitangents) ? normals.length : bitangents, 3);
				Vertex2D textureCoordinatesVertex = Vertex.wrap2D(textureCoordinates, 4);
				BoneID boneIDsVertex = BoneID.wrap(boneIDs, 5);
				Vertex4D weightsVertex = Vertex.wrap4D(weights, 6);
				Index indicesIndex = Index.wrap(indices);) {
			return VertexArrayObject.gen(array -> array
					.vertex(positionsVertex)
					.vertex(normalsVertex)
					.vertex(tangentsVertex)
					.vertex(bitangentsVertex)
					.vertex(textureCoordinatesVertex)
					.vertex(boneIDsVertex)
					.vertex(weightsVertex)
					.elements(indicesIndex));
		}
	}
}
