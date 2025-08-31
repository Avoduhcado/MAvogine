package com.avogine.render.util;

import org.lwjgl.util.par.ParShapesMesh;

/**
 * @param <T> 
 */
public interface InstancedBuildFunction<T> {

	/**
	 * @param parShapesMesh
	 * @param instanceCount
	 * @return a built instance of {@code T} from the given {@link ParShapesMesh} structure.
	 */
	public T build(ParShapesMesh parShapesMesh, int instanceCount);
	
}
