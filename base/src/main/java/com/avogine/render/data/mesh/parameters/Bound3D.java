package com.avogine.render.data.mesh.parameters;

import org.joml.Vector3f;

/**
 *
 */
public interface Bound3D {
	/**
	 * @return the maximum 3D position of the axis-aligned bounding box that contains this object.
	 */
	public Vector3f getAabbMax();

	/**
	 * @return the minimum 3D position of the axis-aligned bounding box that contains this object.
	 */
	public Vector3f getAabbMin();
}
