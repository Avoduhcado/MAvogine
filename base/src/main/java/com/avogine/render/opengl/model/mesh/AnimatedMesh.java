package com.avogine.render.opengl.model.mesh;

import org.joml.primitives.AABBf;

import com.avogine.render.model.mesh.data.MeshData;
import com.avogine.render.model.mesh.data.MeshData.AnimMeshData;
import com.avogine.render.opengl.VertexArrayObject;
import com.avogine.render.opengl.model.mesh.data.Vertex;
import com.avogine.render.opengl.model.mesh.data.Vertex.*;

/**
 *
 */
public class AnimatedMesh extends Mesh {
	/**
	 * 
	 */
	public static final int MAX_WEIGHTS = 4;
	
	private AnimatedMesh(VertexArrayObject vao, int vertexCount, AABBf boundingBox) {
		super(vao, vertexCount, boundingBox);
	}
	
	/**
	 * @param meshData
	 */
	public AnimatedMesh(MeshData meshData) {
		this(VertexArrayObject.gen(builder -> animatedMeshBuilder(meshData, meshData.skeletonData(), builder)), meshData.indices().length, meshData.aabb());
	}
	
	private static VertexArrayObject.Builder animatedMeshBuilder(MeshData meshData, AnimMeshData skeletonData, VertexArrayObject.Builder builder) {
		try (BoneID boneIDsVertex = BoneID.wrap(skeletonData.boneIds(), 5);
				Vertex4D weightsVertex = Vertex.wrap4D(skeletonData.weights(), 6);) {
			return StaticMesh.staticMeshBuilder(meshData.vertices(), meshData.normals(), meshData.tangents(), meshData.bitangents(), meshData.textureCoordinates(), meshData.indices(), builder)
					.vertex(boneIDsVertex)
					.vertex(weightsVertex);
		}
	}
}
