package com.avogine.io.event;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 *
 */
public record CharEvent(int codepoint, long window, AtomicBoolean consumed) implements Event {
	
	public CharEvent(int codepoint, long window) {
		this(codepoint, window, new AtomicBoolean(false));
	}

	@Override
	public void consume() {
		consumed.set(true);
	}

	@Override
	public boolean isConsumed() {
		return consumed.get();
	}

}
