package com.avogine.render.loader.font;

import java.nio.*;
import java.util.List;

import org.lwjgl.stb.*;
import org.lwjgl.system.*;

import com.avogine.render.data.TextureAtlas;
import com.avogine.render.data.material.TexturedMaterial;
import com.avogine.render.data.mesh.*;

public class FontSTB {

	private int height = 12;
	private STBTTFontinfo info;
	private STBTTBakedChar.Buffer cdata;
	
	private TextureAtlas bitmapTexture;
	
	public FontSTB(int height, STBTTFontinfo info, STBTTBakedChar.Buffer charData, TextureAtlas texture) {
		this.height = height;
		this.info = info;
		this.cdata = charData;
		this.bitmapTexture = texture;
	}
	
	public Model buildTextMesh(String text) {
		FloatBuffer vertexBuffer = MemoryUtil.memAllocFloat(text.length() * 4 * 8);
		IntBuffer indices = MemoryUtil.memAllocInt(text.length() * 6);

		int index = 0;
		try (MemoryStack stack = MemoryStack.stackPush()) {
			// Using stack.floats(n) will presumably handle the values being re-used. IDK why repeated calls will start returning Integer.MIN_VALUE
			FloatBuffer xpos = stack.floats(1);
			FloatBuffer ypos = stack.floats(1);
			
			STBTTAlignedQuad q = STBTTAlignedQuad.malloc(stack);
			
			int baseline = getLineGap();
			float lineHeight = baseline;
			
			for (char c : text.toCharArray()) {
				if (c == '\n') {
					// If the character is a newline, increment the ypos
					ypos.put(0, ypos.get(0) + lineHeight);
					xpos.put(0, 0);
				} else if (c < 32 || 128 <= c) {
					// If it's outside of the character set, skip it
				} else {
					STBTruetype.stbtt_GetBakedQuad(cdata, 512, 512, c - 32, xpos, ypos, q, true);
					
					//System.out.println("(" + q.x0() + ", " + q.y0() + "), (" + q.x1() + ", " + q.y1() + ")");
					//System.out.println("(" + q.s0() + ", " + q.t0() + "), (" + q.s1() + ", " + q.t1() + ")");
					// Positions XYZ ordering
					vertexBuffer.put(q.x0());
					vertexBuffer.put(baseline + q.y0());
					vertexBuffer.put(0.0f);
					vertexBuffer.put(q.x1());
					vertexBuffer.put(baseline + q.y0());
					vertexBuffer.put(0.0f);
					vertexBuffer.put(q.x1());
					vertexBuffer.put(baseline + q.y1());
					vertexBuffer.put(0.0f);
					vertexBuffer.put(q.x0());
					vertexBuffer.put(baseline + q.y1());
					vertexBuffer.put(0.0f);
					
					// Texture Coordinates UV ordering
					vertexBuffer.put(q.s0());
					vertexBuffer.put(q.t0());
					vertexBuffer.put(q.s1());
					vertexBuffer.put(q.t0());
					vertexBuffer.put(q.s1());
					vertexBuffer.put(q.t1());
					vertexBuffer.put(q.s0());
					vertexBuffer.put(q.t1());
					
					// Empty Normals
					vertexBuffer.put(new float[12]);
					
					indices.put(new int[] {index, index + 1, index + 3, index + 3, index + 1, index + 2});
					index += 4;
				}
			}
		}
		
		var material = new TexturedMaterial(bitmapTexture);
		Mesh textMesh = new Mesh(vertexBuffer, indices, 0);
		
		return new Model(List.of(textMesh), List.of(material));
	}
	
	public int getLineGap() {
		try (MemoryStack stack = MemoryStack.stackPush()) {
			IntBuffer x0 = stack.mallocInt(1);
			IntBuffer y0 = stack.mallocInt(1);
			IntBuffer x1 = stack.mallocInt(1);
			IntBuffer y1 = stack.mallocInt(1);

			// XXX This method can cause a crash if left running long enough? Does the info address get overwritten?
			float scale = STBTruetype.stbtt_ScaleForPixelHeight(info, height);
			STBTruetype.stbtt_GetFontBoundingBox(info, x0, y0, x1, y1);
			
			int baseline = y1.get();
			return (int) (scale * baseline);
		}
	}
	
	/**
	 * XXX Only computes single line width currently, does not take into account any newlines or kerning
	 */
	public int getTextWidth(String text) {
		int width = 0;
		
		//try (MemoryStack stack = MemoryStack.stackPush()) {
			int[] advanceWidth = new int[1];
			//IntBuffer leftBearing = stack.ints(0);
			float scale = STBTruetype.stbtt_ScaleForPixelHeight(info, height);
			
			for (char c : text.toCharArray()) {
				STBTruetype.stbtt_GetCodepointHMetrics(info, c, advanceWidth, null);
				width += scale * advanceWidth[0];
			}
		//}
		
		return width;
	}
	
	public void cleanup() {
		bitmapTexture.cleanup();
		cdata.free();
		info.free();
	}
	
}
