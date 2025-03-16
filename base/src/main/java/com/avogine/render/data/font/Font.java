package com.avogine.render.data.font;

import static org.lwjgl.stb.STBTruetype.*;

import java.awt.Point;
import java.awt.image.*;
import java.io.*;
import java.nio.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

import javax.imageio.ImageIO;

import org.lwjgl.BufferUtils;
import org.lwjgl.stb.*;
import org.lwjgl.system.*;

import com.avogine.logging.AvoLog;
import com.avogine.render.data.texture.FontMap;
import com.avogine.util.ResourceUtils;

/**
 * A data resource wrapper of TrueType fonts.
 */
public class Font {

	private static final float DEFAULT_SIZE = 24.0f;
	
	private static final int BITMAP_WIDTH = 1024;
	private static final int BITMAP_HEIGHT = 1024;
	
	private final ByteBuffer ttf;
	
	private final STBTTFontinfo fontinfo;
	
	@SuppressWarnings("unused")
	private final FontNameInfo fontNameInfo;
	private final FontMetrics fontMetrics;
	
	private final STBTTPackedchar.Buffer cdata;

	private final FontMap texture;
	
	private final STBTTAlignedQuad quad;
	
	private final List<Float> sizes;
	private final Map<Float, Float> scales;
	
	/**
	 * @param resource 
	 * @param fontSizes 
	 * 
	 */
	public Font(String resource, float...fontSizes) {
		ttf = ResourceUtils.readResourceToBuffer(resource);
		sizes = new ArrayList<>();
		scales = new LinkedHashMap<>();
		
		fontinfo = STBTTFontinfo.create();
		if (!stbtt_InitFont(fontinfo, ttf)) {
			throw new IllegalStateException("Failed to intialize font information.");
		}
		
		fontNameInfo = createFontNameInfo();
		fontMetrics = createFontMetrics();
		sizes.add(DEFAULT_SIZE);
		computeScale(DEFAULT_SIZE);
		for (float size : fontSizes) {
			sizes.add(size);
			computeScale(size);
		}
		
		cdata = STBTTPackedchar.malloc(sizes.size() * 96);
		texture = packFontMap();
		quad = STBTTAlignedQuad.malloc();
	}
	
	/**
	 * 
	 */
	public void cleanup() {
		cdata.free();
		texture.cleanup();
		quad.free();
	}
	
	/**
	 * @param c
	 * @param xPos
	 * @param yPos
	 * @param size 
	 * @param vertexData 
	 */
	public void packQuad(int c, FloatBuffer xPos, FloatBuffer yPos, float size, FloatBuffer vertexData) {
		cdata.position((sizes.contains(size) ? sizes.indexOf(size) : 0) * 96);
		
		stbtt_GetPackedQuad(cdata, BITMAP_WIDTH, BITMAP_HEIGHT, c - 32, xPos, yPos, quad, false);
		
		vertexData.put(quad.x0()).put(quad.y1()).put(quad.s0()).put(quad.t1());
		vertexData.put(quad.x1()).put(quad.y0()).put(quad.s1()).put(quad.t0());
		vertexData.put(quad.x0()).put(quad.y0()).put(quad.s0()).put(quad.t0());
		vertexData.put(quad.x0()).put(quad.y1()).put(quad.s0()).put(quad.t1());
		vertexData.put(quad.x1()).put(quad.y1()).put(quad.s1()).put(quad.t1());
		vertexData.put(quad.x1()).put(quad.y0()).put(quad.s1()).put(quad.t0());
	}
	
	/**
	 * Useful for determining the "starting" point for where to render text.
	 * @param size
	 * @return the vertical distance in pixels from the font ascent to the baseline.
	 */
	public float getScaledBaseline(float size) {
		return getScale(size) * fontMetrics.getBaseline();
	}
	
	/**
	 * @param size 
	 * @return the vertical distance in pixels to advance the line when rendering multiple lines of text.
	 */
	public float getScaledVAdvance(float size) {
		return getScale(size) * fontMetrics.getLineHeight();
	}
	
	/**
	 * @return the texture
	 */
	public FontMap getTexture() {
		return texture;
	}
	
	/**
	 * @return the defaultSize
	 */
	public float getDefaultSize() {
		return sizes.getFirst();
	}
	
	private float computeScale(float size) {
		return scales.computeIfAbsent(size, key -> stbtt_ScaleForPixelHeight(fontinfo, key));
	}
	
	private float getScale(float size) {
		return scales.getOrDefault(size, computeScale(DEFAULT_SIZE));
	}
	
	private String getFontNameString(int nameID) {
		ByteBuffer nameBuffer = stbtt_GetFontNameString(fontinfo, STBTT_PLATFORM_ID_MICROSOFT, STBTT_UNICODE_EID_UNICODE_1_1, STBTT_MS_LANG_ENGLISH, nameID);
		byte[] bytes = new byte[nameBuffer.remaining()];
		nameBuffer.get(bytes);
		return new String(bytes, StandardCharsets.UTF_16);
	}
	
	private FontNameInfo createFontNameInfo() {
		String fontFamily = getFontNameString(1);
		String styleName = getFontNameString(2);
		
		String[] styleArray = styleName.split(" ");
		int style = FontNameInfo.PLAIN;
		for (String s : styleArray) {
			if (s.equalsIgnoreCase("italic")) {
				style |= FontNameInfo.ITALIC;
			} else if (s.equalsIgnoreCase("bold")) {
				style |= FontNameInfo.BOLD;
			} else if (!s.equalsIgnoreCase("regular")) {
				AvoLog.log().debug("Unknown style modifier: {}", s);
			}
		}
		
		return new FontNameInfo(fontFamily, style);
	}
	
	private FontMetrics createFontMetrics() {
		try (MemoryStack stack = MemoryStack.stackPush()) {
			IntBuffer pAscent = stack.mallocInt(1);
			IntBuffer pDescent = stack.mallocInt(1);
			IntBuffer pLineGap = stack.mallocInt(1);
			
			stbtt_GetFontVMetrics(fontinfo, pAscent, pDescent, pLineGap);
			
			return new FontMetrics(pAscent.get(0), pDescent.get(0), pLineGap.get(0));
		}
	}
	
	private FontMap packFontMap() {
		ByteBuffer bitmap = MemoryUtil.memAlloc(BITMAP_WIDTH * BITMAP_HEIGHT);
		try (MemoryStack stack = MemoryStack.stackPush()) {
			STBTTPackContext packContext = STBTTPackContext.malloc(stack);
			stbtt_PackBegin(packContext, bitmap, BITMAP_WIDTH, BITMAP_HEIGHT, 0, 1, MemoryUtil.NULL);
			for (int i = 0; i < sizes.size(); i++) {
				int p = i * 96;
				cdata.limit(p + 96);
				cdata.position(p);
				stbtt_PackSetOversampling(packContext, 4, 4);
				stbtt_PackFontRange(packContext, ttf, 0, sizes.get(i), 32, cdata);
			}
			cdata.clear(); // Doesn't actually clear the data, just resets position/mark
			stbtt_PackEnd(packContext);
			
			return new FontMap(BITMAP_WIDTH, BITMAP_HEIGHT, bitmap);
		} finally {
			MemoryUtil.memFree(bitmap);
		}
	}
	
	/**
	 * XXX Move this to test code, it's kind of pointless here
	 * Save out a PNG file of what would be rendered into a font file as a gray scale bitmap instead.
	 * This will save the file in the root directory of the project as "fontName_DEFAULT_SIZE.png"
	 * @param fontName The font to render
	 */
	public static void debugPrintFontBitmap(String fontName) {
		ByteBuffer ttfBuffer = ResourceUtils.readResourceToBuffer(fontName, 16 * 1024);
		
		STBTTFontinfo info = STBTTFontinfo.create();
		if (!stbtt_InitFont(info, ttfBuffer)) {
			throw new IllegalStateException();
		}

		STBTTBakedChar.Buffer cdata = STBTTBakedChar.malloc(96);
		ByteBuffer bitmap = BufferUtils.createByteBuffer(512 * 512);
		stbtt_BakeFontBitmap(ttfBuffer, DEFAULT_SIZE, bitmap, 512, 512, 32, cdata);
		
		// Rendering font bitmap to png file
		byte[] glyphArray = new byte[bitmap.capacity()];
		bitmap.get(glyphArray);
		BufferedImage img = new BufferedImage(512, 512, BufferedImage.TYPE_BYTE_GRAY);
		img.setData(Raster.createRaster(img.getSampleModel(), new DataBufferByte(glyphArray, glyphArray.length), new Point()));
		
		try {
			ImageIO.write(img, "png", new File(fontName + "_" + DEFAULT_SIZE + ".png"));
			
			AvoLog.log().info("Saved font to: {}_{}.png", fontName, DEFAULT_SIZE);
		} catch (IOException e) {
			AvoLog.log().error("Failed to save debug font bitmap.", e);
		}
	}
}
