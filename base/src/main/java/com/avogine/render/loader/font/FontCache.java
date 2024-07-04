package com.avogine.render.loader.font;

import java.util.*;

import com.avogine.render.data.FontDetails;

/**
 *
 */
public class FontCache {

	private final Map<String, FontDetails> fontMap = new HashMap<>();
	
	private static final FontCache cache = new FontCache();
	
	/**
	 * @return
	 */
	public static FontCache getInstance() {
		return cache;
	}
	
	/**
	 * @param fontName The name and file extension of the file in the {@code fonts} resource folder to load.
	 * @return
	 */
	public FontDetails getFont(String fontName) {
		return fontMap.computeIfAbsent(fontName, FontLoader::loadFont);
	}
	
	/**
	 * 
	 */
	public void cleanup() {
		fontMap.values().forEach(FontDetails::cleanup);
	}
	
}
