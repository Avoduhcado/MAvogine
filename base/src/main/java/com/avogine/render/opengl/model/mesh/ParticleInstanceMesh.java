package com.avogine.render.opengl.model.mesh;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.GL_STREAM_DRAW;

import java.nio.*;

import org.lwjgl.opengl.GL31;

import com.avogine.render.model.mesh.Particle2DMeshData;
import com.avogine.render.model.mesh.parameters.Instanceable;
import com.avogine.render.opengl.*;
import com.avogine.render.opengl.VertexAttrib.Pointer;

/**
 *
 */
public class ParticleInstanceMesh extends Mesh<Particle2DMeshData> implements Instanceable {
	
	private int maxInstances;
	private int currentInstances;
	
	/**
	 * @param meshData 
	 */
	public ParticleInstanceMesh(Particle2DMeshData meshData) {
		super(meshData);
		this.maxInstances = meshData.getMaxInstances();
	}

	@Override
	public VAO buildVertexArray(Particle2DMeshData meshData) {
		try (var vertexBuffers = meshData.getVertexBuffers()) {
			return VAO.gen().bind()
					.vertexBuffer(VBO.gen().bind()
							.bufferData(vertexBuffers.positions())
							.enable(VertexAttrib.array(0)
									.pointer(Pointer.tightlyPackedUnnormalizedFloat(3))
									.divisor(0)))
					.vertexBuffer(VBO.gen().bind()
							.bufferData(4L * Float.BYTES * meshData.getMaxInstances(), GL_STREAM_DRAW)
							.enable(VertexAttrib.array(1)
									.pointer(Pointer.tightlyPackedUnnormalizedFloat(4))
									.divisor(1)))
					.vertexBuffer(VBO.gen().bind()
							.bufferData(4L * Byte.BYTES * meshData.getMaxInstances(), GL_STREAM_DRAW)
							.enable(VertexAttrib.array(2)
									.pointer(new Pointer(4, GL_UNSIGNED_BYTE, true, 0, 0))
									.divisor(1)));
		} finally {
			VAO.unbind();
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
		vao.bind();
		updateInstanceBuffer(1, positions);
		updateInstanceBuffer(2, colors);
	}

	@Override
	public <U extends Buffer> void updateInstanceBuffer(int vboIndex, U buffer) {
		vao.vertexBuffers().get(vboIndex).bind().bufferSubData(buffer);
	}

	@Override
	public int getMaxInstances() {
		return maxInstances;
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
