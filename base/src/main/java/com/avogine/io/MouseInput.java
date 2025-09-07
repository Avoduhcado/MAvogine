package com.avogine.io;

import java.util.*;

import org.lwjgl.glfw.GLFW;

import com.avogine.io.config.InputConfig;
import com.avogine.io.event.MouseEvent.*;
import com.avogine.io.listener.*;

/**
 *
 */
public class MouseInput {
	
	private final Mouse mouse;
	
	private final Set<InputListener> listeners;
	
	/**
	 * 
	 */
	public MouseInput() {
		mouse = new Mouse();
		
		listeners = new HashSet<>();
	}
	
	/**
	 * @param inputConfig 
	 * @param window
	 */
	public void init(InputConfig inputConfig, Window window) {
		mouse.init(inputConfig, window.getId());
		
		configureMouseButtonCallback(window);
		configureCursorPosCallback(window);
		configureScrollCallback(window);
	}
	
	/**
	 * @return the mouse
	 */
	public Mouse getMouse() {
		return mouse;
	}
	
	/**
	 * Register an {@link InputListener} to this {@link Input} and store
	 * a reference to it for potential de-registering later.
	 * @param l The {@code InputListener} to add.
	 * @return the {@code InputListener}.
	 */
	public InputListener addInputListener(InputListener l) {
		listeners.add(l);
		return l;
	}
	
	/**
	 * @param listener
	 * @return true if the set contained listener.
	 */
	public boolean removeInputListener(InputListener listener) {
		return listeners.remove(listener);
	}
	
	private void configureMouseButtonCallback(Window registeredWindow) {
		GLFW.glfwSetMouseButtonCallback(registeredWindow.getId(), (_, button, action, _) -> {
			mouse.setButton(button, action == GLFW.GLFW_PRESS);
			int clickCount = mouse.getButtonClickCount(button);
			
			switch (action) {
				case GLFW.GLFW_PRESS -> fireMouseButtonEvent(new MousePressedEvent(registeredWindow, button, clickCount, mouse.getPosition().x(), mouse.getPosition().y()));
				case GLFW.GLFW_RELEASE -> {
					fireMouseButtonEvent(new MouseReleasedEvent(registeredWindow, button, clickCount, mouse.getPosition().x(), mouse.getPosition().y()));
					
					if (clickCount > 0) {
						fireMouseButtonEvent(new MouseClickedEvent(registeredWindow, button, clickCount, mouse.getPosition().x(), mouse.getPosition().y()));
					}
				}
				default -> throw new IllegalArgumentException("Cannot process mouse button callback event for action: " + action);
			}
		});
	}
	
	private void configureCursorPosCallback(Window registeredWindow) {
		GLFW.glfwSetCursorPosCallback(registeredWindow.getId(), (_, xPos, yPos) -> {
			mouse.setPosition(xPos, yPos);
			
			for (int i = GLFW.GLFW_MOUSE_BUTTON_1; i < GLFW.GLFW_MOUSE_BUTTON_LAST; i++) {
				if (GLFW.glfwGetMouseButton(registeredWindow.getId(), i) == GLFW.GLFW_PRESS) {
					fireMouseMotionEvent(new MouseDraggedEvent(registeredWindow, i, (float) xPos, (float) yPos));
				}
			}
			fireMouseMotionEvent(new MouseMovedEvent(registeredWindow, (float) xPos, (float) yPos));
		});
	}
	
	private void configureScrollCallback(Window registeredWindow) {
		GLFW.glfwSetScrollCallback(registeredWindow.getId(), (_, xOffset, yOffset) -> {
			mouse.setScroll(xOffset, yOffset);
			
			fireMouseScrollEvent(new MouseWheelEvent(registeredWindow, mouse.getPosition().x(), mouse.getPosition().y(), xOffset, yOffset));
		});
	}
	
	private void fireMouseButtonEvent(MouseButtonEvent event) {
		for (var listener : listeners) {
			if (listener instanceof MouseButtonListener mbl && !event.isConsumed()) {
				switch (event) {
					case MousePressedEvent e -> mbl.mousePressed(e);
					case MouseReleasedEvent e -> mbl.mouseReleased(e);
					case MouseClickedEvent e -> mbl.mouseClicked(e);
					case MouseDraggedEvent _ -> {
						// Drag events should be handled by MouseMotionListeners
					}
				}
			}
		}
	}
	
	private void fireMouseMotionEvent(MouseMotionEvent event) {
		for (var listener : listeners) {
			if (listener instanceof MouseMotionListener mml && !event.isConsumed()) {
				switch (event) {
					case MouseDraggedEvent e -> mml.mouseDragged(e);
					case MouseMovedEvent e -> mml.mouseMoved(e);
				}
			}
		}
	}
	
	private void fireMouseScrollEvent(MouseWheelEvent event) {
		for (var listener : listeners) {
			if (listener instanceof MouseWheelListener mwl && !event.isConsumed()) {
				mwl.mouseWheelMoved(event);
			}
		}
	}
}
