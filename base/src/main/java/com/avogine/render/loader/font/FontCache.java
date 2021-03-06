package com.avogine.render.loader.font;

import java.util.HashMap;
import java.util.Map;

/**
 *
 */
public class FontCache {

	private final Map<String, FontSTB> fontMap = new HashMap<>();
	
	private static final FontCache cache = new FontCache();
	
	/**
	 * @return
	 */
	public static FontCache getInstance() {
		return cache;
	}
	
	/**
	 * @param fontname
	 * @return
	 */
	public FontSTB getFont(String fontname) {
		return fontMap.computeIfAbsent(fontname, FontLoaderSTB::loadFont);
	}
	
	/**
	 * 
	 */
	public void cleanup() {
		fontMap.values().forEach(FontSTB::cleanup);
	}
	
}
