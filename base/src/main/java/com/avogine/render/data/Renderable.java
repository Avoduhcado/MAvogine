package com.avogine.render.data;

import java.util.*;
import java.util.function.*;
import java.util.stream.*;

/**
 *
 */
public interface Renderable {

	/**
	 * 
	 */
	public void bind();
	
	/**
	 * 
	 */
	public void unbind();
	
	/**
	 * 
	 */
	public void render();
	
	/**
	 * @param <T>
	 * @param entities
	 * @param action
	 */
	public <T> void renderBatch(Collection<T> entities, Consumer<T> action);

	/**
	 * @param <T>
	 * @param entities
	 * @param action
	 */
	public <T> void renderBatch(Stream<T> entities, Consumer<T> action);

	/**
	 * 
	 */
	public void cleanup();
}
