package com.avogine.render.opengl.model.mesh;

import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL15.glBufferSubData;

import java.nio.*;

import org.lwjgl.opengl.*;

import com.avogine.render.model.mesh.Instanceable;
import com.avogine.render.opengl.*;
import com.avogine.render.opengl.model.mesh.data.MeshData;

/**
 * 
 */
public class InstancedMesh extends Mesh implements Instanceable {
	
	private int maxInstances;
	
	/**
	 * @param meshData
	 * @param maxInstances 
	 */
	public InstancedMesh(MeshData meshData, int maxInstances) {
		super(meshData);
		this.maxInstances = maxInstances;
	}
	
	@Override
	protected int generateVertexArray(MeshData vertexData) {
		try (var vertexBuffers = vertexData.vertexBuffers();
				var instancedBuffers = vertexData.instancedBuffers();) {
			var vertexAttrib3f = VertexAttrib.Pointer.tightlyPackedUnnormalizedFloat(3);
			int vaoID = VAO.gen().bind().id();

			getVertexBufferObjects().add(VBO.gen().bind().bufferData(vertexBuffers.positions())
					.enable(VertexAttrib.array(0)
							.pointer(vertexAttrib3f).divisor(0)).id());
			getVertexBufferObjects().add(VBO.gen().bind().bufferData(vertexBuffers.normals())
					.enable(VertexAttrib.array(1)
							.pointer(vertexAttrib3f).divisor(0)).id());
			getVertexBufferObjects().add(VBO.gen().bind().bufferData(vertexBuffers.tangents())
					.enable(VertexAttrib.array(2)
							.pointer(vertexAttrib3f).divisor(0)).id());
			getVertexBufferObjects().add(VBO.gen().bind().bufferData(vertexBuffers.bitangents())
					.enable(VertexAttrib.array(3)
							.pointer(vertexAttrib3f).divisor(0)).id());
			getVertexBufferObjects().add(VBO.gen().bind().bufferData(vertexBuffers.textureCoordinates())
					.enable(VertexAttrib.array(4)
							.pointer(VertexAttrib.Pointer.tightlyPackedUnnormalizedFloat(2)).divisor(0)).id());
			getVertexBufferObjects().add(VBO.gen().bind().bufferData(instancedBuffers.instanceMatrices())
					.enable(VertexAttrib.array(5).pointer(new VertexAttrib.Pointer(4, GL_FLOAT, false, 4 * (Float.BYTES * 4), 0)).divisor(1))
					.enable(VertexAttrib.array(6).pointer(new VertexAttrib.Pointer(4, GL_FLOAT, false, 4 * (Float.BYTES * 4), 1L * (Float.BYTES * 4))).divisor(1))
					.enable(VertexAttrib.array(7).pointer(new VertexAttrib.Pointer(4, GL_FLOAT, false, 4 * (Float.BYTES * 4), 2L * (Float.BYTES * 4))).divisor(1))
					.enable(VertexAttrib.array(8).pointer(new VertexAttrib.Pointer(4, GL_FLOAT, false, 4 * (Float.BYTES * 4), 3L * (Float.BYTES * 4))).divisor(1))
					.id());
			getVertexBufferObjects().add(VBO.gen().bind().bufferData(instancedBuffers.instanceNormals())
					.enable(VertexAttrib.array(9).pointer(new VertexAttrib.Pointer(4, GL_FLOAT, false, 4 * (Float.BYTES * 4), 0)).divisor(1))
					.enable(VertexAttrib.array(10).pointer(new VertexAttrib.Pointer(4, GL_FLOAT, false, 4 * (Float.BYTES * 4), 1L * (Float.BYTES * 4))).divisor(1))
					.enable(VertexAttrib.array(11).pointer(new VertexAttrib.Pointer(4, GL_FLOAT, false, 4 * (Float.BYTES * 4), 2L * (Float.BYTES * 4))).divisor(1))
					.enable(VertexAttrib.array(12).pointer(new VertexAttrib.Pointer(4, GL_FLOAT, false, 4 * (Float.BYTES * 4), 3L * (Float.BYTES * 4))).divisor(1))
					.id());
			getVertexBufferObjects().add(VBO.genEBO().bind().bufferData(vertexBuffers.indices()).id());
			
			return vaoID;
		} finally {
			VAO.unbind();
		}
	}
	
	@Override
	public void draw() {
		GL31.glDrawElementsInstanced(GL11.GL_TRIANGLES, getVertexCount(), GL11.GL_UNSIGNED_INT, 0, maxInstances);
	}

	/**
	 * @param <U>
	 * @param vboIndex
	 * @param offset
	 * @param buffer
	 */
	public <U extends Buffer> void updateInstanceBuffer(int vboIndex, long offset, U buffer) {
		int vboID = getVertexBufferObjects().get(vboIndex);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboID);
		switch (buffer) {
			case ByteBuffer b -> glBufferSubData(GL15.GL_ARRAY_BUFFER, offset, b);
			case CharBuffer c -> throw new IllegalArgumentException("Cannot buffer CharBuffer.");
			case DoubleBuffer d -> glBufferSubData(GL15.GL_ARRAY_BUFFER, offset, d);
			case FloatBuffer f -> glBufferSubData(GL15.GL_ARRAY_BUFFER, offset, f);
			case IntBuffer i -> glBufferSubData(GL15.GL_ARRAY_BUFFER, offset, i);
			case LongBuffer l -> glBufferSubData(GL15.GL_ARRAY_BUFFER, offset, l);
			case ShortBuffer s -> glBufferSubData(GL15.GL_ARRAY_BUFFER, offset, s);
			case null -> glBufferSubData(GL15.GL_ARRAY_BUFFER, offset, (ByteBuffer) null);
		}
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
