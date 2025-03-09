package com.avogine.io.event;

import com.avogine.Avogine;

/**
 * Base event type for events and listeners in {@link Avogine}.
 */
public interface AvoEvent {
	
	/**
	 * @return true if this event has been consumed and should no longer propagate to further listeners.
	 */
	public default boolean isConsumed() {
		return false;
	}
	
}
