package com.avogine.util;

/**
 * @author Dominus
 *
 */
public class MathUtil {

	/**
	 * Clamp a value between two integers.
	 * @param value the value to be clamped.
	 * @param min the minimum value allowed.
	 * @param max the maximum value allowed.
	 * @return a value that is {@code >= min} and {@code <= max}
	 */
	public static int clamp(int value, int min, int max) {
		if(value < min)
			return min;
		if(value > max)
			return max;
		return value;
	}

	/**
	 * Clamp a value between two floats.
	 * @param value the value to be clamped.
	 * @param min the minimum value allowed.
	 * @param max the maximum value allowed.
	 * @return a value that is {@code >= min} and {@code <= max}
	 */
	public static float clamp(float value, float min, float max) {
		if(value < min)
			return min;
		if(value > max)
			return max;
		return value;
	}
	
	/**
	 * Clamp a value between two doubles.
	 * @param value the value to be clamped.
	 * @param min the minimum value allowed.
	 * @param max the maximum value allowed.
	 * @return a value that is {@code >= min} and {@code <= max}
	 */
	public static double clamp(double value, double min, double max) {
		if(value < min)
			return min;
		if(value > max)
			return max;
		return value;
	}
	
	/**
	 * Change over time at linear rate
	 */
	public static float linearTween(float time, float begin, float change, float duration) {
		return change * time / duration + begin;
	}
	
	/**
	 * Start slow then move quickly
	 */
	public static float easeIn(float time, float begin, float change, float duration) {
		return change * (time /= duration) * time + begin;
	}
	
	/**
	 * Start fast then slow down
	 */
	public static float easeOut(float t, float b, float c, float d) {
		return -c *(t/=d)*(t-2) + b;
	}
	
	/**
	 * Start slow, speed up, end slow.
	 */
	public static float easeInOut(float t, float b, float c, float d) {
		if ((t/=d/2) < 1) return c/2*t*t + b;
		return -c/2 * ((--t)*(t-2) - 1) + b;
	}
	
	/**
	 * Linearly interpolate from {@code startValue} to {@code endValue} blended by {@code blend}.
	 * @param startValue
	 * @param endValue
	 * @param blend
	 * @return the linearly interpolated value.
	 */
	public static float lerp(float startValue, float endValue, float blend) {
		  return (1 - blend) * startValue + blend * endValue;
	}
}
