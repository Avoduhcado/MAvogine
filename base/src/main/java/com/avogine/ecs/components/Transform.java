package com.avogine.ecs.components;

import org.joml.Vector3f;

import com.avogine.ecs.EntityComponent;

/**
 *
 */
public class Transform extends EntityComponent {

	private final Vector3f position;
	
	/**
	 * 
	 */
	public Transform() {
		position = new Vector3f();
	}
	
	/**
	 * 
	 * @param position
	 */
	public Transform(Vector3f position) {
		this.position = position;
	}
	
	/**
	 * @return the position
	 */
	public Vector3f getPosition() {
		return position;
	}

}
