package com.avogine.render.opengl.texture;

import static org.lwjgl.opengl.GL11.glDeleteTextures;
import static org.lwjgl.opengl.GL11.glGetFloat;
import static org.lwjgl.opengl.GL11C.GL_LINEAR;
import static org.lwjgl.opengl.GL11C.GL_LINEAR_MIPMAP_LINEAR;
import static org.lwjgl.opengl.GL11C.GL_LINEAR_MIPMAP_NEAREST;
import static org.lwjgl.opengl.GL11C.GL_NEAREST;
import static org.lwjgl.opengl.GL11C.GL_NEAREST_MIPMAP_LINEAR;
import static org.lwjgl.opengl.GL11C.GL_NEAREST_MIPMAP_NEAREST;
import static org.lwjgl.opengl.GL11C.GL_REPEAT;
import static org.lwjgl.opengl.GL11C.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11C.GL_TEXTURE_MAG_FILTER;
import static org.lwjgl.opengl.GL11C.GL_TEXTURE_MIN_FILTER;
import static org.lwjgl.opengl.GL11C.GL_TEXTURE_WRAP_S;
import static org.lwjgl.opengl.GL11C.GL_TEXTURE_WRAP_T;
import static org.lwjgl.opengl.GL11C.GL_UNSIGNED_BYTE;
import static org.lwjgl.opengl.GL11C.glBindTexture;
import static org.lwjgl.opengl.GL11C.glGenTextures;
import static org.lwjgl.opengl.GL11C.glIsTexture;
import static org.lwjgl.opengl.GL11C.glTexImage2D;
import static org.lwjgl.opengl.GL11C.glTexParameterf;
import static org.lwjgl.opengl.GL11C.glTexParameteri;
import static org.lwjgl.opengl.GL12C.*;
import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13C.*;
import static org.lwjgl.opengl.GL14C.GL_MIRRORED_REPEAT;
import static org.lwjgl.opengl.GL30C.glGenerateMipmap;

import java.nio.*;
import java.util.*;
import java.util.function.*;

import org.lwjgl.opengl.*;
import org.lwjgl.system.MemoryUtil;

import com.avogine.render.Render;
import com.avogine.render.opengl.texture.Texture.Builder.TexImage;

/**
 * @param id the texture object name.
 * @param target the texture target.
 */
public record Texture(int id, int target) {
	/**
	 * 
	 */
	public Texture {
		if (!glIsTexture(id)) {
			throw new IllegalArgumentException("Name is not a valid texture: " + id);
		}
	}
	
	/**
	 * @param target the texture target.
	 * @param params a set of texture parameters to apply.
	 * @param image the {@link TexImage} this texture contains.
	 * @param generateMipmap whether to generate mip-map images for this texture.
	 */
	public Texture(int target, Set<Builder.Parameter> params, TexImage image, boolean generateMipmap) {
		int id = glGenTextures();
		glBindTexture(target, id);
		
		initTexture(target, params, image, generateMipmap);
		
		glBindTexture(target, 0);
		
		this(id, target);
	}
	
	private static void initTexture(int target, Set<Builder.Parameter> params, TexImage image, boolean generateMipmap) {
		for (var parameter : params) {
			parameter.enable(target);
		}
		
		if (Objects.nonNull(image)) {
			image.specify(target);
		}
		
		if (generateMipmap) {
			glGenerateMipmap(target);
		}
	}
	
	/**
	 * @param builder
	 * @return a new {@link Texture} set to the {@link GL11C#GL_TEXTURE_2D} target.
	 */
	public static Texture gen2D(UnaryOperator<Texture2DBuilder> builder) {
		return builder.andThen(Builder::build).apply(new Texture2DBuilder());
	}
	
	/**
	 * @param builder
	 * @return a new {@link Texture} set to the {@link GL13C#GL_TEXTURE_CUBE_MAP} target.
	 */
	public static Texture genCubeMap(UnaryOperator<TextureCubeMapBuilder> builder) {
		return builder.andThen(Builder::build).apply(new TextureCubeMapBuilder());
	}
	
	/**
	 * Delete this texture object.
	 */
	public void cleanup() {
		glDeleteTextures(id);
	}
	
	/**
	 * Activate the specified texture unit and bind this texture.
	 * @param textureSlot
	 */
	public void activate(int textureSlot) {
		glActiveTexture(GL_TEXTURE0 + textureSlot);
		bind();
	}
	
	/**
	 * Bind this texture to its target.
	 */
	public void bind() {
		glBindTexture(target, id);
	}
	
	/**
	 * Un-bind this texture target.
	 */
	public void unbind() {
		glBindTexture(target, 0);
	}
	
	/**
	 * Abstract builder for building {@link Texture}s.
	 * @param <SELF>
	 */
	@SuppressWarnings("java:S119")
	public abstract static class Builder<SELF extends Builder<SELF>> {
		protected final int target;
		private final Set<Parameter> params;
		protected TexImage texImage;
		private boolean generateMipmap;
		
		protected Builder(int target) {
			this.target = target;
			params = new HashSet<>();
		}
		
		protected abstract SELF self();
		
		private Texture build() {
			return new Texture(target, params, texImage, generateMipmap);
		}
		
		public sealed interface TexImage {
			/**
			 * @param target the texture target.
			 */
			public void specify(int target);
			
			/**
			 * @return the level-of-detail number
			 */
			public int level();
			
			/**
			 * @return the texture internal format
			 */
			public int internalFormat();
			
			/**
			 * @return the texture width
			 */
			public int width();
			
			/**
			 * @return the texture height
			 */
			public int height();
			
			/**
			 * @return the texture border width
			 */
			public int border();
			
			/**
			 * @return the texel data format
			 */
			public int format();
			
			/**
			 * @return the texel data type
			 */
			public int type();
		}
		
		/**
		 * @param pname
		 * @param param
		 * @return this
		 */
		public SELF parameteri(int pname, int param) {
			params.add(new Parameteri(pname, param));
			return self();
		}
		
		/**
		 * @param pname
		 * @param param
		 * @return this
		 */
		public SELF parameterf(int pname, float param) {
			params.add(new Parameterf(pname, param));
			return self();
		}
		
		public sealed interface Parameter {
			/**
			 * Sets the value of a texture parameter, which controls how the texel array is treated when specified or changed, and when applied to a fragment.
			 * @param target the texture target.
			 */
			public void enable(int target);
		}
		
		/**
		 *
		 * @param pname
		 * @param param
		 */
		public record Parameteri(int pname, int param) implements Parameter {
			@Override
			public void enable(int target) {
				glTexParameteri(target, pname, param);
			}
			
			@Override
			public int hashCode() {
				return Objects.hash(pname);
			}

			@Override
			public boolean equals(Object obj) {
				if (this == obj)
					return true;
				if (!(obj instanceof Parameteri))
					return false;
				Parameteri other = (Parameteri) obj;
				return pname == other.pname;
			}
		}
		
		/**
		 *
		 * @param pname
		 * @param param
		 */
		public record Parameterf(int pname, float param) implements Parameter {
			@Override
			public void enable(int target) {
				glTexParameterf(target, pname, param);
			}
			
			@Override
			public int hashCode() {
				return Objects.hash(pname);
			}

			@Override
			public boolean equals(Object obj) {
				if (this == obj)
					return true;
				if (!(obj instanceof Parameterf))
					return false;
				Parameterf other = (Parameterf) obj;
				return pname == other.pname;
			}
		}
		
		/**
		 * @return {@link MinFilter}
		 */
		public MinFilter minFilter() {
			return new MinFilter();
		}
		
		/**
		 * @return {@link MagFilter}
		 */
		public MagFilter magFilter() {
			return new MagFilter();
		}
		
		public abstract sealed class FilterParam {
			/**
			 * @param function
			 * @return {@link Builder}
			 */
			public abstract SELF function(int function);
			
			/**
			 * @return {@link Builder}
			 */
			public SELF nearest() {
				return function(GL_NEAREST);
			}
			
			/**
			 * @return {@link Builder}
			 */
			public SELF linear() {
				return function(GL_LINEAR);
			}
		}
		
		/**
		 *
		 */
		public final class MinFilter extends FilterParam {
			@Override
			public SELF function(int function) {
				return Builder.this.parameteri(GL_TEXTURE_MIN_FILTER, function);
			}
			
			/**
			 * @return {@link Builder}
			 */
			public SELF nearestMipmapNearest() {
				return function(GL_NEAREST_MIPMAP_NEAREST);
			}

			/**
			 * @return {@link Builder}
			 */
			public SELF linearMipmapNearest() {
				return function(GL_LINEAR_MIPMAP_NEAREST);
			}

			/**
			 * @return {@link Builder}
			 */
			public SELF nearestMipmapLinear() {
				return function(GL_NEAREST_MIPMAP_LINEAR);
			}

			/**
			 * @return {@link Builder}
			 */
			public SELF linearMipmapLinear() {
				return function(GL_LINEAR_MIPMAP_LINEAR);
			}
		}
		
		/**
		 *
		 */
		public final class MagFilter extends FilterParam {
			@Override
			public SELF function(int function) {
				return Builder.this.parameteri(GL_TEXTURE_MAG_FILTER, function);
			}
		}
		
		/**
		 * @return {@link WrapS}
		 */
		public WrapS wrapS() {
			return new WrapS();
		}
		
		/**
		 * @return {@link WrapT}
		 */
		public WrapT wrapT() {
			return new WrapT();
		}
		
		/**
		 * @return {@link WrapR}
		 */
		public WrapR wrapR() {
			return new WrapR();
		}
		
		/**
		 * @return {@link WrapST}
		 */
		public WrapST wrapST() {
			return new WrapST();
		}
		
		/**
		 * @return {@link WrapSTR}
		 */
		public WrapSTR wrapSTR() {
			return new WrapSTR();
		}
		
		public abstract sealed class WrapParam {
			protected abstract SELF set(int param);
			
			/**
			 * @return {@link Builder}
			 */
			public SELF clampToEdge() {
				return set(GL_CLAMP_TO_EDGE);
			}
			
			/**
			 * @return {@link Builder}
			 */
			public SELF clampToBorder() {
				return set(GL_CLAMP_TO_BORDER);
			}
			
			/**
			 * @return {@link Builder}
			 */
			public SELF mirroredRepeat() {
				return set(GL_MIRRORED_REPEAT);
			}
			
			/**
			 * @return {@link Builder}
			 */
			public SELF repeat() {
				return set(GL_REPEAT);
			}
		}
		
		public sealed class WrapS extends WrapParam {
			@Override
			protected SELF set(int param) {
				return Builder.this.parameteri(GL_TEXTURE_WRAP_S, param);
			}
		}
		
		/**
		 *
		 */
		public final class WrapT extends WrapParam {
			@Override
			protected SELF set(int param) {
				return Builder.this.parameteri(GL_TEXTURE_WRAP_T, param);
			}
		}
		
		/**
		 *
		 */
		public final class WrapR extends WrapParam {
			@Override
			protected SELF set(int param) {
				return Builder.this.parameteri(GL_TEXTURE_WRAP_R, param);
			}
		}
		
		public sealed class WrapST extends WrapS {
			@Override
			protected SELF set(int param) {
				return super.set(param).parameteri(GL_TEXTURE_WRAP_T, param);
			}
		}
		
		/**
		 *
		 */
		public final class WrapSTR extends WrapST {
			@Override
			protected SELF set(int param) {
				return super.set(param).parameteri(GL_TEXTURE_WRAP_R, param);
			}
		}
		
		/**
		 * @return {@link Builder}
		 */
		public SELF generateMipmap() {
			generateMipmap = true;
			return self();
		}
		
		/**
		 * @param amount
		 * @return {@link Builder}
		 */
		public SELF anisotropicFiltering(float amount) {
			if (GL.getCapabilities().GL_EXT_texture_filter_anisotropic) {
				amount = Math.min(amount, glGetFloat(EXTTextureFilterAnisotropic.GL_MAX_TEXTURE_MAX_ANISOTROPY_EXT));
				parameterf(EXTTextureFilterAnisotropic.GL_TEXTURE_MAX_ANISOTROPY_EXT, amount);
			}
			return self();
		}
		
		/**
		 * @return {@link Builder}
		 */
		public SELF anisotropicFiltering() {
			return anisotropicFiltering(Render.getAnisotropicFiltering());
		}
	}
	
	/**
	 *
	 */
	public static final class Texture2DBuilder extends Builder<Texture2DBuilder> {
		protected Texture2DBuilder() {
			super(GL_TEXTURE_2D);
		}

		@Override
		protected Texture2DBuilder self() {
			return this;
		}

		/**
		 * @param builder
		 * @return {@link Texture2DBuilder}
		 */
		public Texture2DBuilder image2D(Consumer<Texture2DBuilder> builder) {
			builder.accept(this);
			return self();
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
		 * @return {@link Texture2DBuilder}
		 */
		public <T extends Buffer> Texture2DBuilder image2D(int level, int internalFormat, int width, int height, int format, int type, T pixels) {
			this.texImage = new TexImage2D<>(level, internalFormat, width, height, 0, format, type, pixels);
			return self();
		}
		
		/**
		 * @param level
		 * @param internalFormat
		 * @param width
		 * @param height
		 * @param format
		 * @param pixels
		 * @return {@link Texture2DBuilder}
		 */
		public Texture2DBuilder image2D(int level, int internalFormat, int width, int height, int format, ByteBuffer pixels) {
			this.texImage = new TexImage2D<>(level, internalFormat, width, height, 0, format, GL_UNSIGNED_BYTE, pixels);
			return self();
		}
		
		/**
		 * @param width
		 * @param height
		 * @param format
		 * @param pixels
		 * @return {@link Texture2DBuilder}
		 */
		public Texture2DBuilder image2D(int width, int height, int format, ByteBuffer pixels) {
			return image2D(0, format, width, height, format, pixels);
		}
		
		/**
		 * @param width
		 * @param height
		 * @param format
		 * @return {@link Texture2DBuilder}
		 */
		public Texture2DBuilder image2D(int width, int height, int format) {
			return image2D(width, height, format, (ByteBuffer) null);
		}
		
		/**
		 *
		 * @param <T>
		 * @param level
		 * @param internalFormat
		 * @param width
		 * @param height
		 * @param border
		 * @param format
		 * @param type
		 * @param pixels
		 */
		public record TexImage2D<T extends Buffer>(int level, int internalFormat, int width, int height, int border, int format, int type, T pixels) implements TexImage {
			@Override
			public void specify(int target) {
				switch (pixels) {
					case ByteBuffer pixelBytes -> glTexImage2D(target, level, internalFormat, width, height, border, format, type, pixelBytes);
					case IntBuffer pixelInts -> glTexImage2D(target, level, internalFormat, width, height, border, format, type, pixelInts);
					case null -> glTexImage2D(target, level, internalFormat, width, height, border, format, type, MemoryUtil.NULL);
					default -> throw new IllegalArgumentException("Unsupported pixel buffer value: " + pixels);
				}
			}
		}
	}
	
	/**
	 *
	 */
	public static final class TextureCubeMapBuilder extends Builder<TextureCubeMapBuilder> {
		protected TextureCubeMapBuilder() {
			super(GL_TEXTURE_CUBE_MAP);
		}

		@Override
		protected TextureCubeMapBuilder self() {
			return this;
		}
		
		/**
		 * @param builder
		 * @return {@link TextureCubeMapBuilder}
		 */
		public TextureCubeMapBuilder cubeMap(Consumer<TextureCubeMapBuilder> builder) {
			builder.accept(this);
			return self();
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
		 * @return {@link TextureCubeMapBuilder}
		 */
		@SuppressWarnings("java:S107") // Cube maps require exactly 6 faces, specifying them as individual parameters avoids missing faces.
		public TextureCubeMapBuilder cubeMap(int width, int height, int format,
				ByteBuffer positiveX, ByteBuffer negativeX, ByteBuffer positiveY, ByteBuffer negativeY, ByteBuffer positiveZ, ByteBuffer negativeZ) {
			this.texImage = new TexImageCubeMap(0, format, width, height, 0, format, GL_UNSIGNED_BYTE, new ByteBuffer[] { positiveX, negativeX, positiveY, negativeY, positiveZ, negativeZ });
			return self();
		}
		
		/**
		 * @param width
		 * @param height
		 * @param format
		 * @param allFaces
		 * @return {@link TextureCubeMapBuilder}
		 */
		public TextureCubeMapBuilder cubeMap(int width, int height, int format, ByteBuffer allFaces) {
			return cubeMap(width, height, format, allFaces, allFaces, allFaces, allFaces, allFaces, allFaces);
		}
		
		/**
		 * @param width
		 * @param height
		 * @param format
		 * @return {@link TextureCubeMapBuilder}
		 */
		public TextureCubeMapBuilder cubeMap(int width, int height, int format) {
			return cubeMap(width, height, format, (ByteBuffer) null);
		}
		
		/**
		 *
		 * @param level
		 * @param internalFormat
		 * @param width
		 * @param height
		 * @param border
		 * @param format
		 * @param type
		 * @param pixels
		 */
		public record TexImageCubeMap(int level, int internalFormat, int width, int height, int border, int format, int type, ByteBuffer[] pixels) implements TexImage {
			/**
			 * 
			 */
			public TexImageCubeMap {
				if (Objects.requireNonNull(pixels).length != 6) {
					throw new IllegalArgumentException("CubeMap must specify 6 image buffers.");
				}
			}
			
			@Override
			public void specify(int target) {
				for (int i = 0; i < 6; i++) {
					glTexImage2D(GL_TEXTURE_CUBE_MAP_POSITIVE_X + i, level, internalFormat, width, height, border, format, type, pixels[i]);
				}
			}
			
			@Override
			public int hashCode() {
				final int prime = 31;
				int result = 1;
				result = prime * result + Arrays.hashCode(pixels);
				result = prime * result + Objects.hash(border, format, height, internalFormat, level, type, width);
				return result;
			}

			@Override
			public boolean equals(Object obj) {
				if (this == obj)
					return true;
				if (!(obj instanceof TexImageCubeMap))
					return false;
				TexImageCubeMap other = (TexImageCubeMap) obj;
				return border == other.border && format == other.format && height == other.height
						&& internalFormat == other.internalFormat && level == other.level
						&& Arrays.equals(pixels, other.pixels) && type == other.type && width == other.width;
			}

			@Override
			public String toString() {
				return "TexImageCubeMap [level=" + level + ", internalFormat=" + internalFormat + ", width=" + width
						+ ", height=" + height + ", border=" + border + ", format=" + format + ", type=" + type
						+ ", pixels=" + pixels + "]";
			}
		}
	}
}
