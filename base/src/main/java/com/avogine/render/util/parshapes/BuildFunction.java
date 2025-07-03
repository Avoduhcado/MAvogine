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
	 * @return a built instance of {@code T} from the given {@link ParShapesMesh} structure.
	 */
	public T build(ParShapesMesh parShapesMesh);
	
}
