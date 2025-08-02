package com.avogine.render.opengl.model.mesh;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.GL_STREAM_DRAW;
import static org.lwjgl.opengl.GL31.glDrawArraysInstanced;

import java.nio.*;

import org.lwjgl.system.MemoryUtil;

import com.avogine.render.model.mesh.Instanceable;
import com.avogine.render.opengl.VertexArrayObject;
import com.avogine.render.opengl.model.mesh.data.ParticleVertexData;

/**
 *
 */
public class ParticleMesh2 extends VertexArrayObject<ParticleVertexData> implements Instanceable {

	private final int maxInstances;
	private int currentInstances;
	
	/**
	 * @param vertexData
	 */
	public ParticleMesh2(ParticleVertexData vertexData) {
		super(vertexData);
		maxInstances = vertexData.maxInstances();
	}

	@Override
	protected Builder init(ParticleVertexData vertexData) {
		try {
			return initVAO()
					.buffer(VertexBufferObject.arrayBufferStaticDraw(vertexData.positions()))
					.attrib(VertexAttrib.array(0).pointer(VertexAttrib.Format.tightlyPackedUnnormalizedFloat(3)).divisor(0))
					.buffer(new VertexBufferObject(GL_STREAM_DRAW).bind().bufferData(4L * Float.BYTES * vertexData.maxInstances()))
					.attrib(VertexAttrib.array(1).pointer(VertexAttrib.Format.tightlyPackedUnnormalizedFloat(4)).divisor(1))
					.buffer(new VertexBufferObject(GL_STREAM_DRAW).bind().bufferData(4L * Float.BYTES * vertexData.maxInstances()))
					.attrib(VertexAttrib.array(2).pointer(4, GL_UNSIGNED_BYTE, true, 0, 0).divisor(1));
		} finally {
			unbind();
			MemoryUtil.memFree(vertexData.positions());
		}
	}

	@Override
	public void draw() {
		glDrawArraysInstanced(GL_TRIANGLE_FAN, 0, getVertexCount(), getCurrentInstances());
	}

	@Override
	public <T extends Buffer> void updateInstanceBuffer(int vboIndex, long offset, T data) {
		getVertexBufferObjects()[vboIndex].bind().bufferSubData(offset, data);
	}
	
	/**
	 * @param positions
	 * @param colors
	 */
	public void update(FloatBuffer positions, ByteBuffer colors) {
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
