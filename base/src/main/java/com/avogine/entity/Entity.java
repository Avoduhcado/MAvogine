/**
 * 
 */
package com.avogine.entity;

import org.joml.Vector3f;

import com.avogine.experimental.annotation.InDev;
import com.avogine.render.data.Mesh;

/**
 *
 */
@InDev
public class Entity {
	
	protected Vector3f position;
	
	protected Mesh[] meshes;
	
	/**
	 * @param position
	 * @param meshes
	 */
	public Entity(Vector3f position, Mesh[] meshes) {
		this.setPosition(position);
		this.setMeshes(meshes);
	}
	
	public Vector3f getPosition() {
		return position;
	}

	public void setPosition(Vector3f position) {
		this.position = position;
	}

	public Mesh[] getMeshes() {
		return meshes;
	}

	public void setMeshes(Mesh[] meshes) {
		this.meshes = meshes;
	}

}
