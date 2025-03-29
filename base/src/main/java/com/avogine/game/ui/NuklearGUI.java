package com.avogine.game.ui;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.nuklear.Nuklear.*;
import static org.lwjgl.opengl.GL11C.*;
import static org.lwjgl.opengl.GL12C.GL_UNSIGNED_INT_8_8_8_8_REV;
import static org.lwjgl.stb.STBTruetype.*;
import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.system.MemoryUtil.*;

import java.nio.*;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.lwjgl.nuklear.*;
import org.lwjgl.stb.*;
import org.lwjgl.system.MemoryStack;

import com.avogine.game.ui.nuklear.NuklearUIElement;
import com.avogine.io.*;
import com.avogine.io.event.*;
import com.avogine.io.event.MouseEvent.*;
import com.avogine.io.listener.*;
import com.avogine.util.ResourceUtils;
import com.avogine.util.resource.ResourceConstants;

/**
 * Wrapper implementation for {@link Nuklear}.
 * </p>
 * Initialize an instance of this class once to gain access to a {@link NkContext} so that you can render Nuklear elements.
 * Handlers for Input commands are automatically configured to translate to {@code nk_input_*} commands.
 */
public class NuklearGUI implements GUI {

	private static final int BUFFER_INITIAL_SIZE = 4 * 1024;

	private static final NkAllocator ALLOCATOR;
	
	static {
		ALLOCATOR = NkAllocator.create()
				.alloc((handle, old, size) -> nmemAllocChecked(size))
				.mfree((handle, ptr) -> nmemFree(ptr));
	}
	
	private record NkKeyEvent(int key, boolean pressed) {}
	private record NkCharEvent(int unicode) {}
	private record NkMouseButtonEvent(int button, int x, int y, boolean pressed) {}
	private record NkMouseMotionEvent(int x, int y) {}
	private record NkMouseWheelEvent(float xOffset, float yOffset) {}
	
	private NkContext context;
	private ByteBuffer ttfBuffer;
	private NkUserFont defaultFont;

	private NkBuffer commands;

	private final List<NuklearUIElement> uiElements;
	
	private final Queue<NkKeyEvent> delayedKeyEvents;
	private final Queue<NkCharEvent> delayedCharEvents;
	private final Queue<NkMouseButtonEvent> delayedMouseButtonEvents;
	private final Queue<NkMouseMotionEvent> delayedMouseMotionEvents;
	private final Queue<NkMouseWheelEvent> delayedMouseWheelEvents;
	
	private InputListener nkInputHandler;
	
	/**
	 * 
	 */
	public NuklearGUI() {
		// Create a Nuklear context, it is used everywhere.
		context = NkContext.create();
		nk_init(context, ALLOCATOR, null);

		commands = NkBuffer.create();
		nk_buffer_init(commands, ALLOCATOR, BUFFER_INITIAL_SIZE);
		
		uiElements = new ArrayList<>();
		
		delayedKeyEvents = new ConcurrentLinkedQueue<>();
		delayedCharEvents = new ConcurrentLinkedQueue<>();
		delayedMouseButtonEvents = new ConcurrentLinkedQueue<>();
		delayedMouseMotionEvents = new ConcurrentLinkedQueue<>();
		delayedMouseWheelEvents = new ConcurrentLinkedQueue<>();
	}
	
	/**
	 * @param window 
	 */
	public void init(Window window) {
		// Nuklear uses STBTrueType in order to easily render fonts, but the STBTT structs are only metadata and reference the allocated TTF buffer, so we must keep it in memory for the duration of use.
		ttfBuffer = ResourceUtils.readResourceToBuffer(ResourceConstants.FONTS.with("Roboto-Regular.ttf"), 512 * 1024);
		
		// This is the Nuklear font object used for rendering text.
		defaultFont = NkUserFont.create();
		initFont();

		nkInputHandler = window.addInputListener(new NuklearInputHandler());
	}
	
	private void initFont() {
		final int BITMAP_W = 1024;
		final int BITMAP_H = 1024;

		final int FONT_HEIGHT = 18;
		int fontTexID = glGenTextures();

		STBTTFontinfo fontInfo = STBTTFontinfo.create();
		STBTTPackedchar.Buffer cdata = STBTTPackedchar.create(96);

		float scale;
		float descent;

		try (MemoryStack stack = stackPush()) {
			stbtt_InitFont(fontInfo, ttfBuffer);
			scale = stbtt_ScaleForPixelHeight(fontInfo, FONT_HEIGHT);

			IntBuffer d = stack.mallocInt(1);
			stbtt_GetFontVMetrics(fontInfo, null, d, null);
			descent = d.get(0) * scale;

			ByteBuffer bitmap = memAlloc(BITMAP_W * BITMAP_H);

			STBTTPackContext pc = STBTTPackContext.malloc(stack);
			stbtt_PackBegin(pc, bitmap, BITMAP_W, BITMAP_H, 0, 1, NULL);
			stbtt_PackSetOversampling(pc, 4, 4);
			stbtt_PackFontRange(pc, ttfBuffer, 0, FONT_HEIGHT, 32, cdata);
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
	
	/**
	 * @param window
	 */
	public void input(Window window) {
		nk_input_begin(context);
		
		drainKeyEvents(window.getKeyboard());
		drainCharEvents();
		
		drainMouseWheelEvents();
		
		drainMouseButtonEvents();
		drainMouseMotionEvents();
		
		nk_input_end(context);
	}
	
	private void drainKeyEvents(Keyboard keyboard) {
		while (!delayedKeyEvents.isEmpty()) {
			var keyEvent = delayedKeyEvents.poll();
			switch (keyEvent.key()) {
				case GLFW_KEY_DELETE -> nk_input_key(context, NK_KEY_DEL, keyEvent.pressed());
				case GLFW_KEY_ENTER -> nk_input_key(context, NK_KEY_ENTER, keyEvent.pressed());
				case GLFW_KEY_TAB -> nk_input_key(context, NK_KEY_TAB, keyEvent.pressed());
				case GLFW_KEY_BACKSPACE -> nk_input_key(context, NK_KEY_BACKSPACE, keyEvent.pressed());
				case GLFW_KEY_UP -> nk_input_key(context, NK_KEY_UP, keyEvent.pressed());
				case GLFW_KEY_DOWN -> nk_input_key(context, NK_KEY_DOWN, keyEvent.pressed());
				case GLFW_KEY_HOME -> {
					nk_input_key(context, NK_KEY_TEXT_START, keyEvent.pressed());
					nk_input_key(context, NK_KEY_SCROLL_START, keyEvent.pressed());
				}
				case GLFW_KEY_END -> {
					nk_input_key(context, NK_KEY_TEXT_END, keyEvent.pressed());
					nk_input_key(context, NK_KEY_SCROLL_END, keyEvent.pressed());
				}
				case GLFW_KEY_PAGE_DOWN -> nk_input_key(context, NK_KEY_SCROLL_DOWN, keyEvent.pressed());
				case GLFW_KEY_PAGE_UP -> nk_input_key(context, NK_KEY_SCROLL_UP, keyEvent.pressed());
				case GLFW_KEY_LEFT_SHIFT | GLFW_KEY_RIGHT_SHIFT -> nk_input_key(context, NK_KEY_SHIFT, keyEvent.pressed());
				case GLFW_KEY_LEFT_CONTROL | GLFW_KEY_RIGHT_CONTROL -> {
					if (keyEvent.pressed()) {
						nk_input_key(context, NK_KEY_COPY, keyboard.isKeyDown(GLFW_KEY_C));
						nk_input_key(context, NK_KEY_PASTE, keyboard.isKeyDown(GLFW_KEY_P));
						nk_input_key(context, NK_KEY_CUT, keyboard.isKeyDown(GLFW_KEY_X));
						nk_input_key(context, NK_KEY_TEXT_UNDO, keyboard.isKeyDown(GLFW_KEY_Z));
						nk_input_key(context, NK_KEY_TEXT_REDO, keyboard.isKeyDown(GLFW_KEY_R));
						nk_input_key(context, NK_KEY_TEXT_WORD_LEFT, keyboard.isKeyDown(GLFW_KEY_LEFT));
						nk_input_key(context, NK_KEY_TEXT_WORD_RIGHT, keyboard.isKeyDown(GLFW_KEY_RIGHT));
						nk_input_key(context, NK_KEY_TEXT_LINE_START, keyboard.isKeyDown(GLFW_KEY_B));
						nk_input_key(context, NK_KEY_TEXT_LINE_END, keyboard.isKeyDown(GLFW_KEY_E));
					} else {
						nk_input_key(context, NK_KEY_LEFT, keyboard.isKeyDown(GLFW_KEY_LEFT));
						nk_input_key(context, NK_KEY_RIGHT, keyboard.isKeyDown(GLFW_KEY_RIGHT));
						nk_input_key(context, NK_KEY_COPY, false);
						nk_input_key(context, NK_KEY_PASTE, false);
						nk_input_key(context, NK_KEY_CUT, false);
						nk_input_key(context, NK_KEY_SHIFT, false);
					}
				}
				default -> {
					// Action not implemented
				}
			}
		}
	}
	
	private void drainCharEvents() {
		while (!delayedCharEvents.isEmpty()) {
			var charEvent = delayedCharEvents.poll();
			nk_input_unicode(context, charEvent.unicode());
		}
	}
	
	private void drainMouseWheelEvents() {
		while (!delayedMouseWheelEvents.isEmpty()) {
			var mouseWheelEvent = delayedMouseWheelEvents.poll();
			try (MemoryStack stack = stackPush()) {
				NkVec2 scroll = NkVec2.malloc(stack)
						.x(mouseWheelEvent.xOffset())
						.y(mouseWheelEvent.yOffset());
				nk_input_scroll(context, scroll);
			}
		}
	}
	
	private void drainMouseButtonEvents() {
		while (!delayedMouseButtonEvents.isEmpty()) {
			var mouseButtonEvent = delayedMouseButtonEvents.poll();
			int nkButton = switch (mouseButtonEvent.button()) {
				case GLFW_MOUSE_BUTTON_RIGHT -> NK_BUTTON_RIGHT;
				case GLFW_MOUSE_BUTTON_MIDDLE -> NK_BUTTON_MIDDLE;
				default -> NK_BUTTON_LEFT;
			};
			
			nk_input_button(context, nkButton, mouseButtonEvent.x(), mouseButtonEvent.y(), mouseButtonEvent.pressed());
		}
	}
	
	private void drainMouseMotionEvents() {
		while (!delayedMouseMotionEvents.isEmpty()) {
			var mouseMotionEvent = delayedMouseMotionEvents.poll();
			nk_input_motion(context, mouseMotionEvent.x(), mouseMotionEvent.y());
		}
	}
	
	/**
	 * 
	 */
	public void cleanup() {
		nk_free(context);
		nk_buffer_free(commands);
		Objects.requireNonNull(defaultFont.query()).free();
		Objects.requireNonNull(defaultFont.width()).free();

		Objects.requireNonNull(ALLOCATOR.alloc()).free();
		Objects.requireNonNull(ALLOCATOR.mfree()).free();
	}
	
	/**
	 * @param window
	 */
	public void layoutElements(Window window) {
		for (NuklearUIElement uiElement : uiElements) {
			uiElement.layout(context, window);
		}
	}
	
	/**
	 * @return the context
	 */
	public NkContext getContext() {
		return context;
	}
	
	/**
	 * @return the commands
	 */
	public NkBuffer getCommands() {
		return commands;
	}
	
	/**
	 * @return the uiElements
	 */
	public List<NuklearUIElement> getUiElements() {
		return uiElements;
	}
	
	/**
	 * @param uiElement
	 * @return the uiElement
	 */
	public NuklearUIElement addUIElement(NuklearUIElement uiElement) {
		uiElements.add(uiElement);
		return uiElement;
	}
	
	/**
	 * @param uiElement
	 * @return true if the uiElement was removed
	 */
	public boolean removeUIElement(NuklearUIElement uiElement) {
		return uiElements.remove(uiElement);
	}
	
	/**
	 * @return the nkInputHandler
	 */
	public InputListener getNkInputHandler() {
		return nkInputHandler;
	}
	
	private class NuklearInputHandler extends InputAdapter {
		@Override
		public void keyReleased(KeyEvent event) {
			delayedKeyEvents.add(new NkKeyEvent(event.key(), false));
			
			if (nk_item_is_any_active(context)) {
				event.consume();
			}
		}

		@Override
		public void keyPressed(KeyEvent event) {
			delayedKeyEvents.add(new NkKeyEvent(event.key(), true));
			
			if (nk_item_is_any_active(context)) {
				event.consume();
			}
		}
		
		@Override
		public void charTyped(CharEvent event) {
			delayedCharEvents.add(new NkCharEvent(event.codepoint()));
		}
		
		@Override
		public void mouseWheelMoved(MouseWheelEvent event) {
			delayedMouseWheelEvents.add(new NkMouseWheelEvent((float) event.xOffset(), (float) event.yOffset()));
			// XXX This may want to consume events as well
		}
		
		@Override
		public void mousePressed(MouseButtonEvent event) {
			delayedMouseButtonEvents.add(new NkMouseButtonEvent(event.button(), (int) event.mouseX(), (int) event.mouseY(), true));
			
			if (nk_item_is_any_active(context)) {
				event.consume();
			}
		}

		@Override
		public void mouseReleased(MouseButtonEvent event) {
			delayedMouseButtonEvents.add(new NkMouseButtonEvent(event.button(), (int) event.mouseX(), (int) event.mouseY(), false));
			
			if (nk_item_is_any_active(context)) {
				event.consume();
			}
		}

		@Override
		public void mouseMoved(MouseMotionEvent event) {
			delayedMouseMotionEvents.add(new NkMouseMotionEvent((int) event.mouseX(), (int) event.mouseY()));
		}
		
		@Override
		public EventLayer getLayer() {
			return EventLayer.UI;
		}
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
}
