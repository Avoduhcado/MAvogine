package com.avogine.render.image.util;

import java.nio.*;

import org.lwjgl.stb.STBImage;
import org.lwjgl.system.MemoryStack;

import com.avogine.logging.AvoLog;
import com.avogine.render.image.data.ImageData;
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
	public static ImageData loadImage(String imagePath, boolean flipVerticallyOnLoad) {
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
			
			return new ImageData(width, height, channels, imageBuffer);
		}
	}
	
	/**
	 * @param imagePath the relative resource location of the image file to load.
	 * @return an {@link ImageData} containing image data of the loaded file and the contained pixels buffer.
	 */
	public static ImageData loadImage(String imagePath) {
		return loadImage(imagePath, false);
	}
	
}
