package com.avogine.render.util.parshapes;

import static org.lwjgl.system.MemoryUtil.*;
import static org.lwjgl.util.par.ParShapes.*;

import java.nio.FloatBuffer;

import org.joml.Vector3f;
import org.lwjgl.system.*;
import org.lwjgl.util.par.*;

import com.avogine.render.data.mesh.StaticMesh;
import com.avogine.render.data.simple.*;
import com.avogine.render.data.vertices.array.*;
import com.avogine.render.data.vertices.vertex.*;

/**
 * Helper utility for constructing parametric shapes with the {@link ParShapes} library and converting them into usable {@link StaticMesh}s.
 */
public class ParShapesBuilder {

	protected ParShapesMesh parMesh;
	
	/**
	 * XXX Planes created through ParShapes seem to be created "backwards"? May require calling par_shapes_invert.
	 * @param slices
	 * @param stacks
	 * @return this
	 * @see ParShapes#par_shapes_create_plane(int, int)
	 */
	public ParShapesBuilder createPlane(int slices, int stacks) {
		parMesh = par_shapes_create_plane(slices, stacks);
		return this;
	}
	
	/**
	 * @param slices
	 * @param stacks
	 * @return this
	 * @see ParShapes#par_shapes_create_parametric_sphere(int, int)
	 */
	public ParShapesBuilder createSphere(int slices, int stacks) {
		parMesh = par_shapes_create_parametric_sphere(slices, stacks);
		return this;
	}
	
	/**
	 * @param subdivisions
	 * @return this
	 * @see ParShapes#par_shapes_create_subdivided_sphere(int)
	 */
	public ParShapesBuilder createSphere(int subdivisions) {
		parMesh = par_shapes_create_subdivided_sphere(subdivisions);
		return this;
	}
	
	/**
	 * @return this
	 * @see ParShapes#par_shapes_create_cube()
	 */
	public ParShapesBuilder createCube() {
		parMesh = par_shapes_create_cube();
		return this;
	}
	
	/**
	 * @param seed 
	 * @param subdivisions 
	 * @return this
	 * @see ParShapes#par_shapes_create_rock(int, int)
	 */
	public ParShapesBuilder createRock(int seed, int subdivisions) {
		parMesh = par_shapes_create_rock(seed, subdivisions);
		return this;
	}
	
	/**
	 * @param program
	 * @param slices
	 * @param maxDepth
	 * @return this
	 * @see ParShapes#par_shapes_create_lsystem(CharSequence, int, int, ParShapesRandFnI, long)
	 */
	public ParShapesBuilder createLSystem(String program, int slices, int maxDepth) {
		parMesh = par_shapes_create_lsystem(program, slices, maxDepth, null, 0);
		return this;
	}
	
	/**
	 * Create 2 hemispheres with a cylinder between them.
	 * <p>
	 * @return this
	 * @see ParShapes#par_shapes_create_hemisphere(int, int)
	 * @see ParShapes#par_shapes_create_cylinder(int, int)
	 */
	public ParShapesBuilder createCapsule() {
		parMesh = par_shapes_create_empty();

		ParShapesMesh topHemi = par_shapes_create_hemisphere(20, 10);
		par_shapes_merge(parMesh, topHemi);

		ParShapesMesh cylinder = par_shapes_create_cylinder(20, 2);
		par_shapes_rotate(cylinder, (float) (Math.PI * 0.5f), new float[] {1, 0, 0});
		par_shapes_scale(cylinder, 1, 2, 1);
		par_shapes_merge_and_free(parMesh, cylinder);

		ParShapesMesh bottomHemi = par_shapes_clone(topHemi, null);
		par_shapes_free_mesh(topHemi);
		par_shapes_rotate(bottomHemi, (float) Math.PI, new float[] {1, 0, 0});
		par_shapes_translate(bottomHemi, 0, -2, 0);
		par_shapes_merge_and_free(parMesh, bottomHemi);
		
		return this;
	}
	
	/**
	 * @param x
	 * @param y
	 * @param z
	 * @return this
	 * @see ParShapes#par_shapes_translate(ParShapesMesh, float, float, float)
	 */
	public ParShapesBuilder translate(float x, float y, float z) {
		par_shapes_translate(parMesh, x, y, z);
		return this;
	}
	
	/**
	 * Rotate the mesh around a given axis.
	 * <p>
	 * <p>
	 * Use this to bake a rotation into the resulting {@link StaticMesh}. A particular use case would be for 2D
	 * elements being painted onto a plane as planes generated from ParShapes seem to have UV coordinates starting in the bottom left
	 * whereas a typical 2D orthographic projection matrix will likely have 0,0 position in the top left. So simply rotating 180 degrees
	 * around the X axis should result in a properly oriented plane.
	 * @param radians the amount to rotate in radians
	 * @param axis the axis to rotate around
	 * @return this
	 * @see ParShapes#par_shapes_rotate(ParShapesMesh, float, float[])
	 */
	public ParShapesBuilder rotate(float radians, float[] axis) {
		par_shapes_rotate(parMesh, radians, axis);
		return this;
	}
	
	/**
	 * <p>
	 * Applies scaling from the origin. Apply a half-offset translation before and after to scale from center.
	 * @param x
	 * @param y
	 * @param z
	 * @return this
	 * @see ParShapes#par_shapes_scale(ParShapesMesh, float, float, float)
	 */
	public ParShapesBuilder scale(float x, float y, float z) {
		par_shapes_scale(parMesh, x, y, z);
		return this;
	}
	
	/**
	 * <p>
	 * <b>XXX</b> Don't call this on cylinders, potentially others.
	 * @see <a href="https://github.com/prideout/par/issues/30">parshapes issue</a>
	 * @return this
	 * @see ParShapes#par_shapes_compute_normals(ParShapesMesh)
	 */
	public ParShapesBuilder computeNormals() {
		par_shapes_compute_normals(parMesh);
		
		return this;
	}
	
	/**
	 * Construct a new {@link SimpleMesh} from the internal {@code parMesh} with all transformations applied.
	 * @return a new {@code SimpleMesh}
	 */
	public SimpleMesh build() {
		int vertexCount = parMesh.npoints();
		int vert3 = vertexCount * 3;
		int vert2 = vertexCount * 2;
		
		try (var position = new PositionVertex(memAllocFloat(vert3).put(parMesh.points(vert3)).flip());
				var shading = new ShadingVertex(
						(!parMesh.isNull(ParShapesMesh.NORMALS) ? memAllocFloat(vert3).put(parMesh.normals(vert3)).flip() : memCallocFloat(vert3)),
						memCallocFloat(vert3),
						memCallocFloat(vert3)
						);
				var textureCoordinate = new TextureCoordinateVertex((!parMesh.isNull(ParShapesMesh.TCOORDS) ? memAllocFloat(vert2).put(parMesh.tcoords(vert2)).flip() : memCallocFloat(vert2)));
				var element = new ElementVertex(memAllocInt(parMesh.ntriangles() * 3).put(parMesh.triangles(parMesh.ntriangles() * 3)).flip());
				MemoryStack stack = MemoryStack.stackPush();) {
			var vertexData = new SimpleVertexArray(position, shading, textureCoordinate, element);

			FloatBuffer aabb = stack.mallocFloat(6);
			par_shapes_compute_aabb(parMesh, aabb);
			var aabbMin = new Vector3f(aabb.get(), aabb.get(), aabb.get());
			var aabbMax = new Vector3f(aabb.get(), aabb.get(), aabb.get());

			return new SimpleMesh(vertexData, 0, aabbMin, aabbMax);
		} finally {
			par_shapes_free_mesh(parMesh);
		}
	}
	
	/**
	 * Construct a new {@link SimpleInstanceMesh} from the internal {@code parMesh} with all transformations applied.
	 * @param instanceCount the total number of instances to allocate.
	 * @return a new {@code SimpleInstanceMesh}
	 */
	public SimpleInstanceMesh build(int instanceCount) {
		int vertexCount = parMesh.npoints();
		int vert3 = vertexCount * 3;
		int vert2 = vertexCount * 2;
		try (var position = new PositionVertex(memAllocFloat(vert3).put(parMesh.points(vert3)).flip());
				var shading = new ShadingVertex(
						(!parMesh.isNull(ParShapesMesh.NORMALS) ? memAllocFloat(vert3).put(parMesh.normals(vert3)).flip() : memCallocFloat(vert3)),
						memCallocFloat(vert3),
						memCallocFloat(vert3)
						);
				var textureCoordinate = new TextureCoordinateVertex((!parMesh.isNull(ParShapesMesh.TCOORDS) ? memAllocFloat(vert2).put(parMesh.tcoords(vert2)).flip() : memCallocFloat(vert2)));
				var element = new ElementVertex(memAllocInt(parMesh.ntriangles() * 3).put(parMesh.triangles(parMesh.ntriangles() * 3)).flip());
				var instanceTransform = new InstanceTransformVertex(MemoryUtil.memAllocFloat(instanceCount * 16), MemoryUtil.memAllocFloat(instanceCount * 16));
				MemoryStack stack = MemoryStack.stackPush();) {
			var vertexData = new SimpleInstanceVertexArray(new SimpleVertexArray(position, shading, textureCoordinate, element), instanceTransform);
			
			FloatBuffer aabb = stack.mallocFloat(6);
			par_shapes_compute_aabb(parMesh, aabb);
			var aabbMin = new Vector3f(aabb.get(), aabb.get(), aabb.get());
			var aabbMax = new Vector3f(aabb.get(), aabb.get(), aabb.get());
			
			return new SimpleInstanceMesh(vertexData, 0, aabbMax, aabbMin, instanceCount);
		} finally {
			par_shapes_free_mesh(parMesh);
		}
	}
	
	/**
	 * @param <T>
	 * @param builder
	 * @return the result of {@code builder}
	 */
	public <T> T build(BuildFunction<T> builder) {
		return builder.build(parMesh);
	}
	
}
