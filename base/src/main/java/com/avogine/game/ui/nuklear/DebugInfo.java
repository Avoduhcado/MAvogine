package com.avogine.game.ui.nuklear;

import static org.lwjgl.nuklear.Nuklear.*;

import org.lwjgl.nuklear.*;
import org.lwjgl.system.MemoryStack;

import com.avogine.game.HotGame;
import com.avogine.game.util.*;
import com.avogine.io.Window;

/**
 *
 */
public class DebugInfo implements UIElement, Renderable {

	private NuklearUI gui;
	
	@Override
	public void onRegister(HotGame game) {
		gui = game.getGUI();
	}

	@Override
	public void onRender(Window window, SceneState sceneState) {
		if (gui != null) {
			prepare(gui.getContext(), window);
		}
	}

	@Override
	public void prepare(NkContext context, Window window) {
		try (MemoryStack stack = MemoryStack.stackPush()) {
			NkStyle style = context.style(); 
			NkColor background = NkColor.malloc(stack);
			// Set background to fully transparent layer
			background.r((byte) 0).g((byte) 0).b((byte) 0).a((byte) 0);
			style.window().fixed_background().data().color().set(background);
			
			NkRect position = NkRect.calloc(stack).x(0).y(0).w(75).h(30);
			if (nk_begin(context, "DEBUG", position, NK_WINDOW_NO_INPUT | NK_WINDOW_NO_SCROLLBAR | NK_WINDOW_BACKGROUND)) {
				nk_layout_row_dynamic(context, 0, 1);
				nk_label(context, "FPS: " + window.getFps(), NK_TEXT_ALIGN_LEFT | NK_TEXT_ALIGN_TOP);
			}
			nk_end(context);
		}
	}

}
