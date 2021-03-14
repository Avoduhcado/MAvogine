package com.avogine.entity;

import org.joml.Quaternionf;
import org.joml.Vector3f;

/**
 *
 */
public class Transform {

	private final Vector3f position;
	private final Quaternionf orientation;
	private final Vector3f scale;
	
	/**
	 * @param position 
	 * @param orientation 
	 * @param scale 
	 */
	public Transform(Vector3f position, Quaternionf orientation, Vector3f scale) {
		this.position = position;
		this.orientation = orientation;
		this.scale = scale;
	}
	
	/**
	 * Create a new {@link Transform} with default position and rotation values of 0, and a scale of 1.
	 */
	public Transform() {
		this(new Vector3f(), new Quaternionf(), new Vector3f(1));
	}
	
	/**
	 * @return the position
	 */
	public Vector3f getPosition() {
		return position;
	}
	
	/**
	 * @return the orientation
	 */
	public Quaternionf getOrientation() {
		return orientation;
	}

	/**
	 * @return the scale
	 */
	public Vector3f getScale() {
		return scale;
	}

}
