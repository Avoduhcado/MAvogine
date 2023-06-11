package com.avogine.game.ui.nuklear;

import org.lwjgl.nuklear.NkContext;

/**
 * TODO Further subclass this into a NuklearUIElement for prepare to take in specifically a NkContext
 */
public interface UIElement {

	/**
	 * TODO Maybe pass in the window so elements can dynamically size/translate themselves
	 * Might also be worth making this private and forcing implementation of Renderable
	 * @param context
	 * @param windowId 
	 */
	public void prepare(NkContext context, long windowId);
	
	/**
	 * @return whether this UI should immediately be rendered once initialized, otherwise it will start hidden.
	 */
	public default boolean showOnInit() {
		return true;
	}
	
}
