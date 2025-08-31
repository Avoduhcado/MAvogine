package com.avogine.render.opengl.texture;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL12.*;
import static org.lwjgl.opengl.GL13.*;
import static org.lwjgl.opengl.GL30.glGenerateMipmap;

import java.nio.*;
import java.util.function.*;

import org.lwjgl.opengl.*;
import org.lwjgl.system.MemoryUtil;

import com.avogine.logging.AvoLog;
import com.avogine.render.image.data.ImageData;

/**
 * @param id 
 * @param target 
 *
 */
public record Texture(int id, int target) {
	
	/**
	 * @param target
	 * @param textureInit
	 * @return a newly constructed {@link Texture} with the configurations from {@code textureInit} applied.
	 */
	public static Texture gen(int target, UnaryOperator<TextureBuilder> textureInit) {
		try (TextureBuilder builder = new TextureBuilder(target)) {
			return textureInit
					.andThen(TextureBuilder.TO_TEXTURE)
					.apply(builder);
		}
	}
	
	/**
	 * @param textureInit
	 * @return a newly constructed 2D {@link Texture} with the configurations from {@code textureInit} applied.
	 */
	public static Texture gen2D(UnaryOperator<TextureBuilder> textureInit) {
		return gen(GL_TEXTURE_2D, textureInit);
	}
	
	/**
	 * @param textureInit
	 * @return a newly constructed cube map {@link Texture} with the configurations from {@code textureInit} applied.
	 */
	public static Texture genCubeMap(UnaryOperator<TextureBuilder> textureInit) {
		return gen(GL_TEXTURE_CUBE_MAP, textureInit);
	}
	
	/**
	 * @param target
	 */
	public static void unbind(int target) {
		glBindTexture(target, 0);
	}
	
	/**
	 * Clear the currently bound 2D texture.
	 */
	public static void unbind2D() {
		unbind(GL_TEXTURE_2D);
	}
	
	/**
	 * Clear the currently bound cube map texture.
	 */
	public static void unbindCubeMap() {
		unbind(GL_TEXTURE_CUBE_MAP);
	}
	
	/**
	 * Delete this texture object.
	 */
	public void cleanup() {
		glDeleteTextures(id);
	}
	
	/**
	 * 
	 */
	protected void bind() {
		glBindTexture(target, id);
	}
	
	/**
	 * Set the currently active texture unit to the given textureSlot and binds this texture.
	 * @param textureSlot The offset to be applied to GL_TEXTURE0 to set as the active texture unit.
	 */
	public void activate(int textureSlot) {
		glActiveTexture(GL_TEXTURE0 + textureSlot);
		bind();
	}
	
	/**
	 * TODO Convert to regular class with private constructor, would allow easily storing parameters dynamically during building to warn of duplicate/overwritten params
	 * @param id
	 * @param target
	 */
	public static record TextureBuilder(int id, int target) implements AutoCloseable {
		
		private static final Function<TextureBuilder, Texture> TO_TEXTURE = builder -> new Texture(builder.id, builder.target);
		
		/**
		 * 
		 */
		public TextureBuilder {
			glBindTexture(target, id);
		}
		
		/**
		 * @param target
		 */
		private TextureBuilder(int target) {
			this(glGenTextures(), target);
		}
		
		@Override
		public void close() {
			Texture.unbind(target);
		}
		
		/**
		 * @param texInit
		 * @return this
		 */
		public TextureBuilder tex(IntConsumer texInit) {
			texInit.accept(target);
			return this;
		}
		
		/**
		 * @param parameter
		 * @return this
		 */
		public TextureBuilder tex(Parameter parameter) {
			return tex(parameter::set);
		}
		
		/**
		 * @param param
		 * @return this
		 */
		public TextureBuilder texFilter(int param) {
			return tex(texInit -> {
				Parameteri.minFilter(param).set(texInit);
				Parameteri.magFilter(param).set(texInit);
			});
		}
		
		/**
		 * @return this
		 */
		public TextureBuilder texFilterNearest() {
			return texFilter(GL_NEAREST);
		}
		
		/**
		 * @return this
		 */
		public TextureBuilder texFilterLinear() {
			return texFilter(GL_LINEAR);
		}
		
		/**
		 * @param param
		 * @param wrap3D true if this should apply wrapping additionally to 3D parameter.
		 * @return this
		 */
		public TextureBuilder texWrap(int param, boolean wrap3D) {
			return tex(texInit -> {
				Parameteri.wrapS(param).set(texInit);
				Parameteri.wrapT(param).set(texInit);
				if (wrap3D) {
					Parameteri.wrapR(param).set(texInit);
				}
			});
		}
		
		/**
		 * @param param
		 * @return this
		 */
		public TextureBuilder texWrap2D(int param) {
			return texWrap(param, false);
		}
		
		/**
		 * @return this
		 */
		public TextureBuilder texWrap2DRepeat() {
			return texWrap2D(GL_REPEAT);
		}
		
		/**
		 * @param param
		 * @return this
		 */
		public TextureBuilder texWrap3D(int param) {
			return texWrap(param, true);
		}
		
		/**
		 * @return this
		 */
		public TextureBuilder texWrap3DClampToEdge() {
			return texWrap3D(GL_CLAMP_TO_EDGE);
		}
		
		/**
		 * @param image2D
		 * @return this
		 */
		public <T extends Buffer> TextureBuilder tex(Image2D<T> image2D) {
			return tex(image2D::specify);
		}
		
		/**
		 * @param <T>
		 * @param internalFormat
		 * @param width
		 * @param height
		 * @param format
		 * @param type
		 * @param pixels
		 * @return this
		 */
		public <T extends Buffer> TextureBuilder texImage2D(int internalFormat, int width, int height, int format, int type, T pixels) {
			return tex(new Image2D<>(internalFormat, width, height, format, type, pixels));
		}
		
		/**
		 * @param <T>
		 * @param width
		 * @param height
		 * @param format
		 * @param pixels
		 * @return this
		 */
		public <T extends Buffer> TextureBuilder texImage2D(int width, int height, int format, T pixels) {
			return tex(new Image2D<>(width, height, format, pixels));
		}
		
		/**
		 * @param <T>
		 * @param width
		 * @param height
		 * @param pixels
		 * @return this
		 */
		public <T extends Buffer> TextureBuilder texImage2D(int width, int height, T pixels) {
			return texImage2D(width, height, GL_RED, pixels);
		}
		
		/**
		 * @param <T>
		 * @param positiveX
		 * @param negativeX
		 * @param positiveY
		 * @param negativeY
		 * @param positiveZ
		 * @param negativeZ
		 * @return this
		 */
		public <T extends Buffer> TextureBuilder texCubeMap(Image2D<T> positiveX, Image2D<T> negativeX, Image2D<T> positiveY, Image2D<T> negativeY, Image2D<T> positiveZ, Image2D<T> negativeZ) {
			return tex(texInit -> {
				positiveX.specify(GL_TEXTURE_CUBE_MAP_POSITIVE_X);
				negativeX.specify(GL_TEXTURE_CUBE_MAP_NEGATIVE_X);
				positiveY.specify(GL_TEXTURE_CUBE_MAP_POSITIVE_Y);
				negativeY.specify(GL_TEXTURE_CUBE_MAP_NEGATIVE_Y);
				positiveZ.specify(GL_TEXTURE_CUBE_MAP_POSITIVE_Z);
				negativeZ.specify(GL_TEXTURE_CUBE_MAP_NEGATIVE_Z);
			});
		}
		
		/**
		 * @param <T>
		 * @param cubeMapImage
		 * @return this
		 */
		public <T extends Buffer> TextureBuilder texCubeMap(Image2D<T> cubeMapImage) {
			return texCubeMap(cubeMapImage, cubeMapImage, cubeMapImage, cubeMapImage, cubeMapImage, cubeMapImage);
		}
		
		/**
		 * @param <T>
		 * @param width
		 * @param height
		 * @param format
		 * @param pixels
		 * @return this
		 */
		public <T extends Buffer> TextureBuilder texCubeMap(int width, int height, int format, T pixels) {
			return texCubeMap(new Image2D<>(width, height, format, pixels));
		}
		
		/**
		 * @return this
		 */
		public TextureBuilder generateMipmap() {
			glGenerateMipmap(target);
			return this;
		}
		
		/**
		 * @return this
		 */
		public TextureBuilder anisotropicFiltering() {
			if (GL.getCapabilities().GL_EXT_texture_filter_anisotropic) {
				// TODO#40: Extract some global Anisotropic filtering value
				float amount = Math.min(4f, glGetFloat(EXTTextureFilterAnisotropic.GL_MAX_TEXTURE_MAX_ANISOTROPY_EXT));
				return tex(new Parameterf(EXTTextureFilterAnisotropic.GL_TEXTURE_MAX_ANISOTROPY_EXT, amount));
			}
			return this;
		}
		
		private sealed interface Parameter {
			/**
			 * @param target
			 */
			public void set(int target);
		}
		
		/**
		 * TODO Validate the param value against the pname
		 * @param pname
		 * @param param
		 */
		public static record Parameteri(int pname, int param) implements Parameter {
			
			/**
			 * @param param
			 * @return a GL_TEXTURE_MIN_FILTER parameter.
			 */
			public static Parameteri minFilter(int param) {
				return new Parameteri(GL_TEXTURE_MIN_FILTER, param);
			}
			
			/**
			 * @param param
			 * @return a GL_TEXTURE_MAG_FILTER parameter.
			 */
			public static Parameteri magFilter(int param) {
				return new Parameteri(GL_TEXTURE_MAG_FILTER, param);
			}
			
			/**
			 * @param param
			 * @return a GL_TEXTURE_WRAP_S parameter.
			 */
			public static Parameteri wrapS(int param) {
				return new Parameteri(GL_TEXTURE_WRAP_S, param);
			}
			
			/**
			 * @param param
			 * @return a GL_TEXTURE_WRAP_T parameter.
			 */
			public static Parameteri wrapT(int param) {
				return new Parameteri(GL_TEXTURE_WRAP_T, param);
			}
			
			/**
			 * @param param
			 * @return a GL_TEXTURE_WRAP_R parameter.
			 */
			public static Parameteri wrapR(int param) {
				return new Parameteri(GL_TEXTURE_WRAP_R, param);
			}
			
			@Override
			public void set(int target) {
				glTexParameteri(target, pname, param);
			}
		}
		
		/**
		 *
		 * @param pname
		 * @param param
		 */
		public static record Parameterf(int pname, float param) implements Parameter {
			@Override
			public void set(int target) {
				glTexParameterf(target, pname, param);
			}
		}
		
		/**
		 * @param <T> 
		 * @param level 
		 * @param internalFormat 
		 * @param width 
		 * @param height 
		 * @param channels 
		 * @param type 
		 * @param pixels 
		 */
		public static record Image2D<T extends Buffer>(int level, int internalFormat, int width, int height, int format, int type, T pixels) {
			
			/**
			 * @param internalFormat
			 * @param width
			 * @param height
			 * @param format
			 * @param type
			 * @param pixels
			 */
			public Image2D(int internalFormat, int width, int height, int format, int type, T pixels) {
				this(0, internalFormat, width, height, format, type, pixels);
			}
			
			/**
			 * @param width
			 * @param height
			 * @param format
			 * @param pixels
			 */
			public Image2D(int width, int height, int format, T pixels) {
				this(internalFormat(format), width, height, format, GL_UNSIGNED_BYTE, pixels);
			}
			
			/**
			 * @param imageData
			 * @return an Image2D from the values of a given ImageData.
			 */
			public static Image2D<ByteBuffer> fromImage(ImageData imageData) {
				return new Image2D<>(imageData.width(), imageData.height(), parseFormat(imageData.channels()), imageData.pixels());
			}
			
			private static int parseFormat(int channels) {
				return switch (channels) {
					case 1 -> GL_RED;
					case 3 -> GL_RGB;
					case 4 -> GL_RGBA;
					default -> {
						AvoLog.log().warn("Image was loaded with channel count: [{}] defaulting to GL_RED.", channels);
						yield GL_RED;
					}
				};
			}
			
			private static int internalFormat(int format) {
				return switch (format) {
					case GL_RGBA -> GL_RGBA8;
					default -> format;
				};
			}
			
			/**
			 * @param target
			 */
			public void specify(int target) {
				switch (pixels) {
					case ByteBuffer b -> glTexImage2D(target, level, internalFormat, width, height, 0, format, type, b);
					case DoubleBuffer d -> glTexImage2D(target, level, internalFormat, width, height, 0, format, type, d);
					case FloatBuffer f -> glTexImage2D(target, level, internalFormat, width, height, 0, format, type, f);
					case IntBuffer i -> glTexImage2D(target, level, internalFormat, width, height, 0, format, type, i);
					case ShortBuffer s -> glTexImage2D(target, level, internalFormat, width, height, 0, format, type, s);
					case null -> glTexImage2D(target, level, internalFormat, width, height, 0, format, type, MemoryUtil.NULL);
					default -> throw new IllegalArgumentException("Cannot specify 2D texture with pixel data of type " + pixels.getClass());
				}
			}
		}
	}
}
