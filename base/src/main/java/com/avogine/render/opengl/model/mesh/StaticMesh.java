package com.avogine.render.opengl.model.mesh;

import java.util.Objects;

import org.joml.primitives.AABBf;

import com.avogine.render.model.mesh.data.MeshData;
import com.avogine.render.opengl.VertexArrayObject;
import com.avogine.render.opengl.model.mesh.data.VertexData.*;
import com.avogine.render.opengl.model.mesh.data.VertexData.Vertex.*;

/**
 *
 */
public final class StaticMesh extends Mesh {
	/**
	 * @param positions
	 * @param normals
	 * @param tangents nullable
	 * @param bitangents nullable
	 * @param textureCoordinates
	 * @param indices
	 * @param aabb
	 */
	@SuppressWarnings("java:S107")
	public StaticMesh(float[] positions, float[] normals, float[] tangents, float[] bitangents, float[] textureCoordinates, int[] indices, AABBf aabb) {
		super(buildVAO(positions, normals, tangents, bitangents, textureCoordinates, indices), indices.length, aabb);
	}
	
	/**
	 * @param meshData
	 */
	public StaticMesh(MeshData meshData) {
		this(meshData.vertices(), meshData.normals(), meshData.tangents(), meshData.bitangents(), meshData.textureCoordinates(), meshData.indices(), meshData.aabb());
	}
	
	private static VertexArrayObject buildVAO(float[] positions, float[] normals, float[] tangents, float[] bitangents, float[] textureCoordinates, int[] indices) {
		try (Vertex3D positionsVertex = Vertex.wrap3D(positions, 0);
				Vertex3D normalsVertex = Vertex.wrap3D(normals, 1);
				Vertex3D tangentsVertex = Vertex.wrap3D(Objects.isNull(tangents) ? normals.length : tangents, 2);
				Vertex3D bitangentsVertex = Vertex.wrap3D(Objects.isNull(bitangents) ? normals.length : bitangents, 3);
				Vertex2D textureCoordinatesVertex = Vertex.wrap2D(textureCoordinates, 4);
				Index indicesIndex = Index.wrap(indices);) {
			return VertexArrayObject.gen(array -> array
					.vertex(positionsVertex)
					.vertex(normalsVertex)
					.vertex(tangentsVertex)
					.vertex(bitangentsVertex)
					.vertex(textureCoordinatesVertex)
					.elements(indicesIndex));
		}
	}
}
