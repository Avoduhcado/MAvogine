package com.avogine.render.opengl;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL12.*;

import java.nio.*;

import org.lwjgl.opengl.*;

import com.avogine.render.image.ImageData;

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
	
	public record Parameteri(int pname, int param) {
		
		public void set(int target) {
			glTexParameteri(target, pname, param);
		}
		
	}
	
	public Texture tex(Parameteri parameter) {
		parameter.set(target);
		return this;
	}
	
	public Texture tex(Parameteri...parameters) {
		for (var parameter : parameters) {
			parameter.set(target);
		}
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
	
	public record Image2D(int level, int internalFormat, int width, int height, int format, int type, Buffer pixels) {
		/**
		 * @param imageData
		 * @return
		 */
		public static Image2D from(ImageData imageData) {
			return new Image2D(0, Texture.internalFormat(imageData.getFormat()), imageData.getWidth(), imageData.getHeight(), imageData.getFormat(), GL_UNSIGNED_BYTE, imageData.getPixelBuffers().pixels());
		}
		
		/**
		 * @param target
		 */
		public void specify(int target) {
			switch (pixels) {
				case ByteBuffer b -> glTexImage2D(target, level, internalFormat, width, height, 0, format, type, b);
				case IntBuffer i -> glTexImage2D(target, level, internalFormat, width, height, 0, format, type, i);
				case null -> glTexImage2D(target, 0, internalFormat, width, height, 0, format, type, (ByteBuffer) null);
				default -> throw new IllegalArgumentException("Buffer type not implemented for: " + pixels.getClass());
			}
		}
	}
	
	public record Image2DTarget(int target, Image2D image2D) {
		
		public static Image2DTarget of(int target, Image2D image2D) {
			return new Image2DTarget(target, image2D);
		}
		
	}
	
	private static int internalFormat(int format) {
		return switch (format) {
			case GL_RGBA -> GL_RGBA8;
			default -> format;
		};
	}
	
	public Texture tex(Image2D image2D) {
		image2D.specify(target);
		return this;
	}
	
	public Texture tex(Image2DTarget...image2dTargets) {
		for (var image2DTarget : image2dTargets) {
			if (image2DTarget instanceof Image2DTarget(var target, var image2D)) {
				image2D.specify(target);
			}
		}
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
	 * @param target
	 * @param internalFormat
	 * @param width
	 * @param height
	 * @param format
	 * @param pixels
	 * @return this
	 */
	public Texture texImage2DTarget(int target, int internalFormat, int width, int height, int format, Buffer pixels) {
		return texImage2DTarget(target, internalFormat, width, height, format, GL_UNSIGNED_BYTE, pixels);
	}
	
	/**
	 * @param target
	 * @param width
	 * @param height
	 * @param format
	 * @param pixels
	 * @return
	 */
	public Texture texImage2DTarget(int target, int width, int height, int format, Buffer pixels) {
		return texImage2DTarget(target, internalFormat(format), width, height, format, GL_UNSIGNED_BYTE, pixels);
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
	 * @param internalFormat
	 * @param width
	 * @param height
	 * @param format
	 * @param pixels
	 * @return this
	 */
	public Texture texImage2D(int internalFormat, int width, int height, int format, Buffer pixels) {
		return texImage2DTarget(target, internalFormat, width, height, format, pixels);
	}
	
	/**
	 * @param width
	 * @param height
	 * @param format
	 * @param pixels
	 * @return
	 */
	public Texture texImage2D(int width, int height, int format, Buffer pixels) {
		return texImage2D(internalFormat(format), width, height, format, pixels);
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
