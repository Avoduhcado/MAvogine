package com.avogine.io.event;

/**
 *
 */
public abstract class AvoEvent {
	
	/**
	 * The type of event produced.
	 */
	protected int id;
	
	protected boolean consumed;

	/**
	 * @return the ID of the event.
	 */
	public int id() {
		return id;
	}
	
	/**
	 * Consumes this event, if this event can be consumed.
	 */
	public void consume() {
		consumed = switch (id) {
			case KeyEvent.KEY_PRESSED, KeyEvent.KEY_RELEASED, MouseEvent.MOUSE_PRESSED, MouseEvent.MOUSE_RELEASED, MouseEvent.MOUSE_MOVED, MouseEvent.MOUSE_DRAGGED, MouseEvent.MOUSE_WHEEL -> true;
			default -> false;
		};
	}
	
	/**
	 * @return whether this event has been consumed and should no longer propagate to further listeners.
	 */
	public boolean isConsumed() {
		return consumed;
	}
	
}
