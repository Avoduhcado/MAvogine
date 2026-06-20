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
import com.avogine.render.opengl.texture.Texture.Texture2DBuilder.TexImage2D;
import com.avogine.render.opengl.texture.Texture.TextureCubeMapBuilder.TexImageCubeMap;

/**
 *
 */
public record Texture(int id, int target) {
	
	public Texture(int target) {
		this(glGenTextures(), target);
	}
	
	public Texture(int target, Set<Builder.Parameter> params, TexImage image, boolean generateMipmap) {
		this(target);
		bind();
		params.forEach(parameter -> {
			switch (parameter) {
				case Builder.Parameteri (int pname, int param) -> glTexParameteri(target, pname, param);
				case Builder.Parameterf (int pname, float param) -> glTexParameterf(target, pname, param);
			}
		});
		switch (image) {
			case TexImage2D (int level, int internalFormat, int width, int height, int border, int format, int type, var pixels) -> {
				switch (pixels) {
					case ByteBuffer pixelBytes -> glTexImage2D(target, level, internalFormat, width, height, border, format, type, pixelBytes);
					case IntBuffer pixelInts -> glTexImage2D(target, level, internalFormat, width, height, border, format, type, pixelInts);
					case null -> glTexImage2D(target, level, internalFormat, width, height, border, format, type, MemoryUtil.NULL);
					default -> throw new IllegalArgumentException("Unsupported pixel buffer value: " + pixels);
				}
			}
			case TexImageCubeMap (int level, int internalFormat, int width, int height, int border, int format, int type, ByteBuffer[] pixels) -> {
				for (int i = 0; i < 6; i++) {
					glTexImage2D(GL_TEXTURE_CUBE_MAP_POSITIVE_X + i, level, internalFormat, width, height, border, format, type, pixels[i]);
				}
			}
			case null -> {
				// No image to load
			}
		}
		if (generateMipmap) {
			glGenerateMipmap(target);
		}
		unbind();
	}
	
	public static Texture gen2D(UnaryOperator<Texture2DBuilder> builder) {
		return builder.andThen(Builder::build).apply(new Texture2DBuilder());
	}

	public static Texture genCubeMap(UnaryOperator<TextureCubeMapBuilder> builder) {
		return builder.andThen(Builder::build).apply(new TextureCubeMapBuilder());
	}

	/**
	 * Delete this texture object.
	 */
	public void cleanup() {
		glDeleteTextures(id);
	}
	
	public void activate(int textureSlot) {
		glActiveTexture(GL_TEXTURE0 + textureSlot);
		bind();
	}
	
	public void bind() {
		glBindTexture(target, id);
	}
	
	public void unbind() {
		glBindTexture(target, 0);
	}
	
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
			public int level();
			
			public int internalFormat();
			
			public int width();
			
			public int height();
			
			public int border();
			
			public int format();
			
			public int type();
		}
		
		public SELF parameteri(int pname, int param) {
			params.add(new Parameteri(pname, param));
			return self();
		}
		
		public SELF parameterf(int pname, float param) {
			params.add(new Parameterf(pname, param));
			return self();
		}
		
		public sealed interface Parameter {}
		
		public record Parameteri(int pname, int param) implements Parameter {
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
		
		public record Parameterf(int pname, float param) implements Parameter {
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
		
		public MinFilter minFilter() {
			return new MinFilter();
		}
		
		public MagFilter magFilter() {
			return new MagFilter();
		}
		
		public abstract sealed class FilterParam {
			public abstract SELF function(int function);
			
			public SELF nearest() {
				return function(GL_NEAREST);
			}
			
			public SELF linear() {
				return function(GL_LINEAR);
			}
		}
		
		public final class MinFilter extends FilterParam {
			@Override
			public SELF function(int function) {
				return Builder.this.parameteri(GL_TEXTURE_MIN_FILTER, function);
			}
			
			public SELF nearestMipmapNearest() {
				return function(GL_NEAREST_MIPMAP_NEAREST);
			}

			public SELF linearMipmapNearest() {
				return function(GL_LINEAR_MIPMAP_NEAREST);
			}

			public SELF nearestMipmapLinear() {
				return function(GL_NEAREST_MIPMAP_LINEAR);
			}

			public SELF linearMipmapLinear() {
				return function(GL_LINEAR_MIPMAP_LINEAR);
			}
		}
		
		public final class MagFilter extends FilterParam {
			@Override
			public SELF function(int function) {
				return Builder.this.parameteri(GL_TEXTURE_MAG_FILTER, function);
			}
		}
		
		public WrapS wrapS() {
			return new WrapS();
		}
		
		public WrapT wrapT() {
			return new WrapT();
		}
		
		public WrapR wrapR() {
			return new WrapR();
		}
		
		public WrapST wrapST() {
			return new WrapST();
		}
		
		public WrapSTR wrapSTR() {
			return new WrapSTR();
		}
		
		public abstract sealed class WrapParam {
			protected abstract SELF set(int param);
			
			public SELF clampToEdge() {
				return set(GL_CLAMP_TO_EDGE);
			}
			
			public SELF clampToBorder() {
				return set(GL_CLAMP_TO_BORDER);
			}
			
			public SELF mirroredRepeat() {
				return set(GL_MIRRORED_REPEAT);
			}
			
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
		
		public final class WrapT extends WrapParam {
			@Override
			protected SELF set(int param) {
				return Builder.this.parameteri(GL_TEXTURE_WRAP_T, param);
			}
		}
		
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
		
		public final class WrapSTR extends WrapST {
			@Override
			protected SELF set(int param) {
				return super.set(param).parameteri(GL_TEXTURE_WRAP_R, param);
			}
		}
		
		public SELF generateMipmap() {
			generateMipmap = true;
			return self();
		}
		
		public SELF anisotropicFiltering(float amount) {
			if (GL.getCapabilities().GL_EXT_texture_filter_anisotropic) {
				amount = Math.min(amount, glGetFloat(EXTTextureFilterAnisotropic.GL_MAX_TEXTURE_MAX_ANISOTROPY_EXT));
				parameterf(EXTTextureFilterAnisotropic.GL_TEXTURE_MAX_ANISOTROPY_EXT, amount);
			}
			return self();
		}
		
		public SELF anisotropicFiltering() {
			return anisotropicFiltering(Render.getAnisotropicFiltering());
		}
	}
	
	public static final class Texture2DBuilder extends Builder<Texture2DBuilder> {
		protected Texture2DBuilder() {
			super(GL_TEXTURE_2D);
		}

		@Override
		protected Texture2DBuilder self() {
			return this;
		}

		public Texture2DBuilder image2D(Consumer<Texture2DBuilder> builder) {
			builder.accept(this);
			return self();
		}
		
		public <T extends Buffer> Texture2DBuilder image2D(int level, int internalFormat, int width, int height, int format, int type, T pixels) {
			this.texImage = new TexImage2D<>(level, internalFormat, width, height, 0, format, type, pixels);
			return self();
		}
		
		public Texture2DBuilder image2D(int level, int internalFormat, int width, int height, int format, ByteBuffer pixels) {
			this.texImage = new TexImage2D<>(level, internalFormat, width, height, 0, format, GL_UNSIGNED_BYTE, pixels);
			return self();
		}
		
		public Texture2DBuilder image2D(int width, int height, int format, ByteBuffer pixels) {
			return image2D(0, format, width, height, format, pixels);
		}
		
		public Texture2DBuilder image2D(int width, int height, int format) {
			return image2D(width, height, format, (ByteBuffer) null);
		}
		
		public record TexImage2D<T extends Buffer>(int level, int internalFormat, int width, int height, int border, int format, int type, T pixels) implements TexImage {}
	}
	
	public static final class TextureCubeMapBuilder extends Builder<TextureCubeMapBuilder> {
		protected TextureCubeMapBuilder() {
			super(GL_TEXTURE_CUBE_MAP);
		}

		@Override
		protected TextureCubeMapBuilder self() {
			return this;
		}
		
		public TextureCubeMapBuilder cubeMap(Consumer<TextureCubeMapBuilder> builder) {
			builder.accept(this);
			return self();
		}
		
		public TextureCubeMapBuilder cubeMap(int width, int height, int format,
				ByteBuffer positiveX, ByteBuffer negativeX, ByteBuffer positiveY, ByteBuffer negativeY, ByteBuffer positiveZ, ByteBuffer negativeZ) {
			this.texImage = new TexImageCubeMap(0, format, width, height, 0, format, GL_UNSIGNED_BYTE, new ByteBuffer[] { positiveX, negativeX, positiveY, negativeY, positiveZ, negativeZ });
			return self();
		}
		
		public TextureCubeMapBuilder cubeMap(int width, int height, int format, ByteBuffer allFaces) {
			return cubeMap(width, height, format, allFaces, allFaces, allFaces, allFaces, allFaces, allFaces);
		}

		public TextureCubeMapBuilder cubeMap(int width, int height, int format) {
			return cubeMap(width, height, format, (ByteBuffer) null);
		}
		
		public record TexImageCubeMap(int level, int internalFormat, int width, int height, int border, int format, int type, ByteBuffer[] pixels) implements TexImage {
			public TexImageCubeMap {
				if (Objects.requireNonNull(pixels).length != 6) {
					throw new IllegalArgumentException("CubeMap must specify 6 image buffers.");
				}
			}
		}
	}
}
