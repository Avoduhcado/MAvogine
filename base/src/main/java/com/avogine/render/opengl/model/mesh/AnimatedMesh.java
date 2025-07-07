package com.avogine.render.opengl.model.mesh;

import com.avogine.render.model.mesh.data.VertexBuffers;
import com.avogine.render.opengl.*;
import com.avogine.render.opengl.model.mesh.data.MeshData;

/**
 * 
 */
public class AnimatedMesh extends Mesh {

	/**
	 * @param meshData 
	 */
	public AnimatedMesh(MeshData meshData) {
		super(meshData);
	}
	
	@Override
	protected int generateVertexArray(MeshData vertexData) {
		try (VertexBuffers vertexBuffers = vertexData.vertexBuffers()) {
			var vertexAttrib3f = VertexAttrib.Pointer.tightlyPackedUnnormalizedFloat(3);
			var vertexAttrib4f = VertexAttrib.Pointer.tightlyPackedUnnormalizedFloat(4);
			return VAO.gen().bind()
					.vertexBuffer(VBO.gen().bind()
							.bufferData(vertexBuffers.positions()).enable(VertexAttrib.array(0)
									.pointer(vertexAttrib3f)))
					.vertexBuffer(VBO.gen().bind()
							.bufferData(vertexBuffers.normals()).enable(VertexAttrib.array(1)
									.pointer(vertexAttrib3f)))
					.vertexBuffer(VBO.gen().bind()
							.bufferData(vertexBuffers.tangents()).enable(VertexAttrib.array(2)
									.pointer(vertexAttrib3f)))
					.vertexBuffer(VBO.gen().bind()
							.bufferData(vertexBuffers.bitangents()).enable(VertexAttrib.array(3)
									.pointer(vertexAttrib3f)))
					.vertexBuffer(VBO.gen().bind()
							.bufferData(vertexBuffers.textureCoordinates()).enable(VertexAttrib.array(4)
									.pointer(VertexAttrib.Pointer.tightlyPackedUnnormalizedFloat(2))))
					.vertexBuffer(VBO.gen().bind()
							.bufferData(vertexBuffers.weights()).enable(VertexAttrib.array(5)
									.pointer(vertexAttrib4f)))
					.vertexBuffer(VBO.gen().bind()
							.bufferData(vertexBuffers.boneIds()).enable(VertexAttrib.array(6)
									.pointer(vertexAttrib4f)))
					.vertexBuffer(VBO.genEBO().bind()
							.bufferData(vertexBuffers.indices()))
					.id();
		} finally {
			VAO.unbind();
		}
	}

}
