package com.avogine.render.model.mesh;

import org.joml.primitives.AABBf;

/**
 *
 */
public interface Boundable {
	
	/**
	 * @return The axis aligned bounding box fully containing the 3D instance.
	 */
	public AABBf getAABB();
	
}
