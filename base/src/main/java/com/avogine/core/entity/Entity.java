package com.avogine.core.entity;

import org.joml.Quaternionf;
import org.joml.Vector3f;

import com.avogine.core.render.Mesh;
import com.avogine.core.scene.SceneObject;
import com.avogine.core.system.Input;
import com.avogine.core.system.Window;
import com.avogine.core.system.listener.KeyboardListener;

public class Entity extends SceneObject implements Renderable {

	private Mesh[] meshes;
	private Mesh debugMesh;
	
	private Vector3f position;
	private Quaternionf orientation;
	private Vector3f rotation;
	private Vector3f scale;
	
	private Vector3f velocity;
	private float speed;
	
	private KeyboardListener keyboardListener;
	
	public Entity(Window theater, Mesh[] meshes, Vector3f position) {
		super(theater);
		this.meshes = meshes;
		setDebugMesh();
		setPosition(position);
		setOrientation(new Quaternionf());
		setRotation(new Vector3f());
		setScale(new Vector3f(1f));
		
		setVelocity(new Vector3f());
		this.speed = 15f;
	}
	
	@Override
	public boolean isInsideFrustum() {
		// TODO Auto-generated method stub
		return true;
	}
	
	public boolean isDynamic() {
		return velocity.length() != 0 || rotation.length() != 0;
	}
	
	/**
	 * TODO Put this in some sort of subclass/interface
	 */
	public void move(float interval) {
		if (velocity.length() > 0) {
			velocity.normalize().mul(speed).mul(interval);
			position.add(velocity);
			velocity.set(0);
		}
		
		if (rotation.length() > 0) {
			rotation.normalize().mul(90f).mul(interval);
			orientation.rotateXYZ((float) Math.toRadians(rotation.x), (float) Math.toRadians(rotation.y), (float) Math.toRadians(rotation.z));
			rotation.set(0);
		}
	}
	
	@Override
	public Mesh[] getMeshes() {
		return meshes;
	}
	
	public Mesh getDebugMesh() {
		return debugMesh;
	}
	
	protected void setDebugMesh() {
		// XXX Singular mesh bounding box
		//Mesh m = meshes[0];
		//debugMesh = ParShapesLoader.loadSphere(m.getBoundingRadius());
	}
	
	@Override
	public void cleanup() {
		for (Mesh mesh : getMeshes()) {
			mesh.cleanup();
		}
		if (debugMesh != null) {
			debugMesh.cleanup();
		}
	}
	
	public Vector3f getPosition() {
		return position;
	}

	public void setPosition(Vector3f position) {
		this.position = position;
	}

	public Quaternionf getOrientation() {
		return orientation;
	}
	
	protected void setOrientation(Quaternionf orientation) {
		this.orientation = orientation;
	}
	
	public Vector3f getRotation() {
		return rotation;
	}

	public void setRotation(Vector3f rotation) {
		this.rotation = rotation;
	}
	
	public void setRotation(float x, float y, float z) {
		rotation.set(x, y, z);
	}
	
	public Vector3f getScale() {
		return scale;
	}

	public void setScale(Vector3f scale) {
		this.scale = scale;
	}

	public Vector3f getVelocity() {
		return velocity;
	}

	public void setVelocity(Vector3f velocity) {
		this.velocity = velocity;
	}
	
	public KeyboardListener getKeyboardListener() {
		return keyboardListener;
	}
	
	public void setKeyboardListener(KeyboardListener keyboardListener) {
		if (keyboardListener == null) {
			Input.removeListener(container.getId(), getKeyboardListener());
		}
		Input.add(container.getId(), keyboardListener);
		this.keyboardListener = keyboardListener;
	}
	
}
