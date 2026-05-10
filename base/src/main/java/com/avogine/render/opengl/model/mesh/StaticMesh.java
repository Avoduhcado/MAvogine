package com.avogine.render.opengl.model.mesh;

import org.joml.primitives.AABBf;

import com.avogine.render.model.mesh.Boundable;
import com.avogine.render.model.mesh.data.MeshData;

/**
 *
 */
public final class StaticMesh extends Mesh implements Boundable {
	private final AABBf aabb;
	
	/**
	 * @param meshData
	 */
	public StaticMesh(MeshData meshData) {
		super(meshData);
		aabb = meshData.aabb();
	}
	
	@Override
	public AABBf getAABB() {
		return aabb;
	}
}
