package com.avogine.game.camera;

import org.joml.*;

/**
 *
 */
public class Camera {
	
	private final Matrix4f view;

	private final Vector3f position;
	private final Vector3f forward;
	private final Vector3f up;
	
	private final Vector3f target;
	
	private final Vector3f direction;
	private final Vector3f velocity;
	
	private final Vector3f rotation;
	
	private float speed;
	private float angularSpeed;
	
	/**
	 * 
	 */
	public Camera() {
		view = new Matrix4f();
		
		position = new Vector3f();
		forward = new Vector3f();
		up = new Vector3f();
		
		target = new Vector3f();
		
		direction = new Vector3f();
		velocity = new Vector3f();
		
		rotation = new Vector3f();
	}
	
	/**
	 * Initialize values
	 */
	public void init() {
		position.set(0, 5, 15);
		forward.set(0, 0, -1);
		up.set(0, 1, 0);
		
		rotation.y = -90f;
				
		view.lookAt(position, position.add(forward, target), up);
		
		speed = 15f;
		angularSpeed = 5f;
	}

	/**
	 * Apply any pending direction and rotation changes and coalesce movement into the view matrix.
	 * @param interval the time passed between calls
	 */
	public void calculateView(float interval) {
		if (direction.length() != 0) {
			position.add(direction.normalize().mul(speed).mul(interval));
			direction.set(0);
		}
//		forward.rotateY((float) (Math.toRadians(angularSpeed) * rotation.y * interval));
//		rotation.set(0);
		
		view.identity().lookAt(position, position.add(forward, target), up);
	}
	
	/**
	 * @return the view
	 */
	public Matrix4f getView() {
		return view;
	}
	
	/**
	 * @return the position
	 */
	public Vector3f getPosition() {
		return position;
	}
	
	/**
	 * @return the front
	 */
	public Vector3f getForward() {
		return forward;
	}
	
	/**
	 * @return the up
	 */
	public Vector3f getUp() {
		return up;
	}
	
	/**
	 * @return the target
	 */
	public Vector3f getTarget() {
		return target;
	}
	
	/**
	 * @return the direction
	 */
	public Vector3f getDirection() {
		return direction;
	}
	
	/**
	 * @return the velocity
	 */
	public Vector3f getVelocity() {
		return velocity;
	}
	
	/**
	 * @return the rotation
	 */
	public Vector3f getRotation() {
		return rotation;
	}
	
	/**
	 * @return the speed
	 */
	public float getSpeed() {
		return speed;
	}
	
	/**
	 * @return the angularSpeed
	 */
	public float getAngularSpeed() {
		return angularSpeed;
	}
	
}
