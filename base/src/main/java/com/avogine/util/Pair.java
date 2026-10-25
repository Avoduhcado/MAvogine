package com.avogine.util;

/**
 * A simple Tuple object containing two immutable values.
 * @param <T> Type of the first element in this Pair.
 * @param <K> Type of the second element in this Pair.
 * @param first The first value.
 * @param second The second value.
 */
public record Pair<T, K>(T first, K second) {

	/**
	 * @param <T>
	 * @param <K>
	 * @param first
	 * @param second
	 * @return
	 */
	public static <T, K> Pair<T, K> of(T first, K second) {
		return new Pair<>(first, second);
	}
}
