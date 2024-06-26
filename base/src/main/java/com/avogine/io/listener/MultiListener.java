package com.avogine.io.listener;

import com.avogine.io.event.AvoEvent;

/**
 *
 */
public class MultiListener {

	private InputListener firstListener;
	
	private MultiListener secondListener;
	
	public MultiListener(InputListener firstListener, MultiListener secondListener) {
		this.firstListener = firstListener;
		this.secondListener = secondListener;
	}
	
	public void fireEvent(AvoEvent event) {
		//firstListener.fireEvent(event);
		if (secondListener != null) {
			secondListener.fireEvent(event);
		}
	}
	
}
