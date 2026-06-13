package com.avogine.render.opengl.particle;

import static org.lwjgl.opengl.GL11C.GL_TRIANGLE_FAN;
import static org.lwjgl.opengl.GL31C.glDrawArraysInstanced;

import java.nio.Buffer;

import com.avogine.render.model.mesh.*;
import com.avogine.render.opengl.VertexArrayObject;
import com.avogine.render.opengl.model.mesh.data.Vertex;
import com.avogine.render.opengl.model.mesh.data.Vertex.Vertex3D;

/**
 *
 */
public class ParticleMesh implements Renderable, Instanceable {

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
					.vertex(iPositions -> iPositions.data(instanceBufferSize).glStream().draw(),
							vertex -> vertex.array(1, attrib -> attrib
									.pointer4f()
									.divisor(1)))
					.vertex(iColors -> iColors.data(instanceBufferSize).glStream().draw(),
							vertex -> vertex.array(2, attrib -> attrib
									.pointer(p -> p.ub().normalized())
									.divisor(1))));
		}
		vertexCount = positions.length / 3;
		this.maxInstances = maxInstances;
	}
	
	@Override
	public void cleanup() {
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
