package com.avogine.render.opengl.particle;

import static org.lwjgl.opengl.GL11C.*;
import static org.lwjgl.opengl.GL15C.GL_STREAM_DRAW;
import static org.lwjgl.opengl.GL31C.glDrawArraysInstanced;

import java.nio.*;

import org.lwjgl.system.MemoryUtil;

import com.avogine.render.model.mesh.*;
import com.avogine.render.opengl.*;
import com.avogine.render.opengl.model.mesh.data.Vertex.*;

/**
 *
 */
public class ParticleMesh implements Renderable, Instanceable {
	private static final Attrib POSITION_ATTRIB = new Attrib(0, Attrib.POINTER_3F);
	private static final Attrib INSTANCE_POSITION_ATTRIB = new Attrib(1, Attrib.POINTER_4F, 1);
	private static final Attrib INSTANCE_COLOR_ATTRIB = new Attrib(2, VertexAttrib.pointer().type(GL_UNSIGNED_BYTE).normalized(true).build(), 1);
	
	private final VAO vao;
	
	private final VBO vbo;
	private final VBO instanceVBO;
	private final VBO instanceColorsVBO;
	
	private final int vertexCount;
	
	private final int maxInstances;
	private int currentInstances;

	/**
	 *
	 * @param positions
	 * @param maxInstances
	 */
	public record ParticleData(FloatBuffer positions, int maxInstances) implements AutoCloseable {
		@Override
		public void close() {
			MemoryUtil.memFree(positions);
		}
	}
	
	/**
	 * @param positions 
	 * @param maxInstances 
	 */
	public ParticleMesh(FloatBuffer positions, int maxInstances) {
		long instanceBufferSize = 4L * Float.BYTES * maxInstances;
		
		vao = new VAO();
		
		vbo = VBO.arrayBuffer(positions);
		POSITION_ATTRIB.enable();
		
		instanceVBO = VBO.arrayBuffer(instanceBufferSize, GL_STREAM_DRAW);
		INSTANCE_POSITION_ATTRIB.enable();
		
		instanceColorsVBO = VBO.arrayBuffer(instanceBufferSize, GL_STREAM_DRAW);
		INSTANCE_COLOR_ATTRIB.enable();
		
		vao.unbind();
		
		vertexCount = positions.limit() / 3;
		this.maxInstances = maxInstances;
	}
	
	/**
	 * @param particleData
	 */
	public ParticleMesh(ParticleData particleData) {
		this(particleData.positions, particleData.maxInstances);
	}
	
	@Override
	public void cleanup() {
		vbo.cleanup();
		instanceVBO.cleanup();
		instanceColorsVBO.cleanup();
		vao.cleanup();
	}
	
	@Override
	public void render() {
		vao.bind();
		draw();
	}
	
	private void draw() {
		glDrawArraysInstanced(GL_TRIANGLE_FAN, 0, vertexCount, getCurrentInstances());
	}
	
	/**
	 * @param offset
	 * @param positions
	 */
	public void updatePositions(long offset, FloatBuffer positions) {
		vao.bind();
		
		instanceVBO.bind();
		instanceVBO.bufferSubData(offset, positions);
		instanceVBO.unbind();
		
		vao.unbind();
	}
	
	/**
	 * @param positions
	 */
	public void updatePositions(FloatBuffer positions) {
		updatePositions(0, positions);
	}
	
	/**
	 * @param offset
	 * @param colors
	 */
	public void updateColors(long offset, ByteBuffer colors) {
		vao.bind();
		
		instanceColorsVBO.bind();
		instanceColorsVBO.bufferSubData(offset, colors);
		instanceColorsVBO.unbind();
		
		vao.unbind();
	}
	
	/**
	 * @param colors
	 */
	public void updateColors(ByteBuffer colors) {
		updateColors(0, colors);
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
