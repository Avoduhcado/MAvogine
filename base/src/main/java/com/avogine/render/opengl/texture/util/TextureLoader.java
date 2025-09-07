package com.avogine.render.opengl.texture.util;

import static org.lwjgl.opengl.GL11.GL_RGBA;

import java.nio.ByteBuffer;

import org.lwjgl.system.MemoryStack;

import com.avogine.render.image.data.ImageData;
import com.avogine.render.image.util.ImageLoader;
import com.avogine.render.opengl.texture.Texture;
import com.avogine.render.opengl.texture.Texture.TextureBuilder.Image2D;

/**
 *
 */
public class TextureLoader {

	private TextureLoader() {
		
	}
	
	/**
	 * Do not use this to allocate a texture larger than MemoryStack's stack size.
	 * @param width
	 * @param height
	 * @param r
	 * @param g
	 * @param b
	 * @param a
	 * @return
	 */
	public static Texture loadRawTexture(int width, int height, byte r, byte g, byte b, byte a) {
		try (MemoryStack stack = MemoryStack.stackPush()) {
			ByteBuffer pixels = stack.malloc(width * height * 4);
			while (pixels.hasRemaining()) {
				pixels.put(r).put(g).put(b).put(a);
			}
			pixels.flip();
			
			return Texture.gen2D(texture -> texture
					.texFilterLinear()
					.texWrap2DRepeat()
					.texImage2D(width, height, GL_RGBA, pixels));
		}
	}
	
	/**
	 * @param texturePath
	 * @return
	 */
	public static Texture loadTexture(String texturePath) {
		try (ImageData imageData = ImageLoader.loadImage(texturePath)) {
			return Texture.gen2D(texture -> texture
					.texFilterLinear()
					.texWrap2DRepeat()
					.tex(Image2D.fromImage(imageData))
					.generateMipmap()
					.anisotropicFiltering());
		}
	}
	
	/**
	 * @param posXImagePath
	 * @param negXImagePath
	 * @param posYImagePath
	 * @param negYImagePath
	 * @param posZImagePath
	 * @param negZImagePath
	 * @return
	 */
	public static Texture loadCubemap(String posXImagePath, String negXImagePath, String posYImagePath, String negYImagePath, String posZImagePath, String negZImagePath) {
		try (ImageData posXImageData = ImageLoader.loadImage(posXImagePath);
				ImageData negXImageData = ImageLoader.loadImage(negXImagePath);
				ImageData posYImageData = ImageLoader.loadImage(posYImagePath);
				ImageData negYImageData = ImageLoader.loadImage(negYImagePath);
				ImageData posZImageData = ImageLoader.loadImage(posZImagePath);
				ImageData negZImageData = ImageLoader.loadImage(negZImagePath);) {
			return Texture.genCubeMap(cubeMap -> cubeMap
					.texFilterLinear()
					.texWrap3DClampToEdge()
					.texCubeMap(Image2D.fromImage(posXImageData),
							Image2D.fromImage(negXImageData),
							Image2D.fromImage(posYImageData),
							Image2D.fromImage(negYImageData),
							Image2D.fromImage(posZImageData),
							Image2D.fromImage(negZImageData)));
		}
	}
	
	/**
	 * Load images from a specified directory and bind to a new cubemap {@link Texture}.
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
	 * @param directory The directory name.
	 * @param fileType The file extension of the images to load. This should be the same for all images.
	 * @return 
	 */
	public static Texture loadCubemap(String directory, String fileType) {
		return loadCubemap(directory + "/right." + fileType,
				directory + "/left." + fileType,
				directory + "/top." + fileType,
				directory + "/bottom." + fileType,
				directory + "/front." + fileType,
				directory + "/back." + fileType);
	}
}
