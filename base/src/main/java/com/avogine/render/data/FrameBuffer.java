/**
 * 
 */
package com.avogine.render.data;

import java.lang.invoke.MethodHandles;
import java.nio.ByteBuffer;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.avogine.experimental.annotation.InDev;
import com.avogine.game.Window;

/**
 * @author Dominus
 *
 */
@InDev
public class FrameBuffer {

	private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass().getSimpleName());

	private final int fbo;
	
	private int width;
	private int height;
	
	private int colorAttachment;
	private int rbo;
	
	/**
	 * Create a new {@code FrameBuffer} with a specified width and height.
	 * @param width
	 * @param height
	 */
	public FrameBuffer(int width, int height) {
		fbo = GL30.glGenFramebuffers();
		
		this.width = width;
		this.height = height;
		
	}
	
	/**
	 * Create a new {@code FrameBuffer} with a size that matches the current {@link Window} size.
	 * <p>
	 * TODO This will currently not respect any resizing that occurs to the given {@code Window}.
	 * @param window the {@code Window} to base the size of this {@code FrameBuffer} off of
	 */
	public FrameBuffer(Window window) {
		this(window.getWidth(), window.getHeight());
	}
	
	/**
	 * Create and attach a color attachment to this {@code FrameBuffer}.
	 */
	public void init() {
		bind();
		
		// Create a color texture
		// TODO Reorganize project so that this can be called from TextureLoader
		colorAttachment = GL11.glGenTextures();
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, colorAttachment);
		
		GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA8, width, height, 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, (ByteBuffer) null);
		
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
		
		// Attach the color texture to this framebuffer
		GL30.glFramebufferTexture2D(GL30.GL_FRAMEBUFFER, GL30.GL_COLOR_ATTACHMENT0, GL11.GL_TEXTURE_2D, colorAttachment, 0);
		
		// Create a depth and stencil renderbuffer
		rbo = GL30.glGenRenderbuffers();
		GL30.glBindRenderbuffer(GL30.GL_RENDERBUFFER, rbo);
		GL30.glRenderbufferStorage(GL30.GL_RENDERBUFFER, GL30.GL_DEPTH24_STENCIL8, width, height);
		GL30.glBindRenderbuffer(GL30.GL_RENDERBUFFER, 0);
		
		// Attach the depth and stencil renderbuffer to this framebuffer
		GL30.glFramebufferRenderbuffer(GL30.GL_FRAMEBUFFER, GL30.GL_DEPTH_STENCIL_ATTACHMENT, GL30.GL_RENDERBUFFER, rbo);
		
		if (GL30.glCheckFramebufferStatus(GL30.GL_FRAMEBUFFER) != GL30.GL_FRAMEBUFFER_COMPLETE) {
			logger.error("Framebuffer is not complete!");
		}
		unbind();
	}
	
	/**
	 * 
	 */
	public void bind() {
		GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, fbo);
	}
	
	/**
	 * 
	 */
	public void unbind() {
		GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, 0);
	}
	
	public void cleanup() {
		GL11.glDeleteTextures(colorAttachment);
		GL30.glDeleteRenderbuffers(rbo);
		GL30.glDeleteFramebuffers(fbo);
	}
	
}
