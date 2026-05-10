package com.avogine.render.opengl.texture;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL12.*;
import static org.lwjgl.opengl.GL13.*;
import static org.lwjgl.opengl.GL30.glGenerateMipmap;

import java.nio.*;
import java.util.*;
import java.util.function.*;

import org.lwjgl.opengl.*;
import org.lwjgl.system.MemoryUtil;

import com.avogine.logging.AvoLog;
import com.avogine.render.image.data.ImageData;
import com.avogine.render.opengl.texture.Texture.TextureBuilder.*;

/**
 * @param id 
 * @param target 
 */
public record Texture(int id, int target) {
	
	private Texture(TextureBuilder<?> builder) {
		this(glGenTextures(), builder.target);
		
		bind();
		
		builder.parameters.forEach(this::tex);
		tex(builder.image2D);
		
		if (builder.generateMipmap) {
			glGenerateMipmap(target);
		}
		
		unbind(target);
	}
	
	/**
	 * @param textureInit
	 * @return a newly constructed 2D {@link Texture} with the configurations from {@code textureInit} applied.
	 */
	public static Texture gen2D(UnaryOperator<Texture2DBuilder> textureInit) {
		return textureInit
				.andThen(TextureBuilder.TO_TEXTURE)
				.apply(new Texture2DBuilder());
	}
	
	/**
	 * @param textureInit
	 * @return a newly constructed cube map {@link Texture} with the configurations from {@code textureInit} applied.
	 */
	public static Texture genCubeMap(UnaryOperator<TextureCubeMapBuilder> textureInit) {
		return textureInit
				.andThen(TextureBuilder.TO_TEXTURE)
				.apply(new TextureCubeMapBuilder());
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
	 * Bind this texture to its target.
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
	
	private void tex(Tex function) {
		switch (function) {
			case null -> {
				// Skip nulls
			}
			case Parameter parameter -> texParameter(parameter);
			case Image2D image2D -> texImage2D(image2D);
		}
	}
	
	private void texParameter(TextureBuilder.Parameter parameter) {
		switch (parameter) {
			case Parameteri(int pname, int param) -> glTexParameteri(target, pname, param);
			case Parameterf(int pname, float param) -> glTexParameterf(target, pname, param);
		}
	}
	
	private void texImage2D(TextureBuilder.Image2D image) {
		switch (image) {
			case Texture2D<?> texture2D -> specifyTextureImage2D(texture2D);
			case TextureCubeMapBuilder.TextureCubeMap textureCubeMap -> {
				specifyTextureImage2D(textureCubeMap.positiveX, GL_TEXTURE_CUBE_MAP_POSITIVE_X);
				specifyTextureImage2D(textureCubeMap.negativeX, GL_TEXTURE_CUBE_MAP_NEGATIVE_X);
				specifyTextureImage2D(textureCubeMap.positiveY, GL_TEXTURE_CUBE_MAP_POSITIVE_Y);
				specifyTextureImage2D(textureCubeMap.negativeY, GL_TEXTURE_CUBE_MAP_NEGATIVE_Y);
				specifyTextureImage2D(textureCubeMap.positiveZ, GL_TEXTURE_CUBE_MAP_POSITIVE_Z);
				specifyTextureImage2D(textureCubeMap.negativeZ, GL_TEXTURE_CUBE_MAP_NEGATIVE_Z);
			}
		}
	}
	
	private <T extends Buffer> void specifyTextureImage2D(Texture2D<T> image, int target) {
		if (image instanceof Texture2D(var level, var internalFormat, var width, var height, var format, var type, var pixels)) {
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
	
	private <T extends Buffer> void specifyTextureImage2D(Texture2D<T> image) {
		specifyTextureImage2D(image, target);
	}
	
	/**
	 * 
	 */
	public abstract static sealed class TextureBuilder<T extends TextureBuilder<T>> {
		private static final Function<TextureBuilder<?>, Texture> TO_TEXTURE = TextureBuilder::build;
		
		private final int target;
		private final Set<Parameter> parameters;
		protected Image2D image2D;
		private boolean generateMipmap;
		
		/**
		 * @param target
		 */
		private TextureBuilder(int target) {
			this.target = target;
			parameters = new HashSet<>();
		}
		
		protected abstract T self();
		
		/**
		 * @return the constructed Texture with configurations applied
		 */
		public Texture build() {
			return new Texture(this);
		}
		
		/**
		 * @param param
		 * @return this
		 */
		public T minFilter(int param) {
			parameters.add(Parameteri.minFilter(param));
			return self();
		}
		
		/**
		 * @param param
		 * @return this
		 */
		public T magFilter(int param) {
			parameters.add(Parameteri.magFilter(param));
			return self();
		}
		
		/**
		 * @return a Filter
		 */
		public Filter filter() {
			return new Filter();
		}
		
		/**
		 *
		 */
		public class Filter {
			private T apply(int param) {
				minFilter(param);
				magFilter(param);
				return self();
			}
			
			/**
			 * @return {@link TextureBuilder}
			 */
			public T nearest() {
				return apply(GL_NEAREST);
			}
			
			/**
			 * @return {@link TextureBuilder}
			 */
			public T linear() {
				return apply(GL_LINEAR);
			}
		}
		
		/**
		 * @param param
		 * @return this
		 */
		public T wrapS(int param) {
			parameters.add(Parameteri.wrapS(param));
			return self();
		}
		
		/**
		 * @param param
		 * @return this
		 */
		public T wrapT(int param) {
			parameters.add(Parameteri.wrapT(param));
			return self();
		}
		
		/**
		 * @param param
		 * @return this
		 */
		public T wrapR(int param) {
			parameters.add(Parameteri.wrapR(param));
			return self();
		}
		
		/**
		 * @return this
		 */
		public Wrap wrap2D() {
			return new Wrap2D();
		}

		/**
		 * @return this
		 */
		public Wrap wrap3D() {
			return new Wrap3D();
		}
		
		public abstract sealed class Wrap {
			protected abstract T apply(int param);
			
			/**
			 * @return TextureBuilder
			 */
			public T repeat() {
				return apply(GL_REPEAT);
			}
			
			/**
			 * @return TextureBuilder
			 */
			public T clampToEdge() {
				return apply(GL_CLAMP_TO_EDGE);
			}
		}
		
		/**
		 *
		 */
		public final class Wrap2D extends Wrap {
			@Override
			protected T apply(int param) {
				wrapS(param);
				wrapT(param);
				return self();
			}
			
		}
		
		/**
		 *
		 */
		public final class Wrap3D extends Wrap {
			@Override
			protected T apply(int param) {
				wrapS(param);
				wrapT(param);
				wrapR(param);
				return self();
			}
		}
		
		/**
		 * @return this
		 */
		public T anisotropicFiltering() {
			if (GL.getCapabilities().GL_EXT_texture_filter_anisotropic) {
				// TODO#40: Extract some global Anisotropic filtering value
				float amount = Math.min(4f, glGetFloat(EXTTextureFilterAnisotropic.GL_MAX_TEXTURE_MAX_ANISOTROPY_EXT));
				parameters.add(new Parameterf(EXTTextureFilterAnisotropic.GL_TEXTURE_MAX_ANISOTROPY_EXT, amount));
			}
			return self();
		}

		public sealed interface Tex {
			
		}
		
		public sealed interface Image2D extends Tex {}
		
		/**
		 * @param <T>
		 * @param level 
		 * @param internalFormat 
		 * @param width 
		 * @param height 
		 * @param format 
		 * @param type 
		 * @param pixels 
		 */
		public record Texture2D<T extends Buffer>(int level, int internalFormat, int width, int height, int format, int type, T pixels) implements Image2D {
			/**
			 * @param width
			 * @param height
			 * @param format
			 * @param pixels
			 */
			public Texture2D(int width, int height, int format, T pixels) {
				this(0, internalFormat(format), width, height, format, GL_UNSIGNED_BYTE, pixels);
			}
			
			/**
			 * @param imageData
			 * @return a {@link Texture2D} allocated from imageData.
			 */
			public static Texture2D<ByteBuffer> fromImage(ImageData imageData) {
				return new Texture2D<>(imageData.width(), imageData.height(), parseFormat(imageData.channels()), imageData.pixels());
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
		}
		
		/**
		 * @return this
		 */
		public T generateMipmap() {
			generateMipmap = true;
			return self();
		}
		
		public sealed interface Parameter extends Tex {}
		
		/**
		 * TODO#40 Validate the param value against the pname
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
			public final int hashCode() {
				return pname;
			}
		}
		
		/**
		 *
		 * @param pname
		 * @param param
		 */
		public static record Parameterf(int pname, float param) implements Parameter {
			@Override
			public final int hashCode() {
				return pname;
			}
		}
	}
	
	/**
	 *
	 */
	public static final class Texture2DBuilder extends TextureBuilder<Texture2DBuilder> {
		private Texture2DBuilder() {
			super(GL_TEXTURE_2D);
		}
		
		@Override
		protected Texture2DBuilder self() {
			return this;
		}
		
		/**
		 * @param imageData 
		 * @return this
		 */
		public Texture2DBuilder image2D(ImageData imageData) {
			this.image2D = Texture2D.fromImage(imageData);
			return this;
		}
		
		/**
		 * @param <T>
		 * @param level
		 * @param internalFormat
		 * @param width
		 * @param height
		 * @param format
		 * @param type
		 * @param pixels
		 * @return this
		 */
		public <T extends Buffer> Texture2DBuilder image2D(int level, int internalFormat, int width, int height, int format, int type, T pixels) {
			this.image2D = new Texture2D<>(level, internalFormat, width, height, format, type, pixels);
			return this;
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
		public <T extends Buffer> Texture2DBuilder image2D(int internalFormat, int width, int height, int format, int type, T pixels) {
			return image2D(0, internalFormat, width, height, format, type, pixels);
		}
		
		/**
		 * @param <T>
		 * @param width
		 * @param height
		 * @param format
		 * @param pixels
		 * @return this
		 */
		public <T extends Buffer> Texture2DBuilder image2D(int width, int height, int format, T pixels) {
			return image2D(Texture2D.internalFormat(format), width, height, format, GL_UNSIGNED_BYTE, pixels);
		}
		
		/**
		 * @param <T>
		 * @param width
		 * @param height
		 * @param pixels
		 * @return this
		 */
		public <T extends Buffer> Texture2DBuilder image2D(int width, int height, T pixels) {
			return image2D(width, height, GL_RED, pixels);
		}
	}
	
	/**
	 *
	 */
	public static final class TextureCubeMapBuilder extends TextureBuilder<TextureCubeMapBuilder> {
		private TextureCubeMapBuilder() {
			super(GL_TEXTURE_CUBE_MAP);
		}
		
		@Override
		protected TextureCubeMapBuilder self() {
			return this;
		}
		
		/**
		 * @return a textureCubeMap
		 */
		public TextureCubeMap image2D() {
			return new TextureCubeMap();
		}
		
		/**
		 *
		 */
		public final class TextureCubeMap implements Image2D {
			private Texture2D<? extends Buffer> positiveX;
			private Texture2D<? extends Buffer> negativeX;
			private Texture2D<? extends Buffer> positiveY;
			private Texture2D<? extends Buffer> negativeY;
			private Texture2D<? extends Buffer> positiveZ;
			private Texture2D<? extends Buffer> negativeZ;
			
			/**
			 * @param <T>
			 * @param positiveX
			 * @param negativeX
			 * @param positiveY
			 * @param negativeY
			 * @param positiveZ
			 * @param negativeZ
			 * @return TextureCubeMapBuilder
			 */
			public <T extends Buffer> TextureCubeMapBuilder cubeMap(Texture2D<T> positiveX, Texture2D<T> negativeX, Texture2D<T> positiveY, Texture2D<T> negativeY, Texture2D<T> positiveZ, Texture2D<T> negativeZ) {
				this.positiveX = positiveX;
				this.negativeX = negativeX;
				this.positiveY = positiveY;
				this.negativeY = negativeY;
				this.positiveZ = positiveZ;
				this.negativeZ = negativeZ;
				image2D = this;
				return TextureCubeMapBuilder.this;
			}
			
			/**
			 * @param positiveX
			 * @param negativeX
			 * @param positiveY
			 * @param negativeY
			 * @param positiveZ
			 * @param negativeZ
			 * @return TextureCubeMapBuilder
			 */
			public TextureCubeMapBuilder cubeMap(ImageData positiveX, ImageData negativeX, ImageData positiveY, ImageData negativeY, ImageData positiveZ, ImageData negativeZ) {
				return cubeMap(Texture2D.fromImage(positiveX), Texture2D.fromImage(negativeX), Texture2D.fromImage(positiveY), Texture2D.fromImage(negativeY), Texture2D.fromImage(positiveZ), Texture2D.fromImage(negativeZ));
			}
			
			/**
			 * @param <T>
			 * @param texture2D
			 * @return TextureCubeMapBuilder
			 */
			public <T extends Buffer> TextureCubeMapBuilder cubeMap(Texture2D<T> texture2D) {
				return cubeMap(texture2D, texture2D, texture2D, texture2D, texture2D, texture2D);
			}
			
			/**
			 * @param imageData
			 * @return TextureCubeMapBuilder
			 */
			public TextureCubeMapBuilder cubeMap(ImageData imageData) {
				return cubeMap(Texture2D.fromImage(imageData));
			}
			
			/**
			 * @param <T>
			 * @param width
			 * @param height
			 * @param format
			 * @param pixels
			 * @return TextureCubeMapBuilder
			 */
			public <T extends Buffer> TextureCubeMapBuilder cubeMap(int width, int height, int format, T pixels) {
				return cubeMap(new Texture2D<>(width, height, format, pixels));
			}
		}
	}
}
