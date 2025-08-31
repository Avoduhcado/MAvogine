package com.avogine.render.model.mesh.data;

import org.joml.primitives.AABBf;

/**
 * @param vertexBuffers All of this mesh's vertex data stored in individual buffers per attribute.
 * @param aabb An axis aligned bounding box fully containing this mesh's vertices.
 * @param materialIndex An index to the material this mesh uses from a model/scene material list. Order should be preserved when loading the associated material list.
 * @param instancedBuffers Optional per instance data stored in individual buffers per instanced attribute.
 * @param maxInstances Optional maximum number of instances to draw during instanced rendering.
 */
public record MeshData(VertexBuffers vertexBuffers, AABBf aabb, int materialIndex, InstancedBuffers instancedBuffers, int maxInstances) {
	
	/**
	 * @param vertexBuffers
	 * @param aabb
	 * @param materialIndex
	 */
	public MeshData(VertexBuffers vertexBuffers, AABBf aabb, int materialIndex) {
		this(vertexBuffers, aabb, materialIndex, null, 1);
	}
	
	/**
	 * @param vertexBuffers
	 * @param aabb
	 */
	public MeshData(VertexBuffers vertexBuffers, AABBf aabb) {
		this(vertexBuffers, aabb, 1);
	}
}
