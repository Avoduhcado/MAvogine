package com.avogine.render;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;

import java.nio.FloatBuffer;

import org.joml.Matrix4f;
import org.lwjgl.system.*;

import com.avogine.logging.AvoLog;
import com.avogine.render.data.font.Font;
import com.avogine.render.data.font.FontIdentifier;
import com.avogine.render.shader.FontShader;
import com.avogine.util.resource.ResourceConstants;

/**
 *
 */
public class TextRender {

	/**
	 * The maximum number of allowable characters to render in a single call to {@link TextRender#renderText(float, float, Font, float, String)}.
	 */
	public static final int TEXT_LENGTH_LIMIT = 1024;
	
	private FontShader fontShader;
	
	private final Matrix4f modelMatrix;
	
	private int textVao;
	private int textVbo;
	
	private Font defaultFont;

	/**
	 * 
	 */
	public TextRender() {
		modelMatrix = new Matrix4f();
	}
	
	/**
	 * @param projection
	 * @param fontCache 
	 */
	public void init(Matrix4f projection, FontCache fontCache) {
		fontShader = new FontShader();
		fontShader.bind();
		fontShader.projection.loadMatrix(projection);
		fontShader.unbind();
		
		int textBufferCapacity = TEXT_LENGTH_LIMIT * 4 * 6;
		FloatBuffer textVertices = MemoryUtil.memCallocFloat(textBufferCapacity);
		
		textVao = glGenVertexArrays();
		
		textVbo = glGenBuffers();
		glBindBuffer(GL_ARRAY_BUFFER, textVbo);
		glBufferData(GL_ARRAY_BUFFER, textVertices, GL_DYNAMIC_DRAW);
		
		glBindVertexArray(textVao);
		glEnableVertexAttribArray(0);
		glVertexAttribPointer(0, 4, GL_FLOAT, false, 0, 0);
		glBindBuffer(GL_ARRAY_BUFFER, 0);
		glBindVertexArray(0);
		
		MemoryUtil.memFree(textVertices);
		
		var fontID = new FontIdentifier(ResourceConstants.FONTS.with("Roboto-Regular.ttf"));
		defaultFont = fontCache.getFont(fontID);
	}
	
	/**
	 * @param x 
	 * @param y 
	 * @param font 
	 * @param size 
	 * @param text The text String to render. This must be less than {@value TextRender#TEXT_LENGTH_LIMIT} characters long.
	 */
	public void renderText(float x, float y, Font font, float size, String text) {
		long totalRenderableChars = text.codePoints().filter(c -> c >= 32 && c <= 128).count();
		if (totalRenderableChars > TEXT_LENGTH_LIMIT) {
			AvoLog.log().warn("Woah nelly that's a big text string! Max length is: {} given text was: {}", TEXT_LENGTH_LIMIT, text.length());
			throw new IllegalArgumentException("Text length too long for TextRender.");
		}
		int vertexCount = (int) totalRenderableChars * 6;
		
		fontShader.bind();
		
		glBindVertexArray(textVao);

		glActiveTexture(GL_TEXTURE0);
		font.getTexture().bind();
		
		modelMatrix.identity();
		fontShader.model.loadMatrix(modelMatrix);
		
		fontShader.textColor.loadVec4(1.0f, 1.0f, 1.0f, 1.0f);
		
		FloatBuffer vertexData = MemoryUtil.memAllocFloat(vertexCount * 4);
		try (MemoryStack stack = MemoryStack.stackPush()) {
			FloatBuffer xPos = stack.floats(x);
			FloatBuffer yPos = stack.floats(y + font.getScaledBaseline(size));
			
			text.codePoints().forEach(c -> {
				if (c == '\n') {
					xPos.put(0, x);
					yPos.put(0, yPos.get(0) + font.getScaledVAdvance(size));
				} else if (c < 32 || c > 128) {
					// Only concerned with rendering the ASCII character set
				} else {
					font.packQuad(c, xPos, yPos, size, vertexData);
				}
			});
			
			vertexData.flip();
			
			glBindBuffer(GL_ARRAY_BUFFER, textVbo);
			glBufferSubData(GL_ARRAY_BUFFER, 0, vertexData);
		} finally {
			MemoryUtil.memFree(vertexData);
		}
		
		glDrawArrays(GL_TRIANGLES, 0, vertexCount);
		
		glBindBuffer(GL_ARRAY_BUFFER, 0);
		glBindVertexArray(0);
		
		fontShader.unbind();
	}
	
	/**
	 * @param x
	 * @param y
	 * @param size 
	 * @param text
	 */
	public void renderText(float x, float y, float size, String text) {
		renderText(x, y, defaultFont, size, text);
	}
	
	/**
	 * @param x
	 * @param y
	 * @param font
	 * @param text
	 */
	public void renderText(float x, float y, Font font, String text) {
		renderText(x, y, font, font.getDefaultSize(), text);
	}
	
	/**
	 * @param x
	 * @param y
	 * @param text
	 */
	public void renderText(float x, float y, String text) {
		renderText(x, y, defaultFont.getDefaultSize(), text);
	}
	
	/**
	 * 
	 */
	public void cleanup() {
		if (fontShader != null) {
			fontShader.cleanup();
		}
		glDeleteVertexArrays(textVao);
		glDeleteBuffers(textVbo);
	}
	
}
