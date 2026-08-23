package com.avogine.game.ui.nuklear;

import org.lwjgl.nuklear.NkContext;

import com.avogine.game.ui.*;

/**
 *
 */
public abstract class NuklearUIElement implements UIElement<NkContext> {
	
	private final NuklearGUI gui;
	
	protected int displayWidth;
	protected int displayHeight;
	
	/**
	 * @param gui
	 * @param displayWidth
	 * @param displayHeight 
	 */
	protected NuklearUIElement(NuklearGUI gui, int displayWidth, int displayHeight) {
		this.gui = gui;
		this.displayWidth = displayWidth;
		this.displayHeight = displayHeight;
		
		init(gui.getContext());
		
		gui.addUIElement(this);
	}
	
	/**
	 * @param context 
	 */
	protected abstract void init(NkContext context);
	
	/**
	 * 
	 */
	public final void cleanup() {
		gui.removeUIElement(this);
		
		onCleanup();
	}
	
	protected abstract void onCleanup();
	
	/**
	 * @return true if this {@link UIElement} should immediately be rendered once initialized, otherwise it will start hidden.
	 */
	public boolean showOnInit() {
		return true;
	}
	
}
