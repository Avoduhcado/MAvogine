package com.avogine.util;

import java.text.*;

import org.joml.*;

/**
 * Static utility methods for operating on Vectors from the JOML library.
 */
public class VectorUtil {
	
	private static final DecimalFormat df = new DecimalFormat("0.00");
	
	/**
	 * Vector indicating positive X axis as "right"
	 */
	public static final Vector3f RIGHT = new Vector3f(1, 0, 0);
	/**
	 * Vector indicating positive Y axis as "up"
	 */
	public static final Vector3f UP = new Vector3f(0, 1, 0);
	/**
	 * Vector indicating positive Z axis as "forward"
	 */
	public static final Vector3f FORWARD = new Vector3f(0, 0, 1);
	
	/**
	 * Vector with 0 in each component. Do Not Modify.
	 */
	private static final Vector3f ZERO = new Vector3f();

	/**
	 * Subtract {@code other} from {@code value} and clamp each component to zero if the operation would otherwise cause
	 * a component to go past 0.
	 * @param value The vector to modify.
	 * @param other The vector to subtract. This is expected to be a positive vector.
	 * @return The result of {@code other - value}
	 */
	public static Vector3f subClampToZero(Vector3f value, Vector3f other) {
		var tempNormal = value.normalize(new Vector3f());
		
		value.absolute().sub(other.absolute());
		value.max(ZERO).mul(tempNormal.div(tempNormal.absolute(new Vector3f())));
		
		return value;
	}
	
	/**
	 * Print the given vector in a more human readable style.
	 * @param vector The vector to print.
	 * @return A String representation of the vector.
	 */
	public static String printVector(Vector3f vector) {
		return printVector(vector, df);
	}
	
	/**
	 * Print the given vector in a more human readable style with the given {@link DecimalFormat} to determine
	 * how to limit the vector's components. This will not modify the actual vector.
	 * @param vector The vector to print.
	 * @param df The {@code DecimalFormat} to use when printing each component of the vector. 
	 * @return A String representation of the vector.
	 */
	public static String printVector(Vector3f vector, DecimalFormat df) {
		return "[" + df.format(vector.x) + ", " + df.format(vector.y) + ", " + df.format(vector.z) + "] Mag: {" + df.format(vector.length()) + "}";
	}
}
