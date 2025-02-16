package com.avogine.io.event;

import com.avogine.io.event.MouseEvent.*;

/**
 *
 */
public sealed interface MouseEvent extends InputEvent permits MouseButtonEvent, MouseMotionEvent, MouseWheelEvent {
	
	public sealed interface MouseButtonEvent extends MouseEvent {
		
		/**
		 * @return the button that triggered the event.
		 */
		public int button();
		
		/**
		 * Return the number of times the button was clicked.
		 * <p>
		 * 1 for all {@link MousePressedEvent}s and {@link MouseReleasedEvent}s. 
		 * 2 for some {@link MouseClickedEvent}s where the mouse button was pressed and released in quick succession.
		 * @return the number of times the button was clicked.
		 */
		public int clickCount();
		
		/**
		 * @return the X position in screen space of where the event was triggered.
		 */
		public float mouseX();
		
		/**
		 * @return the Y position in screen space of where the event was triggered.
		 */
		public float mouseY();
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
	public record MousePressedEvent(long window, int button, int clickCount, float mouseX, float mouseY, boolean consumed) implements MouseButtonEvent, ConsumableEvent<MousePressedEvent> {
		/**
		 * @param window 
		 * @param button 
		 * @param clickCount 
		 * @param mouseX 
		 * @param mouseY 
		 */
		public MousePressedEvent(long window, int button, int clickCount, float mouseX, float mouseY) {
			this(window, button, clickCount, mouseX, mouseY, false);
		}
		
		@Override
		public MousePressedEvent withConsume() {
			return new MousePressedEvent(window, button, clickCount, mouseX, mouseY, true);
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
	public record MouseReleasedEvent(long window, int button, int clickCount, float mouseX, float mouseY, boolean consumed) implements MouseButtonEvent, ConsumableEvent<MouseReleasedEvent> {
		/**
		 * @param window 
		 * @param button 
		 * @param clickCount 
		 * @param mouseX 
		 * @param mouseY 
		 */
		public MouseReleasedEvent(long window, int button, int clickCount, float mouseX, float mouseY) {
			this(window, button, clickCount, mouseX, mouseY, false);
		}
		
		@Override
		public MouseReleasedEvent withConsume() {
			return new MouseReleasedEvent(window, button, clickCount, mouseX, mouseY, true);
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
	public record MouseClickedEvent(long window, int button, int clickCount, float mouseX, float mouseY) implements MouseButtonEvent {
		
	}

	public sealed interface MouseMotionEvent extends MouseEvent {
		
	}

	/**
	 *
	 * @param window
	 * @param button
	 * @param mouseX
	 * @param mouseY
	 * @param consumed
	 */
	public record MouseDraggedEvent(long window, int button, float mouseX, float mouseY, boolean consumed) implements MouseMotionEvent, ConsumableEvent<MouseDraggedEvent> {
		/**
		 * @param window 
		 * @param button 
		 * @param mouseX 
		 * @param mouseY 
		 */
		public MouseDraggedEvent(long window, int button, float mouseX, float mouseY) {
			this(window, button, mouseX, mouseY, false);
		}
		
		@Override
		public MouseDraggedEvent withConsume() {
			return new MouseDraggedEvent(window, button, mouseX, mouseY, true);
		}
	}
	
	/**
	 *
	 * @param window
	 * @param mouseX
	 * @param mouseY
	 * @param consumed
	 */
	public record MouseMovedEvent(long window, float mouseX, float mouseY, boolean consumed) implements MouseMotionEvent, ConsumableEvent<MouseMovedEvent> {
		/**
		 * @param window 
		 * @param mouseX 
		 * @param mouseY 
		 */
		public MouseMovedEvent(long window, float mouseX, float mouseY) {
			this(window, mouseX, mouseY, false);
		}
		
		@Override
		public MouseMovedEvent withConsume() {
			return new MouseMovedEvent(window, mouseX, mouseY, true);
		}
	}
	
	/**
	 *
	 * @param window
	 * @param xOffset
	 * @param yOffset
	 * @param consumed
	 */
	public record MouseWheelEvent(long window, double xOffset, double yOffset, boolean consumed) implements MouseEvent, ConsumableEvent<MouseWheelEvent> {
		/**
		 * @param window 
		 * @param xOffset 
		 * @param yOffset 
		 */
		public MouseWheelEvent(long window, double xOffset, double yOffset) {
			this(window, xOffset, yOffset, false);
		}
		
		@Override
		public MouseWheelEvent withConsume() {
			return new MouseWheelEvent(window, xOffset, yOffset, true);
		}
	}
}
