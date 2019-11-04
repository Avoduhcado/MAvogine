package com.avogine.core.system.listener;

import com.avogine.core.system.event.KeyboardEvent;

public interface KeyboardListener extends InputListener {

	public void keyPressed(KeyboardEvent event);
	
}
