package com.avogine.util;

import java.nio.FloatBuffer;

import org.joml.Math;
import org.lwjgl.system.MemoryUtil;

/**
 *
 */
public class ShapeUtils {

	private ShapeUtils() {

	}


	/**
	 * @param radius the radius of the circle to generate.
	 * @param numberOfSides the total number of sides the circle should have, effectively the smoothness of the shape.
	 * @return a {@link FloatBuffer} containing vertices for a circle.
	 */
	public static FloatBuffer mallocCircleVertices(float radius, int numberOfSides) {
		int numberOfVertices = numberOfSides + 2;

		FloatBuffer verticesBuffer = MemoryUtil.memAllocFloat(numberOfVertices * 3);
		verticesBuffer.put(0.0f).put(0.0f).put(0.0f);

		for (int i = 1; i < numberOfVertices; i++) {
			verticesBuffer
			.put(radius * Math.cos(i * Math.PI_TIMES_2_f / numberOfSides))
			.put(radius * Math.sin(i * Math.PI_TIMES_2_f / numberOfSides))
			.put(0.0f);
		}
		verticesBuffer.flip();

		return verticesBuffer;
	}

}
