package com.avogine.io.event;

import com.avogine.io.Window;

/**
 * {@link InputEvent} where a Unicode character was typed.
 * <p>
 * This differs from {@link KeyEvent} in that it is intended to handle Unicode text input.
 * @param window The window this event was triggered from.
 * @param codepoint The Unicode character produced by this event.
 */
public record CharEvent(Window window, int codepoint) implements InputEvent {

}
