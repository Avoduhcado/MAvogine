package com.avogine.io.listener;

/**
 *
 */
public interface InputListener extends Comparable<InputListener> {

	/**
	 * The "Layer" on which this listener receives events.
	 * </p>
	 * Events fall down through the window starting on the UI layer before ending on the Scene layer. 
	 * If you want an event to be consumed by a UI element before a scene element, then your UI element
	 * should set its EventLayer to {@link EventLayer#UI}.
	 * @return this listener's {@link EventLayer}. Defaults to {@link EventLayer#SCENE}.
	 */
	public default EventLayer getLayer() {
		return EventLayer.SCENE;
	}
	
	@Override
	default int compareTo(InputListener o) {
		return getLayer().compareTo(o.getLayer());
	}
}
