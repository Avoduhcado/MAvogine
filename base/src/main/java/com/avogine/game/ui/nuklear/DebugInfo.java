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
			NkColor transparent = NkColor.calloc(stack).set((byte) 0, (byte) 0, (byte) 0, (byte) 0);
			nk_style_push_color(context, context.style().window().fixed_background().data().color(), transparent);
			
			NkRect position = NkRect.calloc(stack).x(0).y(0).w(100).h(100);
			if (nk_begin(context, "DEBUG", position, NK_WINDOW_NO_INPUT | NK_WINDOW_NO_SCROLLBAR | NK_WINDOW_BACKGROUND)) {
				nk_layout_row_dynamic(context, 35, 1);
				nk_label(context, "FPS: " + game.getWindow().getFps(), NK_TEXT_LEFT);
			}
			nk_end(context);
			
			nk_style_pop_color(context);
		}
	}

}
