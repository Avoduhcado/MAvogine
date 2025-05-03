package com.avogine.render.data;

import java.nio.*;

import com.avogine.render.data.mesh.Mesh;
import com.avogine.render.data.mesh.parameters.Instanceable;
import com.avogine.render.data.vertices.array.ParticleInstanceVertexArray;

/**
 *
 */
public class ParticleInstanceMesh extends Mesh<ParticleInstanceVertexArray> implements Instanceable {
	
	private int maxInstances;
	
	/**
	 * @param vertexData 
	 */
	public ParticleInstanceMesh(ParticleInstanceVertexArray vertexData) {
		super(vertexData.vertexCount(), vertexData);
		this.maxInstances = vertexData.maxInstances();
	}
	
	@Override
	public void draw() {
		// Not implemented
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
		vao.vertexBufferObjects().get(vboIndex).bind().bufferSubData(buffer);
	}

	@Override
	public int getMaxInstances() {
		return maxInstances;
	}
}
