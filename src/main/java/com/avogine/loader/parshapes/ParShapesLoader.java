package com.avogine.loader.parshapes;

import org.lwjgl.util.par.ParShapes;
import org.lwjgl.util.par.ParShapesMesh;

import com.avogine.core.render.Mesh;

/**
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
	
}
