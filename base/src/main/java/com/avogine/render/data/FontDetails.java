package com.avogine.render.data;

import static org.lwjgl.opengl.GL11.glBindTexture;
import static org.lwjgl.opengl.GL11.glGenTextures;
import static org.lwjgl.opengl.GL11.glPixelStorei;
import static org.lwjgl.opengl.GL11.glTexImage2D;
import static org.lwjgl.opengl.GL11.glTexParameteri;
import static org.lwjgl.opengl.GL11C.GL_LINEAR;
import static org.lwjgl.opengl.GL11C.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11C.GL_TEXTURE_MAG_FILTER;
import static org.lwjgl.opengl.GL11C.GL_TEXTURE_MIN_FILTER;
import static org.lwjgl.stb.STBTruetype.*;

import java.awt.Point;
import java.awt.image.*;
import java.io.*;
import java.nio.*;

import javax.imageio.ImageIO;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.*;
import org.lwjgl.stb.*;
import org.lwjgl.system.*;

import com.avogine.logging.AvoLog;
import com.avogine.util.ResourceUtil;

/**
 * TODO#38 <a href="https://github.com/Avoduhcado/MAvogine/issues/38">FontDetails improvements #38</a>
 */
public class FontDetails {

	/** {@value #FONT_HEIGHT} */
	private static final int FONT_HEIGHT = 24;
	
	private static final int BITMAP_WIDTH = 1024;
	private static final int BITMAP_HEIGHT = 1024;
	
	private String fontName;
	
	private int size;
	private float descent;
	private float scale;
	
	private STBTTFontinfo fontInfo;
	private STBTTPackedchar.Buffer charData;
	
	private int textureId;
	
	/**
	 * @param fileName
	 * @param fontHeight
	 */
	public FontDetails(String fileName, int fontHeight) {
		this.fontName = fileName;
		this.size = fontHeight;
		
		ByteBuffer ttfBuffer = ResourceUtil.readResourceToBuffer(fileName, 16 * 1024);
		
		fontInfo = STBTTFontinfo.create();
		charData = STBTTPackedchar.create(96);
		
		try (MemoryStack stack = MemoryStack.stackPush()) {
			stbtt_InitFont(fontInfo, ttfBuffer);
			scale = stbtt_ScaleForPixelHeight(fontInfo, fontHeight);
			
			IntBuffer descentBuffer = stack.mallocInt(1);
			stbtt_GetFontVMetrics(fontInfo, null, descentBuffer, null);
			descent = descentBuffer.get() * scale;
			
			ByteBuffer bitmap = MemoryUtil.memAlloc(BITMAP_WIDTH * BITMAP_HEIGHT);
			
			STBTTPackContext packContext = STBTTPackContext.malloc(stack);
			stbtt_PackBegin(packContext, bitmap, BITMAP_WIDTH, BITMAP_HEIGHT, 0, 1, MemoryUtil.NULL);
			stbtt_PackSetOversampling(packContext, 4, 4);
			stbtt_PackFontRange(packContext, ttfBuffer, 0, fontHeight, 32, charData);
			stbtt_PackEnd(packContext);
			
			generateFontTexture(bitmap);
			
			MemoryUtil.memFree(bitmap);
		}
	}
	
	/**
	 * @param fileName
	 */
	public FontDetails(String fileName) {
		this(fileName, FONT_HEIGHT);
	}
	
	private void generateFontTexture(ByteBuffer bitmap) {
		textureId = glGenTextures();

		glPixelStorei(GL11.GL_UNPACK_ALIGNMENT, 1); // 8 bpp = 1 byte per pixel
		glBindTexture(GL_TEXTURE_2D, textureId);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
		glTexImage2D(GL_TEXTURE_2D, 0, GL30.GL_R8, BITMAP_WIDTH, BITMAP_HEIGHT, 0, GL11.GL_RED, GL11.GL_UNSIGNED_BYTE, bitmap);
		// can free bitmap at this point
	}

	/**
	 * Free up any memory in use by this {@link FontDetails}.
	 */
	public void cleanup() {
		GL11.glDeleteTextures(textureId);
		fontInfo.free();
		charData.free();
	}
	
	/**
	 * @return the fontName
	 */
	public String getFontName() {
		return fontName;
	}
	
	/**
	 * @return the size
	 */
	public int getSize() {
		return size;
	}
	
	/**
	 * @return the descent
	 */
	public float getDescent() {
		return descent;
	}
	
	/**
	 * @return the scale
	 */
	public float getScale() {
		return scale;
	}
	
	/**
	 * @return the fontInfo
	 */
	public STBTTFontinfo getFontInfo() {
		return fontInfo;
	}
	
	/**
	 * @return the charData
	 */
	public STBTTPackedchar.Buffer getCharData() {
		return charData;
	}
	
	/**
	 * @return the textureId
	 */
	public int getTextureId() {
		return textureId;
	}

	/**
	 * TODO Move this to test code, it's kind of pointless here
	 * Save out a PNG file of what would be rendered into a font file as a gray scale bitmap instead.
	 * This will save the file in the root directory of the project as "fontName_{@value #FONT_HEIGHT}.png"
	 * @param fontName The font to render
	 */
	public static void debugPrintFontBitmap(String fontName) {
		ByteBuffer ttfBuffer = ResourceUtil.readResourceToBuffer(fontName, 16 * 1024);
		
		STBTTFontinfo info = STBTTFontinfo.create();
		if (!stbtt_InitFont(info, ttfBuffer)) {
			throw new IllegalStateException();
		}

		STBTTBakedChar.Buffer cdata = STBTTBakedChar.malloc(96);
		ByteBuffer bitmap = BufferUtils.createByteBuffer(512 * 512);
		stbtt_BakeFontBitmap(ttfBuffer, FONT_HEIGHT, bitmap, 512, 512, 32, cdata);
		
		// Rendering font bitmap to png file
		byte[] glyphArray = new byte[bitmap.capacity()];
		bitmap.get(glyphArray);
		BufferedImage img = new BufferedImage(512, 512, BufferedImage.TYPE_BYTE_GRAY);
		img.setData(Raster.createRaster(img.getSampleModel(), new DataBufferByte(glyphArray, glyphArray.length), new Point()));
		
		try {
			ImageIO.write(img, "png", new File(fontName + "_" + FONT_HEIGHT + ".png"));
			
			AvoLog.log().info("Saved font to: {}_{}.png", fontName, FONT_HEIGHT);
		} catch (IOException e) {
			AvoLog.log().error("Failed to save debug font bitmap.", e);
		}
	}
}
