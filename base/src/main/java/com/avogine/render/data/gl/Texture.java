package com.avogine.render.data.gl;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL12.*;

import java.nio.*;

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
	 * @return this
	 */
	public Texture filterLinear() {
		glTexParameteri(target, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
		glTexParameteri(target, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
		return this;
	}
	
	/**
	 * @return this
	 */
	public Texture filterNearest() {
		glTexParameteri(target, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
		glTexParameteri(target, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
		return this;
	}
	
	/**
	 * @return this
	 */
	public Texture wrap2DRepeat() {
		glTexParameteri(target, GL_TEXTURE_WRAP_S, GL_REPEAT);
		glTexParameteri(target, GL_TEXTURE_WRAP_T, GL_REPEAT);
		return this;
	}
	
	/**
	 * @return this
	 */
	public Texture wrap3DClampEdge() {
		glTexParameteri(target, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
		glTexParameteri(target, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
		glTexParameteri(target, GL_TEXTURE_WRAP_R, GL_CLAMP_TO_EDGE);
		return this;
	}
	
	/**
	 * @param target
	 * @param internalFormat
	 * @param width
	 * @param height
	 * @param format
	 * @param type
	 * @param pixels
	 * @return
	 */
	public Texture texImage2DTarget(int target, int internalFormat, int width, int height, int format, int type, Buffer pixels) {
		switch (pixels) {
			case ByteBuffer b -> glTexImage2D(target, 0, internalFormat, width, height, 0, format, type, b);
			case IntBuffer i -> glTexImage2D(target, 0, internalFormat, width, height, 0, format, type, i);
			case null -> glTexImage2D(target, 0, internalFormat, width, height, 0, format, type, (ByteBuffer) null);
			default -> throw new IllegalArgumentException("Buffer type not implemented for: " + pixels.getClass());
		}
		return this;
	}
	
	/**
	 * @param internalFormat
	 * @param width
	 * @param height
	 * @param format
	 * @param type
	 * @param pixels
	 * @return
	 */
	public Texture texImage2D(int internalFormat, int width, int height, int format, int type, Buffer pixels) {
		return texImage2DTarget(target, internalFormat, width, height, format, type, pixels);
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
	public Texture texImage2DTarget(int target, int internalFormat, int width, int height, int format, ByteBuffer pixels) {
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
		return texImage2DTarget(target, internalFormat, width, height, format, pixels);
	}
	
	/**
	 * @param target 
	 * @param image2D
	 * @return this
	 */
	public Texture texImage2DTarget(int target, Image2D image2D) {
		return texImage2DTarget(target, image2D.internalFormat(), image2D.width(), image2D.height(), image2D.format(), image2D.pixels());
	}
	
	/**
	 * @param image2D
	 * @return this
	 */
	public Texture texImage2D(Image2D image2D) {
		return texImage2DTarget(target, image2D);
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
