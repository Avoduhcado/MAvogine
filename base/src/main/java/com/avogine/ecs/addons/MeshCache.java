package com.avogine.ecs.addons;

import java.util.*;

import com.avogine.ecs.*;
import com.avogine.render.data.*;

/**
 *
 */
public class MeshCache implements EntitySystemAddon {

	private final List<Mesh> cache;
	
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
	public int addMesh(Mesh mesh) {
		cache.add(mesh);
		return cache.size() - 1;
	}
	
	/**
	 * @return the cache
	 */
	public List<Mesh> getCache() {
		return cache;
	}
	
}
