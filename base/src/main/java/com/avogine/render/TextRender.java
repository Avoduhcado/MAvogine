package com.avogine.render;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.*;
import static org.lwjgl.opengl.GL15.GL_DYNAMIC_DRAW;

import java.nio.FloatBuffer;

import org.joml.Matrix4f;
import org.lwjgl.system.*;

import com.avogine.logging.AvoLog;
import com.avogine.render.data.font.Font;
import com.avogine.render.data.gl.*;
import com.avogine.render.shader.FontShader;
import com.avogine.render.util.FontCache;
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
	
	private final Matrix4f orthoMatrix;
	private final Matrix4f modelMatrix;
	
	private int width;
	private int height;
	
	private boolean retainResolution;
	
	private VAO textVao;
	
	private Font defaultFont;

	/**
	 * 
	 */
	public TextRender() {
		orthoMatrix = new Matrix4f();
		modelMatrix = new Matrix4f();
		
		retainResolution = true;
	}
	
	/**
	 * @param width 
	 * @param height 
	 * @param fontCache 
	 */
	public void init(int width, int height, FontCache fontCache) {
		fontShader = new FontShader();
		this.width = width;
		this.height = height;
		orthoMatrix.setOrtho2D(0, width, height, 0);
		
		int textBufferCapacity = TEXT_LENGTH_LIMIT * 4 * 6;
		FloatBuffer textVertices = MemoryUtil.memCallocFloat(textBufferCapacity);
		try {
			textVao = VAO.gen().bind()
					.addBuffer(VBO.gen().bind()
							.bufferData(textVertices, GL_DYNAMIC_DRAW).enable(VertexAttrib.array(0).pointer(VertexAttrib.Pointer.tightlyPackedUnnormalizedFloat(4))));
		} finally {
			MemoryUtil.memFree(textVertices);
		}
		
		defaultFont = fontCache.getFont(ResourceConstants.FONTS.with("Roboto-Regular.ttf"));
	}
	
	/**
	 * 
	 */
	public void cleanup() {
		if (fontShader != null) {
			fontShader.cleanup();
		}
		if (textVao != null) {
			textVao.cleanup();
		}
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
		
		glEnable(GL_BLEND);
		glDisable(GL_CULL_FACE);
		glDisable(GL_DEPTH_TEST);

		glViewport(0, 0, width, height);
		
		fontShader.bind();
		fontShader.projection.loadMatrix(orthoMatrix);
		
		glActiveTexture(GL_TEXTURE0);
		font.getTexture(size).bind();
		
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
			
			textVao.bind().vertexBufferObjects().get(0).bind().bufferSubData(vertexData);
		} finally {
			MemoryUtil.memFree(vertexData);
		}
		
		glDrawArrays(GL_TRIANGLES, 0, vertexCount);
		
		VAO.unbind();
		
		fontShader.unbind();
		
		glEnable(GL_DEPTH_TEST);
		glEnable(GL_CULL_FACE);
		glDisable(GL_BLEND);
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
	 * @param width
	 * @param height
	 */
	public void resize(int width, int height) {
		this.width = width;
		this.height = height;
		if (retainResolution) {
			orthoMatrix.setOrtho2D(0, width, height, 0);
		}
	}
	
	/**
	 * @return the retainResolution
	 */
	public boolean isRetainResolution() {
		return retainResolution;
	}
	
	/**
	 * @param retainResolution the retainResolution to set
	 */
	public void setRetainResolution(boolean retainResolution) {
		this.retainResolution = retainResolution;
	}
	
}
