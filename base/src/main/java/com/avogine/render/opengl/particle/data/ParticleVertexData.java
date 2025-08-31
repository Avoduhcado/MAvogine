package com.avogine.render.opengl.particle.data;

import java.nio.FloatBuffer;

import com.avogine.render.model.mesh.data.VertexData;

/**
 * @param positions 
 * @param maxInstances 
 */
public record ParticleVertexData(FloatBuffer positions, int maxInstances) implements VertexData {

	@Override
	public int getVertexCount() {
		return positions.limit() / 3;
	}

}
