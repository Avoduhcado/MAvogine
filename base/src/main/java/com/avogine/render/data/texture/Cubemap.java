package com.avogine.render.data.texture;

import java.nio.*;
import java.util.Arrays;
import java.util.stream.Collectors;

import org.lwjgl.PointerBuffer;
import org.lwjgl.opengl.*;
import org.lwjgl.stb.STBImage;
import org.lwjgl.system.MemoryStack;

import com.avogine.logging.AvoLog;
import com.avogine.util.ResourceUtils;

/**
 * A {@link Texture} sub type that overrides specific texture binding operations to use cube mapping.
 */
public class Cubemap extends Texture {
	
	/**
	 * Load six image files into memory and bind each to a singular OpenGL texture cube map.
	 * @param filePaths The images to construct a cube map from, these should consist of 6 images, and be ordered from {@link GL13#GL_TEXTURE_CUBE_MAP_POSITIVE_X} to {@link GL13#GL_TEXTURE_CUBE_MAP_NEGATIVE_Z}
	 */
	public Cubemap(String...filePaths) {
		super(Arrays.stream(filePaths).collect(Collectors.joining(";")));
	}
	
	/**
	 * Load a cube map texture from a specified directory of textures.
	 * <p>
	 * This assumes the given directory is a valid directory and contains at least 6 files named:
	 * <ul>
	 * <li>right
	 * <li>left
	 * <li>top
	 * <li>bottom
	 * <li>front
	 * <li>back
	 * </ul>
	 * @param directoryName The directory name.
	 * @param fileExtension The file extension of the images to load. This should be the same for all images.
	 */
	public Cubemap(String directoryName, String fileExtension) {
		this(directoryName + "/right." + fileExtension,
				directoryName + "/left." + fileExtension,
				directoryName + "/top." + fileExtension,
				directoryName + "/bottom." + fileExtension,
				directoryName + "/front." + fileExtension,
				directoryName + "/back." + fileExtension);
	}
	
	@Override
	public void bind() {
		GL11.glBindTexture(GL13.GL_TEXTURE_CUBE_MAP, id);
	}
	
	@Override
	protected void loadImageData() {
		String[] filePaths = texturePath.split(";");
		if (filePaths.length != 6) {
			throw new IllegalArgumentException("Can't load a cubemap without 6 textures!");
		}
		
		try (MemoryStack stack = MemoryStack.stackPush()) {
			IntBuffer widthBuffer = stack.mallocInt(6);
			IntBuffer heightBuffer = stack.mallocInt(6);
			IntBuffer channelsBuffer = stack.mallocInt(6);
			PointerBuffer pBuffer = stack.mallocPointer(6);
			IntBuffer dataSizeBuffer = stack.mallocInt(6);

			for (String filePath : filePaths) {
				ByteBuffer fileData = ResourceUtils.readResourceToBuffer(filePath);
				ByteBuffer imageData = STBImage.stbi_load_from_memory(fileData, widthBuffer, heightBuffer, channelsBuffer, 0);
				if (imageData == null) {
					AvoLog.log().error("Cube map texture failed to load at path: {}", filePath);
					throw new IllegalArgumentException();
				}
				widthBuffer.get();
				heightBuffer.get();
				channelsBuffer.get();
				pBuffer.put(imageData);
				dataSizeBuffer.put(imageData.capacity());
			}
			
			widthBuffer.flip();
			heightBuffer.flip();
			channelsBuffer.flip();
			pBuffer.flip();
			dataSizeBuffer.flip();
			
			generateCubemapTexture(widthBuffer, heightBuffer, channelsBuffer, pBuffer, dataSizeBuffer);
		} finally {
			GL11.glBindTexture(GL13.GL_TEXTURE_CUBE_MAP, 0);
		}
	}
	
	private void generateCubemapTexture(IntBuffer widthBuffer, IntBuffer heightBuffer, IntBuffer channelsBuffer, PointerBuffer imageBuffer, IntBuffer dataSizeBuffer) {
		id = GL11.glGenTextures();
		
		bind();
		GL11.glTexParameteri(GL13.GL_TEXTURE_CUBE_MAP, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
		GL11.glTexParameteri(GL13.GL_TEXTURE_CUBE_MAP, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
		GL11.glTexParameteri(GL13.GL_TEXTURE_CUBE_MAP, GL11.GL_TEXTURE_WRAP_S, GL12.GL_CLAMP_TO_EDGE);
		GL11.glTexParameteri(GL13.GL_TEXTURE_CUBE_MAP, GL11.GL_TEXTURE_WRAP_T, GL12.GL_CLAMP_TO_EDGE);
		GL11.glTexParameteri(GL13.GL_TEXTURE_CUBE_MAP, GL12.GL_TEXTURE_WRAP_R, GL12.GL_CLAMP_TO_EDGE);
		for (int i = GL13.GL_TEXTURE_CUBE_MAP_POSITIVE_X; i <= GL13.GL_TEXTURE_CUBE_MAP_NEGATIVE_Z; i++) {
			int channels = channelsBuffer.get();
			int format = switch (channels) {
				case 1 -> GL11.GL_RED;
				case 3 -> GL11.GL_RGB;
				case 4 -> GL11.GL_RGBA;
				default -> {
					AvoLog.log().warn("Cube map texture was loaded with channel count: [{}] defaulting to GL_RED.", channels);
					yield GL11.GL_RED;
				}
			};
			ByteBuffer imageData = imageBuffer.getByteBuffer(dataSizeBuffer.get());
			GL11.glTexImage2D(i, 0, format, widthBuffer.get(), heightBuffer.get(), 0, format, GL11.GL_UNSIGNED_BYTE, imageData);
			STBImage.stbi_image_free(imageData);
		}
	}
	
}
