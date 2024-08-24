package com.avogine.render;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.stb.STBTruetype.stbtt_GetPackedQuad;

import java.nio.FloatBuffer;
import java.util.concurrent.atomic.AtomicInteger;

import org.joml.Matrix4f;
import org.lwjgl.stb.STBTTAlignedQuad;
import org.lwjgl.system.MemoryStack;

import com.avogine.render.data.FontDetails;
import com.avogine.render.loader.font.FontCache;
import com.avogine.render.shader.FontShader;

/**
 *
 */
public class TextRender {

	private FontShader fontShader;
	
	private int textVao;
	private int textVbo;
	
	private int textBufferCapacity;
	
	private FontDetails defaultFont;

	/**
	 * @param projection
	 */
	public void init(Matrix4f projection) {
		fontShader = new FontShader("textVertex.glsl", "textFragment.glsl");
		fontShader.bind();
		fontShader.projection.loadMatrix(projection);
		fontShader.unbind();
		
		textBufferCapacity = 20 * 4 * 6;
		var textVertices = new float[textBufferCapacity];
		
		textVao = glGenVertexArrays();
		
		textVbo = glGenBuffers();
		glBindBuffer(GL_ARRAY_BUFFER, textVbo);
		glBufferData(GL_ARRAY_BUFFER, textVertices, GL_DYNAMIC_DRAW);
		
		glBindVertexArray(textVao);
		glEnableVertexAttribArray(0);
		glVertexAttribPointer(0, 4, GL_FLOAT, false, 0, 0);
		glBindBuffer(GL_ARRAY_BUFFER, 0);
		glBindVertexArray(0);
		
		defaultFont = FontCache.getInstance().getFont("Roboto-Regular.ttf");
	}
	
	/**
	 * @param x 
	 * @param y 
	 * @param text
	 */
	public void renderText(float x, float y, String text, FontDetails font) {
		fontShader.bind();
		
		glBindVertexArray(textVao);
		glBindBuffer(GL_ARRAY_BUFFER, textVbo);
		
		int length = text.length() * 4 * 6;
		if (textBufferCapacity < length) {
			textBufferCapacity = length;
			glBufferData(GL_ARRAY_BUFFER, new float[textBufferCapacity], GL_DYNAMIC_DRAW);
		}
		
		glActiveTexture(GL_TEXTURE0);
		glBindTexture(GL_TEXTURE_2D, font.textureID());
		
		Matrix4f modelMatrix = new Matrix4f();
		modelMatrix.translate(x, y + font.fontSize() + font.descent(), 0);
		fontShader.model.loadMatrix(modelMatrix);
		
		fontShader.textColor.loadVec4(1.0f, 1.0f, 1.0f, 1.0f);
		
		try (MemoryStack stack = MemoryStack.stackPush()) {
			STBTTAlignedQuad q = STBTTAlignedQuad.malloc(stack);
			FloatBuffer xpos = stack.floats(0.0f);
			FloatBuffer ypos = stack.floats(0.0f);
			
			// XXX This may need to be allocated through MemoryUtil for large Strings
			FloatBuffer vertexData = stack.mallocFloat(length);
			AtomicInteger numVertices = new AtomicInteger();
			
			text.codePoints()
			.forEach(c -> {
				if (c == '\n') {
					xpos.put(0, 0.0f);
					ypos.put(0, ypos.get(0) + font.fontSize() + font.descent());
				} else if (c < 32 || c > 128) {
					// Only concerned with rendering the ASCII character set
				} else {
					stbtt_GetPackedQuad(font.cdata(), 1024, 1024, c - 32, xpos, ypos, q, false);
					
					vertexData.put(q.x0()).put(q.y1()).put(q.s0()).put(q.t1());
					vertexData.put(q.x1()).put(q.y0()).put(q.s1()).put(q.t0());
					vertexData.put(q.x0()).put(q.y0()).put(q.s0()).put(q.t0());
					vertexData.put(q.x0()).put(q.y1()).put(q.s0()).put(q.t1());
					vertexData.put(q.x1()).put(q.y1()).put(q.s1()).put(q.t1());
					vertexData.put(q.x1()).put(q.y0()).put(q.s1()).put(q.t0());
					numVertices.addAndGet(6);
				}
			});
			vertexData.flip();
			
			glBufferSubData(GL_ARRAY_BUFFER, 0, vertexData);
			glDrawArrays(GL_TRIANGLES, 0, numVertices.get());
		}
		
		glBindBuffer(GL_ARRAY_BUFFER, 0);
		glBindVertexArray(0);
		
		fontShader.unbind();
	}
	
	/**
	 * @param x
	 * @param y
	 * @param text
	 */
	public void renderText(float x, float y, String text) {
		renderText(x, y, text, defaultFont);
	}
	
	/**
	 * 
	 */
	public void cleanup() {
		if (fontShader != null) {
			fontShader.cleanup();
		}
		glDeleteVertexArrays(textVao);
	}
	
}
