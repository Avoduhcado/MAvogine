package com.avogine.io;

import java.nio.DoubleBuffer;
import java.util.*;

import org.joml.Vector2f;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.system.MemoryStack;

import com.avogine.io.config.InputConfig;

/**
 *
 */
public class Mouse {
	/**
	 *
	 * @param position
	 * @param time
	 */
	public record ButtonClick(Vector2f position, double time) {
		
	}
	
	private final boolean[] buttons;
	
	private final Vector2f position;
	private final Map<Integer, List<ButtonClick>> buttonClicks;
	private float clickDriftTolerance;
	private float doubleClickDelayTolerance;
	
	private final Vector2f delta;
	
	private final Vector2f scroll;
	
	private boolean inWindow;
	private boolean captured;
	
	/**
	 * 
	 */
	public Mouse() {
		buttons = new boolean[GLFW.GLFW_MOUSE_BUTTON_LAST];
		Arrays.fill(buttons, false);
		
		position = new Vector2f();
		buttonClicks = new HashMap<>();
		for (int i = GLFW.GLFW_MOUSE_BUTTON_1; i < GLFW.GLFW_MOUSE_BUTTON_LAST; i++) {
			buttonClicks.put(i, new ArrayList<>());
		}
		delta = new Vector2f();
		
		scroll = new Vector2f();
	}
	
	/**
	 * Initialize mouse.
	 * </p>
	 * This should only be called from the main thread.
	 * @param inputConfig
	 * @param windowHandle 
	 */
	public void init(InputConfig inputConfig, long windowHandle) {
		setClickDriftTolerance(inputConfig.clickDriftTolerance());
		setDoubleClickDelayTolerance(inputConfig.doubleClickDelayTolerance());
		
		try (MemoryStack stack = MemoryStack.stackPush()) {
			DoubleBuffer xPos = stack.mallocDouble(1);
			DoubleBuffer yPos = stack.mallocDouble(1);
			
			GLFW.glfwGetCursorPos(windowHandle, xPos, yPos);
			
			setPosition(xPos.get(0), yPos.get(0));
		}
	}
	
	/**
	 * Reset frame to frame statistics like motion or scrolling.
	 */
	public void newFrame() {
		delta.zero();
		scroll.zero();
	}
	
	/**
	 * @return the buttons
	 */
	public boolean[] getButtons() {
		return buttons;
	}
	
	/**
	 * @param button
	 * @return true if the button is currently being pressed.
	 */
	public boolean isButtonDown(int button) {
		return buttons[button];
	}
	
	/**
	 * @param button
	 * @param pressed
	 */
	public void setButton(int button, boolean pressed) {
		buttons[button] = pressed;
		
		List<ButtonClick> clicks = buttonClicks.get(button);
		double time = GLFW.glfwGetTime();
		if (pressed) {
			if (!clicks.isEmpty() && (clicks.getLast().position.distance(position) > clickDriftTolerance || time - clicks.getLast().time > doubleClickDelayTolerance)) {
				buttonClicks.get(button).clear();
			}
			buttonClicks.get(button).add(new ButtonClick(new Vector2f(position), time));
		} else {
			if (clicks.getLast().position.distance(position) > clickDriftTolerance) {
				buttonClicks.get(button).clear();
			}
		}
	}
	
	/**
	 * @return the buttonClicks
	 */
	public Map<Integer, List<ButtonClick>> getButtonClicks() {
		return buttonClicks;
	}
	
	/**
	 * @param button
	 * @return the number of consecutive clicks performed with little delay
	 */
	public int getButtonClickCount(int button) {
		return buttonClicks.get(button).size();
	}
	
	/**
	 * @return the clickDriftTolerance
	 */
	public float getClickDriftTolerance() {
		return clickDriftTolerance;
	}
	
	/**
	 * @param clickDriftTolerance the clickDriftTolerance to set
	 */
	public void setClickDriftTolerance(float clickDriftTolerance) {
		this.clickDriftTolerance = clickDriftTolerance;
	}
	
	/**
	 * @return the doubleClickDelayTolerance
	 */
	public float getDoubleClickDelayTolerance() {
		return doubleClickDelayTolerance;
	}
	
	/**
	 * @param doubleClickDelayTolerance the doubleClickDelayTolerance to set
	 */
	public void setDoubleClickDelayTolerance(float doubleClickDelayTolerance) {
		this.doubleClickDelayTolerance = doubleClickDelayTolerance;
	}
	
	/**
	 * @return the position
	 */
	public Vector2f getPosition() {
		return position;
	}
	
	/**
	 * @param x
	 * @param y
	 * @param withMovement
	 */
	public void setPosition(double x, double y, boolean withMovement) {
		if (withMovement) {
			position.sub((float) x, (float) y, delta).negate();
		} else {
			delta.zero();
		}
		position.set(x, y);
	}
	
	/**
	 * @param x
	 * @param y
	 */
	public void setPosition(double x, double y) {
		setPosition(x, y, true);
	}
	
	/**
	 * Return true if the cursor position has changed.
	 * </p>
	 * This will only be valid for the duration of the frame that the movement occurred.
	 * @return true if the cursor position has changed.
	 */
	public boolean isMoved() {
		return delta.length() != 0;
	}
	
	/**
	 * @return the delta
	 */
	public Vector2f getDelta() {
		return delta;
	}
	
	/**
	 * @param xOffset
	 * @param yOffset
	 */
	public void setDelta(double xOffset, double yOffset) {
		delta.set(xOffset, yOffset);
	}
	
	/**
	 * Return true if the scroll wheel has been updated.
	 * </p>
	 * This will only be valid for the duration of the frame that the scroll occurred.
	 * @return true if the scroll wheel has been updated.
	 */
	public boolean isScrolled() {
		return scroll.length() != 0f;
	}
	
	/**
	 * @return the scroll
	 */
	public Vector2f getScroll() {
		return scroll;
	}
	
	/**
	 * @param xOffset
	 * @param yOffset
	 */
	public void setScroll(double xOffset, double yOffset) {
		scroll.set(xOffset, yOffset);
	}
	
	/**
	 * @return the inWindow
	 */
	public boolean isInWindow() {
		return inWindow;
	}
	
	/**
	 * @param inWindow the inWindow to set
	 */
	public void setInWindow(boolean inWindow) {
		this.inWindow = inWindow;
	}
	
	/**
	 * @return the captured
	 */
	public boolean isCaptured() {
		return captured;
	}
	
	/**
	 * @param captured the captured to set
	 */
	public void setCaptured(boolean captured) {
		this.captured = captured;
	}
	
}
