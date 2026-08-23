package com.avogine.render.opengl.model.util;

import static org.lwjgl.util.par.ParShapes.*;

import java.nio.*;
import java.util.function.Function;

import org.joml.primitives.AABBf;
import org.lwjgl.system.*;
import org.lwjgl.util.par.ParShapesMesh;

import com.avogine.render.model.mesh.data.*;
import com.avogine.render.opengl.model.mesh.*;
import com.avogine.render.opengl.model.mesh.StaticMesh.StaticInstancedMesh;
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
		int triangleCount = parMesh.ntriangles();

		FloatBuffer positions = parMesh.points(vertexCount * 3);
		FloatBuffer normals = parMesh.isNull(ParShapesMesh.NORMALS) ? MemoryUtil.memCallocFloat(vertexCount * 3) : parMesh.normals(vertexCount * 3);
		FloatBuffer tangents = MemoryUtil.memCallocFloat(vertexCount * 3);
		FloatBuffer bitangents = MemoryUtil.memCallocFloat(vertexCount * 3);
		FloatBuffer textureCoordinates = parMesh.isNull(ParShapesMesh.TCOORDS) ? MemoryUtil.memCallocFloat(vertexCount * 2) : parMesh.tcoords(vertexCount * 2);
		IntBuffer indices = parMesh.triangles(triangleCount * 3);
		
		try (MemoryStack stack = MemoryStack.stackPush()) {
			FloatBuffer aabb = stack.mallocFloat(6);
			par_shapes_compute_aabb(parMesh, aabb);

			var aabbf = new AABBf(aabb.get(), aabb.get(), aabb.get(), aabb.get(), aabb.get(), aabb.get());
			var vertexData = new VertexData(normals, tangents, bitangents, textureCoordinates);
			
			return new StaticMesh(positions, vertexData, indices, aabbf);
		} finally {
			if (parMesh.isNull(ParShapesMesh.NORMALS)) {
				MemoryUtil.memFree(normals);
			}
			if (parMesh.isNull(ParShapesMesh.TCOORDS)) {
				MemoryUtil.memFree(textureCoordinates);
			}
			par_shapes_free_mesh(parMesh);
			MemoryUtil.memFree(tangents);
			MemoryUtil.memFree(bitangents);
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
	public static StaticMesh loadCube(float scale) {
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
	public static StaticMesh loadPlane(float scale) {
		return builder.createPlane(100, 100)
				.scale(scale, scale, scale)
				.translate(-scale / 2, 0, -scale / 2)
				.build(STATIC_MESH_BUILDER);
	}
	
	/**
	 * Generate a sphere {@link StaticMesh}.
	 * @param radius the radius of the sphere.
	 * @return a sphere {@link StaticMesh}.
	 */
	public static StaticMesh loadSphere(float radius) {
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
	public static StaticMesh loadLSystem(String program) {
		return builder.createLSystem(program, 5, 60)
				.build(STATIC_MESH_BUILDER);
	}
	
	/**
	 * Generate a new custom {@link StaticMesh}.
	 * @param builder the {@link ParShapesBuilder} defining the mesh to create with all transformations.
	 * @return a custom {@link StaticMesh}.
	 */
	public static StaticMesh loadBuilder(ParShapesBuilder builder) {
		return builder.build(STATIC_MESH_BUILDER);
	}
	
	/**
	 * Generate a new custom {@link StaticMesh}.
	 * @param buildFunction a Function to construct a mesh from.
	 * @return a custom {@link StaticMesh}.
	 */
	public static StaticMesh loadFromBuilder(Function<ParShapesBuilder, StaticMesh> buildFunction) {
		return buildFunction.apply(builder);
	}
	
	/**
	 * Generate a new {@link StaticInstancedMesh}.
	 * @param builder the {@link ParShapesBuilder} defining the mesh to create with all transformations.
	 * @param instanceCount the total number of instances to allocate.
	 * @return a new {@code InstancedMesh}
	 */
	public static StaticInstancedMesh loadInstancedBuilder(ParShapesBuilder builder, int instanceCount) {
		return builder.build(parMesh -> {
			int vertexCount = parMesh.npoints();
			int triangleCount = parMesh.ntriangles();

			FloatBuffer positions = parMesh.points(vertexCount * 3);
			FloatBuffer normals = parMesh.isNull(ParShapesMesh.NORMALS) ? MemoryUtil.memCallocFloat(vertexCount * 3) : parMesh.normals(vertexCount * 3);
			FloatBuffer tangents = MemoryUtil.memCallocFloat(vertexCount * 3);
			FloatBuffer bitangents = MemoryUtil.memCallocFloat(vertexCount * 3);
			FloatBuffer textureCoordinates = parMesh.isNull(ParShapesMesh.TCOORDS) ? MemoryUtil.memCallocFloat(vertexCount * 2) : parMesh.tcoords(vertexCount * 2);
			IntBuffer indices = parMesh.triangles(triangleCount * 3);
			FloatBuffer instanceMatrices = MemoryUtil.memCallocFloat(16 * instanceCount);
			
			try (MemoryStack stack = MemoryStack.stackPush()) {
				FloatBuffer aabb = stack.mallocFloat(6);
				par_shapes_compute_aabb(parMesh, aabb);

				var aabbf = new AABBf(aabb.get(), aabb.get(), aabb.get(), aabb.get(), aabb.get(), aabb.get());
				var vertexData = new VertexData(normals, tangents, bitangents, textureCoordinates);
//				var indexData = new IndexData(indices);
//				var meshData = new MeshData(vertexData, indexData, aabbf);
				
				var instanceData = new InstanceData(instanceMatrices, instanceCount);
				
				return new StaticInstancedMesh(positions, vertexData, indices, aabbf, instanceData);
			} finally {
				if (parMesh.isNull(ParShapesMesh.NORMALS)) {
					MemoryUtil.memFree(normals);
				}
				if (parMesh.isNull(ParShapesMesh.TCOORDS)) {
					MemoryUtil.memFree(textureCoordinates);
				}
				par_shapes_free_mesh(parMesh);
				MemoryUtil.memFree(tangents);
				MemoryUtil.memFree(bitangents);
				MemoryUtil.memFree(instanceMatrices);
			}
		});
	}
	
}
