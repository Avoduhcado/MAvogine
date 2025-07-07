package com.avogine.render.opengl.model.mesh.data;

import org.joml.primitives.AABBf;

import com.avogine.render.model.mesh.data.*;
import com.avogine.render.opengl.model.*;

/**
 * @param vertexBuffers Buffer data for drawing this mesh.
 * @param aabb An axis aligned bounding box fully containing this mesh's vertices.
 * @param materialIndex An index to a {@link Model}'s material list for which {@link Material} to use when drawing this mesh.
 * @param instancedBuffers Optional buffer data for drawing this mesh with instanced rendering.
 */
public record MeshData(VertexBuffers vertexBuffers, AABBf aabb, int materialIndex, InstancedBuffers instancedBuffers) implements VertexData {
	
	/**
	 * @param vertexBuffers
	 * @param aabb
	 * @param materialIndex
	 */
	public MeshData(VertexBuffers vertexBuffers, AABBf aabb, int materialIndex) {
		this(vertexBuffers, aabb, materialIndex, null);
	}

	/**
	 * @param vertexBuffers
	 * @param aabb
	 */
	public MeshData(VertexBuffers vertexBuffers, AABBf aabb) {
		this(vertexBuffers, aabb, 1);
	}
	
	@Override
	public int getVertexCount() {
		return vertexBuffers.indices().limit();
	}
	
	/**
	 * @return true if this {@link MeshData} contains populated {@link InstancedBuffers} indicating that it represents an instanced mesh.
	 */
	public boolean hasInstancedBuffers() {
		return instancedBuffers != null;
	}

}
