package com.avogine.game.ui.nuklear;

import static org.lwjgl.nuklear.Nuklear.*;

import java.nio.ByteBuffer;

import org.lwjgl.nuklear.*;
import org.lwjgl.system.MemoryStack;

/**
 *
 */
public class MenuTheme extends NuklearTheme {

	@Override
	public void initializeTheme(NkContext context) {
		try (MemoryStack stack = MemoryStack.stackPush()) {
			// The list of colors we want to use
			NkColor white = createColor(stack, 255, 255, 255, 255);
			NkColor black = createColor(stack, 0, 0, 0, 255);
			NkColor grey01 = createColor(stack, 45, 45, 45, 255);
			NkColor grey02 = createColor(stack, 70, 70, 70, 255);
			NkColor grey03 = createColor(stack, 120, 120, 120, 255);
			NkColor grey04 = createColor(stack, 140, 140, 140, 255);
			NkColor grey05 = createColor(stack, 150, 150, 150, 255);
			NkColor grey06 = createColor(stack, 160, 160, 160, 255);
			NkColor grey07 = createColor(stack, 170, 170, 170, 255);
			NkColor grey08 = createColor(stack, 180, 180, 180, 255);
			NkColor grey09 = createColor(stack, 185, 185, 185, 255);
			NkColor grey10 = createColor(stack, 190, 190, 190, 255);
			NkColor grey11 = createColor(stack, 200, 200, 200, 255);
			NkColor grey12 = createColor(stack, 240, 240, 240, 255);
			NkColor blue1 = createColor(stack, 80, 80, 200, 255);
			NkColor blue2 = createColor(stack, 128, 196, 255, 255);
			NkColor blue3 = createColor(stack, 64, 196, 255, 255);
			NkColor red = createColor(stack, 255, 0, 0, 255);

			// This buffer acts like an array of NkColor structs
			int size = NkColor.SIZEOF * NK_COLOR_COUNT; // How much memory we need to store all the color data
			ByteBuffer buffer = stack.calloc(size);
			NkColor.Buffer colors = new NkColor.Buffer(buffer);
			colors.put(NK_COLOR_TEXT, black);
			colors.put(NK_COLOR_WINDOW, grey11);
			colors.put(NK_COLOR_HEADER, blue1);
			colors.put(NK_COLOR_BORDER, black);
			colors.put(NK_COLOR_BUTTON, grey09);
			colors.put(NK_COLOR_BUTTON_HOVER, grey07);
			colors.put(NK_COLOR_BUTTON_ACTIVE, grey06);
			colors.put(NK_COLOR_TOGGLE, grey05);
			colors.put(NK_COLOR_TOGGLE_HOVER, grey03);
			colors.put(NK_COLOR_TOGGLE_CURSOR, grey10);
			colors.put(NK_COLOR_SELECT, grey06);
			colors.put(NK_COLOR_SELECT_ACTIVE, white);
			colors.put(NK_COLOR_SLIDER, grey12);
			colors.put(NK_COLOR_SLIDER_CURSOR, blue2);
			colors.put(NK_COLOR_SLIDER_CURSOR_HOVER, blue3);
			colors.put(NK_COLOR_SLIDER_CURSOR_ACTIVE, blue2);
			colors.put(NK_COLOR_PROPERTY, grey10);
			colors.put(NK_COLOR_EDIT, grey05);
			colors.put(NK_COLOR_EDIT_CURSOR, black);
			colors.put(NK_COLOR_COMBO, grey10);
			colors.put(NK_COLOR_CHART, grey06);
			colors.put(NK_COLOR_CHART_COLOR, grey01);
			colors.put(NK_COLOR_CHART_COLOR_HIGHLIGHT, red);
			colors.put(NK_COLOR_SCROLLBAR, grey08);
			colors.put(NK_COLOR_SCROLLBAR_CURSOR, grey04);
			colors.put(NK_COLOR_SCROLLBAR_CURSOR_HOVER, grey05);
			colors.put(NK_COLOR_SCROLLBAR_CURSOR_ACTIVE, grey06);
			colors.put(NK_COLOR_TAB_HEADER, grey08);
			nk_style_from_table(context, colors);
			
			// Unused, to avoid needing to re-allocate this anywhere a button, or god forbid different widget, would need to be disabled
			// it may be preferable to just not display options if they're unavailable. Not great but works for now.
			NkStyleItem disabledStyleItem = nk_style_item_color(grey01, NkStyleItem.malloc(stack));
			NkStyleButton disabledStyleButton = NkStyleButton.malloc(stack);
			disabledStyleButton.normal(disabledStyleItem);
			disabledStyleButton.active(disabledStyleItem);
			disabledStyleButton.hover(disabledStyleItem);
			disabledStyleButton.border_color(grey02);
			disabledStyleButton.text_background(grey02);
			disabledStyleButton.text_normal(grey02);
			disabledStyleButton.text_active(grey02);
			disabledStyleButton.text_hover(grey02);
		}
	}

}
