package com.avogine.render.loader.font;

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
import static org.lwjgl.system.MemoryUtil.*;

import java.awt.Point;
import java.awt.image.*;
import java.io.*;
import java.nio.*;

import javax.imageio.ImageIO;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.*;
import org.lwjgl.stb.*;
import org.lwjgl.system.MemoryStack;

import com.avogine.logging.AvoLog;
import com.avogine.render.data.FontDetails;
import com.avogine.util.resource.*;

/**
 * TODO Customizable font sizes, scalable bitmap sizes to match, customizable character sets (may require multiple textures and some sort of texture sheet indexing to fit everything)
 *
 */
public class FontLoader {

	/** {@value #FONT_HEIGHT} */
	private static final int FONT_HEIGHT = 24;
	
	private FontLoader() {
		
	}
	
	/**
	 * @param fontFile
	 * @param height 
	 * @return A {@link FontDetails} containing the loaded font.
	 */
	public static FontDetails loadFont(String fontFile, int height) {
		ByteBuffer ttfBuffer = ResourceFileReader.ioResourceToByteBuffer(ResourceConstants.FONTS.with(fontFile), 1024);
		
		final int BITMAP_WIDTH = 1024;
		final int BITMAP_HEIGHT = 1024;

		int fontTextureID = glGenTextures();

		STBTTFontinfo fontInfo = STBTTFontinfo.create();
		STBTTPackedchar.Buffer cdata = STBTTPackedchar.create(96);

		float scale;
		float descent;
		
		try (MemoryStack stack = MemoryStack.stackPush()) {
			stbtt_InitFont(fontInfo, ttfBuffer);
			scale = stbtt_ScaleForPixelHeight(fontInfo, height);
			
			IntBuffer descentBuffer = stack.mallocInt(1);
			stbtt_GetFontVMetrics(fontInfo, null, descentBuffer, null);
			descent = descentBuffer.get(0) * scale;
			
			ByteBuffer bitmap = memAlloc(BITMAP_WIDTH * BITMAP_HEIGHT);
			
			STBTTPackContext packContext = STBTTPackContext.malloc(stack);
			stbtt_PackBegin(packContext, bitmap, BITMAP_WIDTH, BITMAP_HEIGHT, 0, 1, NULL);
			stbtt_PackSetOversampling(packContext, 4, 4);
			stbtt_PackFontRange(packContext, ttfBuffer, 0, height, 32, cdata);
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
	
	public static FontDetails loadFont(String fontFile) {
		return loadFont(fontFile, FONT_HEIGHT);
	}
	
	/**
	 * TODO Move this to test code, it's kind of pointless here
	 * Save out a PNG file of what would be rendered into a font file as a gray scale bitmap instead.
	 * This will save the file in the root directory of the project as "fontName_{@value #FONT_HEIGHT}.png"
	 * @param fontName The font to render
	 */
	public static void debugPrintFontBitmap(String fontName) {
		ByteBuffer ttfBuffer = ResourceFileReader.ioResourceToByteBuffer(ResourceConstants.FONTS.with(fontName), 1024);
		
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
			
			AvoLog.log().info("Saved font to: {}_{}.png", fontName, FONT_HEIGHT);
		} catch (IOException e) {
			AvoLog.log().error("Failed to save debug font bitmap.", e);
		}
	}
	
}
