package com.avogine.core.scene;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.avogine.core.entity.Renderable;
import com.avogine.render.data.Mesh;

public class Scene<T extends Renderable> {

	private List<T> entities = new ArrayList<>();
	private Map<Mesh, Set<T>> meshMap = new HashMap<>();
	
	private Mesh skybox;
	
	public List<T> getEntities() {
		return entities;
	}
	
	public void addEntity(T entity) {
		entities.add(entity);
		addToMeshMap(entity);
	}
	
	public void removeEntity(T entity) {
		entities.remove(entity);
		removeFromMeshMap(entity);
	}
	
	private void addToMeshMap(T entity) {
		for (Mesh mesh : entity.getMeshes()) {
			meshMap.computeIfAbsent(mesh, v -> new HashSet<>()).add(entity);
		}
	}
	
	private void removeFromMeshMap(T entity) {
		for (Mesh mesh : entity.getMeshes()) {
			meshMap.get(mesh).remove(entity);
			if (meshMap.get(mesh).isEmpty()) {
				meshMap.remove(mesh);
			}
		}
	}
	
	public Map<Mesh, Set<T>> getMeshMap() {
		return meshMap;
	}
	
	public Mesh getSkybox() {
		return skybox;
	}
	
	public void setSkybox(Mesh skybox) {
		this.skybox = skybox;
	}
	
	public void cleanup() {
		entities.forEach(T::cleanup);
		if (skybox != null) {
			skybox.cleanup();
		}
	}
	
}
