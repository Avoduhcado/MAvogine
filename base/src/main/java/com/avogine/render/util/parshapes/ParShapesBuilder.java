package com.avogine.render.util.parshapes;

import static org.lwjgl.util.par.ParShapes.*;

import org.joml.Vector3f;
import org.lwjgl.util.par.*;

import com.avogine.render.data.*;

/**
 * Helper utility for constructing parametric shapes with the {@link ParShapes} library and converting them into usable {@link Mesh}s.
 */
public class ParShapesBuilder {

	protected ParShapesMesh parMesh;
	
	/**
	 * {@link ParShapes#par_shapes_create_plane(int, int)}
	 * <p>
	 * XXX Planes created through ParShapes seem to be created "backwards"? May require calling par_shapes_invert.
	 * @param slices
	 * @param stacks
	 * @return this
	 */
	public ParShapesBuilder createPlane(int slices, int stacks) {
		parMesh = par_shapes_create_plane(slices, stacks);
		return this;
	}
	
	/**
	 * {@link ParShapes#par_shapes_create_parametric_sphere(int, int)}
	 * @param slices
	 * @param stacks
	 * @return this
	 */
	public ParShapesBuilder createSphere(int slices, int stacks) {
		parMesh = par_shapes_create_parametric_sphere(slices, stacks);
		return this;
	}
	
	/**
	 * {@link ParShapes#par_shapes_create_subdivided_sphere(int)}
	 * @param subdivisions
	 * @return this
	 */
	public ParShapesBuilder createSphere(int subdivisions) {
		parMesh = par_shapes_create_subdivided_sphere(subdivisions);
		return this;
	}
	
	/**
	 * {@link ParShapes#par_shapes_create_cube()}
	 * @return this
	 */
	public ParShapesBuilder createCube() {
		parMesh = par_shapes_create_cube();
		return this;
	}
	
	/**
	 * {@link ParShapes#par_shapes_create_rock(int, int)}
	 * @param seed 
	 * @param subdivisions 
	 * @return this
	 */
	public ParShapesBuilder createRock(int seed, int subdivisions) {
		parMesh = par_shapes_create_rock(seed, subdivisions);
		return this;
	}
	
	/**
	 * {@link ParShapes#par_shapes_create_lsystem(CharSequence, int, int, ParShapesRandFnI, long)}
	 * @param program
	 * @param slices
	 * @param maxDepth
	 * @return this
	 */
	public ParShapesBuilder createLSystem(String program, int slices, int maxDepth) {
		parMesh = par_shapes_create_lsystem(program, slices, maxDepth, null, 0);
		return this;
	}
	
	/**
	 * Create 2 hemispheres with a cylinder between them.
	 * <p>
	 * {@link ParShapes#par_shapes_create_hemisphere(int, int)}
	 * </br>
	 * {@link ParShapes#par_shapes_create_cylinder(int, int)}
	 * @return this
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
	 * {@link ParShapes#par_shapes_translate(ParShapesMesh, float, float, float)}
	 * @param x
	 * @param y
	 * @param z
	 * @return this
	 */
	public ParShapesBuilder translate(float x, float y, float z) {
		par_shapes_translate(parMesh, x, y, z);
		return this;
	}
	
	/**
	 * Rotate the mesh around a given axis.
	 * <p>
	 * {@link ParShapes#par_shapes_rotate(ParShapesMesh, float, float[])}
	 * <p>
	 * Use this to bake a rotation into the resulting {@link Mesh}. A particular use case would be for 2D
	 * elements being painted onto a plane as planes generated from ParShapes seem to have UV coordinates starting in the bottom left
	 * whereas a typical 2D orthographic projection matrix will likely have 0,0 position in the top left. So simply rotating 180 degrees
	 * around the X axis should result in a properly oriented plane.
	 * @param radians the amount to rotate in radians
	 * @param axis the axis to rotate around
	 * @return this
	 */
	public ParShapesBuilder rotate(float radians, float[] axis) {
		par_shapes_rotate(parMesh, radians, axis);
		return this;
	}
	
	/**
	 * {@link ParShapes#par_shapes_scale(ParShapesMesh, float, float, float)}
	 * <p>
	 * Applies scaling from the origin. Apply a half-offset translation before and after to scale from center.
	 * @param x
	 * @param y
	 * @param z
	 * @return this
	 */
	public ParShapesBuilder scale(float x, float y, float z) {
		par_shapes_scale(parMesh, x, y, z);
		return this;
	}
	
	/**
	 * {@link ParShapes#par_shapes_compute_normals(ParShapesMesh)}
	 * <p>
	 * <b>XXX</b> Don't call this on cylinders, potentially others.
	 * @see <a href="https://github.com/prideout/par/issues/30">parshapes issue</a>
	 * @return this
	 */
	public ParShapesBuilder computeNormals() {
		par_shapes_compute_normals(parMesh);
		
		return this;
	}
	
	/**
	 * Construct a new {@link Mesh} from the internal {@code parMesh} with all transformations applied.
	 * @return a new {@code Mesh}
	 */
	public Mesh build() {
		float[] vertices = new float[parMesh.npoints() * 3];
		float[] normals = new float[parMesh.npoints() * 3];
		float[] tangents = new float[parMesh.npoints() * 3];
		float[] bitangents = new float[parMesh.npoints() * 3];
		float[] textureCoordinates = new float[parMesh.npoints() * 2];
		parMesh.points(parMesh.npoints() * 3).get(vertices);
		if (!parMesh.isNull(ParShapesMesh.NORMALS)) {
			parMesh.normals(parMesh.npoints() * 3).get(normals);
		}
		if (!parMesh.isNull(ParShapesMesh.TCOORDS)) {
			parMesh.tcoords(parMesh.npoints() * 2).get(textureCoordinates);
		}
		
		int[] indices = new int[parMesh.ntriangles() * 3];
		parMesh.triangles(parMesh.ntriangles() * 3).get(indices);
		
		float[] aabb = new float[6];
		par_shapes_compute_aabb(parMesh, aabb);
		Vector3f aabbMin = new Vector3f(aabb[0], aabb[1], aabb[2]);
		Vector3f aabbMax = new Vector3f(aabb[3], aabb[4], aabb[5]);

		var mesh = new Mesh(new VertexData(vertices, normals, tangents, bitangents, textureCoordinates, indices), 0, aabbMin, aabbMax);

		parMesh.free();
		return mesh;
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
