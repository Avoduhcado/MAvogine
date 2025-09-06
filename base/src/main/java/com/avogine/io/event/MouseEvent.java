package com.avogine.io.event;

import java.util.concurrent.atomic.AtomicBoolean;

import com.avogine.io.Window;
import com.avogine.io.config.InputConfig;
import com.avogine.io.event.MouseEvent.*;

/**
 *
 */
public sealed interface MouseEvent extends InputEvent, ConsumableEvent permits MouseButtonEvent, MouseMotionEvent, MouseWheelEvent {
	
	/**
	 * @return the X position in screen space of where the event was triggered.
	 */
	public float mouseX();
	
	/**
	 * @return the Y position in screen space of where the event was triggered.
	 */
	public float mouseY();
	
	public sealed interface MouseButtonEvent extends MouseEvent {
		
		/**
		 * @return the button that triggered the event.
		 */
		public int button();
		
		/**
		 * Return the number of times the button was clicked.
		 * <p>
		 * <ul>
		 * <li>1 for all {@link MousePressedEvent}s and {@link MouseReleasedEvent}s.</li> 
		 * <li>2 for some {@link MouseClickedEvent}s where the mouse button was pressed and released in quick succession as determined by {@link InputConfig#doubleClickDelayTolerance()}.</li>
		 * </ul>
		 * @return the number of times the button was clicked.
		 */
		public int clickCount();
	}
	
	/**
	 *
	 * @param window
	 * @param button
	 * @param clickCount
	 * @param mouseX
	 * @param mouseY
	 * @param consumed
	 */
	public record MousePressedEvent(Window window, int button, int clickCount, float mouseX, float mouseY, AtomicBoolean consumed) implements MouseButtonEvent {
		/**
		 * @param window 
		 * @param button 
		 * @param clickCount 
		 * @param mouseX 
		 * @param mouseY 
		 */
		public MousePressedEvent(Window window, int button, int clickCount, float mouseX, float mouseY) {
			this(window, button, clickCount, mouseX, mouseY, new AtomicBoolean());
		}
		
		@Override
		public void consume() {
			consumed.set(true);
		}
	}
	
	/**
	 *
	 * @param window
	 * @param button
	 * @param clickCount
	 * @param mouseX
	 * @param mouseY
	 * @param consumed
	 */
	public record MouseReleasedEvent(Window window, int button, int clickCount, float mouseX, float mouseY, AtomicBoolean consumed) implements MouseButtonEvent {
		/**
		 * @param window 
		 * @param button 
		 * @param clickCount 
		 * @param mouseX 
		 * @param mouseY 
		 */
		public MouseReleasedEvent(Window window, int button, int clickCount, float mouseX, float mouseY) {
			this(window, button, clickCount, mouseX, mouseY, new AtomicBoolean());
		}
		
		@Override
		public void consume() {
			consumed.set(true);
		}
	}
	
	/**
	 *
	 * @param window
	 * @param button
	 * @param clickCount
	 * @param mouseX
	 * @param mouseY
	 */
	public record MouseClickedEvent(Window window, int button, int clickCount, float mouseX, float mouseY) implements MouseButtonEvent {
		
	}

	public sealed interface MouseMotionEvent extends MouseEvent {
		
	}

	/**
	 *
	 * @param window
	 * @param button
	 * @param clickCount 
	 * @param mouseX
	 * @param mouseY
	 * @param consumed
	 */
	public record MouseDraggedEvent(Window window, int button, int clickCount, float mouseX, float mouseY, AtomicBoolean consumed) implements MouseButtonEvent, MouseMotionEvent {
		/**
		 * @param window 
		 * @param button 
		 * @param mouseX 
		 * @param mouseY 
		 */
		public MouseDraggedEvent(Window window, int button, float mouseX, float mouseY) {
			this(window, button, 1, mouseX, mouseY, new AtomicBoolean());
		}
		
		@Override
		public void consume() {
			consumed.set(true);
		}
	}
	
	/**
	 *
	 * @param window
	 * @param mouseX
	 * @param mouseY
	 * @param consumed
	 */
	public record MouseMovedEvent(Window window, float mouseX, float mouseY, AtomicBoolean consumed) implements MouseMotionEvent {
		/**
		 * @param window 
		 * @param mouseX 
		 * @param mouseY 
		 */
		public MouseMovedEvent(Window window, float mouseX, float mouseY) {
			this(window, mouseX, mouseY, new AtomicBoolean());
		}
		
		@Override
		public void consume() {
			consumed.set(true);
		}
	}
	
	/**
	 *
	 * @param window
	 * @param mouseX 
	 * @param mouseY 
	 * @param xOffset
	 * @param yOffset
	 * @param consumed
	 */
	public record MouseWheelEvent(Window window, float mouseX, float mouseY, double xOffset, double yOffset, AtomicBoolean consumed) implements MouseEvent {
		/**
		 * @param window 
		 * @param mouseX 
		 * @param mouseY 
		 * @param xOffset 
		 * @param yOffset 
		 */
		public MouseWheelEvent(Window window, float mouseX, float mouseY, double xOffset, double yOffset) {
			this(window, mouseX, mouseY, xOffset, yOffset, new AtomicBoolean());
		}
		
		@Override
		public void consume() {
			consumed.set(true);
		}
	}
}
