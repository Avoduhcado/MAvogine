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
		switch (id) {
			case KeyEvent.KEY_PRESSED, KeyEvent.KEY_RELEASED, MouseEvent.MOUSE_PRESSED, MouseEvent.MOUSE_RELEASED, MouseEvent.MOUSE_MOVED, MouseEvent.MOUSE_DRAGGED, MouseEvent.MOUSE_WHEEL:
				consumed = false;
				break;
			default:
		}
	}
	
	/**
	 * @return
	 */
	public boolean isConsumed() {
		return consumed;
	}
	
}
