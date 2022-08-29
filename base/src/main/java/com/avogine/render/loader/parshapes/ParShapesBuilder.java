package com.avogine.render.loader.parshapes;

import java.nio.*;

import org.lwjgl.system.*;
import org.lwjgl.util.par.*;

import com.avogine.render.data.*;

/**
 * TODO Implement a scaleAroundCenter method, which will likely require the ParShapesBuilder instance to store current transform values to pass along for context.
 */
public class ParShapesBuilder {

	private ParShapesMesh parMesh;
	
	/**
	 * {@link ParShapes#par_shapes_create_plane(int, int)}
	 * @param slices
	 * @param stacks
	 * @return
	 */
	public ParShapesBuilder createPlane(int slices, int stacks) {
		parMesh = ParShapes.par_shapes_create_plane(slices, stacks);
		// XXX Planes created through ParShapes seem to be created "backwards"?
//		ParShapes.par_shapes_invert(parMesh, 0, 0);
		return this;
	}
	
	/**
	 * {@link ParShapes#par_shapes_create_parametric_sphere(int, int)}
	 * @param slices
	 * @param stacks
	 * @return
	 */
	public ParShapesBuilder createSphere(int slices, int stacks) {
		parMesh = ParShapes.par_shapes_create_parametric_sphere(slices, stacks);
		return this;
	}
	
	/**
	 * {@link ParShapes#par_shapes_create_cube()}
	 * @return
	 */
	public ParShapesBuilder createCube() {
		parMesh = ParShapes.par_shapes_create_cube();
		return this;
	}
	
	/**
	 * TODO Better clarify params
	 * @param width 
	 * @param height
	 * @return
	 */
	public ParShapesBuilder createCapsule(float width, float height) {
		parMesh = ParShapes.par_shapes_create_empty();

		ParShapesMesh topHemi = ParShapes.par_shapes_create_hemisphere(20, 10);
		ParShapes.par_shapes_scale(topHemi, width, width, width);
		ParShapes.par_shapes_merge(parMesh, topHemi);

		ParShapesMesh cylinder = ParShapes.par_shapes_create_cylinder(20, 2);
		ParShapes.par_shapes_rotate(cylinder, (float) (Math.PI * 0.5f), new float[] {1, 0, 0});
		ParShapes.par_shapes_scale(cylinder, width, height, width);
		ParShapes.par_shapes_merge(parMesh, cylinder);

		ParShapesMesh bottomHemi = ParShapes.par_shapes_clone(topHemi, null);
		ParShapes.par_shapes_rotate(bottomHemi, (float) Math.PI, new float[] {1, 0, 0});
		ParShapes.par_shapes_translate(bottomHemi, 0, -height, 0);
		ParShapes.par_shapes_merge(parMesh, bottomHemi);
		
		return this;
	}
	
	public ParShapesBuilder translate(float x, float y, float z) {
		ParShapes.par_shapes_translate(parMesh, x, y, z);
		return this;
	}
	
	/**
	 * Rotate the mesh around a given axis.
	 * <p>
	 * Use this to bake a rotation into the resulting {@link Mesh}. A particular use case would be for 2D
	 * elements being painted onto a plane as planes generated from ParShapes seem to have UV coordinates starting in the bottom left
	 * whereas a typical 2D orthographic projection matrix will likely have 0,0 position in the top left. So simply rotating 180 degrees
	 * around the X axis should result in a properly oriented plane.
	 * @param radians the amount to rotate in radians
	 * @param axis the axis to rotate around
	 * @return
	 */
	public ParShapesBuilder rotate(float radians, float[] axis) {
		ParShapes.par_shapes_rotate(parMesh, radians, axis);
		return this;
	}
	
	public ParShapesBuilder scale(float x, float y, float z) {
		ParShapes.par_shapes_scale(parMesh, x, y, z);
		return this;
	}
	
	/**
	 * <b>XXX</b> Don't call this on cylinders, potentially others.
	 * @see <a href="https://github.com/prideout/par/issues/30">parshapes issue</a>
	 * @return {@code this}
	 */
	public ParShapesBuilder computeNormals() {
//		ParShapes.par_shapes_unweld(parMesh, true);
		ParShapes.par_shapes_compute_normals(parMesh);
		
		return this;
	}
	
	/**
	 * Construct a new {@link Mesh} from {@link #parMesh} with all transformations applied.
	 * @return a new {@code Mesh}
	 */
	public Mesh build() {
		// TODO Convert magic numbers into vertex data size constant
		FloatBuffer vertexData = null;
		try {
			vertexData = MemoryUtil.memAllocFloat(parMesh.npoints() * (3 + 3 + 2));
	
			FloatBuffer positions = parMesh.points(parMesh.npoints() * 3);
			FloatBuffer normals = null;
			if (!parMesh.isNull(ParShapesMesh.NORMALS)) {
				normals = parMesh.normals(parMesh.npoints() * 3);
			}
			FloatBuffer textureCoordinates = null;
			if (!parMesh.isNull(ParShapesMesh.TCOORDS)) {
				textureCoordinates = parMesh.tcoords(parMesh.npoints() * 2);
			}
	
			for (int i = 0; i < parMesh.npoints(); i++) {
				// Vertex positions
				vertexData.put(positions.get());
				vertexData.put(positions.get());
				vertexData.put(positions.get());
	
				// Vertex normals
				if (normals != null) {
					vertexData.put(normals.get());
					vertexData.put(normals.get());
					vertexData.put(normals.get());
				} else {
					vertexData.put(0.0f);
					vertexData.put(0.0f);
					vertexData.put(0.0f);
				}
	
				// Vertex texture coordinates
				if (textureCoordinates != null) {
					vertexData.put(textureCoordinates.get());
					vertexData.put(textureCoordinates.get());
				} else {
					vertexData.put(0.0f);
					vertexData.put(0.0f);
				}
			}
			vertexData.flip();
			IntBuffer indices = parMesh.triangles(parMesh.ntriangles() * 3);
	
			return new Mesh(vertexData, indices);
		} finally {
			parMesh.free();
			if (vertexData != null) {
				MemoryUtil.memFree(vertexData);
			}
		}
	}
	
	/**
	 * @param instances
	 * @return
	 */
	public InstancedMesh buildInstanced(int instances) {
		FloatBuffer vertexData = null;
		try {
		vertexData = MemoryUtil.memAllocFloat(parMesh.npoints() * (3 + 3 + 2));

		FloatBuffer positions = parMesh.points(parMesh.npoints() * 3);
		FloatBuffer normals = null;
		if (!parMesh.isNull(ParShapesMesh.NORMALS)) {
			normals = parMesh.normals(parMesh.npoints() * 3);
		}
		FloatBuffer textureCoordinates = null;
		if (!parMesh.isNull(ParShapesMesh.TCOORDS)) {
			textureCoordinates = parMesh.tcoords(parMesh.npoints() * 2);
		}

		for (int i = 0; i < parMesh.npoints(); i++) {
			// Vertex positions
			vertexData.put(positions.get());
			vertexData.put(positions.get());
			vertexData.put(positions.get());

			// Vertex normals
			if (normals != null) {
				vertexData.put(normals.get());
				vertexData.put(normals.get());
				vertexData.put(normals.get());
			} else {
				vertexData.put(0.0f);
				vertexData.put(0.0f);
				vertexData.put(0.0f);
			}

			// Vertex texture coordinates
			if (textureCoordinates != null) {
				vertexData.put(textureCoordinates.get());
				vertexData.put(textureCoordinates.get());
			} else {
				vertexData.put(0.0f);
				vertexData.put(0.0f);
			}
		}
		vertexData.flip();
		IntBuffer indices = parMesh.triangles(parMesh.ntriangles() * 3);
		
		FloatBuffer instancedVertexData = MemoryUtil.memAllocFloat(17 * instances);

		return new InstancedMesh(vertexData, instancedVertexData, indices, instances);
		} finally {
			parMesh.free();
			if (vertexData != null) {
				MemoryUtil.memFree(vertexData);
			}
		}
	}
	
}
