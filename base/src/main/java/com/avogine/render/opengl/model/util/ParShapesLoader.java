package com.avogine.render.opengl.model.util;

import static org.lwjgl.system.MemoryUtil.*;
import static org.lwjgl.util.par.ParShapes.*;

import java.nio.FloatBuffer;
import java.util.function.Function;

import org.joml.primitives.AABBf;
import org.lwjgl.system.*;
import org.lwjgl.util.par.ParShapesMesh;

import com.avogine.render.model.mesh.data.*;
import com.avogine.render.model.util.*;
import com.avogine.render.opengl.model.mesh.*;
import com.avogine.render.opengl.model.mesh.data.*;

/**
 * Utility class for immediate {@link MaterialMesh} creation via {@link ParShapesBuilder}.
 *
 */
public class ParShapesLoader {
	
	private static final ParShapesBuilder builder = new ParShapesBuilder();

	/**
	 * 
	 */
	public static final BuildFunction<Mesh> STATIC_MESH_BUILDER = parMesh -> {
		int vertexCount = parMesh.npoints();
		int vert3D = vertexCount * 3;
		int vert2D = vertexCount * 2;

		try (MemoryStack stack = MemoryStack.stackPush()) { 
			var vertexBuffers = new VertexBuffers(memAllocFloat(vert3D).put(parMesh.points(vert3D)).flip(),
					(!parMesh.isNull(ParShapesMesh.NORMALS) ? memAllocFloat(vert3D).put(parMesh.normals(vert3D)).flip() : memCallocFloat(vert3D)),
					memCallocFloat(vert3D),
					memCallocFloat(vert3D),
					(!parMesh.isNull(ParShapesMesh.TCOORDS) ? memAllocFloat(vert2D).put(parMesh.tcoords(vert2D)).flip() : memCallocFloat(vert2D)),
					memAllocInt(parMesh.ntriangles() * 3).put(parMesh.triangles(parMesh.ntriangles() * 3)).flip());
			FloatBuffer aabb = stack.mallocFloat(6);
			par_shapes_compute_aabb(parMesh, aabb);

			return new Mesh(new MeshData(vertexBuffers, new AABBf(aabb.get(), aabb.get(), aabb.get(), aabb.get(), aabb.get(), aabb.get())));
		} finally {
			par_shapes_free_mesh(parMesh);
		}
	};
	
	private ParShapesLoader() {}

	/**
	 * Generate a new cube {@link Mesh}.
	 * <p>
	 * This will center the cube on [0, 0, 0].
	 * @param scale size of the cube.
	 * @return a cube {@code Mesh}
	 */
	public static Mesh loadCube(float scale) {
		return builder
				.createCube()
				.scale(scale, scale, scale)
				.translate(-scale / 2, -scale / 2, -scale / 2)
				.build(STATIC_MESH_BUILDER);
	}
	
	/**
	 * Generate a plane {@link Mesh}.
	 * @param scale size of the plane.
	 * @return a plane {@code Mesh}.
	 */
	public static Mesh loadPlane(float scale) {
		return builder.createPlane(100, 100)
				.scale(scale, 1, scale)
				.translate(-scale / 2, 0, -scale / 2)
				.build(STATIC_MESH_BUILDER);
	}
	
	/**
	 * Generate a sphere {@link Mesh}.
	 * @param radius the radius of the sphere.
	 * @return a sphere {@link Mesh}.
	 */
	public static Mesh loadSphere(float radius) {
		float diameter = radius * 2;
		return builder.createSphere(16, 16)
				.scale(diameter, diameter, diameter)
				.translate(-radius, -radius, -radius)
				.build(STATIC_MESH_BUILDER);
	}
	
	/**
	 * Generate an L-System {@link Mesh}.
	 * @param program instructions defining the L-System to create.
	 * @return an L-System {@link Mesh}.
	 */
	public static Mesh loadLSystem(String program) {
		return builder.createLSystem(program, 5, 60)
				.build(STATIC_MESH_BUILDER);
	}
	
	/**
	 * Generate a new custom {@link Mesh}.
	 * @param builder the {@link ParShapesBuilder} defining the mesh to create with all transformations.
	 * @return a custom {@link Mesh}.
	 */
	public static Mesh loadBuilder(ParShapesBuilder builder) {
		return builder.build(STATIC_MESH_BUILDER);
	}
	
	/**
	 * Generate a new custom {@link Mesh}.
	 * @param buildFunction a Function to construct a mesh from.
	 * @return a custom {@link Mesh}.
	 */
	public static Mesh loadFromBuilder(Function<ParShapesBuilder, Mesh> buildFunction) {
		return buildFunction.apply(builder);
	}
	
	/**
	 * Generate a new {@link InstancedMesh}.
	 * @param builder the {@link ParShapesBuilder} defining the mesh to create with all transformations.
	 * @param instanceCount the total number of instances to allocate.
	 * @return a new {@code InstancedMesh}
	 */
	public static InstancedMesh loadInstancedBuilder(ParShapesBuilder builder, int instanceCount) {
		return builder.build(parMesh -> {
			int vertexCount = parMesh.npoints();
			int vert3D = vertexCount * 3;
			int vert2D = vertexCount * 2;

			try (MemoryStack stack = MemoryStack.stackPush()) {
				var vertexBuffers = new VertexBuffers(memAllocFloat(vert3D).put(parMesh.points(vert3D)).flip(),
						(!parMesh.isNull(ParShapesMesh.NORMALS) ? memAllocFloat(vert3D).put(parMesh.normals(vert3D)).flip() : memCallocFloat(vert3D)),
						memCallocFloat(vert3D),
						memCallocFloat(vert3D),
						(!parMesh.isNull(ParShapesMesh.TCOORDS) ? memAllocFloat(vert2D).put(parMesh.tcoords(vert2D)).flip() : memCallocFloat(vert2D)),
						memAllocInt(parMesh.ntriangles() * 3).put(parMesh.triangles(parMesh.ntriangles() * 3)).flip());
				var instancedBuffers = new InstancedBuffers(MemoryUtil.memAllocFloat(instanceCount * 16), MemoryUtil.memAllocFloat(instanceCount * 16));
				FloatBuffer aabb = stack.mallocFloat(6);
				par_shapes_compute_aabb(parMesh, aabb);

				return new InstancedMesh(new InstancedMeshData(new MeshData(vertexBuffers, new AABBf(aabb.get(), aabb.get(), aabb.get(), aabb.get(), aabb.get(), aabb.get()), 1), instancedBuffers, instanceCount));
			} finally {
				par_shapes_free_mesh(parMesh);
			}
		});
	}
	
}
