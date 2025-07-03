package com.avogine.entity;

import java.util.Arrays;

import org.joml.Matrix4f;

import com.avogine.render.model.animation.*;
import com.avogine.render.util.assimp.AssimpModelLoader;

/**
 *
 */
public class AnimationData {

	public static final Matrix4f[] DEFAULT_BONES_MATRICES = new Matrix4f[AssimpModelLoader.MAX_BONES];

	static {
		Matrix4f zeroMatrix = new Matrix4f().zero();
		Arrays.fill(DEFAULT_BONES_MATRICES, zeroMatrix);
	}

	private Animation currentAnimation;
	private int currentFrameIdx;

	public AnimationData(Animation currentAnimation) {
		currentFrameIdx = 0;
		this.currentAnimation = currentAnimation;
	}

	public Animation getCurrentAnimation() {
		return currentAnimation;
	}

	public AnimatedFrame getCurrentFrame() {
		return currentAnimation.frames().get(currentFrameIdx);
	}

	public int getCurrentFrameIdx() {
		return currentFrameIdx;
	}

	public void nextFrame() {
		int nextFrame = currentFrameIdx + 1;
		if (nextFrame > currentAnimation.frames().size() - 1) {
			currentFrameIdx = 0;
		} else {
			currentFrameIdx = nextFrame;
		}
	}

	public void setCurrentAnimation(Animation currentAnimation) {
		currentFrameIdx = 0;
		this.currentAnimation = currentAnimation;
	}

}
