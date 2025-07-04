package com.avogine.entity;

import com.avogine.render.model.animation.*;

/**
 *
 */
public class AnimationData {

	private Animation currentAnimation;
	private int currentFrameIdx;

	/**
	 * @param currentAnimation
	 */
	public AnimationData(Animation currentAnimation) {
		currentFrameIdx = 0;
		this.currentAnimation = currentAnimation;
	}

	/**
	 * @return the currently active {@link Animation}.
	 */
	public Animation getCurrentAnimation() {
		return currentAnimation;
	}

	/**
	 * @return the current {@link AnimatedFrame} of the the currently active {@link Animation}.
	 */
	public AnimatedFrame getCurrentFrame() {
		return currentAnimation.frames().get(currentFrameIdx);
	}

	/**
	 * @return the frame index of the current {@link AnimatedFrame}.
	 */
	public int getCurrentFrameIdx() {
		return currentFrameIdx;
	}

	/**
	 * Increment the current frame index by one, and loop back to the start if the end of the {@link AnimatedFrame} is reached.
	 */
	public void nextFrame() {
		int nextFrame = currentFrameIdx + 1;
		if (nextFrame > currentAnimation.frames().size() - 1) {
			currentFrameIdx = 0;
		} else {
			currentFrameIdx = nextFrame;
		}
	}

	/**
	 * @param currentAnimation
	 */
	public void setCurrentAnimation(Animation currentAnimation) {
		currentFrameIdx = 0;
		this.currentAnimation = currentAnimation;
	}

}
