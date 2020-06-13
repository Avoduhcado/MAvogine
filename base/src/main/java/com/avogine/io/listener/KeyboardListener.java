package com.avogine.io.listener;

import com.avogine.io.event.KeyboardEvent;

public interface KeyboardListener extends InputListener {

	public void keyPressed(KeyboardEvent event);
	
}
