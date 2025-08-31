package com.avogine.render.opengl.particle;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.GL_STREAM_DRAW;
import static org.lwjgl.opengl.GL31.glDrawArraysInstanced;

import java.nio.*;

import org.lwjgl.system.MemoryUtil;

import com.avogine.render.model.mesh.Instanceable;
import com.avogine.render.opengl.*;
import com.avogine.render.opengl.VAO.VAOBuilder.VertexAttrib;
import com.avogine.render.opengl.particle.data.ParticleVertexData;

/**
 *
 */
public class ParticleMesh implements Instanceable {

	private final VAO vao;
	private final int vertexCount;
	
	private final int maxInstances;
	private int currentInstances;

	/**
	 * @param vertexData
	 */
	public ParticleMesh(ParticleVertexData vertexData) {
		try {
			vao = VAO.gen(vertexArray -> vertexArray
					.bindBufferData(VBO.staticDraw(), vertexData.positions())
					.enablePointerDivisor(0, VertexAttrib.Format.tightlyPackedUnnormalizedFloat(3), 0)
					.bind(new VBO(GL_STREAM_DRAW), vbo -> vbo.bufferData(4L * Float.BYTES * vertexData.maxInstances()))
					.enablePointerDivisor(1, VertexAttrib.Format.tightlyPackedUnnormalizedFloat(4), 1)
					.bind(new VBO(GL_STREAM_DRAW), vbo -> vbo.bufferData(4L * Float.BYTES * vertexData.maxInstances()))
					.enablePointerDivisor(2, new VertexAttrib.Format(4, GL_UNSIGNED_BYTE, true, 0, 0), 1));
		} finally {
			MemoryUtil.memFree(vertexData.positions());
		}
		vertexCount = vertexData.getVertexCount();
		maxInstances = vertexData.maxInstances();
	}
	
	/**
	 * 
	 */
	public void cleanup() {
		vao.cleanup();
	}
	
	/**
	 * 
	 */
	public void draw() {
		glDrawArraysInstanced(GL_TRIANGLE_FAN, 0, vertexCount, getCurrentInstances());
	}

	@Override
	public <T extends Buffer> void updateInstanceBuffer(int vboIndex, long offset, T data) {
		VBO instanceBuffer = vao.vertexBufferObjects()[vboIndex];
		instanceBuffer.bind();
		instanceBuffer.bufferSubData(offset, data);
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
