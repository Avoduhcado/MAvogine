package com.avogine.loader.font;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.lwjgl.stb.STBTTAlignedQuad;
import org.lwjgl.stb.STBTTBakedChar;
import org.lwjgl.stb.STBTTFontinfo;
import org.lwjgl.stb.STBTruetype;
import org.lwjgl.system.MemoryStack;

import com.avogine.core.render.Material;
import com.avogine.core.render.Mesh;
import com.avogine.core.render.Texture;
import com.avogine.loader.util.ArrayUtils;

public class FontSTB {

	private int height = 12;
	private STBTTFontinfo info;
	private STBTTBakedChar.Buffer cdata;
	
	private Texture bitmapTexture;
	
	public FontSTB(int height, STBTTFontinfo info, STBTTBakedChar.Buffer charData, Texture texture) {
		this.height = height;
		this.info = info;
		this.cdata = charData;
		this.bitmapTexture = texture;
	}
	
	public Mesh buildTextMesh(String text, float x, float y) {
		List<Float> vertices = new ArrayList<>();
		List<Float> textureCoords = new ArrayList<>();
		List<Integer> indices = new ArrayList<>();

		int index = 0;
		try (MemoryStack stack = MemoryStack.stackPush()) {
			// Using stack.floats(n) will presumably handle the values being re-used. IDK why repeated calls will start returning Integer.MIN_VALUE
			FloatBuffer xpos = stack.floats(x);
			FloatBuffer ypos = stack.floats(y);
			
			STBTTAlignedQuad q = STBTTAlignedQuad.mallocStack(stack);
			
			int baseline = getLineGap();
			float height = baseline;
			
			for (char c : text.toCharArray()) {
				// If the character is a newline, increment the ypos
				if (c == '\n') {
					ypos.put(0, ypos.get(0) + height);
					xpos.put(0, x);
					continue;
					// If it's outside of the character set, skip it
				} else if (c < 32 || 128 <= c) {
					continue;
				}

				STBTruetype.stbtt_GetBakedQuad(cdata, 512, 512, c - 32, xpos, ypos, q, true);

				//System.out.println("(" + q.x0() + ", " + q.y0() + "), (" + q.x1() + ", " + q.y1() + ")");
				//System.out.println("(" + q.s0() + ", " + q.t0() + "), (" + q.s1() + ", " + q.t1() + ")");
				vertices.addAll(Arrays.asList(q.x0(), baseline + q.y0(), 0.0f));
				vertices.addAll(Arrays.asList(q.x1(), baseline + q.y0(), 0.0f));
				vertices.addAll(Arrays.asList(q.x1(), baseline + q.y1(), 0.0f));
				vertices.addAll(Arrays.asList(q.x0(), baseline + q.y1(), 0.0f));
				textureCoords.addAll(Arrays.asList(q.s0(), q.t0()));
				textureCoords.addAll(Arrays.asList(q.s1(), q.t0()));
				textureCoords.addAll(Arrays.asList(q.s1(), q.t1()));
				textureCoords.addAll(Arrays.asList(q.s0(), q.t1()));
				indices.addAll(Arrays.asList(index, index + 1, index + 3, index + 3, index + 1, index + 2));
				index += 4;
			}
		}
		
		Mesh textMesh = new Mesh(ArrayUtils.toPrimitive(vertices.toArray(Float[]::new)));
		textMesh.addAttribute(1, ArrayUtils.toPrimitive(textureCoords.toArray(Float[]::new)), 2);
		textMesh.addIndexAttribute(indices.stream().mapToInt(Integer::intValue).toArray());
		textMesh.setMaterial(new Material(bitmapTexture));
		
		return textMesh;
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
	
	/*
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
