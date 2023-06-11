package com.avogine.render.loader.texture;

import java.io.File;
import java.nio.*;

import org.lwjgl.opengl.*;
import org.lwjgl.stb.STBImage;
import org.lwjgl.system.MemoryStack;

import com.avogine.logging.AvoLog;
import com.avogine.render.data.*;
import com.avogine.render.data.texture.*;
import com.avogine.util.resource.*;

/**
 * TODO Add some default textures if anything fails to load
 * <p>
 * Utility class for loading image files into usable OpenGL textures.
 * <p>
 * This class has protected access methods and as such should be used through the {@link TextureCache} to avoid
 * loading duplicate assets.
 * @author Dominus
 *
 */
public class TextureLoader {

	private TextureLoader() {
		
	}
	
	/**
	 * Load a single image file into memory and bind it to an OpenGL texture ID.
	 * @param filename The file to be loaded.
	 * @return A {@link TextureAtlas} for the image
	 */
	protected static Texture loadTexture(String filename) {
		int textureID = GL11.glGenTextures();
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureID);
		
		int width;
		int height;
		int channels;
		try (MemoryStack stack = MemoryStack.stackPush()) {
			IntBuffer widthBuffer = stack.mallocInt(1);
			IntBuffer heightBuffer = stack.mallocInt(1);
			IntBuffer nrChannels = stack.mallocInt(1);
			
			String filePath = ResourceConstants.TEXTURE_PATH + filename;
			ByteBuffer fileData = ResourceFileReader.ioResourceToByteBuffer(filePath, 8 * 1024);
			ByteBuffer imageData = STBImage.stbi_load_from_memory(fileData, widthBuffer, heightBuffer, nrChannels, 0);
//			STBImage.stbi_set_flip_vertically_on_load(true);
			if (imageData != null) {
				width = widthBuffer.get();
				height = heightBuffer.get();
				channels = nrChannels.get();
				int format = 0;
				if (channels == 1) {
					format = GL11.GL_RED;
				} else if (channels == 3) {
					format = GL11.GL_RGB;
				} else if (channels == 4) {
					format = GL11.GL_RGBA;
				}
//				GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA8, width, height, 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, imageData);
				GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, format, width, height, 0, format, GL11.GL_UNSIGNED_BYTE, imageData);
				// TODO Add check to some sort of Options object for mipmaps/anisotropic filtering
				GL30.glGenerateMipmap(GL11.GL_TEXTURE_2D);
				// TODO Add customizable options for these when loading textures
				GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR_MIPMAP_LINEAR);
				GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
				GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL11.GL_REPEAT);
				GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL11.GL_REPEAT);
				if (GL.getCapabilities().GL_EXT_texture_filter_anisotropic) {
					// XXX: Extract some global Anisotropic filtering value
					float amount = Math.min(4f, GL11.glGetFloat(EXTTextureFilterAnisotropic.GL_MAX_TEXTURE_MAX_ANISOTROPY_EXT));
					GL11.glTexParameterf(GL11.GL_TEXTURE_2D, EXTTextureFilterAnisotropic.GL_TEXTURE_MAX_ANISOTROPY_EXT, amount);
				}
				STBImage.stbi_image_free(imageData);
			} else {
				AvoLog.log().error("Texture failed to load at path: {}", filePath);
				return null;
			}
		}
		
		return new TextureAtlas(textureID, width, height);
	}
	
	/**
	 * 
	 * @param filename
	 * @param columns
	 * @param rows
	 * @return
	 */
	protected static Texture loadTextureAtlas(String filename, int columns, int rows) {
		int textureID = GL11.glGenTextures();
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureID);
		
		int width;
		int height;
		try (MemoryStack stack = MemoryStack.stackPush()) {
			IntBuffer widthBuffer = stack.mallocInt(1);
			IntBuffer heightBuffer = stack.mallocInt(1);
			IntBuffer nrChannels = stack.mallocInt(1);
			
			String filePath = ResourceConstants.TEXTURE_PATH + filename;
			ByteBuffer fileData = ResourceFileReader.ioResourceToByteBuffer(filePath, 8 * 1024);
			ByteBuffer imageData = STBImage.stbi_load_from_memory(fileData, widthBuffer, heightBuffer, nrChannels, 0);
//			STBImage.stbi_set_flip_vertically_on_load(true);
			if (imageData != null) {
				width = widthBuffer.get();
				height = heightBuffer.get();
				GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA8, width, height, 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, imageData);
				// TODO Add check to some sort of Options object for mipmaps/anisotropic filtering
				GL30.glGenerateMipmap(GL11.GL_TEXTURE_2D);
				// TODO Add customizable options for these when loading textures
				GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
				GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
				GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL11.GL_REPEAT);
				GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL11.GL_REPEAT);
				if (GL.getCapabilities().GL_EXT_texture_filter_anisotropic) {
					// XXX: Extract some global Anisotropic filtering value
					float amount = Math.min(4f, GL11.glGetFloat(EXTTextureFilterAnisotropic.GL_MAX_TEXTURE_MAX_ANISOTROPY_EXT));
					GL11.glTexParameterf(GL11.GL_TEXTURE_2D, EXTTextureFilterAnisotropic.GL_TEXTURE_MAX_ANISOTROPY_EXT, amount);
				}
			} else {
				AvoLog.log().error("Texture failed to load at path: {}", filePath);
				return null;
			}
			STBImage.stbi_image_free(imageData);
		}
		
		return new TextureAtlas(textureID, width, height, columns, rows);
	}
	
	/**
	 * Load six image files into memory and bind each to a singular OpenGL texture cube map.
	 * @param filenames The images to construct a cubemap from, these should consist of 6 images, and be ordered from {@link GL13#GL_TEXTURE_CUBE_MAP_POSITIVE_X} to {@link GL13#GL_TEXTURE_CUBE_MAP_NEGATIVE_Z}
	 * @return A {@link Cubemap} for the images
	 */
	protected static Texture loadCubemap(String... filenames) {
		if (filenames.length != 6) {
			throw new IllegalArgumentException("Can't load a cubemap without 6 textures!");
		}
		
		int textureID = GL11.glGenTextures();
		GL11.glBindTexture(GL13.GL_TEXTURE_CUBE_MAP, textureID);

		IntBuffer width;
		IntBuffer height;
		IntBuffer nrChannels;
		// Load an image for each side of the cube, reallocating the IntBuffers each time
		for (int i = 0; i < filenames.length; i++) {
			try (MemoryStack stack = MemoryStack.stackPush()) {
				width = stack.mallocInt(1);
				height = stack.mallocInt(1);
				nrChannels = stack.mallocInt(1);
				String filePath = ResourceConstants.TEXTURE_PATH + filenames[i];
				ByteBuffer fileData = ResourceFileReader.ioResourceToByteBuffer(filePath, 8 * 1024);
				ByteBuffer imageData = STBImage.stbi_load_from_memory(fileData, width, height, nrChannels, 0);
				if (imageData != null) {
					GL11.glTexImage2D(GL13.GL_TEXTURE_CUBE_MAP_POSITIVE_X + i, 0, GL11.GL_RGBA8, width.get(), height.get(), 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, imageData);
					STBImage.stbi_image_free(imageData);
				} else {
					AvoLog.log().error("Cubemap texture failed to load at path: {}", filePath);
				}
			}
		}
		GL11.glTexParameteri(GL13.GL_TEXTURE_CUBE_MAP, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
		GL11.glTexParameteri(GL13.GL_TEXTURE_CUBE_MAP, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
		GL11.glTexParameteri(GL13.GL_TEXTURE_CUBE_MAP, GL11.GL_TEXTURE_WRAP_S, GL12.GL_CLAMP_TO_EDGE);
		GL11.glTexParameteri(GL13.GL_TEXTURE_CUBE_MAP, GL11.GL_TEXTURE_WRAP_T, GL12.GL_CLAMP_TO_EDGE);
		GL11.glTexParameteri(GL13.GL_TEXTURE_CUBE_MAP, GL12.GL_TEXTURE_WRAP_R, GL12.GL_CLAMP_TO_EDGE);

		return new Cubemap(textureID);
	}
	
	/**
	 * Load a cube map texture from a specified directory of textures.
	 * <p>
	 * This method assumes the given directory is a valid directory and contains at least 6 files named:
	 * <ul>
	 * <li>right
	 * <li>left
	 * <li>top
	 * <li>bottom
	 * <li>front
	 * <li>back
	 * </ul>
	 * @param directoryName The directory name contained in {@link ResourceConstants#TEXTURE_PATH}
	 * @return
	 */
	protected static Texture loadCubemap(String directoryName, String fileType) {
		String texturePathPrefix = directoryName + File.separator;
		return loadCubemap(
				texturePathPrefix + "right." + fileType,
				texturePathPrefix + "left." + fileType,
				texturePathPrefix + "top." + fileType,
				texturePathPrefix + "bottom." + fileType,
				texturePathPrefix + "front." + fileType,
				texturePathPrefix + "back." + fileType);
	}
	
	/**
	 * Create a new {@code Texture} with a specified size but do not fill it with any actual data.
	 * <p>
	 * This should be used exclusively for {@link FrameBuffer}s as the returned {@code Texture} object <b>will not</b> be
	 * stored in the {@link TextureCache} and is the responsibility of the creator to handle cleanup through whatever
	 * containing object creates this {@code Texture}.
	 * @param width the width of the Texture
	 * @param height the height of the Texture
	 * @return a new {@code Texture} with a specified size that does not contain any actual data.
	 */
	public static Texture createEmptyTexture(int width, int height) {
		int textureID = GL11.glGenTextures();
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureID);
		
		GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA8, width, height, 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, (ByteBuffer) null);
		
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
		
		return new TextureAtlas(textureID, width, height);
	}
	
}
