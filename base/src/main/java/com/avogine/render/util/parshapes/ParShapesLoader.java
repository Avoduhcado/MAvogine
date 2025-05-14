package com.avogine.render.util.parshapes;

import com.avogine.render.data.simple.SimpleMesh;

/**
 * Utility class for immediate {@link SimpleMesh} creation via {@link ParShapesBuilder}.
 *
 */
public class ParShapesLoader {
	
	private static final ParShapesBuilder builder = new ParShapesBuilder();
	
	/**
	 * 
	 */
	private ParShapesLoader() {
		
	}

	/**
	 * Generate a new cube {@link SimpleMesh}.
	 * <p>
	 * This will center the cube on [0, 0, 0].
	 * @param scale size of the cube.
	 * @return a cube {@code SimpleMesh}
	 */
	public static SimpleMesh loadCube(float scale) {
		return builder
				.createCube()
				.scale(scale, scale, scale)
				.translate(-scale / 2, -scale / 2, -scale / 2)
				.build();
	}
	
	/**
	 * Generate a plane {@link SimpleMesh}.
	 * @param scale size of the plane.
	 * @return a plane {@code SimpleMesh}.
	 */
	public static SimpleMesh loadPlane(float scale) {
		return builder.createPlane(100, 100)
				.scale(scale, 1, scale)
				.translate(-scale / 2, 0, -scale / 2)
				.build();
	}
	
	/**
	 * Generate a sphere {@link SimpleMesh}.
	 * @param radius the radius of the sphere.
	 * @return a sphere {@link SimpleMesh}.
	 */
	public static SimpleMesh loadSphere(float radius) {
		float diameter = radius * 2;
		return builder.createSphere(16, 16)
				.scale(diameter, diameter, diameter)
				.translate(-radius, -radius, -radius)
				.build();
	}
	
	/**
	 * Generate an L-System {@link SimpleMesh}.
	 * @param program instructions defining the L-System to create.
	 * @return an L-System {@link SimpleMesh}.
	 */
	public static SimpleMesh loadLSystem(String program) {
		return builder.createLSystem(program, 5, 60)
				.build();
	}
	
}
