package com.avogine.util;

/**
 * A simple Tuple object containing two immutable values.
 * @param <T> Type of the first element in this Pair.
 * @param <K> Type of the second element in this Pair.
 * @param first The first value.
 * @param second The second value.
 */
public record Pair<T, K>(T first, K second) {

}
