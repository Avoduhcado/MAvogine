package com.avogine.loader.font;

import java.awt.Point;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.Raster;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;

import javax.imageio.ImageIO;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import org.lwjgl.stb.STBTTBakedChar;
import org.lwjgl.stb.STBTTFontinfo;
import org.lwjgl.stb.STBTruetype;

import com.avogine.core.render.Texture;
import com.avogine.core.resource.util.ResourceConstants;
import com.avogine.core.resource.util.ResourceFileReader;

/**
 * TODO Customizable font sizes, scalable bitmap sizes to match, customizable character sets (may require multiple textures and some sort of texture sheet indexing to fit everything)
 * @author Dominus
 *
 */
public class FontLoaderSTB {

	/** {@value #FONT_HEIGHT} */
	private static final int FONT_HEIGHT = 24;
	
	public static FontSTB loadFont(String fontname) {
		//ByteBuffer ttfBuffer = ResourceFileReader.readResourceToByteBuffer(ResourceConstants.FONT_PATH + fontname);
		ByteBuffer ttfBuffer = ResourceFileReader.ioResourceToByteBuffer(ResourceConstants.FONT_PATH + fontname, 1024);
		
		STBTTFontinfo info = STBTTFontinfo.create();
		if (!STBTruetype.stbtt_InitFont(info, ttfBuffer)) {
			throw new IllegalStateException();
		}
		
		STBTTBakedChar.Buffer cdata = STBTTBakedChar.malloc(96);
		
		ByteBuffer bitmap = BufferUtils.createByteBuffer(512 * 512);
		STBTruetype.stbtt_BakeFontBitmap(ttfBuffer, FONT_HEIGHT, bitmap, 512, 512, 32, cdata);
		
		int bTex = GL11.glGenTextures();
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, bTex);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL12.GL_CLAMP_TO_EDGE);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL12.GL_CLAMP_TO_EDGE);

		GL11.glPixelStorei(GL11.GL_UNPACK_ALIGNMENT, 1); // 8 bpp = 1 byte per pixel
		GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_ALPHA8, 512, 512, 0, GL11.GL_ALPHA, GL11.GL_UNSIGNED_BYTE, bitmap);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
		
		Texture bitmapTexture = new Texture(bTex);
		
		return new FontSTB(FONT_HEIGHT, info, cdata, bitmapTexture);
	}
	
	/**
	 * TODO Move this to test code, it's kind of pointless here
	 * Save out a PNG file of what would be rendered into a font file as a gray scale bitmap instead.
	 * This will save the file in the root directory of the project as "fontname_{@value #FONT_HEIGHT}.png"
	 * @param fontname The font to render
	 */
	public static void debugPrintFontBitmap(String fontname) {
		ByteBuffer ttfBuffer = ResourceFileReader.ioResourceToByteBuffer(ResourceConstants.FONT_PATH + fontname, 1024);
		
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
			ImageIO.write(img, "png", new File(fontname + "_" + FONT_HEIGHT + ".png"));
			
			System.out.println("Saved font to: " + fontname + "_" + FONT_HEIGHT + ".png");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}
