package com.avogine.util;

/**
 * Utility for applying easing functions to compute interpolated values.
 */
public class EasingUtils {
	
	private EasingUtils() {
		
	}
	
	/**
	 * Change over time at linear rate
	 * @param time The current time between the start and end of the change.
	 * @param begin The beginning value to be interpolated from.
	 * @param change The total change from beginning value to expected end value.
	 * @param duration The total time to get from the beginning value to expected end value.
	 * @return a value linearly interpolated between {@code begin} and {@code begin + change} after {@code time} over {@code duration}.
	 */
	public static float linear(float time, float begin, float change, float duration) {
		return change * time / duration + begin;
	}
	
	/**
	 * Start slow then move quickly
	 * @param time The current time between the start and end of the change.
	 * @param begin The beginning value to be interpolated from.
	 * @param change The total change from beginning value to expected end value.
	 * @param duration The total time to get from the beginning value to expected end value.
	 * @return a value interpolated between {@code begin} and {@code begin + change} after {@code time} over {@code duration} with an ease-in formula.
	 */
	public static float easeInQuad(float time, float begin, float change, float duration) {
		var timeOverDuration = time / duration;
		return change * timeOverDuration * time + begin;
	}
	
	/**
	 * Start fast then slow down
	 * @param time The current time between the start and end of the change.
	 * @param begin The beginning value to be interpolated from.
	 * @param change The total change from beginning value to expected end value.
	 * @param duration The total time to get from the beginning value to expected end value.
	 * @return a value interpolated between {@code begin} and {@code begin + change} after {@code time} over {@code duration} with an ease-out formula.
	 */
	public static float easeOutQuad(float time, float begin, float change, float duration) {
		var timeOverDuration = time / duration;
		return -change * timeOverDuration * (time - 2) + begin;
	}
	
	/**
	 * Start slow, speed up, end slow.
	 * @param time The current time between the start and end of the change.
	 * @param begin The beginning value to be interpolated from.
	 * @param change The total change from beginning value to expected end value.
	 * @param duration The total time to get from the beginning value to expected end value.
	 * @return a value interpolated between {@code begin} and {@code begin + change} after {@code time} over {@code duration} with an ease-in-out formula.
	 */
	public static float easeInOutQuad(float time, float begin, float change, float duration) {
		var timeOverDuration = time / duration;
		if ((timeOverDuration / 2) < 1) {
			return change / 2 * time * time + begin;
		}
		return -change / 2 * ((--time) * (time - 2) - 1) + begin;
	}
}
