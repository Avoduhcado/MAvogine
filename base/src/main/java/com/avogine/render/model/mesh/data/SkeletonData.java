package com.avogine.render.model.mesh.data;

import java.nio.*;

/**
 * @param boneIds
 * @param weights
 */
public record SkeletonData(IntBuffer boneIds, FloatBuffer weights) {
	
}
