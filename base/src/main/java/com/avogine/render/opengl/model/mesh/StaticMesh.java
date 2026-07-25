package com.avogine.render.opengl.model.mesh;

import static org.lwjgl.opengl.GL11C.*;
import static org.lwjgl.opengl.GL31C.glDrawElementsInstanced;

import java.nio.*;
import java.util.*;

import org.joml.primitives.AABBf;
import org.lwjgl.system.MemoryUtil;

import com.avogine.render.model.mesh.Instanceable;
import com.avogine.render.model.mesh.data.*;
import com.avogine.render.opengl.*;
import com.avogine.render.opengl.model.mesh.data.*;
import com.avogine.render.opengl.model.mesh.data.Vertex.Attrib;

/**
 *
 */
public class StaticMesh extends Mesh {
	
	protected record StaticVertexArray(Vertex positions, Vertex attributes, Index indices) {
		
	}
	
	private StaticMesh(VAO vao, VBO[] vbos, VBO ebo, int vertexCount, AABBf boundingBox) {
		super(vao, vbos, ebo, vertexCount, boundingBox);
	}
	
	/**
	 * @param positions
	 * @param vertexData
	 * @param indices
	 * @param boundingBox
	 */
	public StaticMesh(FloatBuffer positions, VertexData vertexData, IntBuffer indices, AABBf boundingBox) {
		var meshData = assembleStaticVertices(positions, vertexData, indices);
		var vao = new VAO(meshData.vertices(), meshData.index());
		
		this(vao, meshData.vertices().stream().map(Vertex::buffer).toArray(VBO[]::new), meshData.index().buffer(), meshData.index().vertexCount(), boundingBox);
	}
	
	@SuppressWarnings("unused") // Experimental testing of interleaved static vertex data
	private static Vertex interleaveVertexData(VertexData vertices, int vertexCount) {
		int vertexSize = vertices.vertexSize();
		FloatBuffer interleavedVertices = MemoryUtil.memAllocFloat(vertices.vertexSize() * vertexCount);
		
		for (int i = 0; i < vertexCount / 3; i++) {
			interleavedVertices
			.put(i * vertexSize, vertices.normals(), i * 3, 3)
			.put((i * vertexSize) + 3, vertices.tangents(), i * 3, 3)
			.put((i * vertexSize) + 3 + 3, vertices.bitangents(), i * 3, 3)
			.put((i * vertexSize) + 3 + 3 + 3, vertices.textureCoordinates(), i * 2, 2);
		}
		
		var staticVbo = VBO.arrayBuffer(interleavedVertices);
		
		int vertexStride = vertexSize * Float.BYTES;
		
		return new Vertex(staticVbo, 
				new Attrib(1, new Attrib.Pointer(3, GL_FLOAT, false, vertexStride, 0)),
				new Attrib(2, new Attrib.Pointer(3, GL_FLOAT, false, vertexStride, 3L * Float.BYTES)),
				new Attrib(3, new Attrib.Pointer(3, GL_FLOAT, false, vertexStride, (3 + 3L) * Float.BYTES)),
				new Attrib(4, new Attrib.Pointer(2, GL_FLOAT, false, vertexStride, (3 + 3 + 3L) * Float.BYTES)));
	}
	
	protected static MeshData assembleStaticVertices(FloatBuffer positions, VertexData vertices, IntBuffer indices) {
		Set<Vertex> vertexArray = Set.of(
				Vertex.vertex3D(positions, 0),
				Vertex.vertex3D(vertices.normals(), 1),
				Vertex.vertex3D(vertices.tangents(), 2),
				Vertex.vertex3D(vertices.bitangents(), 3),
				Vertex.vertex2D(vertices.textureCoordinates(), 4));
		var index = new Index(indices);
		return new MeshData(vertexArray, index);
	}
	
	/**
	 *
	 */
	public static class StaticInstancedMesh extends StaticMesh implements Instanceable {
		
		private final VBO instanceBuffer;
		private final int maxInstances;
		
		/**
		 * @param vao
		 * @param vertexCount
		 * @param boundingBox
		 */
		private StaticInstancedMesh(VAO vao, VBO[] vbos, VBO ebo, int vertexCount, AABBf boundingBox, VBO instanceBuffer, int maxInstances) {
			super(vao, vbos, ebo, vertexCount, boundingBox);
			this.instanceBuffer = instanceBuffer;
			this.maxInstances = maxInstances;
		}

		/**
		 * @param positions
		 * @param vertexData 
		 * @param indices
		 * @param boundingBox
		 * @param instanceData 
		 */
		public StaticInstancedMesh(FloatBuffer positions, VertexData vertexData, IntBuffer indices, AABBf boundingBox, InstanceData instanceData) {
			MeshData meshData = assembleStaticVertices(positions, vertexData, indices);
			var instanceMatrices = Vertex.vertex4x4Instanced(instanceData.instanceMatrices(), 5);
			Set<Vertex> vertices = new HashSet<>(meshData.vertices());
			vertices.add(instanceMatrices);
			var vao = new VAO(Set.copyOf(vertices), meshData.index());
			
			this(vao, meshData.vertices().stream().map(Vertex::buffer).toArray(VBO[]::new), meshData.index().buffer(), meshData.index().vertexCount(), boundingBox, 
					instanceMatrices.buffer(), instanceData.instanceCount());
		}
		
		@Override
		public void cleanup() {
			instanceBuffer.cleanup();
			super.cleanup();
		}
		
		@Override
		public <T extends Buffer> void updateInstanceBuffer(int vboIndex, long offset, T data) {
			vao.bind();
			
			instanceBuffer.bind();
			instanceBuffer.bufferSubData(offset, data);
			instanceBuffer.unbind();
			
			vao.unbind();
		}

		@Override
		protected void draw() {
			glDrawElementsInstanced(GL_TRIANGLES, vertexCount, GL_UNSIGNED_INT, 0, maxInstances);
		}
		
		@Override
		public int getMaxInstances() {
			return maxInstances;
		}
	}
	
}
