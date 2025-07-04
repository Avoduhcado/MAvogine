package com.avogine.render.model.animation;

import java.util.Arrays;

import org.joml.Matrix4f;

/**
 * @param boneMatrices 
 */
public record AnimatedFrame(Matrix4f[] boneMatrices) {
	
	@Override
	public String toString() {
		return "AnimatedFrame [boneMatrices=" + Arrays.toString(boneMatrices) + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Arrays.hashCode(boneMatrices);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!(obj instanceof AnimatedFrame))
			return false;
		AnimatedFrame other = (AnimatedFrame) obj;
		return Arrays.equals(boneMatrices, other.boneMatrices);
	}

}
