package com.avogine.game.scene;

import java.util.*;

import org.joml.*;

import com.avogine.entity.*;
import com.avogine.game.camera.*;
import com.avogine.io.*;
import com.avogine.render.data.*;

/**
 * A 3D {@link Scene} implementation that contains a map of all meshes and entities currently
 * existing in the game world. This implementation also provides a {@link Mesh} for a skybox as well.
 */
public class DefaultScene3D extends Scene {

	private Map<Mesh, Set<Entity>> meshMap = new HashMap<>();
	
	private Mesh skybox;

	/**
	 * 
	 */
	public DefaultScene3D() {
		super(new Matrix4f(), new Camera());
	}
	
	@Override
	public void init(Window window) {
		// TODO Auto-generated method stub
		
	}
	
	/**
	 * @param entity
	 */
	public void addEntity(Entity entity) {
		addToMeshMap(entity);
	}
	
	/**
	 * @param entity
	 */
	public void removeEntity(Entity entity) {
		removeFromMeshMap(entity);
	}
	
	private void addToMeshMap(Entity entity) {
		for (Mesh mesh : entity.getMeshes()) {
			meshMap.computeIfAbsent(mesh, v -> new HashSet<>()).add(entity);
		}
	}
	
	private void removeFromMeshMap(Entity entity) {
		for (Mesh mesh : entity.getMeshes()) {
			meshMap.get(mesh).remove(entity);
			if (meshMap.get(mesh).isEmpty()) {
				meshMap.remove(mesh);
			}
		}
	}
	
	/**
	 * @return
	 */
	public Map<Mesh, Set<Entity>> getMeshMap() {
		return meshMap;
	}
	
	/**
	 * @return
	 */
	public Mesh getSkybox() {
		return skybox;
	}
	
	/**
	 * @param skybox
	 */
	public void setSkybox(Mesh skybox) {
		this.skybox = skybox;
	}
	
	/**
	 * 
	 */
	@Override
	public void cleanup() {
		meshMap.keySet().forEach(Mesh::cleanup);
		if (skybox != null) {
			skybox.cleanup();
		}
	}

	@Override
	public void onUpdate(float delta) {
		// TODO Auto-generated method stub
		
	}

}
