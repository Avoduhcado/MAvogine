package com.avogine.io.event;

/**
 * {@link InputEvent} where a Unicode character was typed.
 * <p>
 * This differs from {@link KeyEvent} in that it is intended to handle Unicode text input.
 * @param window The window ID this event was triggered from.
 * @param codepoint The Unicode character produced by this event.
 */
public record CharEvent(long window, int codepoint) implements InputEvent {

}
