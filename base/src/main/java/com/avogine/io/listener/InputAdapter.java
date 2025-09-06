package com.avogine.io.listener;

import com.avogine.io.event.*;
import com.avogine.io.event.MouseEvent.*;

/**
 *
 */
public abstract class InputAdapter implements KeyListener, CharListener, MouseButtonListener, MouseMotionListener, MouseWheelListener {

	/**
	 * 
	 */
	protected InputAdapter() {
		
	}
	
	@Override
	public void keyPressed(KeyEvent event) {
		
	}
	
	@Override
	public void keyReleased(KeyEvent event) {
		
	}
	
	@Override
	public void charTyped(CharEvent event) {
		
	}
	
	@Override
	public void mouseClicked(MouseButtonEvent event) {
		
	}
	
	@Override
	public void mousePressed(MouseButtonEvent event) {
		
	}
	
	@Override
	public void mouseReleased(MouseButtonEvent event) {
		
	}
	
	@Override
	public void mouseDragged(MouseDraggedEvent event) {
		
	}
	
	@Override
	public void mouseMoved(MouseMotionEvent event) {
		
	}
	
	@Override
	public void mouseWheelMoved(MouseWheelEvent event) {
		
	}
	
}
