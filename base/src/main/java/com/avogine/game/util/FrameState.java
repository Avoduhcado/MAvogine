package com.avogine.game.util;

/**
 * Simple immutable object containing relevant data about the current frame being processed.
 * @param delta The amount of time that has passed since the last frame was updated in fractions of a second.
 */
public record FrameState(float delta) {
	
}
