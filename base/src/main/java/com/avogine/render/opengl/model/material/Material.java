package com.avogine.render.opengl.model.material;

import java.util.*;
import java.util.stream.Stream;

import com.avogine.render.model.mesh.Boundable;
import com.avogine.render.opengl.model.mesh.*;

/**
 *
 */
public abstract sealed class Material permits BPMaterial, PBRMaterial, CustomMaterial {
	
	private final List<StaticMesh> staticMeshes;
	private final List<AnimatedMesh> animatedMeshes;
	private final List<InstancedMesh> instancedMeshes;
	
	protected Material() {
		staticMeshes = new ArrayList<>();
		animatedMeshes = new ArrayList<>();
		instancedMeshes = new ArrayList<>();
	}
	
	protected Material(List<Mesh> meshes) {
		this();
		meshes.forEach(this::addMesh);
	}
	
	/**
	 * Free all {@link Mesh} data.
	 */
	public void cleanup() {
		staticMeshes.forEach(Mesh::cleanup);
		animatedMeshes.forEach(Mesh::cleanup);
		instancedMeshes.forEach(Mesh::cleanup);
	}
	
	/**
	 * Retrieve a stream of all {@link Mesh Meshes} contained in this material.
	 * </br>
	 * This method exists for convenience, the typed retrieval methods, i.e. {@link Material#getStaticMeshes()}, {@link Material#getAnimatedMeshes()}...etc.
	 * should be preferred for actual rendering operations.
	 * @return a stream of all meshes this material applies to.
	 */
	public Stream<Mesh> getAllMeshes() {
		return Stream.concat(staticMeshes.stream(), Stream.concat(animatedMeshes.stream(), instancedMeshes.stream()));
	}
	
	/**
	 * @return a list of all {@link StaticMesh StaticMeshes} this material applies to.
	 */
	public List<StaticMesh> getStaticMeshes() {
		return staticMeshes;
	}
	
	/**
	 * @return a list of all {@link AnimatedMesh AnimatedMeshes} this material applies to.
	 */
	public List<AnimatedMesh> getAnimatedMeshes() {
		return animatedMeshes;
	}
	
	/**
	 * @return a list of all {@link InstancedMesh InstancedMeshes} this material applies to.
	 */
	public List<InstancedMesh> getInstancedMeshes() {
		return instancedMeshes;
	}
	
	/**
	 * @param mesh
	 */
	public void addMesh(Mesh mesh) {
		switch (mesh) {
			case StaticMesh s -> staticMeshes.add(s);
			case AnimatedMesh a -> animatedMeshes.add(a);
			case InstancedMesh i -> instancedMeshes.add(i);
		}
	}
	
	/**
	 * @return a stream of all {@link Mesh Meshes} that also implement {@link Boundable}.
	 */
	public Stream<Boundable> getAllBoundables() {
		return getAllMeshes().filter(Boundable.class::isInstance).map(Boundable.class::cast);
	}
	
}
