package com.avogine.game.ui.nuklear;

import org.lwjgl.nuklear.*;
import org.lwjgl.system.MemoryStack;

/**
 *
 */
public abstract class NuklearTheme {

	/**
	 * @param context
	 */
	public abstract void initializeTheme(NkContext context);
	
	/**
	 * Allocate a new {@link NkColor}.
	 * @param stack
	 * @param r
	 * @param g
	 * @param b
	 * @param a
	 * @return a {@link NkColor} allocated on the given stack.
	 */
	protected NkColor createColor(MemoryStack stack, int r, int g, int b, int a) {
		return NkColor.malloc(stack).set((byte) r, (byte) g, (byte) b, (byte) a);
	}
	
}
