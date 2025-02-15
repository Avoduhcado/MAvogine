package com.avogine.render.data.texture;

import static org.lwjgl.opengl.GL11.*;

import java.nio.*;

import org.lwjgl.opengl.*;
import org.lwjgl.stb.STBImage;
import org.lwjgl.system.MemoryStack;

import com.avogine.logging.AvoLog;
import com.avogine.util.ResourceUtil;

/**
 *
 */
public class Texture {
	
	protected int id;
	protected String texturePath;

	/**
	 * Load a single image file into memory and bind it to an OpenGL texture ID.
	 * @param filePath The resource file to be loaded.
	 */
	public Texture(String filePath) {
		this.texturePath = filePath;
		loadImageData();
	}
	
	/**
	 * @param width
	 * @param height
	 * @param buffer
	 */
	public Texture(int width, int height, ByteBuffer buffer) {
		this.texturePath = "";
		generateTexture(width, height, GL_RGBA, buffer);
		glBindTexture(GL_TEXTURE_2D, 0);
	}
	
	/**
	 * 
	 */
	public void bind() {
		glBindTexture(GL_TEXTURE_2D, id);
	}

	/**
	 * Free any memory allocated by this Texture.
	 */
	public void cleanup() {
		glDeleteTextures(getId());
	}
	
	protected void loadImageData() {
		try (MemoryStack stack = MemoryStack.stackPush()) {
			IntBuffer widthBuffer = stack.mallocInt(1);
			IntBuffer heightBuffer = stack.mallocInt(1);
			IntBuffer channelsBuffer = stack.mallocInt(1);
			
			ByteBuffer fileBuffer = ResourceUtil.readResourceToBuffer(texturePath);
			ByteBuffer imageBuffer = STBImage.stbi_load_from_memory(fileBuffer, widthBuffer, heightBuffer, channelsBuffer, 0);
			if (imageBuffer == null) {
				AvoLog.log().error("Texture failed to load at path: {}", texturePath);
				throw new IllegalArgumentException();
			}
			
			int width = widthBuffer.get();
			int height = heightBuffer.get();
			int channels = channelsBuffer.get();
			int format = switch (channels) {
				case 1 -> GL_RED;
				case 3 -> GL_RGB;
				case 4 -> GL_RGBA;
				default -> {
					AvoLog.log().warn("Texture: [{}] was loaded with channel count: [{}] defaulting to GL_RED.", texturePath, channels);
					yield GL_RED;
				}
			};
			
			generateTexture(width, height, format, imageBuffer);
			
			STBImage.stbi_image_free(imageBuffer);
		} finally {
			glBindTexture(GL_TEXTURE_2D, 0);
		}
	}
	
	/**
	 * <a href="https://github.com/Avoduhcado/MAvogine/issues/40">Configurable texture loading options #40</a>
	 */
	private void generateTexture(int width, int height, int format, ByteBuffer buffer) {
		id = glGenTextures();
		
		bind();
		// TODO#40 Add customizable options for these when loading textures
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR_MIPMAP_LINEAR);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);
		glTexImage2D(GL_TEXTURE_2D, 0, format, width, height, 0, format, GL_UNSIGNED_BYTE, buffer); // internalFormat and format here are using the same value, which seems to work in most cases.
		// TODO#40 Add check to some sort of Options object for mipmaps/anisotropic filtering
		GL30.glGenerateMipmap(GL_TEXTURE_2D);
		if (GL.getCapabilities().GL_EXT_texture_filter_anisotropic) {
			// TODO#40: Extract some global Anisotropic filtering value
			float amount = Math.min(4f, glGetFloat(EXTTextureFilterAnisotropic.GL_MAX_TEXTURE_MAX_ANISOTROPY_EXT));
			glTexParameterf(GL_TEXTURE_2D, EXTTextureFilterAnisotropic.GL_TEXTURE_MAX_ANISOTROPY_EXT, amount);
		}
	}
	
	/**
	 * @return The ID of the texture object to bind.
	 */
	public int getId() {
		return id;
	}
	
	/**
	 * @return the texturePath
	 */
	public String getTexturePath() {
		return texturePath;
	}

}
