package com.avogine.ecs.components;

import org.joml.Quaternionf;
import org.joml.Vector3f;

import com.avogine.ecs.EntityComponent;

/**
 *
 */
public class Transform extends EntityComponent {

	private final Vector3f position;
	private final Quaternionf orientation;
	private final Vector3f scale;
	
	/**
	 * 
	 */
	public Transform() {
		position = new Vector3f();
		orientation = new Quaternionf();
		scale = new Vector3f();
	}
	
	/**
	 * 
	 * @param position
	 */
	public Transform(Vector3f position) {
		this.position = position;
		orientation = new Quaternionf();
		scale = new Vector3f();
	}
	
	// TODO More constructors
	
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
