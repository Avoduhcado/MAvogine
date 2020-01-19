package com.avogine.core.system.listener;

import com.avogine.core.system.event.MouseClickEvent;

public interface MouseClickListener extends InputListener {

	public void mouseClicked(MouseClickEvent event);
	
}
