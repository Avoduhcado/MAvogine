package com.avogine.render.data;

import org.joml.primitives.AABBf;

/**
 *
 */
public final class InstancedMeshData extends MeshData {

	private InstancedBuffers instancedBuffers;
	
	/**
	 * @param vertexBuffers
	 * @param aabb
	 * @param instancedBuffers 
	 */
	public InstancedMeshData(VertexBuffers vertexBuffers, AABBf aabb, InstancedBuffers instancedBuffers) {
		super(vertexBuffers, aabb);
		this.instancedBuffers = instancedBuffers;
	}
	
	/**
	 * @return the instancedBuffers
	 */
	public InstancedBuffers getInstancedBuffers() {
		return instancedBuffers;
	}
	
}