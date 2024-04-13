package com.avogine.game.ui.nuklear;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.nuklear.Nuklear.*;
import static org.lwjgl.opengl.GL11.glBindTexture;
import static org.lwjgl.opengl.GL11.glGenTextures;
import static org.lwjgl.opengl.GL11.glTexImage2D;
import static org.lwjgl.opengl.GL11.glTexParameteri;
import static org.lwjgl.opengl.GL11C.GL_BLEND;
import static org.lwjgl.opengl.GL11C.GL_CULL_FACE;
import static org.lwjgl.opengl.GL11C.GL_DEPTH_TEST;
import static org.lwjgl.opengl.GL11C.GL_LINEAR;
import static org.lwjgl.opengl.GL11C.GL_ONE_MINUS_SRC_ALPHA;
import static org.lwjgl.opengl.GL11C.GL_RGBA;
import static org.lwjgl.opengl.GL11C.GL_RGBA8;
import static org.lwjgl.opengl.GL11C.GL_SCISSOR_TEST;
import static org.lwjgl.opengl.GL11C.GL_SRC_ALPHA;
import static org.lwjgl.opengl.GL11C.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11C.GL_TEXTURE_MAG_FILTER;
import static org.lwjgl.opengl.GL11C.GL_TEXTURE_MIN_FILTER;
import static org.lwjgl.opengl.GL11C.glBlendFunc;
import static org.lwjgl.opengl.GL11C.glDisable;
import static org.lwjgl.opengl.GL11C.glEnable;
import static org.lwjgl.opengl.GL12.GL_UNSIGNED_INT_8_8_8_8_REV;
import static org.lwjgl.opengl.GL13C.*;
import static org.lwjgl.opengl.GL14C.*;
import static org.lwjgl.stb.STBTruetype.*;
import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.system.MemoryUtil.*;

import java.nio.*;
import java.util.Objects;

import org.joml.Matrix4f;
import org.lwjgl.nuklear.*;
import org.lwjgl.opengl.GL11;
import org.lwjgl.stb.*;
import org.lwjgl.system.MemoryStack;

import com.avogine.io.Window;
import com.avogine.io.event.*;
import com.avogine.io.listener.*;
import com.avogine.render.data.nuklear.NuklearMesh;
import com.avogine.render.shader.NuklearShader;
import com.avogine.util.resource.ResourceFileReader;

/**
 * Wrapper implementation for {@link Nuklear}.
 * 
 * Initialize an instance of this class once to gain access to a {@link NkContext} so that you can render Nuklear elements.
 * Handlers for Input commands are automatically configured to translate to {@code nk_input_*} commands.
 */
public class NuklearUI {

	private static final int BUFFER_INITIAL_SIZE = 4 * 1024;

	private static final NkAllocator ALLOCATOR;
	
	static {
		ALLOCATOR = NkAllocator.create()
				.alloc((handle, old, size) -> nmemAllocChecked(size))
				.mfree((handle, ptr) -> nmemFree(ptr));
	}
	
	// Storage for font data
	private final ByteBuffer ttf;

	private final Matrix4f projectionMatrix;
	
	private NkContext context;
	private NkUserFont defaultFont;
	
	private NuklearShader nuklearShader;
	
	private NkBuffer commands;

	private int displayWidth;
	private int displayHeight;
	
	private NuklearMesh mesh;
	
	/**
	 * 
	 */
	public NuklearUI() {
		ttf = ResourceFileReader.ioResourceToByteBuffer("/Roboto-Regular.ttf", 512 * 1024);
		projectionMatrix = new Matrix4f();
		
		// Create a Nuklear context, it is used everywhere.
		context = NkContext.create();
		nk_init(context, ALLOCATOR, null);
		
		commands = NkBuffer.create();
		nk_buffer_init(commands, ALLOCATOR, BUFFER_INITIAL_SIZE);
	}
	
	/**
	 * @param window
	 */
	public void init(Window window) {
		displayWidth = window.getWidth();
		displayHeight = window.getHeight();

		float halfWidth = displayWidth / 2.0f;
		float halfHeight = displayHeight / 2.0f;
		projectionMatrix.ortho2D(-halfWidth, halfWidth, halfHeight, -halfHeight).translate(-halfWidth, -halfHeight, 0);

		// This is the Nuklear font object used for rendering text.
		defaultFont = NkUserFont.create();
		initFont();

		nuklearShader = new NuklearShader("nuklearVertex.glsl", "nuklearFragment.glsl");
		
		mesh = new NuklearMesh(window.getWidth(), window.getHeight(), window.getWidth(), window.getHeight());
		
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

	/**
	 * Call this prior to processing events, then call inputEnd() directly after
	 */
	public void inputBegin() {
		nk_input_begin(context);
	}
	
	/**
	 * 
	 */
	public void inputEnd() {
		nk_input_end(context);
	}
	
	/**
	 * @param window
	 */
	public void onRender(Window window) {
		try (MemoryStack stack = stackPush()) {
			IntBuffer w = stack.mallocInt(1);
			IntBuffer h = stack.mallocInt(1);

//			glfwGetWindowSize(windowId, w, h);
//			width = w.get(0);
//			height = h.get(0);

			glfwGetFramebufferSize(window.getId(), w, h);
			displayWidth = w.get(0);
			displayHeight = h.get(0);

			float halfWidth = displayWidth / 2.0f;
			float halfHeight = displayHeight / 2.0f;
			projectionMatrix.identity().ortho2D(-halfWidth, halfWidth, halfHeight, -halfHeight).translate(-halfWidth, -halfHeight, 0);
			
			// TODO Currently setting width/height and display values to just the framebuffer value, not ideal.
			mesh.setSize(displayWidth, displayHeight);
		}
		
		setupUIState();
		
		// setup program
		nuklearShader.bind();
		nuklearShader.projectionMatrix.loadMatrix(projectionMatrix);
		GL11.glViewport(0, 0, displayWidth, displayHeight);

		mesh.render(context, commands);
		
		nk_clear(context);
		
		teardownUIState();
	}
	
	private void setupUIState() {
		// setup global state
		glEnable(GL_BLEND);
		glBlendEquation(GL_FUNC_ADD);
		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
		glDisable(GL_CULL_FACE);
		glDisable(GL_DEPTH_TEST);
		glEnable(GL_SCISSOR_TEST);
		glActiveTexture(GL_TEXTURE0);
	}
	
	private void teardownUIState() {
		nuklearShader.unbind();
		glDisable(GL_BLEND);
		glDisable(GL_SCISSOR_TEST);
		// TODO Re-enable these based on some global render settings
		glEnable(GL_CULL_FACE);
		glEnable(GL_DEPTH_TEST);
	}
	
	private class NuklearKeyboardHandler implements KeyListener {
		@Override
		public void keyTyped(KeyEvent event) {
			nk_input_unicode(context, event.codepoint());
		}

		@Override
		public void keyReleased(KeyEvent event) {
			handleKeyEvent(event);
			
			if (nk_item_is_any_active(context)) {
				event.consume();
			}
		}

		@Override
		public void keyPressed(KeyEvent event) {
			handleKeyEvent(event);
			
			if (nk_item_is_any_active(context)) {
				event.consume();
			}
		}
		
		private void handleKeyEvent(KeyEvent event) {
			boolean press = event.id() == KeyEvent.KEY_PRESSED;
			switch (event.key()) {
				// XXX Hmm, this also seems like a terrible way to handle this, this event already exists in Input
				case GLFW_KEY_ESCAPE ->	glfwSetWindowShouldClose(event.window(), true);
				case GLFW_KEY_DELETE -> nk_input_key(context, NK_KEY_DEL, press);
				case GLFW_KEY_ENTER -> nk_input_key(context, NK_KEY_ENTER, press);
				case GLFW_KEY_TAB -> nk_input_key(context, NK_KEY_TAB, press);
				case GLFW_KEY_BACKSPACE -> nk_input_key(context, NK_KEY_BACKSPACE, press);
				case GLFW_KEY_UP -> nk_input_key(context, NK_KEY_UP, press);
				case GLFW_KEY_DOWN -> nk_input_key(context, NK_KEY_DOWN, press);
				case GLFW_KEY_HOME -> {
					nk_input_key(context, NK_KEY_TEXT_START, press);
					nk_input_key(context, NK_KEY_SCROLL_START, press);
				}
				case GLFW_KEY_END -> {
					nk_input_key(context, NK_KEY_TEXT_END, press);
					nk_input_key(context, NK_KEY_SCROLL_END, press);
				}
				case GLFW_KEY_PAGE_DOWN -> nk_input_key(context, NK_KEY_SCROLL_DOWN, press);
				case GLFW_KEY_PAGE_UP -> nk_input_key(context, NK_KEY_SCROLL_UP, press);
				case GLFW_KEY_LEFT_SHIFT | GLFW_KEY_RIGHT_SHIFT -> nk_input_key(context, NK_KEY_SHIFT, press);
				case GLFW_KEY_LEFT_CONTROL | GLFW_KEY_RIGHT_CONTROL -> {
					if (press) {
						nk_input_key(context, NK_KEY_COPY, glfwGetKey(event.window(), GLFW_KEY_C) == GLFW_PRESS);
						nk_input_key(context, NK_KEY_PASTE, glfwGetKey(event.window(), GLFW_KEY_P) == GLFW_PRESS);
						nk_input_key(context, NK_KEY_CUT, glfwGetKey(event.window(), GLFW_KEY_X) == GLFW_PRESS);
						nk_input_key(context, NK_KEY_TEXT_UNDO, glfwGetKey(event.window(), GLFW_KEY_Z) == GLFW_PRESS);
						nk_input_key(context, NK_KEY_TEXT_REDO, glfwGetKey(event.window(), GLFW_KEY_R) == GLFW_PRESS);
						nk_input_key(context, NK_KEY_TEXT_WORD_LEFT, glfwGetKey(event.window(), GLFW_KEY_LEFT) == GLFW_PRESS);
						nk_input_key(context, NK_KEY_TEXT_WORD_RIGHT, glfwGetKey(event.window(), GLFW_KEY_RIGHT) == GLFW_PRESS);
						nk_input_key(context, NK_KEY_TEXT_LINE_START, glfwGetKey(event.window(), GLFW_KEY_B) == GLFW_PRESS);
						nk_input_key(context, NK_KEY_TEXT_LINE_END, glfwGetKey(event.window(), GLFW_KEY_E) == GLFW_PRESS);
					} else {
						nk_input_key(context, NK_KEY_LEFT, glfwGetKey(event.window(), GLFW_KEY_LEFT) == GLFW_PRESS);
						nk_input_key(context, NK_KEY_RIGHT, glfwGetKey(event.window(), GLFW_KEY_RIGHT) == GLFW_PRESS);
						nk_input_key(context, NK_KEY_COPY, false);
						nk_input_key(context, NK_KEY_PASTE, false);
						nk_input_key(context, NK_KEY_CUT, false);
						nk_input_key(context, NK_KEY_SHIFT, false);
					}
				}
			}
		}
		
		@Override
		public EventLayer getLayer() {
			return EventLayer.UI;
		}
	}
	
	private class NuklearScrollHandler implements MouseWheelListener {
		@Override
		public void mouseWheelMoved(MouseWheelEvent event) {
			try (MemoryStack stack = stackPush()) {
				NkVec2 scroll = NkVec2.malloc(stack)
						.x((float) event.xOffset())
						.y((float) event.yOffset());
				nk_input_scroll(context, scroll);
			}
			// TODO This may want to consume events as well
		}
		
		@Override
		public EventLayer getLayer() {
			return EventLayer.UI;
		}
	}
	
	private class NuklearMouseHandler implements MouseButtonListener, MouseMotionListener {
		
		@Override
		public void mouseClicked(MouseEvent event) {
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
				// XXX Input reports held mouse clicks as GLFW_REPEAT, unclear if that's correct.
				nk_input_button(context, nkButton, x, y, true);
				
				if (nk_item_is_any_active(context)) {
//				if (nk_window_is_any_hovered(context)) {
					event.consume();
				}
			}
		}

		@Override
		public void mousePressed(MouseEvent event) {
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
				// XXX Input reports held mouse clicks as GLFW_REPEAT, unclear if that's correct.
				nk_input_button(context, nkButton, x, y, true);
				
				if (nk_item_is_any_active(context)) {
//				if (nk_window_is_any_hovered(context)) {
					event.consume();
				}
			}
		}

		@Override
		public void mouseReleased(MouseEvent event) {
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
				// XXX Input reports held mouse clicks as GLFW_REPEAT, unclear if that's correct.
				nk_input_button(context, nkButton, x, y, false);
				
				if (nk_item_is_any_active(context)) {
//				if (nk_window_is_any_hovered(context)) {
					event.consume();
				}
			}
		}

		@Override
		public void mouseMoved(MouseEvent event) {
			nk_input_motion(context, (int)event.mouseX(), (int)event.mouseY());
		}

		@Override
		public void mouseDragged(MouseEvent event) {
			// Not implemented
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
	
	/**
	 * @return the {@link NkContext} for controlling all Nuklear UI.
	 */
	public NkContext getContext() {
		return context;
	}
	
	public void cleanup() {
		nk_free(context);
		mesh.cleanup();
		nuklearShader.cleanup();
		nk_buffer_free(commands);
		Objects.requireNonNull(defaultFont.query()).free();
		Objects.requireNonNull(defaultFont.width()).free();

		Objects.requireNonNull(ALLOCATOR.alloc()).free();
		Objects.requireNonNull(ALLOCATOR.mfree()).free();
		
		// TODO Remove InputListeners
	}
}
