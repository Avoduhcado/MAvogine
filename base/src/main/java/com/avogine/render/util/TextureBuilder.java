package com.avogine.render.util;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL12.*;
import static org.lwjgl.opengl.GL13.*;

import java.nio.ByteBuffer;

import org.lwjgl.system.MemoryStack;

import com.avogine.render.data.gl.Texture;
import com.avogine.render.data.image.ImageSTB;
import com.avogine.render.util.stb.ImageLoader;

/**
 *
 */
public class TextureBuilder {

	private TextureBuilder() {
		
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
	public static Texture buildTexture(int width, int height, byte r, byte g, byte b, byte a) {
		try (MemoryStack stack = MemoryStack.stackPush()) {
			ByteBuffer pixels = stack.malloc(width * height * 4);
			while (pixels.hasRemaining()) {
				pixels.put(r).put(g).put(b).put(a);
			}
			pixels.flip();
			
			return Texture.gen().bind()
					.texParameteri(GL_TEXTURE_MIN_FILTER, GL_LINEAR)
					.texParameteri(GL_TEXTURE_MAG_FILTER, GL_LINEAR)
					.texParameteri(GL_TEXTURE_WRAP_S, GL_REPEAT)
					.texParameteri(GL_TEXTURE_WRAP_T, GL_REPEAT)
					.texImage2D(GL_RGBA8, width, height, GL_RGBA, pixels);
		} finally {
			Texture.unbind();
		}
	}
	
	/**
	 * @param texturePath
	 * @return
	 */
	public static Texture buildTexture(String texturePath) {
		try (ImageSTB imageData = ImageLoader.loadImageSTB(texturePath);) {
			return Texture.gen().bind()
					.texParameteri(GL_TEXTURE_MIN_FILTER, GL_LINEAR)
					.texParameteri(GL_TEXTURE_MAG_FILTER, GL_LINEAR)
					.texParameteri(GL_TEXTURE_WRAP_S, GL_REPEAT)
					.texParameteri(GL_TEXTURE_WRAP_T, GL_REPEAT)
					.texImage2D(imageData)
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
	public static Texture buildCubemap(String posXImagePath, String negXImagePath, String posYImagePath, String negYImagePath, String posZImagePath, String negZImagePath) {
		try (ImageSTB posXImageData = ImageLoader.loadImageSTB(posXImagePath);
				ImageSTB negXImageData = ImageLoader.loadImageSTB(negXImagePath);
				ImageSTB posYImageData = ImageLoader.loadImageSTB(posYImagePath);
				ImageSTB negYImageData = ImageLoader.loadImageSTB(negYImagePath);
				ImageSTB posZImageData = ImageLoader.loadImageSTB(posZImagePath);
				ImageSTB negZImageData = ImageLoader.loadImageSTB(negZImagePath);) {
			return Texture.gen(GL_TEXTURE_CUBE_MAP).bind()
					.texParameteri(GL_TEXTURE_MIN_FILTER, GL_LINEAR)
					.texParameteri(GL_TEXTURE_MAG_FILTER, GL_LINEAR)
					.texParameteri(GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE)
					.texParameteri(GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE)
					.texParameteri(GL_TEXTURE_WRAP_R, GL_CLAMP_TO_EDGE)
					.texImage2D(GL_TEXTURE_CUBE_MAP_POSITIVE_X, posXImageData)
					.texImage2D(GL_TEXTURE_CUBE_MAP_NEGATIVE_X, negXImageData)
					.texImage2D(GL_TEXTURE_CUBE_MAP_POSITIVE_Y, posYImageData)
					.texImage2D(GL_TEXTURE_CUBE_MAP_NEGATIVE_Y, negYImageData)
					.texImage2D(GL_TEXTURE_CUBE_MAP_POSITIVE_Z, posZImageData)
					.texImage2D(GL_TEXTURE_CUBE_MAP_NEGATIVE_Z, negZImageData);
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
	public static Texture buildCubemap(String directory, String fileType) {
		return buildCubemap(directory + "/right." + fileType,
				directory + "/left." + fileType,
				directory + "/top." + fileType,
				directory + "/bottom." + fileType,
				directory + "/front." + fileType,
				directory + "/back." + fileType);
	}
	
}
