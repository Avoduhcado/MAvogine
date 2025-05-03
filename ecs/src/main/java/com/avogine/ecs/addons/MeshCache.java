package com.avogine.ecs.addons;

import java.util.*;

import com.avogine.ecs.EntitySystemAddon;
import com.avogine.render.data.mesh.StaticMesh;

/**
 *
 */
public class MeshCache implements EntitySystemAddon {

	private final List<StaticMesh> cache;
	
	/**
	 * 
	 */
	public MeshCache() {
		cache = new ArrayList<>();
	}
	
	/**
	 * @param mesh
	 * @return the index of the newly added mesh.
	 */
	public int addMesh(StaticMesh mesh) {
		cache.add(mesh);
		return cache.size() - 1;
	}
	
	/**
	 * @return the cache
	 */
	public List<StaticMesh> getCache() {
		return cache;
	}
	
}
