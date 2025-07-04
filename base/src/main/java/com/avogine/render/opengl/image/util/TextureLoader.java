package com.avogine.render.opengl.image.util;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.*;

import java.nio.ByteBuffer;

import org.lwjgl.system.MemoryStack;

import com.avogine.render.image.ImageData;
import com.avogine.render.opengl.Texture;
import com.avogine.render.opengl.Texture.*;

/**
 *
 */
public class TextureLoader {

	private TextureLoader() {
		
	}
	
	/**
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
			
			return Texture.gen().bind()
					.filterLinear()
					.wrap2DRepeat()
					.texImage2D(GL_RGBA8, width, height, GL_RGBA, pixels);
		} finally {
			Texture.unbind();
		}
	}
	
	/**
	 * @param texturePath
	 * @return
	 */
	public static Texture loadTexture(String texturePath) {
		ImageData imageData = ImageLoader.loadImageSTB(texturePath);
		try (var pixelBuffer = imageData.getPixelBuffers()) {
			return Texture.gen().bind()
					.filterLinear()
					.wrap2DRepeat()
					.texImage2D(imageData.getWidth(), imageData.getHeight(), imageData.getFormat(), pixelBuffer.pixels())
					.generateMipmap()
					.anisotropicFiltering();
		} finally {
			Texture.unbind();
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
		ImageData posXImageData = ImageLoader.loadImageSTB(posXImagePath);
		ImageData negXImageData = ImageLoader.loadImageSTB(negXImagePath);
		ImageData posYImageData = ImageLoader.loadImageSTB(posYImagePath);
		ImageData negYImageData = ImageLoader.loadImageSTB(negYImagePath);
		ImageData posZImageData = ImageLoader.loadImageSTB(posZImagePath);
		ImageData negZImageData = ImageLoader.loadImageSTB(negZImagePath);
		try (var posXPixels = posXImageData.getPixelBuffers();
				var negXPixels = negXImageData.getPixelBuffers();
				var posYPixels = posYImageData.getPixelBuffers();
				var negYPixels = negYImageData.getPixelBuffers();
				var posZPixels = posZImageData.getPixelBuffers();
				var negZPixels = negZImageData.getPixelBuffers();) {
			return Texture.gen(GL_TEXTURE_CUBE_MAP).bind()
					.filterLinear()
					.wrap3DClampEdge()
					.tex(Image2DTarget.of(GL_TEXTURE_CUBE_MAP_POSITIVE_X, Image2D.from(posXImageData)),
							Image2DTarget.of(GL_TEXTURE_CUBE_MAP_NEGATIVE_X, Image2D.from(negXImageData)),
							Image2DTarget.of(GL_TEXTURE_CUBE_MAP_POSITIVE_Y, Image2D.from(posYImageData)),
							Image2DTarget.of(GL_TEXTURE_CUBE_MAP_NEGATIVE_Y, Image2D.from(negYImageData)),
							Image2DTarget.of(GL_TEXTURE_CUBE_MAP_POSITIVE_Z, Image2D.from(posZImageData)),
							Image2DTarget.of(GL_TEXTURE_CUBE_MAP_NEGATIVE_Z, Image2D.from(negZImageData)));
		} finally {
			Texture.unbind(GL_TEXTURE_CUBE_MAP);
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
