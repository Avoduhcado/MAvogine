package com.avogine.render.loader.font;

import static org.lwjgl.opengl.GL11.glBindTexture;
import static org.lwjgl.opengl.GL11.glGenTextures;
import static org.lwjgl.opengl.GL11.glPixelStorei;
import static org.lwjgl.opengl.GL11.glTexImage2D;
import static org.lwjgl.opengl.GL11.glTexParameteri;
import static org.lwjgl.opengl.GL11C.GL_LINEAR;
import static org.lwjgl.opengl.GL11C.GL_RGBA;
import static org.lwjgl.opengl.GL11C.GL_RGBA8;
import static org.lwjgl.opengl.GL11C.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11C.GL_TEXTURE_MAG_FILTER;
import static org.lwjgl.opengl.GL11C.GL_TEXTURE_MIN_FILTER;
import static org.lwjgl.opengl.GL12.GL_UNSIGNED_INT_8_8_8_8_REV;
import static org.lwjgl.stb.STBTruetype.*;
import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.system.MemoryUtil.*;

import java.awt.Point;
import java.awt.image.*;
import java.io.*;
import java.nio.*;
import java.util.AbstractMap.SimpleEntry;

import javax.imageio.ImageIO;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.*;
import org.lwjgl.stb.*;
import org.lwjgl.system.MemoryStack;

import com.avogine.render.data.*;
import com.avogine.util.resource.*;

/**
 * TODO Customizable font sizes, scalable bitmap sizes to match, customizable character sets (may require multiple textures and some sort of texture sheet indexing to fit everything)
 *
 */
public class FontLoaderSTB {

	/** {@value #FONT_HEIGHT} */
	private static final int FONT_HEIGHT = 24;
	
	private FontLoaderSTB() {
		
	}
	
	/**
	 * @param fontFile
	 * @return 
	 */
	public static FontDetails quickLoad(String fontFile) {
		ByteBuffer ttfBuffer = ResourceFileReader.ioResourceToByteBuffer(fontFile, 1024);
		
		final int BITMAP_WIDTH = 1024;
		final int BITMAP_HEIGHT = 1024;

		final int FONT_HEIGHT = 18;
		int fontTextureID = glGenTextures();

		STBTTFontinfo fontInfo = STBTTFontinfo.create();
		STBTTPackedchar.Buffer cdata = STBTTPackedchar.create(96);

		float scale;
		float descent;
		
		try (MemoryStack stack = MemoryStack.stackPush()) {
			stbtt_InitFont(fontInfo, ttfBuffer);
			scale = stbtt_ScaleForPixelHeight(fontInfo, FONT_HEIGHT);
			
			IntBuffer descentBuffer = stack.mallocInt(1);
			stbtt_GetFontVMetrics(fontInfo, null, descentBuffer, null);
			descent = descentBuffer.get(0) * scale;
			
			ByteBuffer bitmap = memAlloc(BITMAP_WIDTH * BITMAP_HEIGHT);
			
			STBTTPackContext packContext = STBTTPackContext.malloc(stack);
			stbtt_PackBegin(packContext, bitmap, BITMAP_WIDTH, BITMAP_HEIGHT, 0, 1, NULL);
			stbtt_PackSetOversampling(packContext, 4, 4);
			stbtt_PackFontRange(packContext, ttfBuffer, 0, FONT_HEIGHT, 32, cdata);
			stbtt_PackEnd(packContext);
			
			glPixelStorei(GL11.GL_UNPACK_ALIGNMENT, 1); // 8 bpp = 1 byte per pixel
			glBindTexture(GL_TEXTURE_2D, fontTextureID);
			glTexImage2D(GL_TEXTURE_2D, 0, GL30.GL_R8, BITMAP_WIDTH, BITMAP_HEIGHT, 0, GL11.GL_RED, GL11.GL_UNSIGNED_BYTE, bitmap);
			// can free bitmap at this point
			glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
			glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
			
			memFree(bitmap);
		}
		
		return new FontDetails(FONT_HEIGHT, descent, scale, fontInfo, cdata, fontTextureID);
	}
	
	/**
	 * 
		fread(ttf_buffer, 1, 1<<20, fopen("c:/windows/fonts/times.ttf", "rb"));
	    stbtt_BakeFontBitmap(ttf_buffer,0, 32.0, temp_bitmap,512,512, 32,96, cdata); // no guarantee this fits!
	    // can free ttf_buffer at this point
	    glGenTextures(1, &ftex);
	    glBindTexture(GL_TEXTURE_2D, ftex);
	    glTexImage2D(GL_TEXTURE_2D, 0, GL_ALPHA, 512,512, 0, GL_ALPHA, GL_UNSIGNED_BYTE, temp_bitmap);
	    // can free temp_bitmap at this point
	    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);

	 * @param fontName
	 * @param fontSize
	 * @return
	 */
	public static FontSTB loadFont(String fontName, int fontSize) {
		ByteBuffer ttfBuffer = ResourceFileReader.ioResourceToByteBuffer(ResourceConstants.FONT_PATH + fontName, 1024);
		
		STBTTFontinfo info = STBTTFontinfo.create();
		if (!STBTruetype.stbtt_InitFont(info, ttfBuffer)) {
			throw new IllegalStateException();
		}
		
		STBTTBakedChar.Buffer cdata = STBTTBakedChar.malloc(96);
		
		ByteBuffer bitmap = BufferUtils.createByteBuffer(512 * 512);
		STBTruetype.stbtt_BakeFontBitmap(ttfBuffer, fontSize, bitmap, 512, 512, 32, cdata);
		
		int bTex = GL11.glGenTextures();
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, bTex);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL12.GL_CLAMP_TO_EDGE);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL12.GL_CLAMP_TO_EDGE);

		GL11.glPixelStorei(GL11.GL_UNPACK_ALIGNMENT, 1); // 8 bpp = 1 byte per pixel
		GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_ALPHA8, 512, 512, 0, GL11.GL_ALPHA, GL11.GL_UNSIGNED_BYTE, bitmap);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
		
		TextureAtlas bitmapTexture = new TextureAtlas(bTex);
		
		return new FontSTB(fontSize, info, cdata, bitmapTexture);
	}
	
	/**
	 * @param fontName
	 * @return
	 */
	public static FontSTB loadFont(String fontName) {
		return loadFont(fontName, FONT_HEIGHT);
	}
	
	public static SimpleEntry<STBTTFontinfo, ByteBuffer> loadFont(ByteBuffer ttf) {
		final int BITMAP_W = 1024;
		final int BITMAP_H = 1024;

		final int FONT_HEIGHT = 18;
		int fontTextureId = glGenTextures();

		STBTTFontinfo fontInfo = STBTTFontinfo.create();
		STBTTPackedchar.Buffer cdata = STBTTPackedchar.create(95);

		float scale;
		float descent;

		try (MemoryStack stack = stackPush()) {
			stbtt_InitFont(fontInfo, ttf);
			scale = stbtt_ScaleForPixelHeight(fontInfo, FONT_HEIGHT);

			IntBuffer descentBuffer = stack.mallocInt(1);
			stbtt_GetFontVMetrics(fontInfo, null, descentBuffer, null);
			descent = descentBuffer.get(0) * scale;

			ByteBuffer bitmap = memAlloc(BITMAP_W * BITMAP_H);

			STBTTPackContext pc = STBTTPackContext.malloc(stack);
			stbtt_PackBegin(pc, bitmap, BITMAP_W, BITMAP_H, 0, 1, NULL);
			stbtt_PackSetOversampling(pc, 4, 4);
			stbtt_PackFontRange(pc, ttf, 0, FONT_HEIGHT, 32, cdata);
			stbtt_PackEnd(pc);

			// Convert R8 to RGBA8
			ByteBuffer texture = memAlloc(BITMAP_W * BITMAP_H * 4);
			for (int i = 0; i < bitmap.capacity(); i++) {
				texture.putInt((bitmap.get(i) << 24) | 0x00FFFFFF);
			}
			texture.flip();

			glBindTexture(GL_TEXTURE_2D, fontTextureId);
			glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA8, BITMAP_W, BITMAP_H, 0, GL_RGBA, GL_UNSIGNED_INT_8_8_8_8_REV, texture);
			glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
			glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);

			memFree(texture);
			memFree(bitmap);
		}
		
		return new SimpleEntry<STBTTFontinfo, ByteBuffer>(fontInfo, ttf);
	}
	
	/**
	 * TODO Move this to test code, it's kind of pointless here
	 * Save out a PNG file of what would be rendered into a font file as a gray scale bitmap instead.
	 * This will save the file in the root directory of the project as "fontName_{@value #FONT_HEIGHT}.png"
	 * @param fontName The font to render
	 */
	public static void debugPrintFontBitmap(String fontName) {
		ByteBuffer ttfBuffer = ResourceFileReader.ioResourceToByteBuffer(ResourceConstants.FONT_PATH + fontName, 1024);
		
		STBTTFontinfo info = STBTTFontinfo.create();
		if (!STBTruetype.stbtt_InitFont(info, ttfBuffer)) {
			throw new IllegalStateException();
		}

		STBTTBakedChar.Buffer cdata = STBTTBakedChar.malloc(96);
		ByteBuffer bitmap = BufferUtils.createByteBuffer(512 * 512);
		STBTruetype.stbtt_BakeFontBitmap(ttfBuffer, FONT_HEIGHT, bitmap, 512, 512, 32, cdata);
		
		// Rendering font bitmap to png file
		byte[] glyphArray = new byte[bitmap.capacity()];
		bitmap.get(glyphArray);
		BufferedImage img = new BufferedImage(512, 512, BufferedImage.TYPE_BYTE_GRAY);
		img.setData(Raster.createRaster(img.getSampleModel(), new DataBufferByte(glyphArray, glyphArray.length), new Point()));
		
		try {
			ImageIO.write(img, "png", new File(fontName + "_" + FONT_HEIGHT + ".png"));
			
			System.out.println("Saved font to: " + fontName + "_" + FONT_HEIGHT + ".png");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}
