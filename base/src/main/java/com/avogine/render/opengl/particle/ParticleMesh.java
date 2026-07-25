package com.avogine.render.opengl.particle;

import static org.lwjgl.opengl.GL11C.*;
import static org.lwjgl.opengl.GL15C.GL_STREAM_DRAW;
import static org.lwjgl.opengl.GL31C.glDrawArraysInstanced;

import java.nio.*;
import java.util.Set;

import com.avogine.render.model.mesh.*;
import com.avogine.render.opengl.*;
import com.avogine.render.opengl.model.mesh.data.Vertex;
import com.avogine.render.opengl.model.mesh.data.Vertex.*;

/**
 *
 */
public class ParticleMesh implements Renderable, Instanceable {

	private final VAO vao;
	
	private final VBO vbo;
	private final VBO instanceVBO;
	private final VBO instanceColorsVBO;
	
	private final int vertexCount;
	
	private final int maxInstances;
	private int currentInstances;

	/**
	 * @param positions 
	 * @param maxInstances 
	 */
	public ParticleMesh(FloatBuffer positions, int maxInstances) {
		long instanceBufferSize = 4L * Float.BYTES * maxInstances;
		
		Vertex vertex = Vertex.vertex3D(positions, 0);
		var instanceVertex = new Vertex(VBO.arrayBuffer(instanceBufferSize, GL_STREAM_DRAW), new Attrib(1, Attrib.POINTER_4F, 1));
		var instanceColorsVertex = new Vertex(VBO.arrayBuffer(instanceBufferSize, GL_STREAM_DRAW), new Attrib(2, VertexAttrib.pointer().type(GL_UNSIGNED_BYTE).normalized(true).build(), 1));
		
		Set<Vertex> vertices = Set.of(vertex, instanceVertex, instanceColorsVertex);
		vao = new VAO(vertices);
		vbo = vertex.buffer();
		instanceVBO = instanceVertex.buffer();
		instanceColorsVBO = instanceColorsVertex.buffer();
		
		vertexCount = positions.limit() / 3;
		this.maxInstances = maxInstances;
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
	public <T extends Buffer> void updateInstanceBuffer(int vboIndex, long offset, T data) {
		vao.bind();
		
		instanceColorsVBO.bind();
		instanceColorsVBO.bufferSubData(offset, data);
		instanceColorsVBO.unbind();
		
		vao.unbind();
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
