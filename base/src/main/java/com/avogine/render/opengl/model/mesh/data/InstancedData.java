package com.avogine.render.opengl.model.mesh.data;

import com.avogine.render.model.mesh.data.InstancedBuffers;

/**
 * @param instancedBuffers 
 * @param maxInstances 
 */
public record InstancedData(InstancedBuffers instancedBuffers, int maxInstances) {
	
}
