package com.avogine.util;

/**
 * Utility class for managing arrays.
 */
public class ArrayUtils {
	
	private ArrayUtils() {
		
	}

	private static final float[] EMPTY_FLOAT_ARRAY = new float[0];

	/**
	 * <p>Converts an array of object Floats to primitives.
	 *
	 * <p>This method returns an empty array for a {@code null} input array.
	 *
	 * @param array  a {@code Float} array, may be {@code null}
	 * @return a {@code float} array, {@code null} if null array input
	 */
	public static float[] toPrimitive(final Float[] array) {
		return switch (array) {
			case null -> new float[0];
			case Float[] f when f.length == 0 -> EMPTY_FLOAT_ARRAY;
			default -> {
				final float[] result = new float[array.length];
				for (int i = 0; i < array.length; i++) {
					result[i] = array[i].floatValue();
				}
				yield result;
			}
		};
	}

}
