package com.avogine.render.loader.parshapes;

import org.lwjgl.util.par.ParShapesMesh;

/**
 * @param <T> 
 *
 */
public interface BuildFunction<T> {

	public T build(ParShapesMesh parShapesMesh);
	
}
