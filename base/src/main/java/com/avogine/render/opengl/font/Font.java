package com.avogine.render.opengl.font;

import static org.lwjgl.stb.STBTruetype.*;

import java.awt.Point;
import java.awt.image.*;
import java.io.*;
import java.nio.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

import javax.imageio.ImageIO;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.stb.*;
import org.lwjgl.system.*;

import com.avogine.logging.AvoLog;
import com.avogine.render.font.*;
import com.avogine.render.image.ImageData;
import com.avogine.render.image.data.PixelBuffer;
import com.avogine.render.opengl.Texture;
import com.avogine.render.opengl.Texture.Image2D;
import com.avogine.util.ResourceUtils;

/**
 * A data resource wrapper of TrueType fonts.
 */
public class Font {

	private static final int DEFAULT_SIZE = 24;
	
	private static final int BITMAP_WIDTH = 1024;
	private static final int BITMAP_HEIGHT = 1024;
	
	private record FontRenderData(List<Integer> sizes, STBTTPackedchar.Buffer cdata, Texture texture) {
		public void cleanup() {
			cdata.free();
			texture.cleanup();
		}
	}
	
	private final ByteBuffer ttf;
	
	private final STBTTFontinfo fontinfo;
	
	@SuppressWarnings("unused")
	private final FontNameInfo fontNameInfo;
	private final FontMetrics fontMetrics;
	
	private final SequencedMap<Integer, FontRenderData> renderCache;
	
	private final STBTTAlignedQuad quad;
	
	/**
	 * @param resource 
	 * @param fontSizes 
	 * 
	 */
	public Font(String resource, int...fontSizes) {
		ttf = ResourceUtils.readResourceToBuffer(resource);
		
		fontinfo = STBTTFontinfo.create();
		if (!stbtt_InitFont(fontinfo, ttf)) {
			throw new IllegalStateException("Failed to intialize font information.");
		}
		
		fontNameInfo = createFontNameInfo();
		fontMetrics = createFontMetrics();
		
		renderCache = new LinkedHashMap<>();
		if (fontSizes.length == 0) {
			fontSizes = new int[] { DEFAULT_SIZE };
		}
		addRenderData(fontSizes);
		
		quad = STBTTAlignedQuad.malloc();
	}
	
	/**
	 * 
	 */
	public void cleanup() {
		renderCache.values().stream().distinct().forEach(FontRenderData::cleanup);
		renderCache.clear();
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
		var renderData = renderCache.getOrDefault((int) size, renderCache.firstEntry().getValue());
		renderData.cdata.position((renderData.sizes.contains((int) size) ? renderData.sizes.indexOf((int) size) : 0) * 96);
		
		stbtt_GetPackedQuad(renderData.cdata, BITMAP_WIDTH, BITMAP_HEIGHT, c - 32, xPos, yPos, quad, false);
		
		vertexData.put(quad.x0()).put(quad.y1()).put(quad.s0()).put(quad.t1());
		vertexData.put(quad.x1()).put(quad.y0()).put(quad.s1()).put(quad.t0());
		vertexData.put(quad.x0()).put(quad.y0()).put(quad.s0()).put(quad.t0());
		vertexData.put(quad.x0()).put(quad.y1()).put(quad.s0()).put(quad.t1());
		vertexData.put(quad.x1()).put(quad.y1()).put(quad.s1()).put(quad.t1());
		vertexData.put(quad.x1()).put(quad.y0()).put(quad.s1()).put(quad.t0());
	}
	
	/**
	 * @param fontSizes
	 * @return this
	 */
	public Font addRenderData(int...fontSizes) {
		var cdata = STBTTPackedchar.malloc(fontSizes.length * 96);
		Texture fontMap = packFontMap(cdata, fontSizes);
		var fontData = new FontRenderData(Arrays.stream(fontSizes).boxed().toList(), cdata, fontMap);
		for (int size : fontSizes) {
			renderCache.put(size, fontData);
		}
		return this;
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
	 * @param size 
	 * @return the texture
	 */
	public Texture getTexture(float size) {
		return renderCache.getOrDefault((int) size, renderCache.firstEntry().getValue()).texture;
	}
	
	/**
	 * @return the defaultSize
	 */
	public float getDefaultSize() {
		return renderCache.firstEntry().getKey();
	}
	
	/**
	 * @param size
	 * @return true if the given size is already packed in this {@link Font}.
	 */
	public boolean containsSize(int size) {
		return renderCache.containsKey(size);
	}
	
	/**
	 * @param sizes
	 * @return true if all of the given sizes are already packed in this {@link Font}.
	 */
	public boolean containsAllSizes(int...sizes) {
		for (int size : sizes) {
			if (!containsSize(size)) {
				return false;
			}
		}
		return true;
	}
	
	private float getScale(float size) {
		return stbtt_ScaleForPixelHeight(fontinfo, size);
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
	
	private Texture packFontMap(STBTTPackedchar.Buffer cdata, int[] sizes) {
		try (MemoryStack stack = MemoryStack.stackPush()) {
			STBTTPackContext packContext = STBTTPackContext.malloc(stack);
			ByteBuffer bitmap = MemoryUtil.memAlloc(BITMAP_WIDTH * BITMAP_HEIGHT);
			stbtt_PackBegin(packContext, bitmap, BITMAP_WIDTH, BITMAP_HEIGHT, 0, 1, MemoryUtil.NULL);
			for (int i = 0; i < sizes.length; i++) {
				int p = i * 96;
				cdata.limit(p + 96);
				cdata.position(p);
				stbtt_PackSetOversampling(packContext, 4, 4);
				stbtt_PackFontRange(packContext, ttf, 0, sizes[i], 32, cdata);
			}
			cdata.clear(); // Doesn't actually clear the data, just resets position/mark
			stbtt_PackEnd(packContext);

			var fontPixels = new ImageData(BITMAP_WIDTH, BITMAP_HEIGHT, GL11.GL_RED, new PixelBuffer(bitmap));
			try (var pixelBuffer = fontPixels.getPixelBuffers()) {
				return Texture.gen().bind()
						.filterLinear()
						.wrap2DRepeat()
						.tex(Image2D.from(fontPixels));
			} finally {
				Texture.unbind();
			}
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
