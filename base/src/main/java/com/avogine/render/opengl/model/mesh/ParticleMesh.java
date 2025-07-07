package com.avogine.render.opengl.model.mesh;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;

import java.nio.*;

import org.lwjgl.opengl.*;
import org.lwjgl.system.MemoryUtil;

import com.avogine.render.model.mesh.Instanceable;
import com.avogine.render.opengl.*;
import com.avogine.render.opengl.VertexAttrib.Pointer;
import com.avogine.render.opengl.model.mesh.data.ParticleMeshData;

/**
 *
 */
public class ParticleMesh extends VertexArrayObject<ParticleMeshData> implements Instanceable {
	
	private int instanceLimit;
	private int currentInstances;
	
	/**
	 * @param meshData 
	 */
	public ParticleMesh(ParticleMeshData meshData) {
		super(meshData);
		instanceLimit = meshData.maxInstances();
	}
	
	@Override
	protected int generateVertexArray(ParticleMeshData vertexData) {
		try {
			int vaoID = VAO.gen().bind().id();
			
			getVertexBufferObjects().add(VBO.gen().bind()
					.bufferData(vertexData.positions())
					.enable(VertexAttrib.array(0)
							.pointer(Pointer.tightlyPackedUnnormalizedFloat(3))
							.divisor(0)).id());
			getVertexBufferObjects().add(VBO.gen().bind()
					.bufferData(4L * Float.BYTES * vertexData.maxInstances(), GL_STREAM_DRAW)
					.enable(VertexAttrib.array(1)
							.pointer(Pointer.tightlyPackedUnnormalizedFloat(4))
							.divisor(1)).id());
			getVertexBufferObjects().add(VBO.gen().bind()
					.bufferData(4L * Byte.BYTES * vertexData.maxInstances(), GL_STREAM_DRAW)
					.enable(VertexAttrib.array(2)
							.pointer(new Pointer(4, GL_UNSIGNED_BYTE, true, 0, 0))
							.divisor(1)).id());
			
			return vaoID;
		} finally {
			VAO.unbind();
			MemoryUtil.memFree(vertexData.positions());
		}
	}

	@Override
	public void draw() {
		GL31.glDrawArraysInstanced(GL_TRIANGLE_FAN, 0, getVertexCount(), getCurrentInstances());
	}
	
	/**
	 * @param positions
	 * @param colors
	 */
	public void update(FloatBuffer positions, ByteBuffer colors) {
		bind();
		updateInstanceBuffer(1, positions);
		updateInstanceBuffer(2, colors);
	}

	@Override
	public <U extends Buffer> void updateInstanceBuffer(int vboIndex, U buffer) {
		int vboID = getVertexBufferObjects().get(vboIndex);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboID);
		switch (buffer) {
			case ByteBuffer b -> glBufferSubData(GL15.GL_ARRAY_BUFFER, 0, b);
			case CharBuffer c -> throw new IllegalArgumentException("Cannot buffer CharBuffer.");
			case DoubleBuffer d -> glBufferSubData(GL15.GL_ARRAY_BUFFER, 0, d);
			case FloatBuffer f -> glBufferSubData(GL15.GL_ARRAY_BUFFER, 0, f);
			case IntBuffer i -> glBufferSubData(GL15.GL_ARRAY_BUFFER, 0, i);
			case LongBuffer l -> glBufferSubData(GL15.GL_ARRAY_BUFFER, 0, l);
			case ShortBuffer s -> glBufferSubData(GL15.GL_ARRAY_BUFFER, 0, s);
			case null -> glBufferSubData(GL15.GL_ARRAY_BUFFER, 0, (ByteBuffer) null);
		}
	}

	@Override
	public int getMaxInstances() {
		return instanceLimit;
	}
	
	/**
	 * @return the currentInstances
	 */
	public int getCurrentInstances() {
		return currentInstances;
	}
	
	/**
	 * @param currentInstances the currentInstances to set
	 */
	public void setCurrentInstances(int currentInstances) {
		this.currentInstances = currentInstances;
	}
}
