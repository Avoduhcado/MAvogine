package com.avogine.render.opengl.model.mesh.data;

import java.nio.FloatBuffer;

/**
 * @param positions 
 * @param maxInstances 
 */
public record ParticleMeshData(FloatBuffer positions, int maxInstances) {

}
