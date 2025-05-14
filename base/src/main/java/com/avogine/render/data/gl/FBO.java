package com.avogine.render.data.gl;

import static org.lwjgl.opengl.GL30.*;

import com.avogine.logging.AvoLog;

/**
 * @param id 
 * @param target 
 */
public record FBO(int id, int target) {

	/**
	 * @param target
	 * @return
	 */
	public static FBO gen(int target) {
		return new FBO(glGenFramebuffers(), target);
	}
	
	/**
	 * @return
	 */
	public static FBO gen() {
		return gen(GL_FRAMEBUFFER);
	}
	
	/**
	 * @param target
	 */
	public static void unbind(int target) {
		glBindFramebuffer(target, 0);
	}
	
	/**
	 * 
	 */
	public static void unbind() {
		unbind(GL_FRAMEBUFFER);
	}
	
	/**
	 * 
	 */
	public void cleanup() {
		glDeleteFramebuffers(id);
	}
	
	/**
	 * @return this
	 */
	public FBO bind() {
		glBindFramebuffer(target, id);
		return this;
	}
	
	/**
	 * @param attachment
	 * @param texTarget
	 * @param texture
	 * @param level
	 * @return this
	 */
	public FBO attachTexture2D(int attachment, int texTarget, int texture, int level) {
		glFramebufferTexture2D(target, attachment, texTarget, texture, level);
		return this;
	}
	
	/**
	 * @param target
	 * @return this
	 */
	public FBO validate(int target) {
		int fboStatus = glCheckFramebufferStatus(target);
		if (fboStatus != GL_FRAMEBUFFER_COMPLETE) {
			AvoLog.log().warn("Framebuffer error: {}", fboStatus);
		}
		return this;
	}
	
	/**
	 * @return this
	 */
	public FBO validate() {
		return validate(target);
	}
	
}
