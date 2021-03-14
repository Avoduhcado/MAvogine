package com.avogine.util;

/**
 * @param <T> 
 * @param <K> 
 *
 */
public class Pair<T, K> {

	private T first;
	private K second;
	
	/**
	 * @param first
	 * @param second
	 */
	public Pair(T first, K second) {
		this.setFirst(first);
		this.setSecond(second);
	}

	/**
	 * @return
	 */
	public T getFirst() {
		return first;
	}

	/**
	 * @param first
	 */
	public void setFirst(T first) {
		this.first = first;
	}

	/**
	 * @return
	 */
	public K getSecond() {
		return second;
	}

	/**
	 * @param second
	 */
	public void setSecond(K second) {
		this.second = second;
	}
	
}
