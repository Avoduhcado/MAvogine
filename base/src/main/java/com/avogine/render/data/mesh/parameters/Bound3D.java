package com.avogine.render.data.mesh.parameters;

import org.joml.primitives.AABBf;

/**
 *
 */
public interface Bound3D {
	
	/**
	 * @return The axis aligned bounding box fully containing a mesh's vertices.
	 */
	public AABBf getAABB();
	
}
