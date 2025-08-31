package com.avogine.render.opengl.ui.nuklear;

import static org.lwjgl.nuklear.Nuklear.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL12.GL_UNSIGNED_INT_8_8_8_8_REV;
import static org.lwjgl.opengl.GL13.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.system.MemoryStack.stackPush;

import java.nio.ByteBuffer;
import java.util.Objects;

import org.lwjgl.nuklear.*;
import org.lwjgl.system.*;

import com.avogine.render.opengl.*;
import com.avogine.render.opengl.VAO.VAOBuilder.VertexAttrib;

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
	
	private final VAO vao;
	
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
		vao = VAO.gen(vertexArray -> vertexArray
				.bindBufferData(VBO.staticDraw(), null)
				.enablePointer(0, new VertexAttrib.Format(2, GL_FLOAT, false, 20, 0))
				.enablePointer(1, new VertexAttrib.Format(2, GL_FLOAT, false, 20, 8))
				.enablePointer(2, new VertexAttrib.Format(4, GL_UNSIGNED_BYTE, true, 20, 16))
				.bindElements(null));
		this.displayWidth = displayWidth;
		this.displayHeight = displayHeight;
		this.width = width;
		this.height = height;
		
		// An empty texture used for drawing.
		nullTexture = NkDrawNullTexture.create();
		setupTexture();
	}
	
	/**
	 * Free all GPU memory.
	 */
	public void cleanup() {
		vao.cleanup();
		glDeleteTextures(nullTexture.texture().id());
	}
	
	private void setupTexture() {
		// null texture setup
		try (MemoryStack stack = stackPush()) {
			int nullTexID = Texture.gen().bind()
					.filterNearest()
					.texImage2D(GL_RGBA8, 1, 1, GL_RGBA, GL_UNSIGNED_INT_8_8_8_8_REV, stack.ints(0xFFFFFFFF))
					.id();

			nullTexture.texture().id(nullTexID);
			nullTexture.uv().set(0.5f, 0.5f);
		} finally {
			Texture.unbind();
		}
	}
	
	/**
	 * @param context
	 * @param commands
	 */
	public void prepareCommandQueue(NkContext context, NkBuffer commands) {
		// convert from command queue into draw list and draw to screen

		// allocate vertex and element buffer
		vao.bind();
		vao.vertexBufferObjects()[0].bind();

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

		long offset = MemoryUtil.NULL;
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
		nk_clear(context);
		nk_buffer_clear(commands);

		glBindBuffer(GL_ARRAY_BUFFER, 0);
		// OpenGL law states to not unbind an EBO while a VAO is still bound, but idk what Nuklear is actually doing with this buffer mapping.
		glBindVertexArray(0);
	}
	
	/**
	 * Should this only be updating displayWidth/Height?
	 * @param width 
	 * @param height 
	 */
	public void setSize(int width, int height) {
		this.displayWidth = width;
		this.displayHeight = height;
		this.width = width;
		this.height = height;
	}
}
