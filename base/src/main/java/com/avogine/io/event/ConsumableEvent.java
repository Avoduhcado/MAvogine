package com.avogine.io.event;

/**
 * Interface for enabling an {@link AvoEvent} to be consumable.
 */
public interface ConsumableEvent {

	/**
	 * Toggle an event's {@code consumed} state to true.
	 */
	public default void consume() {
		
	}
}
