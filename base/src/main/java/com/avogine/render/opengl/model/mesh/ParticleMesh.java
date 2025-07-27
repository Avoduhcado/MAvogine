package com.avogine.render.opengl.model.mesh;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.GL_STREAM_DRAW;

import java.nio.*;

import org.lwjgl.opengl.GL31;
import org.lwjgl.system.MemoryUtil;

import com.avogine.render.model.mesh.Instanceable;
import com.avogine.render.opengl.*;
import com.avogine.render.opengl.VertexAttrib.Pointer;
import com.avogine.render.opengl.model.mesh.data.ParticleMeshData;

/**
 *
 */
public class ParticleMesh implements Renderable, Instanceable {
	
	private int instanceLimit;
	private int currentInstances;
	
	private int vertexCount;
	private VAO vao;
	
	/**
	 * @param meshData 
	 */
	public ParticleMesh(ParticleMeshData meshData) {
		instanceLimit = meshData.maxInstances();
		vertexCount = meshData.positions().limit() / 3;
		try {
			vao = VAO.gen(
					() -> {
						var vbo = VBO.gen().bind().bufferData(meshData.positions());
						VertexAttrib.array(0).pointer(Pointer.tightlyPackedUnnormalizedFloat(3)).divisor(0).enable();
						return vbo;
					},
					() -> {
						var vbo = VBO.gen().bind().bufferData(4L * Float.BYTES * meshData.maxInstances(), GL_STREAM_DRAW);
						VertexAttrib.array(1).pointer(Pointer.tightlyPackedUnnormalizedFloat(4)).divisor(1).enable();
						return vbo;
					},
					() -> {
						var vbo = VBO.gen().bind().bufferData(4L * Byte.BYTES * meshData.maxInstances(), GL_STREAM_DRAW);
						VertexAttrib.array(2).pointer(new Pointer(4, GL_UNSIGNED_BYTE, true, 0, 0)).divisor(1).enable();
						return vbo;
					});
		} finally {
			VAO.unbind();
			MemoryUtil.memFree(meshData.positions());
		}
	}

	@Override
	public void cleanup() {
		vao.cleanup();
	}

	@Override
	public void bind() {
		vao.bind();
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
		vao.vertexBuffers().get(vboIndex).bind().bufferSubData(buffer);
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

	@Override
	public int getVertexCount() {
		return vertexCount;
	}

	@Override
	public VAO getVAO() {
		return vao;
	}
}
