package com.avogine.game.ui.nuklear;

import static org.lwjgl.nuklear.Nuklear.*;

import org.lwjgl.nuklear.*;

/**
 *
 */
public class Sidebar {
	private final NkContext context;
	private final String name;
	private final NkRect rect;
	private boolean alignToRight;
	private int width;

	public Sidebar(NkContext context, String name, int width, boolean alignToRight) {
		this.context = context;
		this.name = name;
		this.alignToRight = alignToRight;
		this.width = width;
		rect = NkRect.create().x(0).y(0).w(width);
	}

	public void begin(int screenWidth, int screenHeight, int windowOptions) {
		// Set the height of the rectangle to the height of the screen
		rect.h(screenHeight);
		// If we want to align it to the right, set the X coordinate to the
		// screen width minus the width of the sidebar
		if(alignToRight) {
			rect.x(screenWidth - width);
		}
		// Begin drawing the sidebar
		nk_begin(context, name, rect, windowOptions);
	}
	
	public void end() {
		// Finish drawing the sidebar
		nk_end(context);
	}
}