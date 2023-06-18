package com.avogine.game.ui.nuklear;

import static org.lwjgl.nuklear.Nuklear.*;

import org.lwjgl.nuklear.*;
import org.lwjgl.system.MemoryStack;

import com.avogine.game.Game;
import com.avogine.game.util.*;

/**
 *
 */
public class DebugInfo implements UIElement, Renderable {

	private Game game;
	
	@Override
	public void onRegister(Game game) {
		this.game = game;
	}

	@Override
	public void onRender(SceneState sceneState) {
		var context = game.getGUI().getContext();
		prepare(context, game.getWindow().getId());
	}

	@Override
	public void prepare(NkContext context, long windowId) {
		try (MemoryStack stack = MemoryStack.stackPush()) {
			NkStyle style = context.style(); 
			NkColor background = NkColor.malloc(stack);
			// Set background to fully transparent layer
			background.r((byte) 0).g((byte) 0).b((byte) 0).a((byte) 0);
			style.window().fixed_background().data().color().set(background);
			
			NkRect position = NkRect.calloc(stack).x(0).y(0).w(100).h(100);
			if (nk_begin(context, "DEBUG", position, NK_WINDOW_NO_INPUT | NK_WINDOW_NO_SCROLLBAR | NK_WINDOW_BACKGROUND)) {
				nk_layout_row_dynamic(context, 35, 1);
				nk_label(context, "FPS: " + game.getWindow().getFps(), NK_TEXT_LEFT);
			}
			nk_end(context);
		}
	}

}
