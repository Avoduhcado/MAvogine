package com.avogine.render.loader.parshapes;

import org.lwjgl.util.par.*;

import com.avogine.render.data.RawMesh;
import com.avogine.render.data.mesh.Mesh;

/**
 * TODO Add param objects to pass to each method for shape customization
 * <p>
 * Utility class for creating usable {@link Mesh} objects from the {@link ParShapes} library.
 * @author Dominus
 *
 */
public class ParShapesLoader {

	/**
	 * Generate a new cube {@link Mesh}.
	 * <p>
	 * This will automatically scale and re-center the cube to 0, 0, 0
	 * @return
	 */
	public static Mesh loadCubemap() {
		// ParShapes generates a 1x1x1 cube centered around (0.5, 0.5, 0.5) so we need to scale and offset it to cover -1 to 1
		// XXX Reading the above comment, does this actually do what it says it should?
		return new ParShapesBuilder()
				.createCube()
				.scale(2, 2, 2)
				.translate(-1, -1, -1)
				.build();
	}
	
	/**
	 * @return
	 */
	public static RawMesh loadPlane() {
		ParShapesMesh parMesh = ParShapes.par_shapes_create_plane(100, 100);
		ParShapes.par_shapes_scale(parMesh, 512, 512, 1);
		ParShapes.par_shapes_rotate(parMesh, (float) Math.toRadians(-90), new float[] {1, 0, 0});
		ParShapes.par_shapes_translate(parMesh, -256, 0, 256);
		
		RawMesh mesh = new RawMesh(parMesh.points(parMesh.npoints() * 3));
		mesh.addAttribute(1, parMesh.tcoords(parMesh.npoints() * 2), 2);
		mesh.addAttribute(2, parMesh.normals(parMesh.npoints() * 3), 3);
		mesh.addIndexAttribute(parMesh.triangles(parMesh.ntriangles() * 3), parMesh.ntriangles() * 3);
		
		parMesh.free();
		
		return mesh;
	}
	
	/**
	 * @param radius
	 * @return
	 */
	public static RawMesh loadSphere(float radius) {
		ParShapesMesh parMesh = ParShapes.par_shapes_create_parametric_sphere(16, 16);
		
		ParShapes.par_shapes_scale(parMesh, radius, radius, radius);
		ParShapes.par_shapes_translate(parMesh, 0, radius * 0.5f, 0);
		
		RawMesh mesh = new RawMesh(parMesh.points(parMesh.npoints() * 3));
		mesh.addAttribute(1, parMesh.tcoords(parMesh.npoints() * 2), 2);
		mesh.addAttribute(2, parMesh.normals(parMesh.npoints() * 3), 3);
		mesh.addIndexAttribute(parMesh.triangles(parMesh.ntriangles() * 3), parMesh.ntriangles() * 3);
		
		parMesh.free();
		
		return mesh;
	}
	
	/**
	 * @return
	 */
	public static RawMesh loadLSystem() {
		String program =
	            "sx 2 sy 2" +
	            " ry -90 rx 90" +
	            " shape tube rx 15  call rlimb rx -15" +
	            " shape tube rx -15 call llimb rx 15" +
	            " shape tube ry 15  call rlimb ry -15" +
	            " shape tube ry 15  call llimb ry -15" +
	            " rule rlimb" +
	            "     sx 0.925 sy 0.925 tz 1 rx 1.2" +
	            "     call rlimb2" +
	            " rule rlimb2.1" +
	            "     shape connect" +
	            "     call rlimb" +
	            " rule rlimb2.1" +
	            "     rx 15  shape tube call rlimb rx -15" +
	            "     rx -15 shape tube call llimb rx 15" +
	            " rule rlimb.1" +
	            "     call llimb" +
	            " rule llimb.1" +
	            "     call rlimb" +
	            " rule llimb.10" +
	            "     sx 0.925 sy 0.925" +
	            "     tz 1" +
	            "     rx -1.2" +
	            "     shape connect" +
	            "     call llimb";
		ParShapesMesh parMesh = ParShapes.par_shapes_create_lsystem(program, 5, 60, null, 0);
		
		RawMesh mesh = new RawMesh(parMesh.points(parMesh.npoints() * 3));
		mesh.addIndexAttribute(parMesh.triangles(parMesh.ntriangles() * 3), parMesh.ntriangles() * 3);
		
		parMesh.free();
		
		return mesh;
	}
	
}
