package com.avogine.render.data;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL30.*;

import java.nio.ByteBuffer;
import java.util.function.Supplier;

import com.avogine.experimental.annotation.MemoryManaged;
import com.avogine.io.Window;
import com.avogine.logging.AvoLog;

/**
 * @author Dominus
 *
 */
@MemoryManaged
public class FrameBuffer {

	protected final int fbo;
	
	protected Supplier<Integer> width;
	protected Supplier<Integer> height;
	
	protected Window window;
	
	protected TextureAtlas colorTexture;
	protected int colorAttachment;
	protected int rbo;
	
	private FrameBuffer(Window window, Supplier<Integer> width, Supplier<Integer> height) {
		this.window = window;
		this.width = width;
		this.height = height;
		fbo = glGenFramebuffers();
	}
	
	/**
	 * Create a new {@code FrameBuffer} with a specified width and height.
	 * @param window the parent window that contains this {@code FrameBuffer}
	 * @param width a static width that this {@code FrameBuffer} will be sized to before each render
	 * @param height a static height that this {@code FrameBuffer} will be sized to before each render
	 */
	public FrameBuffer(Window window, int width, int height) {
		this(window, () -> width, () -> height);
		
	}
	
	/**
	 * Create a new {@code FrameBuffer} with a size that matches the current {@link Window} size.
	 * @param window the {@code Window} to base the size of this {@code FrameBuffer} off of
	 */
	public FrameBuffer(Window window) {
		this(window, window::getWidth, window::getHeight);
	}
	
	/**
	 * TODO XXX Call this automatically? I keep forgetting to call it and things just don't work, who knows
	 * Create and attach a color attachment to this {@code FrameBuffer}.
	 */
	public void init() {
		bind();
		
		// Create a color texture
		// TODO Reorganize project so that this can be called from TextureLoader
		colorAttachment = glGenTextures();
		glBindTexture(GL_TEXTURE_2D, colorAttachment);
		
		glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA8, width.get(), height.get(), 0, GL_RGBA, GL_UNSIGNED_BYTE, (ByteBuffer) null);
		
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
		glBindTexture(GL_TEXTURE_2D, 0);
		
		// Attach the color texture to this framebuffer
		glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D, colorAttachment, 0);
		
		colorTexture = new TextureAtlas(colorAttachment, width.get(), height.get());
		
		// Create a depth and stencil renderbuffer
		rbo = glGenRenderbuffers();
		glBindRenderbuffer(GL_RENDERBUFFER, rbo);
		glRenderbufferStorage(GL_RENDERBUFFER, GL_DEPTH24_STENCIL8, width.get(), height.get());
		glBindRenderbuffer(GL_RENDERBUFFER, 0);
		
		// Attach the depth and stencil renderbuffer to this framebuffer
		glFramebufferRenderbuffer(GL_FRAMEBUFFER, GL_DEPTH_STENCIL_ATTACHMENT, GL_RENDERBUFFER, rbo);
		
		if (glCheckFramebufferStatus(GL_FRAMEBUFFER) != GL_FRAMEBUFFER_COMPLETE) {
			AvoLog.log().error("Framebuffer is not complete!");
		}
		unbind();
	}
	
	/**
	 * Bind this framebuffer to be drawn to.
	 * <p>
	 * This will handle resizing the viewport if necessary to the specific size of this framebuffer.
	 */
	public void bind() {
		glViewport(0, 0, width.get(), height.get());
		glBindFramebuffer(GL_FRAMEBUFFER, fbo);
	}
	
	/**
	 * Unbind this framebuffer so that drawing will occur on the default screen buffer again.
	 * <p>
	 * This will handle resizing the viewport back to {@code window}'s default width and height. Custom handling
	 * may need to be implemented if your target viewport size is not equal to the window's width and height.
	 */
	public void unbind() {
		glBindFramebuffer(GL_FRAMEBUFFER, 0);
		glViewport(0, 0, window.getWidth(), window.getHeight());
	}

	/**
	 * @return The ID of this framebuffer object
	 */
	public int getFbo() {
		return fbo;
	}
	
	/**
	 * @return The texture containing the color buffer 
	 */
	public TextureAtlas getColorTexture() {
		return colorTexture;
	}
	
	/**
	 * Delete any texture attachments, renderbuffers, and finally the framebuffer itself from OpenGL memory.
	 */
	public void cleanup() {
		glDeleteTextures(colorAttachment);
		glDeleteRenderbuffers(rbo);
		glDeleteFramebuffers(fbo);
	}
	
}
