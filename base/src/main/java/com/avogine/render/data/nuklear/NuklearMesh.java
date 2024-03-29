package com.avogine.render.data.nuklear;

import static org.lwjgl.nuklear.Nuklear.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL12.GL_UNSIGNED_INT_8_8_8_8_REV;
import static org.lwjgl.opengl.GL13.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.system.MemoryUtil.NULL;

import java.nio.ByteBuffer;
import java.util.Objects;

import org.lwjgl.nuklear.*;
import org.lwjgl.system.MemoryStack;

/**
 *
 */
public class NuklearMesh {

	private static final int MAX_VERTEX_BUFFER  = 512 * 1024;
	private static final int MAX_ELEMENT_BUFFER = 128 * 1024;
	
	private static final NkDrawVertexLayoutElement.Buffer VERTEX_LAYOUT;

	static {
		VERTEX_LAYOUT = NkDrawVertexLayoutElement.create(4)
				.position(0).attribute(NK_VERTEX_POSITION).format(NK_FORMAT_FLOAT).offset(0)
				.position(1).attribute(NK_VERTEX_TEXCOORD).format(NK_FORMAT_FLOAT).offset(8)
				.position(2).attribute(NK_VERTEX_COLOR).format(NK_FORMAT_R8G8B8A8).offset(16)
				.position(3).attribute(NK_VERTEX_ATTRIBUTE_COUNT).format(NK_FORMAT_COUNT).offset(0)
				.flip();
	}
	
	private int vao;
	private int vbo;
	private int ebo;

	private NkDrawNullTexture nullTexture;
	
	private int displayWidth;
	private int displayHeight;
	
	private float width;
	private float height;

	/**
	 * @param displayWidth 
	 * @param displayHeight 
	 * @param width 
	 * @param height 
	 * 
	 */
	public NuklearMesh(int displayWidth, int displayHeight, float width, float height) {
		this.displayWidth = displayWidth;
		this.displayHeight = displayHeight;
		this.width = width;
		this.height = height;
		
		setupMesh();
		// An empty texture used for drawing.
		nullTexture = NkDrawNullTexture.create();
		setupTexture();
	}

	private void setupMesh() {
		// buffer setup
		vao = glGenVertexArrays();
		vbo = glGenBuffers();
		ebo = glGenBuffers();

		glBindVertexArray(vao);
		glBindBuffer(GL_ARRAY_BUFFER, vbo);
		glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, ebo);

		glEnableVertexAttribArray(0);
		glVertexAttribPointer(0, 2, GL_FLOAT, false, 20, 0);
		glEnableVertexAttribArray(1);
		glVertexAttribPointer(1, 2, GL_FLOAT, false, 20, 8);
		glEnableVertexAttribArray(2);
		glVertexAttribPointer(2, 4, GL_UNSIGNED_BYTE, true, 20, 16);

		glBindVertexArray(0);
	}

	private void setupTexture() {
		// null texture setup
		int nullTexID = glGenTextures();

		nullTexture.texture().id(nullTexID);
		nullTexture.uv().set(0.5f, 0.5f);

		glBindTexture(GL_TEXTURE_2D, nullTexID);
		try (MemoryStack stack = stackPush()) {
			glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA8, 1, 1, 0, GL_RGBA, GL_UNSIGNED_INT_8_8_8_8_REV, stack.ints(0xFFFFFFFF));
		}
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);

		glBindTexture(GL_TEXTURE_2D, 0);
	}

	/**
	 * @param context
	 * @param commands
	 */
	public void render(NkContext context, NkBuffer commands) {
		// convert from command queue into draw list and draw to screen

		// allocate vertex and element buffer
		glBindVertexArray(vao);
		glBindBuffer(GL_ARRAY_BUFFER, vbo);
		glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, ebo);

		glBufferData(GL_ARRAY_BUFFER, MAX_VERTEX_BUFFER, GL_STREAM_DRAW);
		glBufferData(GL_ELEMENT_ARRAY_BUFFER, MAX_ELEMENT_BUFFER, GL_STREAM_DRAW);
		
		glActiveTexture(GL_TEXTURE0);

		// load draw vertices & elements directly into vertex + element buffer
		ByteBuffer vertices = Objects.requireNonNull(glMapBuffer(GL_ARRAY_BUFFER, GL_WRITE_ONLY, MAX_VERTEX_BUFFER, null));
		ByteBuffer elements = Objects.requireNonNull(glMapBuffer(GL_ELEMENT_ARRAY_BUFFER, GL_WRITE_ONLY, MAX_ELEMENT_BUFFER, null));
		try (MemoryStack stack = stackPush()) {
			// fill convert configuration
			NkConvertConfig config = NkConvertConfig.calloc(stack)
					.vertex_layout(VERTEX_LAYOUT)
					.vertex_size(20)
					.vertex_alignment(4)
					.tex_null(nullTexture)
					.circle_segment_count(22)
					.curve_segment_count(22)
					.arc_segment_count(22)
					.global_alpha(1.0f)
					.shape_AA(NK_ANTI_ALIASING_ON)
					.line_AA(NK_ANTI_ALIASING_ON);

			// setup buffers to load vertices and elements
			NkBuffer vbuf = NkBuffer.malloc(stack);
			NkBuffer ebuf = NkBuffer.malloc(stack);

			nk_buffer_init_fixed(vbuf, vertices/*, max_vertex_buffer*/);
			nk_buffer_init_fixed(ebuf, elements/*, max_element_buffer*/);
			nk_convert(context, commands, vbuf, ebuf, config);
		}
		glUnmapBuffer(GL_ELEMENT_ARRAY_BUFFER);
		glUnmapBuffer(GL_ARRAY_BUFFER);

		// iterate over and execute each draw command
		float fbScaleX = displayWidth / width;
		float fbScaleY = displayHeight / height;

		long offset = NULL;
		for (NkDrawCommand cmd = nk__draw_begin(context, commands); cmd != null; cmd = nk__draw_next(cmd, commands, context)) {
			if (cmd.elem_count() == 0) {
				continue;
			}
			glBindTexture(GL_TEXTURE_2D, cmd.texture().id());
			glScissor(
					(int)(cmd.clip_rect().x() * fbScaleX),
					(int)((height - (int)(cmd.clip_rect().y() + cmd.clip_rect().h())) * fbScaleY),
					(int)(cmd.clip_rect().w() * fbScaleX),
					(int)(cmd.clip_rect().h() * fbScaleY)
					);
			glDrawElements(GL_TRIANGLES, cmd.elem_count(), GL_UNSIGNED_SHORT, offset);
			offset += cmd.elem_count() * 2;
		}

		glBindBuffer(GL_ARRAY_BUFFER, 0);
		glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);
		glBindVertexArray(0);
	}
	
	/**
	 * TODO This is caching framebuffer/window size and it probably shouldn't be.
	 */
	public void setSize(int width, int height) {
		this.displayWidth = width;
		this.displayHeight = height;
		this.width = width;
		this.height = height;
	}

	/**
	 * Free all GPU memory.
	 */
	public void cleanup() {
		glDeleteVertexArrays(vao);
		glDeleteBuffers(vbo);
		glDeleteBuffers(ebo);
		glDeleteTextures(nullTexture.texture().id());
		// TODO Somehow handle freeing up textures?
	}

}
