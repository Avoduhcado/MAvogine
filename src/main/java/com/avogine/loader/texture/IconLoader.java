package com.avogine.loader.texture;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import org.lwjgl.glfw.GLFWImage;
import org.lwjgl.glfw.GLFWImage.Buffer;
import org.lwjgl.stb.STBImage;
import org.lwjgl.system.MemoryStack;

import com.avogine.core.resource.util.ResourceConstants;
import com.avogine.core.resource.util.ResourceFileReader;

public class IconLoader {

	public static GLFWImage loadIcon(String filename, MemoryStack stack) {
		GLFWImage icon = null;
		
		IntBuffer width = stack.mallocInt(1);
		IntBuffer height = stack.mallocInt(1);
		IntBuffer nrChannels = stack.mallocInt(1);

		String filePath = ResourceConstants.TEXTURE_PATH + filename;
		ByteBuffer fileData = ResourceFileReader.readResourceToByteBuffer(filePath);
		ByteBuffer imageData = STBImage.stbi_load_from_memory(fileData, width, height, nrChannels, 4);
		if (imageData != null) {
			icon = GLFWImage.mallocStack(stack);
			icon.set(width.get(), height.get(), imageData);

			STBImage.stbi_image_free(imageData);
		} else {
			System.err.println("Icon failed to load: " + filePath);
		}
		
		return icon;
	}
	
	public static Buffer loadIcons(String directoryName) {
		// Fail fast if we were not given a valid directory
		URL url = IconLoader.class.getClassLoader().getResource(ResourceConstants.TEXTURE_PATH + directoryName);
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
		
		Buffer iconBuffer = null;
		
		try (MemoryStack stack = MemoryStack.stackPush()) {
			iconBuffer = GLFWImage.mallocStack(directory.list().length, stack);
			// TODO Add file filter for image files only
			for (String filename : directory.list()) {
				IntBuffer width = stack.mallocInt(1);
				IntBuffer height = stack.mallocInt(1);
				IntBuffer nrChannels = stack.mallocInt(1);

				String filePath = ResourceConstants.TEXTURE_PATH + directoryName + System.getProperty("file.separator") + filename;
				ByteBuffer fileData = ResourceFileReader.readResourceToByteBuffer(filePath);
				ByteBuffer imageData = STBImage.stbi_load_from_memory(fileData, width, height, nrChannels, 4);
				if (imageData != null) {
					GLFWImage icon = GLFWImage.mallocStack(stack);
					icon.set(width.get(), height.get(), imageData);
					
					// XXX Freeing the image seems to cause artifacts to appear in the icon
					//STBImage.stbi_image_free(imageData);
					iconBuffer.put(icon);
				} else {
					System.err.println("Icon failed to load: " + filePath);
				}
			}
			
			iconBuffer.flip();
		}
		
		return iconBuffer;
	}
	
}
