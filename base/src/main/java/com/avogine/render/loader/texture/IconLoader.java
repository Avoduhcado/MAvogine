package com.avogine.render.loader.texture;

import java.nio.*;
import java.util.*;

import org.lwjgl.glfw.*;
import org.lwjgl.glfw.GLFWImage.Buffer;
import org.lwjgl.stb.*;
import org.lwjgl.system.*;

import com.avogine.util.resource.*;

/**
 * TODO Move this into a more suitable module? It not only deals with loading, which is necessary to have {@code STB}, but also handles setting the actual icons to avoid memory cleanup issues.
 * <p>
 * Utility class for loading images into raw {@link GLFWImage}s.
 * <p>
 * XXX Whether or not the memory here is properly freed is still unclear as calling free from {@code STB} causes artifacts in the icons,
 * calling free from the actual {@link GLFWImage} crashes the JVM, but is just using {@link MemoryStack} enough?
 * @author Dominus
 *
 */
public class IconLoader {

	/**
	 * 
	 * @param filename
	 * @param stack
	 * @return
	 */
	public static GLFWImage loadIcon(String filename, MemoryStack stack) {
		GLFWImage icon = null;
		
		IntBuffer width = stack.mallocInt(1);
		IntBuffer height = stack.mallocInt(1);
		IntBuffer nrChannels = stack.mallocInt(1);

		String filePath = ResourceConstants.TEXTURE_PATH + filename;
		ByteBuffer fileData = ResourceFileReader.ioResourceToByteBuffer(filePath, 1024);
		ByteBuffer imageData = STBImage.stbi_load_from_memory(fileData, width, height, nrChannels, 4);
		if (imageData != null) {
			icon = GLFWImage.mallocStack(stack);
			icon.set(width.get(), height.get(), imageData);
		} else {
			System.err.println("Icon failed to load: " + filePath);
		}
		
		return icon;
	}
	
	/**
	 * 
	 * @param window
	 * @param directoryName
	 */
	public static void loadAndSetIcons(long window, String directoryName) {
		//Set<Path> files = ResourceFileReader.listResourcePaths(ResourceConstants.TEXTURE_PATH + directoryName);
		Set<String> files = ResourceFileReader.listFileNames(ResourceConstants.TEXTURE_PATH + directoryName);
		if (files == null || files.isEmpty()) {
			return;
		}
		
		Buffer iconBuffer = null;
		
		try (MemoryStack stack = MemoryStack.stackPush()) {
			iconBuffer = GLFWImage.mallocStack(files.size(), stack);
			// TODO Add file filter for image files only
			for (String filePath : files) {
				iconBuffer.put(loadIcon(directoryName + "/" + filePath, stack));
			}
			
			iconBuffer.flip();
			
			GLFW.glfwSetWindowIcon(window, iconBuffer);
		}
	}
	
}
