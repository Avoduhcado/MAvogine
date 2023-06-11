package com.avogine.io.listener;

import com.avogine.io.event.CharEvent;

/**
 *
 */
public interface CharListener extends InputListener {

	public void charInput(CharEvent event);
	
}
