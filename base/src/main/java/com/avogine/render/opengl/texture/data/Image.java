package com.avogine.render.opengl.texture.data;

import static org.lwjgl.opengl.GL11C.*;
import static org.lwjgl.opengl.GL30C.GL_RG;

import java.nio.ByteBuffer;
import java.util.function.Consumer;

import com.avogine.render.image.data.ImageData;
import com.avogine.render.opengl.texture.Texture.*;

/**
 *
 */
public sealed interface Image {
	/**
	 * @param channels
	 * @return a texel format based on the number of channels in the image.
	 */
	private static int parseFormat(int channels) {
		return switch (channels) {
			case 1 -> GL_RED;
			case 2 -> GL_RG;
			case 3 -> GL_RGB;
			default -> GL_RGBA;
		};
	}
	
	/**
	 * @return the width of the image in pixels.
	 */
	public int width();
	
	/**
	 * @return the height of the image in pixels.
	 */
	public int height();
	
	/**
	 * @return the texel data format.
	 */
	public int format();
	
	/**
	 *
	 * @param width 
	 * @param height 
	 * @param format 
	 * @param pixelData
	 */
	public record Image2D(int width, int height, int format, ByteBuffer pixelData) implements Image, Consumer<Texture2DBuilder> {
		/**
		 * @param imageData
		 * @return an {@link Image2D} wrapping the image data.
		 */
		public static Image2D of(ImageData imageData) {
			return new Image2D(imageData.width(), imageData.height(), Image.parseFormat(imageData.channels()), imageData.pixels());
		}
		
		@Override
		public void accept(Texture2DBuilder builder) {
			builder.image2D(width, height, format, pixelData);
		}
	}

	/**
	 *
	 * @param width 
	 * @param height 
	 * @param format 
	 * @param positiveX
	 * @param negativeX
	 * @param positiveY
	 * @param negativeY
	 * @param positiveZ
	 * @param negativeZ
	 */
	public record CubeMap(int width, int height, int format,
			ByteBuffer positiveX, ByteBuffer negativeX, ByteBuffer positiveY, ByteBuffer negativeY, ByteBuffer positiveZ, ByteBuffer negativeZ) implements Image, Consumer<TextureCubeMapBuilder> {
		/**
		 * @param positiveX
		 * @param negativeX
		 * @param positiveY
		 * @param negativeY
		 * @param positiveZ
		 * @param negativeZ
		 * @return a {@link CubeMap} wrapping each image data.
		 */
		public static CubeMap of(ImageData positiveX, ImageData negativeX, ImageData positiveY, ImageData negativeY, ImageData positiveZ, ImageData negativeZ) {
			return new CubeMap(positiveX.width(), positiveX.height(), Image.parseFormat(positiveX.channels()),
					positiveX.pixels(), negativeX.pixels(), positiveY.pixels(), negativeY.pixels(), positiveZ.pixels(), negativeZ.pixels());
		}
		
		@Override
		public void accept(TextureCubeMapBuilder builder) {
			builder.cubeMap(width, height, format, positiveX, negativeX, positiveY, negativeY, positiveZ, negativeZ);
		}
	}

}
