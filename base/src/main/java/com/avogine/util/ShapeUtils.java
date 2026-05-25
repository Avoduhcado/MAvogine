package com.avogine.util;

import org.joml.Math;

/**
 *
 */
public class ShapeUtils {

	private ShapeUtils() {

	}

	/**
	 * @param radius the radius of the circle to generate.
	 * @param numberOfSides the total number of sides the circle should have, effectively the smoothness of the shape.
	 * @return a {@code float[]} containing vertices for a circle.
	 */
	public static float[] generateCircleVertices(float radius, int numberOfSides) {
		int numberOfVertices = numberOfSides + 2;

		float[] vertices = new float[numberOfVertices * 3];
		vertices[0] = 0.0f;
		vertices[1] = 0.0f;
		vertices[2] = 0.0f;

		int pos = 3;
		for (int i = 1; i < numberOfVertices; i++) {
			vertices[pos++] = radius * Math.cos(i * Math.PI_TIMES_2_f / numberOfSides);
			vertices[pos++] = radius * Math.sin(i * Math.PI_TIMES_2_f / numberOfSides);
			vertices[pos++] = 0.0f;
		}
		return vertices;
	}

}
