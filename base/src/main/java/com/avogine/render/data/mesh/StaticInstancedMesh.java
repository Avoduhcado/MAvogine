package com.avogine.render.data.mesh;

import static org.lwjgl.opengl.GL11.GL_FLOAT;

import java.nio.Buffer;

import org.lwjgl.opengl.*;

import com.avogine.render.data.InstancedMeshData;
import com.avogine.render.data.gl.*;
import com.avogine.render.data.mesh.parameters.Instanceable;

/**
 * 
 */
public class StaticInstancedMesh extends Mesh<InstancedMeshData> implements Instanceable {
	
	protected int maxInstances;
	
	/**
	 * @param meshData
	 * @param maxInstances 
	 */
	public StaticInstancedMesh(InstancedMeshData meshData, int maxInstances) {
		super(meshData);
		this.maxInstances = maxInstances;
	}
	
	@Override
	public VAO buildVertexArray(InstancedMeshData meshData) {
		try (var vertexBuffers = meshData.getVertexBuffers();
				var instancedBuffers = meshData.getInstancedBuffers();) {
			var vertexAttrib3f = VertexAttrib.Pointer.tightlyPackedUnnormalizedFloat(3);
			return VAO.gen().bind()
					.addBuffer(VBO.gen().bind().bufferData(vertexBuffers.positions())
							.enable(VertexAttrib.array(0).pointer(vertexAttrib3f).divisor(0)))
					.addBuffer(VBO.gen().bind().bufferData(vertexBuffers.normals())
							.enable(VertexAttrib.array(1).pointer(vertexAttrib3f).divisor(0)))
					.addBuffer(VBO.gen().bind().bufferData(vertexBuffers.tangents())
							.enable(VertexAttrib.array(2).pointer(vertexAttrib3f).divisor(0)))
					.addBuffer(VBO.gen().bind().bufferData(vertexBuffers.bitangents())
							.enable(VertexAttrib.array(3).pointer(vertexAttrib3f).divisor(0)))
					.addBuffer(VBO.gen().bind().bufferData(vertexBuffers.textureCoordinates())
							.enable(VertexAttrib.array(4).pointer(VertexAttrib.Pointer.tightlyPackedUnnormalizedFloat(2)).divisor(0)))
					.addBuffer(VBO.gen().bind().bufferData(instancedBuffers.instanceMatrices())
							.enable(VertexAttrib.array(5).pointer(new VertexAttrib.Pointer(4, GL_FLOAT, false, 4 * (Float.BYTES * 4), 0)).divisor(1))
							.enable(VertexAttrib.array(6).pointer(new VertexAttrib.Pointer(4, GL_FLOAT, false, 4 * (Float.BYTES * 4), 1L * (Float.BYTES * 4))).divisor(1))
							.enable(VertexAttrib.array(7).pointer(new VertexAttrib.Pointer(4, GL_FLOAT, false, 4 * (Float.BYTES * 4), 2L * (Float.BYTES * 4))).divisor(1))
							.enable(VertexAttrib.array(8).pointer(new VertexAttrib.Pointer(4, GL_FLOAT, false, 4 * (Float.BYTES * 4), 3L * (Float.BYTES * 4))).divisor(1)))
					.addBuffer(VBO.gen().bind().bufferData(instancedBuffers.instanceNormals())
							.enable(VertexAttrib.array(9).pointer(new VertexAttrib.Pointer(4, GL_FLOAT, false, 4 * (Float.BYTES * 4), 0)).divisor(1))
							.enable(VertexAttrib.array(10).pointer(new VertexAttrib.Pointer(4, GL_FLOAT, false, 4 * (Float.BYTES * 4), 1L * (Float.BYTES * 4))).divisor(1))
							.enable(VertexAttrib.array(11).pointer(new VertexAttrib.Pointer(4, GL_FLOAT, false, 4 * (Float.BYTES * 4), 2L * (Float.BYTES * 4))).divisor(1))
							.enable(VertexAttrib.array(12).pointer(new VertexAttrib.Pointer(4, GL_FLOAT, false, 4 * (Float.BYTES * 4), 3L * (Float.BYTES * 4))).divisor(1)))
					.addBuffer(VBO.genEBO().bind().bufferData(vertexBuffers.indices()));
		} finally {
			VAO.unbind();
		}
	}
	
	@Override
	public void draw() {
		vao.bind();
		GL31.glDrawElementsInstanced(GL11.GL_TRIANGLES, getVertexCount(), GL11.GL_UNSIGNED_INT, 0, maxInstances);
		VAO.unbind();
	}

	/**
	 * @param <U>
	 * @param vboIndex
	 * @param offset
	 * @param buffer
	 */
	public <U extends Buffer> void updateInstanceBuffer(int vboIndex, long offset, U buffer) {
		vao.vertexBufferObjects().get(vboIndex).bind().bufferSubData(offset, buffer);
	}
	
	@Override
	public <U extends Buffer> void updateInstanceBuffer(int vboIndex, U buffer) {
		updateInstanceBuffer(vboIndex, 0, buffer);
	}

	@Override
	public int getMaxInstances() {
		return maxInstances;
	}
	
}
