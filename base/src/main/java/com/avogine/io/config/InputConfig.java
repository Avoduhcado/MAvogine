package com.avogine.io.config;

import com.avogine.io.event.MouseEvent.MouseClickedEvent;

/**
 * @param clickDriftTolerance The distance in screen space that the mouse can move while still triggering a {@link MouseClickedEvent} event.
 * @param doubleClickDelayTolerance
 */
public record InputConfig(float clickDriftTolerance, float doubleClickDelayTolerance) {

	/**
	 * @return
	 */
	public static InputConfig defaultConfig() {
		return new InputConfig(1f, 0.5f);
	}
	
}
