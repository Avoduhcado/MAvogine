package com.avogine.render.opengl.model.mesh;

import static org.lwjgl.opengl.GL11C.*;
import static org.lwjgl.opengl.GL31C.glDrawElementsInstanced;

import java.nio.*;
import java.util.*;

import org.joml.primitives.AABBf;
import org.lwjgl.system.MemoryUtil;

import com.avogine.render.model.mesh.Instanceable;
import com.avogine.render.model.mesh.data.*;
import com.avogine.render.opengl.model.mesh.data.*;
import com.avogine.render.opengl.model.mesh.data.Vertex.Attrib;

/**
 *
 */
public class StaticMesh extends Mesh {
	
	/**
	 * @param positions
	 * @param vertexData
	 * @param indices
	 * @param boundingBox
	 */
	public StaticMesh(FloatBuffer positions, VertexData vertexData, IntBuffer indices, AABBf boundingBox) {
		super(assembleVertices(positions, vertexData), new Index(indices), boundingBox);
	}
	
	protected StaticMesh(List<Vertex> vertices, Index index, AABBf boundingBox) {
		super(vertices, index, boundingBox);
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
		
//		var staticVbo = VBO.arrayBuffer(interleavedVertices);
		
		int vertexStride = vertexSize * Float.BYTES;
		
		return new Vertex(interleavedVertices, 
				new Attrib(1, new Attrib.Pointer(3, GL_FLOAT, false, vertexStride, 0)),
				new Attrib(2, new Attrib.Pointer(3, GL_FLOAT, false, vertexStride, 3L * Float.BYTES)),
				new Attrib(3, new Attrib.Pointer(3, GL_FLOAT, false, vertexStride, (3 + 3L) * Float.BYTES)),
				new Attrib(4, new Attrib.Pointer(2, GL_FLOAT, false, vertexStride, (3 + 3 + 3L) * Float.BYTES)));
	}
	
	protected static List<Vertex> assembleVertices(FloatBuffer positions, VertexData vertices) {
		return List.of(
				Vertex.vertex3D(positions, 0),
				Vertex.vertex3D(vertices.normals(), 1),
				Vertex.vertex3D(vertices.tangents(), 2),
				Vertex.vertex3D(vertices.bitangents(), 3),
				Vertex.vertex2D(vertices.textureCoordinates(), 4));
	}
	
	/**
	 * 
	 */
	public static class StaticInstancedMesh extends StaticMesh implements Instanceable {
		
		private static final int INSTANCE_VBO_INDEX = 5;
		
		private final int maxInstances;

		/**
		 * @param positions
		 * @param vertexData 
		 * @param indices
		 * @param boundingBox
		 * @param instanceData 
		 */
		public StaticInstancedMesh(FloatBuffer positions, VertexData vertexData, IntBuffer indices, AABBf boundingBox, InstanceData instanceData) {
			List<Vertex> vertices = new ArrayList<>(assembleVertices(positions, vertexData));
			var instanceMatrices = Vertex.vertex4x4Instanced(instanceData.instanceMatrices(), INSTANCE_VBO_INDEX);
			vertices.add(instanceMatrices);
			
			super(vertices, new Index(indices), boundingBox);
			maxInstances = instanceData.instanceCount();
		}
		
		/**
		 * @param <T>
		 * @param offset
		 * @param data
		 */
		public <T extends Buffer> void updateInstanceBuffer(long offset, T data) {
			vao.bind();
			
			vbos[INSTANCE_VBO_INDEX].bind();
			vbos[INSTANCE_VBO_INDEX].bufferSubData(offset, data);
			vbos[INSTANCE_VBO_INDEX].unbind();
			
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
