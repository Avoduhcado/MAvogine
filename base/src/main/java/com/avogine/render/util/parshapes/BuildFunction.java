package com.avogine.render.util.parshapes;

import org.lwjgl.util.par.ParShapesMesh;

/**
 * @param <T> 
 *
 */
@FunctionalInterface
public interface BuildFunction<T> {

	/**
	 * @param parShapesMesh
	 * @return
	 */
	public T build(ParShapesMesh parShapesMesh);
	
}
