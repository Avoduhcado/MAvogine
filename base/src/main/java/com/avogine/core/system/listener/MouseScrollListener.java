package com.avogine.core.system.listener;

import com.avogine.core.system.event.MouseScrollEvent;

public interface MouseScrollListener extends InputListener {

	public void mouseScrolled(MouseScrollEvent event);
	
}
