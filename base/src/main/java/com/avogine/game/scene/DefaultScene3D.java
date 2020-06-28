package com.avogine.game.scene;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.avogine.entity.Entity;
import com.avogine.render.data.Mesh;

/**
 * A 3D {@link Scene} implementation that contains a map of all meshes and entities currently
 * existing in the game world. This implementation also provides a {@link Mesh} for a skybox as well.
 */
public class DefaultScene3D implements Scene {

	private List<Entity> entities = new ArrayList<>();
	private Map<Mesh, Set<Entity>> meshMap = new HashMap<>();
	
	private Mesh skybox;
	
	/**
	 * @return
	 */
	public List<Entity> getEntities() {
		return entities;
	}
	
	/**
	 * @param entity
	 */
	public void addEntity(Entity entity) {
		entities.add(entity);
		addToMeshMap(entity);
	}
	
	public void removeEntity(Entity entity) {
		entities.remove(entity);
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
	
	public Map<Mesh, Set<Entity>> getMeshMap() {
		return meshMap;
	}
	
	public Mesh getSkybox() {
		return skybox;
	}
	
	public void setSkybox(Mesh skybox) {
		this.skybox = skybox;
	}
	
	/**
	 * 
	 */
	@Override
	public void cleanup() {
		entities.forEach(Entity::cleanup);
		if (skybox != null) {
			skybox.cleanup();
		}
	}
	
}
