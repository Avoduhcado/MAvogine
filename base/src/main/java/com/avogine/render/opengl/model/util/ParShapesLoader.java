package com.avogine.render.opengl.model.util;

import static org.lwjgl.util.par.ParShapes.*;

import java.nio.FloatBuffer;
import java.util.function.Function;

import org.joml.primitives.AABBf;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.util.par.ParShapesMesh;

import com.avogine.render.opengl.model.mesh.*;
import com.avogine.render.util.*;

/**
 * Utility class for immediate {@link Mesh} creation via {@link ParShapesBuilder}.
 */
public class ParShapesLoader {
	
	private static final ParShapesBuilder builder = new ParShapesBuilder();

	/**
	 * 
	 */
	public static final BuildFunction<StaticMesh> STATIC_MESH_BUILDER = parMesh -> {
		int vertexCount = parMesh.npoints();

		float[] positions = new float[vertexCount * 3];
		parMesh.points(positions.length).get(positions);
		float[] normals = new float[vertexCount * 3];
		if (!parMesh.isNull(ParShapesMesh.NORMALS)) {
			parMesh.normals(normals.length).get(normals);
		}
		float[] textureCoordinates = new float[vertexCount * 2];
		if (!parMesh.isNull(ParShapesMesh.TCOORDS)) {
			parMesh.tcoords(vertexCount * 2).get(textureCoordinates);
		}
		
		int triangleCount = parMesh.ntriangles();
		int[] indices = new int[triangleCount * 3];
		parMesh.triangles(indices.length).get(indices);
		
		try (MemoryStack stack = MemoryStack.stackPush()) {
			FloatBuffer aabb = stack.mallocFloat(6);
			par_shapes_compute_aabb(parMesh, aabb);

			var aabbf = new AABBf(aabb.get(), aabb.get(), aabb.get(), aabb.get(), aabb.get(), aabb.get());
			return new StaticMesh(positions, normals, null, null, textureCoordinates, indices, aabbf);
		} finally {
			par_shapes_free_mesh(parMesh);
		}
	};
	
	private ParShapesLoader() {}

	/**
	 * Generate a new cube {@link StaticMesh}.
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
	 * Generate a plane {@link StaticMesh}.
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
	 * Generate a sphere {@link StaticMesh}.
	 * @param radius the radius of the sphere.
	 * @return a sphere {@link StaticMesh}.
	 */
	public static Mesh loadSphere(float radius) {
		float diameter = radius * 2;
		return builder.createSphere(16, 16)
				.scale(diameter, diameter, diameter)
				.translate(-radius, -radius, -radius)
				.build(STATIC_MESH_BUILDER);
	}
	
	/**
	 * Generate an L-System {@link StaticMesh}.
	 * @param program instructions defining the L-System to create.
	 * @return an L-System {@link StaticMesh}.
	 */
	public static Mesh loadLSystem(String program) {
		return builder.createLSystem(program, 5, 60)
				.build(STATIC_MESH_BUILDER);
	}
	
	/**
	 * Generate a new custom {@link StaticMesh}.
	 * @param builder the {@link ParShapesBuilder} defining the mesh to create with all transformations.
	 * @return a custom {@link StaticMesh}.
	 */
	public static Mesh loadBuilder(ParShapesBuilder builder) {
		return builder.build(STATIC_MESH_BUILDER);
	}
	
	/**
	 * Generate a new custom {@link StaticMesh}.
	 * @param buildFunction a Function to construct a mesh from.
	 * @return a custom {@link StaticMesh}.
	 */
	public static Mesh loadFromBuilder(Function<ParShapesBuilder, StaticMesh> buildFunction) {
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

			float[] positions = new float[vertexCount * 3];
			parMesh.points(positions.length).get(positions);
			float[] normals = new float[vertexCount * 3];
			if (!parMesh.isNull(ParShapesMesh.NORMALS)) {
				parMesh.normals(normals.length).get(normals);
			}
			float[] textureCoordinates = new float[vertexCount * 2];
			if (!parMesh.isNull(ParShapesMesh.TCOORDS)) {
				parMesh.tcoords(vertexCount * 2).get(textureCoordinates);
			}

			int triangleCount = parMesh.ntriangles();
			int[] indices = new int[triangleCount * 3];
			parMesh.triangles(indices.length).get(indices);
			
			try (MemoryStack stack = MemoryStack.stackPush()) {
				FloatBuffer aabb = stack.mallocFloat(6);
				par_shapes_compute_aabb(parMesh, aabb);

				var aabbf = new AABBf(aabb.get(), aabb.get(), aabb.get(), aabb.get(), aabb.get(), aabb.get());
				return new InstancedMesh(positions, normals, null, null, textureCoordinates, indices, aabbf, instanceCount);
			} finally {
				par_shapes_free_mesh(parMesh);
			}
		});
	}
	
}
