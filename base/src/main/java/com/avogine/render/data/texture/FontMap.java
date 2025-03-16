package com.avogine.render.data.texture;

import static org.lwjgl.opengl.GL11.*;

import java.nio.ByteBuffer;

import org.lwjgl.opengl.*;

/**
 *
 */
public class FontMap extends Texture {

	/**
	 * @param width
	 * @param height
	 * @param buffer
	 */
	public FontMap(int width, int height, ByteBuffer buffer) {
		super("");
		generateTexture(width, height, buffer);
	}
	
	@Override
	protected void loadImageData() {
		// No-op, rely on generateTexture()
	}
	
	private void generateTexture(int width, int height, ByteBuffer buffer) {
		id = glGenTextures();
		
		bind();
		glPixelStorei(GL11.GL_UNPACK_ALIGNMENT, 1); // 8 bpp = 1 byte per pixel
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);
		glTexImage2D(GL_TEXTURE_2D, 0, GL30.GL_R8, width, height, 0, GL_RED, GL_UNSIGNED_BYTE, buffer);
	}

}
