package com.avogine.ecs.components;

import org.joml.*;

import com.avogine.ecs.*;

/**
 *
 */
public class TransformComponent extends EntityComponent {

	private final Vector3f position;
	private final Quaternionf orientation;
	private final Vector3f scale;
	
	/**
	 * 
	 */
	public TransformComponent() {
		this(new Vector3f());
	}
	
	/**
	 * 
	 * @param position
	 */
	public TransformComponent(Vector3f position) {
		this(position, new Vector3f(1.0f));
	}
	
	/**
	 * 
	 * @param position
	 * @param scale
	 */
	public TransformComponent(Vector3f position, Vector3f scale) {
		this.position = position;
		orientation = new Quaternionf();
		this.scale = scale;
	}
	
	// TODO More constructors
	
	/**
	 * @return the position
	 */
	public Vector3f getPosition() {
		return position;
	}

	/**
	 * @param x
	 * @param y
	 * @param z
	 */
	public void setPosition(float x, float y, float z) {
		this.position.set(x, y, z);
	}

	/**
	 * @return the orientation
	 */
	public Quaternionf getOrientation() {
		return orientation;
	}
	
	/**
	 * 
	 * @param x
	 * @param y
	 * @param z
	 * @param w
	 */
	public void setOrientation(float x, float y, float z, float w) {
		this.orientation.set(x, y, z, w);
	}
	
	/**
	 * @return the scale
	 */
	public Vector3f getScale() {
		return scale;
	}
	
	/**
	 * 
	 * @param scale
	 */
	public void setScale(float scale) {
		this.scale.set(scale);
	}

}
