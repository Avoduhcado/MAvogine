package com.avogine.render.data.gl;

import static org.lwjgl.opengl.GL11.*;

import java.nio.ByteBuffer;

import org.lwjgl.opengl.*;

import com.avogine.render.data.image.Image2D;

/**
 * @param id 
 * @param target 
 *
 */
public record Texture(int id, int target) {
	
	/**
	 * @param target
	 * @return
	 */
	public static Texture gen(int target) {
		return new Texture(glGenTextures(), target);
	}
	
	/**
	 * @return
	 */
	public static Texture gen() {
		return gen(GL_TEXTURE_2D);
	}
	
	/**
	 * @param target
	 */
	public static void unbind(int target) {
		glBindTexture(target, 0);
	}
	
	/**
	 * 
	 */
	public static void unbind() {
		unbind(GL_TEXTURE_2D);
	}
	
	/**
	 * 
	 */
	public void cleanup() {
		glDeleteTextures(id);
	}
	
	/**
	 * @return this
	 */
	public Texture bind() {
		glBindTexture(target, id);
		return this;
	}
	
	/**
	 * @param pname
	 * @param param
	 * @return this
	 */
	public Texture texParameteri(int pname, int param) {
		glTexParameteri(target, pname, param);
		return this;
	}
	
	/**
	 * @param target
	 * @param internalFormat
	 * @param width
	 * @param height
	 * @param format
	 * @param pixels
	 * @return this
	 */
	public Texture texImage2D(int target, int internalFormat, int width, int height, int format, ByteBuffer pixels) {
		glTexImage2D(target, 0, internalFormat, width, height, 0, format, GL_UNSIGNED_BYTE, pixels);
		return this;
	}
	
	/**
	 * @param internalFormat
	 * @param width
	 * @param height
	 * @param format
	 * @param pixels
	 * @return this
	 */
	public Texture texImage2D(int internalFormat, int width, int height, int format, ByteBuffer pixels) {
		return texImage2D(target, internalFormat, width, height, format, pixels);
	}
	
	/**
	 * @param target 
	 * @param image2D
	 * @return this
	 */
	public Texture texImage2D(int target, Image2D image2D) {
		return texImage2D(target, image2D.internalFormat(), image2D.width(), image2D.height(), image2D.format(), image2D.pixels());
	}
	
	/**
	 * @param image2D
	 * @return this
	 */
	public Texture texImage2D(Image2D image2D) {
		return texImage2D(target, image2D);
	}
	
	/**
	 * @return this
	 */
	public Texture generateMipmap() {
		GL30.glGenerateMipmap(target);
		return this;
	}
	
	/**
	 * @return this
	 */
	public Texture anisotropicFiltering() {
		if (GL.getCapabilities().GL_EXT_texture_filter_anisotropic) {
			// TODO#40: Extract some global Anisotropic filtering value
			float amount = Math.min(4f, glGetFloat(EXTTextureFilterAnisotropic.GL_MAX_TEXTURE_MAX_ANISOTROPY_EXT));
			glTexParameterf(target, EXTTextureFilterAnisotropic.GL_TEXTURE_MAX_ANISOTROPY_EXT, amount);
		}
		return this;
	}
	
}
