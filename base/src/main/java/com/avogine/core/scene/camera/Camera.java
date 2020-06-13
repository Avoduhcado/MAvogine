package com.avogine.core.scene.camera;

import org.joml.Matrix4f;
import org.joml.Vector3f;

import com.avogine.core.scene.SceneObject;
import com.avogine.game.Window;

public class Camera extends SceneObject {

	protected Vector3f position;
	protected Vector3f velocity;
	protected float speed;
	
	protected Vector3f forward = new Vector3f(0, 0, -1);
	protected Vector3f up = new Vector3f(0, 1, 0);
	protected Vector3f right = new Vector3f(1, 0, 0);
	
	protected float yaw;
	protected float pitch;
	protected float lastX;
	protected float lastY;
	
	protected float fov;
	protected float offset;
	
	protected final Matrix4f view;
	
	public Camera(Window window, Vector3f position, float speed, float yaw, float fov) {
		super(window);
		this.position = position;
		this.velocity = new Vector3f();
		this.speed = speed;
		this.yaw = yaw;
		this.fov = fov;
		this.offset = 25;
		view = new Matrix4f();
	}
	
	public Camera(Window window, Vector3f position) {
		this(window, position, 10f, -90f, 45f);
	}
	
	public Camera(Window window) {
		this(window, new Vector3f());
	}
	
	public void update(float interval) {
		velocity.mul(speed);
		position.add(velocity.mul(interval));
		velocity.set(0);
	}
	
	public Matrix4f getView() {
		return view;
	}
	
	public Vector3f getPosition() {
		return position;
	}
	
	public void setPosition(Vector3f position) {
		this.position = position;
	}
	
	public Vector3f getVelocity() {
		return velocity;
	}
	
	public void setVelocity(Vector3f velocity) {
		this.velocity = velocity;
	}
	
	public float getSpeed() {
		return speed;
	}
	
	public void setSpeed(float speed) {
		this.speed = speed;
	}
	
	public Vector3f getForward() {
		return forward;
	}
	
	public void setForward(Vector3f forward) {
		this.forward = forward;
	}
	
	public Vector3f getUp() {
		return up;
	}
	
	public void setUp(Vector3f up) {
		this.up = up;
	}
	
	public Vector3f getRight() {
		return right;
	}
	
	public void setRight(Vector3f right) {
		this.right = right;
	}
	
	public float getYaw() {
		return yaw;
	}
	
	public void setYaw(float yaw) {
		this.yaw = yaw;
	}
	
	public float getPitch() {
		return pitch;
	}
	
	public void setPitch(float pitch) {
		this.pitch = pitch;
	}
	
	public float getLastX() {
		return lastX;
	}
	
	public void setLastX(float lastX) {
		this.lastX = lastX;
	}
	
	public float getLastY() {
		return lastY;
	}
	
	public void setLastY(float lastY) {
		this.lastY = lastY;
	}
	
	public float getFov() {
		return fov;
	}
	
	public void setFov(float fov) {
		this.fov = fov;
	}
	
	/**
	 * @return the offset
	 */
	public float getOffset() {
		return offset;
	}
	
	/**
	 * @param offset the offset to set
	 */
	public void setOffset(float offset) {
		this.offset = offset;
	}
	
}
