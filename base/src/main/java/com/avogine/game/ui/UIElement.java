package com.avogine.game.ui;

import com.avogine.io.Window;

/**
 * @param <T> 
 */
public interface UIElement<T> {

	/**
	 * @param context
	 * @param window 
	 */
	public void layout(T context, Window window);
	
}
