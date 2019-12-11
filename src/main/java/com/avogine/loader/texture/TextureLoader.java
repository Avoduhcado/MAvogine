package com.avogine.loader.texture;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL14;
import org.lwjgl.opengl.GL30;
import org.lwjgl.stb.STBImage;
import org.lwjgl.system.MemoryStack;

import com.avogine.core.render.Cubemap;
import com.avogine.core.render.Texture;
import com.avogine.core.resource.util.ResourceConstants;
import com.avogine.core.resource.util.ResourceFileReader;

/**
 * <p>Utility class for loading image files into usable OpenGL textures.
 * 
 * <p>This class has protected access methods and as such should be used through the {@link TextureCache} to avoid
 * loading duplicate assets.
 * @author Dominus
 *
 */
public class TextureLoader {

	/**
	 * Load a single image file into memory and bind it to a OpenGL texture ID.
	 * @param filename The file to be loaded.
	 * @return A {@link Texture} for the image
	 */
	protected static Texture loadTexture(String filename) {
		int textureID = GL11.glGenTextures();
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureID);
		
		int width;
		int height;
		try (MemoryStack stack = MemoryStack.stackPush()) {
			IntBuffer widthBuffer = stack.mallocInt(1);
			IntBuffer heightBuffer = stack.mallocInt(1);
			IntBuffer nrChannels = stack.mallocInt(1);
			
			String filePath = ResourceConstants.TEXTURE_PATH + filename;
			ByteBuffer fileData = ResourceFileReader.readResourceToByteBuffer(filePath);
			ByteBuffer imageData = STBImage.stbi_load_from_memory(fileData, widthBuffer, heightBuffer, nrChannels, 0);
			if (imageData != null) {
				width = widthBuffer.get();
				height = heightBuffer.get();
				GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGB, width, height, 0, GL11.GL_RGB, GL11.GL_UNSIGNED_BYTE, imageData);
				STBImage.stbi_image_free(imageData);
			} else {
				System.err.println("Texture failed to load at path: " + filePath);
				return null;
			}
		}
		// TODO Add customizable options for these when loading textures
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL12.GL_CLAMP_TO_EDGE);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL12.GL_CLAMP_TO_EDGE);
		// TODO Add check to some sort of Options object for mipmaps/anisotropic filtering
		GL30.glGenerateMipmap(GL11.GL_TEXTURE_2D);
		
		return new Texture(textureID, width, height);
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
		GL11.glBindTexture(GL14.GL_TEXTURE_CUBE_MAP, textureID);

		IntBuffer width, height, nrChannels;
		// Load an image for each side of the cube, reallocating the IntBuffers each time
		for (int i = 0; i < filenames.length; i++) {
			try (MemoryStack stack = MemoryStack.stackPush()) {
				width = stack.mallocInt(1);
				height = stack.mallocInt(1);
				nrChannels = stack.mallocInt(1);
				String filePath = ResourceConstants.TEXTURE_PATH + filenames[i];
				ByteBuffer fileData = ResourceFileReader.readResourceToByteBuffer(filePath);
				ByteBuffer imageData = STBImage.stbi_load_from_memory(fileData, width, height, nrChannels, 0);
				if (imageData != null) {
					GL11.glTexImage2D(GL13.GL_TEXTURE_CUBE_MAP_POSITIVE_X + i, 0, GL11.GL_RGB, width.get(), height.get(), 0, GL11.GL_RGB, GL11.GL_UNSIGNED_BYTE, imageData);
					STBImage.stbi_image_free(imageData);
				} else {
					System.err.println("Cubemap texture failed to load at path: " + filePath);
				}
			}
		}
		GL11.glTexParameteri(GL14.GL_TEXTURE_CUBE_MAP, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
		GL11.glTexParameteri(GL14.GL_TEXTURE_CUBE_MAP, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
		GL11.glTexParameteri(GL14.GL_TEXTURE_CUBE_MAP, GL11.GL_TEXTURE_WRAP_S, GL12.GL_CLAMP_TO_EDGE);
		GL11.glTexParameteri(GL14.GL_TEXTURE_CUBE_MAP, GL11.GL_TEXTURE_WRAP_T, GL12.GL_CLAMP_TO_EDGE);
		GL11.glTexParameteri(GL14.GL_TEXTURE_CUBE_MAP, GL12.GL_TEXTURE_WRAP_R, GL12.GL_CLAMP_TO_EDGE);

		return new Cubemap(textureID);
	}
	
	/**
	 * <p>Load a cube map texture from a specified directory of textures.
	 * 
	 * <p>This method assumes the given directory is a valid directory and contains at least 6 files named:
	 * <ul>
	 * <li><tt>right.jpg
	 * <li>left.jpg
	 * <li>top.jpg
	 * <li>bottom.jpg
	 * <li>front.jpg
	 * <li>back.jpg</tt>
	 * </ul>
	 * @param directoryName The directory name contained in {@link ResourceConstants#TEXTURE_PATH}
	 * @return
	 */
	protected static Texture loadCubemap(String directoryName) {
		// Fail fast if we were not given a valid directory
		URL url = TextureLoader.class.getClassLoader().getResource(ResourceConstants.TEXTURE_PATH + directoryName);
		File directory;
		try {
			directory = new File(url.toURI().getPath());
			if (!directory.isDirectory()) {
				return null;
			}
		} catch (URISyntaxException e) {
			e.printStackTrace();
			return null;
		}
		
		String texturePathPrefix = directoryName + ResourceConstants.SEPARATOR;
		return loadCubemap(texturePathPrefix + "right.jpg", texturePathPrefix + "left.jpg", texturePathPrefix + "top.jpg", texturePathPrefix + "bottom.jpg", texturePathPrefix + "front.jpg", texturePathPrefix + "back.jpg");
	}
	
}
