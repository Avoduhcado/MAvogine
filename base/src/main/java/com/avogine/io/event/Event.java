package com.avogine.io.event;

/**
 *
 */
public interface Event {

	/**
	 * 
	 */
	public void consume();
	
	/**
	 * @return
	 */
	public boolean isConsumed();
	
}
