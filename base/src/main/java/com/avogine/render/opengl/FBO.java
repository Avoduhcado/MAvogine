package com.avogine.render.opengl;

import static org.lwjgl.opengl.GL11C.*;
import static org.lwjgl.opengl.GL12C.GL_TEXTURE_3D;
import static org.lwjgl.opengl.GL13C.*;
import static org.lwjgl.opengl.GL30C.*;

import java.util.Objects;

import com.avogine.logging.AvoLog;
import com.avogine.render.opengl.texture.Texture;

/**
 * 
 * @param id 
 * @param target 
 */
public record FBO(int id, int target) {
	/**
	 * 
	 */
	public FBO {
		if (!glIsFramebuffer(id)) {
			throw new IllegalArgumentException("Name is not a valid Framebuffer: " + id);
		}
	}
	
	private static FBO generate(int target, Attachment...attachments) {
		int fbo = glGenFramebuffers();
		glBindFramebuffer(target, fbo);
		
		for (Attachment attachment : attachments) {
			attachment.attach(target);
		}
		
		FBO.validate(target);
		
		glBindFramebuffer(target, 0);
		
		return new FBO(fbo, target);
	}
	
	/**
	 * @param attachments
	 * @return
	 */
	public static FBO framebuffer(Attachment...attachments) {
		return FBO.generate(GL_FRAMEBUFFER, attachments);
	}
	
	/**
	 * @param attachments
	 * @return
	 */
	public static FBO readFramebuffer(Attachment...attachments) {
		return FBO.generate(GL_READ_FRAMEBUFFER, attachments);
	}
	
	/**
	 * @param attachments
	 * @return
	 */
	public static FBO drawFramebuffer(Attachment...attachments) {
		return FBO.generate(GL_DRAW_FRAMEBUFFER, attachments);
	}
	
	/**
	 * 
	 */
	public void cleanup() {
		glDeleteFramebuffers(id);
	}
	
	/**
	 * 
	 */
	public void bind() {
		glBindFramebuffer(target, id);
	}
	
	/**
	 * 
	 */
	public void unbind() {
		glBindFramebuffer(target, 0);
	}
	
	/**
	 * @param attachment
	 */
	public void texture(FramebufferTexture attachment) {
		attachment.attach(target);
	}
	
	/**
	 * @param renderbuffer
	 */
	public void renderbuffer(Renderbuffer renderbuffer) {
		renderbuffer.attach(target);
	}
	
	/**
	 * @param target 
	 */
	public static void validate(int target) {
		int status = glCheckFramebufferStatus(target);
		if (status != GL_FRAMEBUFFER_COMPLETE) {
			AvoLog.log().error("Framebuffer error: {}", status);
		}
	}
	
	/**
	 * 
	 */
	public void validate() {
		FBO.validate(target);
	}
	
	public sealed interface Attachment {
		/**
		 * @return
		 */
		public int attachment();
		
		/**
		 * @param target
		 */
		public void attach(int target);
	}
	
	/**
	 *
	 * @param attachment
	 * @param texTarget
	 * @param texture
	 * @param level
	 * @param layer
	 */
	public record FramebufferTexture(int attachment, int texTarget, int texture, int level, int layer) implements Attachment {
		/**
		 * @param attachment
		 * @param texTarget
		 * @param texture
		 * @param level
		 */
		public FramebufferTexture(int attachment, int texTarget, int texture, int level) {
			this(attachment, texTarget, texture, level, 0);
		}
		
		/**
		 * @param attachment
		 * @param texTarget
		 * @param texture
		 */
		public FramebufferTexture(int attachment, int texTarget, int texture) {
			this(attachment, texTarget, texture, 0);
		}
		
		/**
		 * @param slot
		 * @param texTarget
		 * @param texture
		 * @return
		 */
		public static FramebufferTexture color(int slot, int texTarget, int texture) {
			return new FramebufferTexture(GL_COLOR_ATTACHMENT0 + slot, texTarget, texture);
		}

		/**
		 * @param slot
		 * @param texture
		 * @return
		 */
		public static FramebufferTexture color(int slot, Texture texture) {
			return color(slot, texture.target(), texture.id());
		}
		
		/**
		 * @param texTarget
		 * @param texture
		 * @return
		 */
		public static FramebufferTexture color0(int texTarget, int texture) {
			return color(0, texTarget, texture);
		}
		
		/**
		 * @param texture
		 * @return
		 */
		public static FramebufferTexture color0(Texture texture) {
			return color0(texture.target(), texture.id());
		}

		/**
		 * @param texTarget 
		 * @param texture
		 * @return
		 */
		public static FramebufferTexture depth(int texTarget, int texture) {
			return new FramebufferTexture(GL_DEPTH_ATTACHMENT, texTarget, texture);
		}
		
		/**
		 * @param texture
		 * @return
		 */
		public static FramebufferTexture depth(Texture texture) {
			return depth(texture.target(), texture.id());
		}

		/**
		 * @param texTarget 
		 * @param texture
		 * @return
		 */
		public static FramebufferTexture stencil(int texTarget, int texture) {
			return new FramebufferTexture(GL_STENCIL_ATTACHMENT, texTarget, texture);
		}
		
		/**
		 * @param texture
		 * @return
		 */
		public static FramebufferTexture stencil(Texture texture) {
			return stencil(texture.target(), texture.id());
		}

		/**
		 * @param texTarget 
		 * @param texture
		 * @return
		 */
		public static FramebufferTexture depthStencil(int texTarget, int texture) {
			return new FramebufferTexture(GL_DEPTH_STENCIL_ATTACHMENT, texTarget, texture);
		}
		
		/**
		 * @param texture
		 * @return
		 */
		public static FramebufferTexture depthStencil(Texture texture) {
			return depthStencil(texture.target(), texture.id());
		}
		
		@Override
		public void attach(int target) {
			switch (texTarget) {
				case GL_TEXTURE_1D -> glFramebufferTexture1D(target, attachment, texTarget, texture, level);
				case GL_TEXTURE_2D,
				GL_TEXTURE_CUBE_MAP_POSITIVE_X, GL_TEXTURE_CUBE_MAP_NEGATIVE_X,
				GL_TEXTURE_CUBE_MAP_POSITIVE_Y, GL_TEXTURE_CUBE_MAP_NEGATIVE_Y,
				GL_TEXTURE_CUBE_MAP_POSITIVE_Z, GL_TEXTURE_CUBE_MAP_NEGATIVE_Z -> glFramebufferTexture2D(target, attachment, texTarget, texture, level);
				case GL_TEXTURE_3D -> glFramebufferTexture3D(target, attachment, texTarget, texture, level, layer);
				default -> AvoLog.log().warn("Unsupported texture target: {}", texTarget);
			}
		}
	}
	
	/**
	 *
	 * @param id
	 * @param attachment 
	 */
	public record Renderbuffer(int id, int attachment) implements Attachment {
		/**
		 * 
		 */
		public Renderbuffer {
			if (!glIsRenderbuffer(id)) {
				throw new IllegalArgumentException("Name is not a valid renderbuffer: " + id);
			}
		}
		
		/**
		 * @param attachment 
		 * @param storage
		 */
		public Renderbuffer(int attachment, Storage storage) {
			int id = glGenRenderbuffers();
			glBindRenderbuffer(GL_RENDERBUFFER, id);
			
			if (Objects.nonNull(storage)) {
				storage.establish();
			}
			
			glBindRenderbuffer(GL_RENDERBUFFER, 0);
			
			this(id, attachment);
		}
		
		/**
		 * @param slot
		 * @param storage
		 * @return
		 */
		public static Renderbuffer color(int slot, Storage storage) {
			return new Renderbuffer(GL_COLOR_ATTACHMENT0 + slot, storage);
		}
		
		/**
		 * @param storage
		 * @return
		 */
		public static Renderbuffer color0(Storage storage) {
			return color(0, storage);
		}
		
		/**
		 * @param storage
		 * @return
		 */
		public static Renderbuffer depth(Storage storage) {
			return new Renderbuffer(GL_DEPTH_ATTACHMENT, storage);
		}
		
		/**
		 * @param storage
		 * @return
		 */
		public static Renderbuffer stencil(Storage storage) {
			return new Renderbuffer(GL_STENCIL_ATTACHMENT, storage);
		}
		
		/**
		 * @param storage
		 * @return
		 */
		public static Renderbuffer depthStencil(Storage storage) {
			return new Renderbuffer(GL_DEPTH_STENCIL_ATTACHMENT, storage);
		}
		
		@Override
		public void attach(int target) {
			glFramebufferRenderbuffer(target, attachment(), GL_RENDERBUFFER, id);
		}
		
		/**
		 *
		 * @param internalFormat
		 * @param width
		 * @param height
		 */
		public record Storage(int internalFormat, int width, int height) {
			/**
			 * 
			 */
			public void establish() {
				glRenderbufferStorage(GL_RENDERBUFFER, internalFormat, width, height);
			}
		}
	}
}
