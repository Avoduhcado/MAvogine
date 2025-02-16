package com.avogine.io.event;

/**
 * @param <T> The type that should be returned by the implementations of these consume methods. Ideally, the base type implementing {@link ConsumableEvent}.
 */
public interface ConsumableEvent<T extends ConsumableEvent<T>> {

	/**
	 * @return a new instance of this event with copied values, but {@code consumed} set to true.
	 */
	public T withConsume();
	
	/**
	 * @return a new instance of this {@link ConsumableEvent} by calling {@link #withConsume()}.
	 */
	default T consume() {
		return withConsume();
	}
	
}
