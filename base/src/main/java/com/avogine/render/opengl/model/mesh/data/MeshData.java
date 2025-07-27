package com.avogine.render.opengl.model.mesh.data;

import org.joml.primitives.AABBf;

import com.avogine.render.model.mesh.data.VertexBuffers;
import com.avogine.render.opengl.model.*;

/**
 * @param vertexBuffers Buffer data for drawing this mesh.
 * @param aabb An axis aligned bounding box fully containing this mesh's vertices.
 * @param materialIndex An index to a {@link Model}'s material list for which {@link Material} to use when drawing this mesh.
 */
public record MeshData(VertexBuffers vertexBuffers, AABBf aabb, int materialIndex) {
	
	/**
	 * @param vertexBuffers
	 * @param aabb
	 */
	public MeshData(VertexBuffers vertexBuffers, AABBf aabb) {
		this(vertexBuffers, aabb, 1);
	}
	
	/**
	 * @return
	 */
	public int getVertexCount() {
		return vertexBuffers.indices().limit();
	}
	
}
