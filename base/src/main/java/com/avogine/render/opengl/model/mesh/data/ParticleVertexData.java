package com.avogine.render.opengl.model.mesh.data;

import java.nio.FloatBuffer;

import com.avogine.render.model.mesh.VertexArrayData;

/**
 * @param positions 
 * @param maxInstances 
 */
public record ParticleVertexData(FloatBuffer positions, int maxInstances) implements VertexArrayData {

	@Override
	public int getVertexCount() {
		return positions.limit() / 3;
	}

}
