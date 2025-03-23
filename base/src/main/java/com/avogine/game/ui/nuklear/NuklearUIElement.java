package com.avogine.game.ui.nuklear;

import org.lwjgl.nuklear.NkContext;

import com.avogine.game.ui.*;

/**
 *
 */
public abstract class NuklearUIElement implements UIElement<NkContext> {
	
	protected int displayWidth;
	protected int displayHeight;
	
	/**
	 * @param nuklearContext
	 * @param displayWidth
	 * @param displayHeight 
	 */
	protected NuklearUIElement(NuklearGUI nuklearContext, int displayWidth, int displayHeight) {
		this.displayWidth = displayWidth;
		this.displayHeight = displayHeight;
		
		init(nuklearContext.getContext());
		
		nuklearContext.addUIElement(this);
	}
	
	/**
	 * @param context 
	 */
	protected abstract void init(NkContext context);
	
	/**
	 * 
	 */
	public abstract void cleanup();
	
	/**
	 * @return true if this {@link UIElement} should immediately be rendered once initialized, otherwise it will start hidden.
	 */
	public boolean showOnInit() {
		return true;
	}
	
}
