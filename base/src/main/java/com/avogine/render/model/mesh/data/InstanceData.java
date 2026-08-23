package com.avogine.render.model.mesh.data;

import java.nio.FloatBuffer;

/**
 *
 * @param instanceMatrices
 * @param instanceCount
 */
public record InstanceData(FloatBuffer instanceMatrices, int instanceCount) {
	
}
