package com.avogine.game.ui.nuklear;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.nuklear.Nuklear.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL12.*;
import static org.lwjgl.stb.STBTruetype.*;
import static org.lwjgl.system.MemoryStack.*;
import static org.lwjgl.system.MemoryUtil.*;

import java.nio.*;

import org.lwjgl.nuklear.*;
import org.lwjgl.stb.*;
import org.lwjgl.system.*;

import com.avogine.io.*;
import com.avogine.io.event.*;
import com.avogine.io.listener.*;
import com.avogine.util.resource.*;

/**
 * Wrapper implementation for {@link Nuklear}.
 * 
 * Initialize an instance of this class once to gain access to a {@link NkContext} so that you can render Nuklear elements.
 * Handlers for Input commands are automatically configured to translate to {@code nk_input_*} commands.
 */
public class AvoNuklear {

	/**
	 * Nuklear Allocator struct for managing all Nuklear related memory allocations.
	 */
	public static final NkAllocator ALLOCATOR = NkAllocator.create()
			.alloc((handle, old, size) -> nmemAllocChecked(size))
			.mfree((handle, ptr) -> nmemFree(ptr));
	
	// Storage for font data
	private final ByteBuffer ttf;
	
	private NkContext context;
	private NkUserFont defaultFont;
	
	/**
	 * 
	 */
	public AvoNuklear() {
		ttf = ResourceFileReader.ioResourceToByteBuffer("/demo/alagard.ttf", 512 * 1024);
		// Create a Nuklear context, it is used everywhere.
		context = NkContext.create();
		// This is the Nuklear font object used for rendering text.
		defaultFont = NkUserFont.create();
	}
	
	/**
	 * @param window
	 */
	public void init(Window window) {
		nk_init(context, ALLOCATOR, null);
		initFont();
		
		window.getInput().add(new NuklearKeyboardHandler());
		window.getInput().add(new NuklearScrollHandler());
		window.getInput().add(new NuklearMouseHandler());
	}
	
	private void initFont() {
		final int BITMAP_W = 1024;
		final int BITMAP_H = 1024;

		final int FONT_HEIGHT = 18;
		int fontTexID = glGenTextures();

		STBTTFontinfo fontInfo = STBTTFontinfo.create();
		STBTTPackedchar.Buffer cdata = STBTTPackedchar.create(95);

		float scale;
		float descent;

		try (MemoryStack stack = stackPush()) {
			stbtt_InitFont(fontInfo, ttf);
			scale = stbtt_ScaleForPixelHeight(fontInfo, FONT_HEIGHT);

			IntBuffer d = stack.mallocInt(1);
			stbtt_GetFontVMetrics(fontInfo, null, d, null);
			descent = d.get(0) * scale;

			ByteBuffer bitmap = memAlloc(BITMAP_W * BITMAP_H);

			STBTTPackContext pc = STBTTPackContext.malloc(stack);
			stbtt_PackBegin(pc, bitmap, BITMAP_W, BITMAP_H, 0, 1, NULL);
			stbtt_PackSetOversampling(pc, 4, 4);
			stbtt_PackFontRange(pc, ttf, 0, FONT_HEIGHT, 32, cdata);
			stbtt_PackEnd(pc);

			// Convert R8 to RGBA8
			ByteBuffer texture = memAlloc(BITMAP_W * BITMAP_H * 4);
			for (int i = 0; i < bitmap.capacity(); i++) {
				texture.putInt((bitmap.get(i) << 24) | 0x00FFFFFF);
			}
			texture.flip();

			glBindTexture(GL_TEXTURE_2D, fontTexID);
			glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA8, BITMAP_W, BITMAP_H, 0, GL_RGBA, GL_UNSIGNED_INT_8_8_8_8_REV, texture);
			glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
			glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);

			memFree(texture);
			memFree(bitmap);
		}

		defaultFont
		.width((handle, h, text, len) -> {
			float textWidth = 0;
			try (MemoryStack stack = stackPush()) {
				IntBuffer unicode = stack.mallocInt(1);

				int glyphLength = nnk_utf_decode(text, memAddress(unicode), len);
				int textLength = glyphLength;

				if (glyphLength == 0) {
					return 0;
				}

				IntBuffer advance = stack.mallocInt(1);
				while (textLength <= len && glyphLength != 0) {
					if (unicode.get(0) == NK_UTF_INVALID) {
						break;
					}

					/* query currently drawn glyph information */
					stbtt_GetCodepointHMetrics(fontInfo, unicode.get(0), advance, null);
					textWidth += advance.get(0) * scale;

					/* offset next glyph */
					glyphLength = nnk_utf_decode(text + textLength, memAddress(unicode), len - textLength);
					textLength += glyphLength;
				}
			}
			return textWidth;
		})
		.height(FONT_HEIGHT)
		.query((handle, fontHeight, glyph, codepoint, nextCodepoint) -> {
			try (MemoryStack stack = stackPush()) {
				FloatBuffer x = stack.floats(0.0f);
				FloatBuffer y = stack.floats(0.0f);

				STBTTAlignedQuad quad = STBTTAlignedQuad.malloc(stack);
				IntBuffer advance = stack.mallocInt(1);

				stbtt_GetPackedQuad(cdata, BITMAP_W, BITMAP_H, codepoint - 32, x, y, quad, false);
				stbtt_GetCodepointHMetrics(fontInfo, codepoint, advance, null);

				NkUserFontGlyph ufg = NkUserFontGlyph.create(glyph);

				ufg.width(quad.x1() - quad.x0());
				ufg.height(quad.y1() - quad.y0());
				ufg.offset().set(quad.x0(), quad.y0() + (FONT_HEIGHT + descent));
				ufg.xadvance(advance.get(0) * scale);
				ufg.uv(0).set(quad.s0(), quad.t0());
				ufg.uv(1).set(quad.s1(), quad.t1());
			}
		})
		.texture(it -> it.id(fontTexID));

		nk_style_set_font(context, defaultFont);
	}

	private class NuklearKeyboardHandler implements KeyboardListener {
		@Override
		public void keyTyped(KeyboardEvent event) {
			// Not implemented
		}

		@Override
		public void keyReleased(KeyboardEvent event) {
			if (event.key() == GLFW_KEY_LEFT_CONTROL || event.key() == GLFW_KEY_RIGHT_CONTROL) {
				nk_input_key(context, NK_KEY_LEFT, glfwGetKey(event.window(), GLFW_KEY_LEFT) == GLFW_PRESS);
				nk_input_key(context, NK_KEY_RIGHT, glfwGetKey(event.window(), GLFW_KEY_RIGHT) == GLFW_PRESS);
				nk_input_key(context, NK_KEY_COPY, false);
				nk_input_key(context, NK_KEY_PASTE, false);
				nk_input_key(context, NK_KEY_CUT, false);
				nk_input_key(context, NK_KEY_SHIFT, false);
			}
		}

		@Override
		public void keyPressed(KeyboardEvent event) {
			switch (event.key()) {
				case GLFW_KEY_ESCAPE ->	glfwSetWindowShouldClose(event.window(), true);
				case GLFW_KEY_DELETE -> nk_input_key(context, NK_KEY_DEL, true);
				case GLFW_KEY_ENTER -> nk_input_key(context, NK_KEY_ENTER, true);
				case GLFW_KEY_TAB -> nk_input_key(context, NK_KEY_TAB, true);
				case GLFW_KEY_BACKSPACE -> nk_input_key(context, NK_KEY_BACKSPACE, true);
				case GLFW_KEY_UP -> nk_input_key(context, NK_KEY_UP, true);
				case GLFW_KEY_DOWN -> nk_input_key(context, NK_KEY_DOWN, true);
				case GLFW_KEY_HOME -> {
					nk_input_key(context, NK_KEY_TEXT_START, true);
					nk_input_key(context, NK_KEY_SCROLL_START, true);
				}
				case GLFW_KEY_END -> {
					nk_input_key(context, NK_KEY_TEXT_END, true);
					nk_input_key(context, NK_KEY_SCROLL_END, true);
				}
				case GLFW_KEY_PAGE_DOWN -> nk_input_key(context, NK_KEY_SCROLL_DOWN, true);
				case GLFW_KEY_PAGE_UP -> nk_input_key(context, NK_KEY_SCROLL_UP, true);
				case GLFW_KEY_LEFT_SHIFT | GLFW_KEY_RIGHT_SHIFT -> nk_input_key(context, NK_KEY_SHIFT, true);
				case GLFW_KEY_LEFT_CONTROL | GLFW_KEY_RIGHT_CONTROL -> {
					nk_input_key(context, NK_KEY_COPY, glfwGetKey(event.window(), GLFW_KEY_C) == GLFW_PRESS);
					nk_input_key(context, NK_KEY_PASTE, glfwGetKey(event.window(), GLFW_KEY_P) == GLFW_PRESS);
					nk_input_key(context, NK_KEY_CUT, glfwGetKey(event.window(), GLFW_KEY_X) == GLFW_PRESS);
					nk_input_key(context, NK_KEY_TEXT_UNDO, glfwGetKey(event.window(), GLFW_KEY_Z) == GLFW_PRESS);
					nk_input_key(context, NK_KEY_TEXT_REDO, glfwGetKey(event.window(), GLFW_KEY_R) == GLFW_PRESS);
					nk_input_key(context, NK_KEY_TEXT_WORD_LEFT, glfwGetKey(event.window(), GLFW_KEY_LEFT) == GLFW_PRESS);
					nk_input_key(context, NK_KEY_TEXT_WORD_RIGHT, glfwGetKey(event.window(), GLFW_KEY_RIGHT) == GLFW_PRESS);
					nk_input_key(context, NK_KEY_TEXT_LINE_START, glfwGetKey(event.window(), GLFW_KEY_B) == GLFW_PRESS);
					nk_input_key(context, NK_KEY_TEXT_LINE_END, glfwGetKey(event.window(), GLFW_KEY_E) == GLFW_PRESS);
				}
			}
		}
	}
	
	private class NuklearScrollHandler implements MouseScrollListener {
		@Override
		public void mouseScrolled(MouseScrollEvent event) {
			try (MemoryStack stack = stackPush()) {
				NkVec2 scroll = NkVec2.malloc(stack)
						.x(event.xOffset())
						.y(event.yOffset());
				nk_input_scroll(context, scroll);
			}
		}
	}
	
	private class NuklearMouseHandler implements MouseClickListener, MouseMotionListener {
		@Override
		public void mouseClicked(MouseClickEvent event) {
			try (MemoryStack stack = stackPush()) {
				DoubleBuffer cx = stack.mallocDouble(1);
				DoubleBuffer cy = stack.mallocDouble(1);

				glfwGetCursorPos(event.window(), cx, cy);

				int x = (int)cx.get(0);
				int y = (int)cy.get(0);

				int nkButton = switch (event.button()) {
					case GLFW_MOUSE_BUTTON_RIGHT -> NK_BUTTON_RIGHT;
					case GLFW_MOUSE_BUTTON_MIDDLE -> NK_BUTTON_MIDDLE;
					default -> NK_BUTTON_LEFT;
				};
				nk_input_button(context, nkButton, x, y, event.type() == GLFW_PRESS);
			}
		}

		@Override
		public void mouseMoved(MouseMotionEvent event) {
			nk_input_motion(context, (int)event.xPosition(), (int)event.yPosition());
		}
	}
	
	@SuppressWarnings("unused")
	private void configureKeyUnicode(long windowID) {
		// TODO Configure a char handler in Input
		glfwSetCharCallback(windowID, (w, codepoint) -> nk_input_unicode(context, codepoint));
	}
	
	@SuppressWarnings("unused")
	private void configureCopyPaste(long windowID) {
		context.clip()
		.copy((handle, text, len) -> {
			if (len == 0) {
				return;
			}

			try (MemoryStack stack = stackPush()) {
				ByteBuffer str = stack.malloc(len + 1);
				memCopy(text, memAddress(str), len);
				str.put(len, (byte)0);

				glfwSetClipboardString(windowID, str);
			}
		})
		.paste((handle, edit) -> {
			long text = nglfwGetClipboardString(windowID);
			if (text != NULL) {
				nnk_textedit_paste(edit, text, nnk_strlen(text));
			}
		});
	}
	
	/**
	 * @return the {@link NkContext} for controlling all Nuklear UI.
	 */
	public NkContext getContext() {
		return context;
	}
}
