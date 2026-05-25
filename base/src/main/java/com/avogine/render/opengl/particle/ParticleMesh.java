package com.avogine.render.opengl.particle;

import static org.lwjgl.opengl.GL11.GL_TRIANGLE_FAN;
import static org.lwjgl.opengl.GL31.glDrawArraysInstanced;

import java.nio.Buffer;

import com.avogine.render.opengl.VertexArrayObject;
import com.avogine.render.opengl.model.mesh.data.VertexData.Vertex;
import com.avogine.render.opengl.model.mesh.data.VertexData.Vertex.Vertex3D;
import com.avogine.render.util.Instanceable;

/**
 *
 */
public class ParticleMesh implements Instanceable {

	private final VertexArrayObject vao;
	private final int vertexCount;
	
	private final int maxInstances;
	private int currentInstances;

	/**
	 * @param positions 
	 * @param maxInstances 
	 */
	public ParticleMesh(float[] positions, int maxInstances) {
		long instanceBufferSize = 4L * Float.BYTES * maxInstances;
		
		try (Vertex3D positionsVertex = Vertex.wrap3D(positions, 0)) {
			vao = VertexArrayObject.gen(mesh -> mesh
					.vertex(positionsVertex)
					.vertex(instancePositionVertex -> instancePositionVertex
							.buffer(buffer -> buffer
									.data(instanceBufferSize)
									.glStream().draw())
							.vertexAttribArray(1, attrib -> attrib
									.pointer(_ -> {})
									.divisor(1)))
					.vertex(instanceColorVertex -> instanceColorVertex
							.buffer(buffer -> buffer
									.data(instanceBufferSize)
									.glStream().draw())
							.vertexAttribArray(2, attrib -> attrib
									.pointer(pointer -> pointer.unsignedByte().normalized())
									.divisor(1))));
		}
		vertexCount = positions.length / 3;
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
		vao.bindVBO(vboIndex, instanceBuffer -> instanceBuffer.bufferSubData(offset, data));
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
