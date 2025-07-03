package com.avogine.render.opengl.image.util;

import static org.lwjgl.opengl.GL11.*;

import java.nio.*;

import org.lwjgl.stb.STBImage;
import org.lwjgl.system.MemoryStack;

import com.avogine.logging.AvoLog;
import com.avogine.render.image.ImageData;
import com.avogine.render.image.data.STBPixelBuffer;
import com.avogine.util.ResourceUtils;

/**
 * Static utility for loading images with {@link STBImage}.
 */
public class ImageLoader {
	
	private ImageLoader() {
		
	}
	
	/**
	 * @param imagePath the relative resource location of the image file to load.
	 * @param flipVerticallyOnLoad true if the image data should be flipped vertically when loading.
	 * @return an {@link ImageData} containing image data of the loaded file and the contained pixels buffer.
	 */
	public static ImageData loadImageSTB(String imagePath, boolean flipVerticallyOnLoad) {
		try (MemoryStack stack = MemoryStack.stackPush()) {
			IntBuffer widthBuffer = stack.mallocInt(1);
			IntBuffer heightBuffer = stack.mallocInt(1);
			IntBuffer channelsBuffer = stack.mallocInt(1);
			
			ByteBuffer fileBuffer = ResourceUtils.readResourceToBuffer(imagePath);
			STBImage.stbi_set_flip_vertically_on_load(flipVerticallyOnLoad);
			ByteBuffer imageBuffer = STBImage.stbi_load_from_memory(fileBuffer, widthBuffer, heightBuffer, channelsBuffer, 0);
			if (imageBuffer == null) {
				AvoLog.log().error("Image failed to load at path: {}", imagePath);
				throw new IllegalArgumentException();
			}
			
			int width = widthBuffer.get(0);
			int height = heightBuffer.get(0);
			int channels = channelsBuffer.get(0);
			int format = switch (channels) {
				case 1 -> GL_RED;
				case 3 -> GL_RGB;
				case 4 -> GL_RGBA;
				default -> {
					AvoLog.log().warn("Image: [{}] was loaded with channel count: [{}] defaulting to GL_RED.", imagePath, channels);
					yield GL_RED;
				}
			};
			
			return new ImageData(width, height, format, new STBPixelBuffer(imageBuffer));
		}
	}
	
	/**
	 * @param imagePath the relative resource location of the image file to load.
	 * @return an {@link ImageData} containing image data of the loaded file and the contained pixels buffer.
	 */
	public static ImageData loadImageSTB(String imagePath) {
		return loadImageSTB(imagePath, false);
	}
	
}
