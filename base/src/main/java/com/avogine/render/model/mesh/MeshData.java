package com.avogine.render.model.mesh;

import org.joml.primitives.AABBf;

import com.avogine.render.model.mesh.data.VertexBuffers;

/**
 * Wrapper class for holding {@link VertexBuffers} and an {@link AABBf} describing a render-able mesh.
 */
public sealed class MeshData permits InstancedMeshData, Particle2DMeshData {

	private VertexBuffers vertexBuffers;
	private AABBf aabb;
	
	/**
	 * @param vertexBuffers
	 * @param aabb
	 */
	public MeshData(VertexBuffers vertexBuffers, AABBf aabb) {
		this.vertexBuffers = vertexBuffers;
		this.aabb = aabb;
	}
	
	/**
	 * @return the vertexBuffers
	 */
	public VertexBuffers getVertexBuffers() {
		return vertexBuffers;
	}
	
	/**
	 * @return the aabb
	 */
	public AABBf getAabb() {
		return aabb;
	}
	
	/**
	 * @return the number of vertices that make up the mesh.
	 */
	public int getVertexCount() {
		return vertexBuffers.indices().limit();
	}
	
}
