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
	 * @param position
	 * @param orientation 
	 * @param scale
	 */
	public TransformComponent(Vector3f position, Quaternionf orientation, Vector3f scale) {
		this.position = position;
		this.orientation = orientation;
		this.scale = scale;
	}
	
	/**
	 * 
	 * @param position
	 * @param orientation 
	 */
	public TransformComponent(Vector3f position, Quaternionf orientation) {
		this(position, orientation, new Vector3f(1.0f));
	}
	
	/**
	 * 
	 * @param position
	 * @param scale
	 */
	public TransformComponent(Vector3f position, Vector3f scale) {
		this(position, new Quaternionf(), scale);
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
	 */
	public TransformComponent() {
		this(new Vector3f());
	}
	
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
	 * Set this transform's position to a copy of the input {@code position}.
	 * @param position The position to set this transform to.
	 */
	public void setPosition(Vector3f position) {
		this.position.set(position);
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
	 * Set this transform's orientation to a copy of the input {@code orientation}.
	 * @param orientation The orientation to set this transform to.
	 */
	public void setOrientation(Quaternionf orientation) {
		this.orientation.set(orientation);
	}
	
	/**
	 * @return the scale
	 */
	public Vector3f getScale() {
		return scale;
	}
	
	/**
	 * Set the scale to a uniform value across all 3 axes.
	 * @param scale a value to set the x, y, z values of this transform's scale to.
	 */
	public void setScale(float scale) {
		this.scale.set(scale);
	}
	
	/**
	 * @param x
	 * @param y
	 * @param z
	 */
	public void setScale(float x, float y, float z) {
		this.scale.set(x, y, z);
	}
	
	/**
	 * Set this transform's scale to a copy of the input {@code scale}.
	 * @param scale The scale to set this transform to.
	 */
	public void setScale(Vector3f scale) {
		this.scale.set(scale);
	}

}
