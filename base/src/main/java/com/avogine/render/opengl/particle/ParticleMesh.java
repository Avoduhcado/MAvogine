package com.avogine.render.opengl.particle;

import static org.lwjgl.opengl.GL11.GL_TRIANGLE_FAN;
import static org.lwjgl.opengl.GL31.glDrawArraysInstanced;

import java.nio.*;

import org.lwjgl.system.MemoryUtil;

import com.avogine.render.opengl.VAO;
import com.avogine.render.util.Instanceable;

/**
 *
 */
public class ParticleMesh implements Instanceable {

	private final VAO vao;
	private final int vertexCount;
	
	private final int maxInstances;
	private int currentInstances;

	/**
	 * @param positions 
	 * @param maxInstances 
	 */
	public ParticleMesh(FloatBuffer positions, int maxInstances) {
		long instanceBufferSize = 4L * Float.BYTES * maxInstances;
		
		try {
			vao = VAO.gen(builder -> builder
					.buffer().data(positions).bind()
					.vertexAttribArray(0)
						.pointer().size(3).tightlyPacked()
						.divisor(0)
						.enable()
					.buffer()
						.stream().draw()
						.data(instanceBufferSize)
						.bind()
					.vertexAttribArray(1)
						.pointer().tightlyPacked()
						.divisor(1)
						.enable()
					.buffer()
						.stream().draw()
						.data(instanceBufferSize)
						.bind()
					.vertexAttribArray(2)
						.pointer().size(4).unsignedByte().normalized().tightlyPacked()
						.divisor(1)
						.enable());
		} finally {
			MemoryUtil.memFree(positions);
		}
		vertexCount = positions.limit() / 3;
		this.maxInstances = maxInstances;
	}
	
	/**
	 * 
	 */
	public void cleanup() {
		vao.cleanup();
	}
	
	protected void draw() {
		glDrawArraysInstanced(GL_TRIANGLE_FAN, 0, vertexCount, getCurrentInstances());
	}
	
	/**
	 * 
	 */
	public void render() {
		vao.bind();
		draw();
	}

	@Override
	public <T extends Buffer> void updateInstanceBuffer(int vboIndex, long offset, T data) {
		vao.bindBuffer(vboIndex, instanceBuffer -> instanceBuffer.bufferSubData(offset, data));
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
