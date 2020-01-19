package com.avogine.loader.parshapes;

import org.lwjgl.util.par.ParShapes;
import org.lwjgl.util.par.ParShapesMesh;

import com.avogine.core.render.Mesh;

/**
 * TODO Add param objects to pass to each method for shape customization
 * <p>
 * Utility class for creating usable {@link Mesh} objects from the {@link ParShapes} library.
 * @author Dominus
 *
 */
public class ParShapesLoader {

	public static Mesh loadCubemap() {
		ParShapesMesh parMesh = ParShapes.par_shapes_create_cube();
		// ParShapes generates a 1x1x1 cube centered around (0.5, 0.5, 0.5) so we need to scale and offset it to cover -1 to 1
		ParShapes.par_shapes_scale(parMesh, 2, 2, 2);
		ParShapes.par_shapes_translate(parMesh, -1, -1, -1);
		
		Mesh mesh = new Mesh(parMesh.points(parMesh.npoints() * 3));
		mesh.addIndexAttribute(parMesh.triangles(parMesh.ntriangles() * 3), parMesh.ntriangles() * 3);
		
		parMesh.free();
		
		return mesh;
	}
	
	public static Mesh loadPlane() {
		ParShapesMesh parMesh = ParShapes.par_shapes_create_plane(1, 1);
		ParShapes.par_shapes_scale(parMesh, 2, 2, 2);
//		ParShapes.par_shapes_rotate(parMesh, (float) Math.toRadians(180), new float[] {0, 1, 0});
//		ParShapes.par_shapes_rotate(parMesh, (float) Math.toRadians(180), new float[] {0, 0, 1});
		
		Mesh mesh = new Mesh(parMesh.points(parMesh.npoints() * 3));
		mesh.addAttribute(1, parMesh.tcoords(parMesh.npoints() * 2), 2);
		mesh.addAttribute(2, parMesh.normals(parMesh.npoints() * 3), 3);
		mesh.addIndexAttribute(parMesh.triangles(parMesh.ntriangles() * 3), parMesh.ntriangles() * 3);
		
		parMesh.free();
		
		return mesh;
	}
	
	public static Mesh loadSphere(float radius) {
		ParShapesMesh parMesh = ParShapes.par_shapes_create_parametric_sphere(16, 16);
		
		ParShapes.par_shapes_scale(parMesh, radius, radius, radius);
		ParShapes.par_shapes_translate(parMesh, 0, radius * 0.5f, 0);
		
		Mesh mesh = new Mesh(parMesh.points(parMesh.npoints() * 3));
		mesh.addAttribute(1, parMesh.tcoords(parMesh.npoints() * 2), 2);
		mesh.addAttribute(2, parMesh.normals(parMesh.npoints() * 3), 3);
		mesh.addIndexAttribute(parMesh.triangles(parMesh.ntriangles() * 3), parMesh.ntriangles() * 3);
		
		parMesh.free();
		
		return mesh;
	}
	
}
