package com.avogine.render.opengl.model.mesh;

import static org.lwjgl.opengl.GL11C.*;
import static org.lwjgl.opengl.GL31C.glDrawElementsInstanced;

import java.nio.Buffer;
import java.util.Objects;

import org.joml.primitives.AABBf;

import com.avogine.render.model.mesh.Instanceable;
import com.avogine.render.model.mesh.data.*;
import com.avogine.render.opengl.VertexArrayObject;
import com.avogine.render.opengl.model.mesh.data.*;
import com.avogine.render.opengl.model.mesh.data.Vertex.*;

/**
 *
 */
public class StaticMesh extends Mesh {

	private StaticMesh(VertexArrayObject vao, int vertexCount, AABBf boundingBox) {
		super(vao, vertexCount, boundingBox);
	}
	
	/**
	 * @param meshData
	 */
	public StaticMesh(MeshData meshData) {
		this(VertexArrayObject.gen(builder -> staticMeshBuilder(
				meshData.vertices(),
				meshData.normals(),
				meshData.tangents(),
				meshData.bitangents(),
				meshData.textureCoordinates(),
				meshData.indices(),
				builder)),
				meshData.indices().length, meshData.aabb());
	}
	
	protected static VertexArrayObject.Builder staticMeshBuilder(float[] vertices, float[] normals, float[] tangents, float[] bitangents, float[] textureCoordinates, int[] indices, 
			VertexArrayObject.Builder builder) {
		try (Vertex3D positionsVertex = Vertex.wrap3D(vertices, 0);
				Vertex3D normalsVertex = Vertex.wrap3D(normals, 1);
				Vertex3D tangentsVertex = Vertex.wrap3D(Objects.isNull(tangents) ? normals.length : tangents, 2);
				Vertex3D bitangentsVertex = Vertex.wrap3D(Objects.isNull(bitangents) ? normals.length : bitangents, 3);
				Vertex2D textureCoordinatesVertex = Vertex.wrap2D(textureCoordinates, 4);
				Index indicesIndex = Index.wrap(indices);) {
			return builder
					.vertex(positionsVertex)
					.vertex(normalsVertex)
					.vertex(tangentsVertex)
					.vertex(bitangentsVertex)
					.vertex(textureCoordinatesVertex)
					.index(indicesIndex);
		}
	}
	
	/**
	 *
	 */
	public static class StaticInstancedMesh extends StaticMesh implements Instanceable {
		
		private final int maxInstances;
		
		/**
		 * @param vao
		 * @param vertexCount
		 * @param boundingBox
		 */
		private StaticInstancedMesh(VertexArrayObject vao, int vertexCount, AABBf boundingBox, int maxInstances) {
			super(vao, vertexCount, boundingBox);
			this.maxInstances = maxInstances;
		}
		
		/**
		 * @param instanceMeshData 
		 */
		public StaticInstancedMesh(InstanceMeshData instanceMeshData) {
			this(VertexArrayObject.gen(builder -> instancedMeshBuilder(instanceMeshData.meshData(), instanceMeshData.instanceTransforms(), builder)),
					instanceMeshData.meshData().indices().length, instanceMeshData.meshData().aabb(), instanceMeshData.maxInstances());
		}
		
		@Override
		public <T extends Buffer> void updateInstanceBuffer(int vboIndex, long offset, T data) {
			vao.bindVBO(vboIndex, vbo -> vbo.bufferSubData(offset, data));
		}

		@Override
		protected void draw() {
			glDrawElementsInstanced(GL_TRIANGLES, vertexCount, GL_UNSIGNED_INT, 0, maxInstances);
		}
		
		@Override
		public int getMaxInstances() {
			return maxInstances;
		}
		
		private static VertexArrayObject.Builder instancedMeshBuilder(MeshData meshData, float[] instanceTransforms, VertexArrayObject.Builder builder) {
			try (Vertex4x4fInstanced instanceTransformsVertex = Vertex4x4fInstanced.wrap(instanceTransforms, 5)) {
				return StaticMesh.staticMeshBuilder(meshData.vertices(), meshData.normals(), meshData.tangents(), meshData.bitangents(), meshData.textureCoordinates(), meshData.indices(), builder)
						.vertex(instanceTransformsVertex);
			}
		}
	}
	
}
