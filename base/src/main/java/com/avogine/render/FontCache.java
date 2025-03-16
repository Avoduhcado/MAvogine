package com.avogine.render;

import java.util.*;

import com.avogine.render.data.font.Font;

/**
 *
 */
public class FontCache {

	private final Map<String, Font> fontMap;
	
	/**
	 * 
	 */
	public FontCache() {
		fontMap = new HashMap<>();
	}
	
	/**
	 * @param filePath The file path to the resource file containing the Font data to load.
	 * @param fontSizes 
	 * @return the cached {@link Font} for the given path, or if it didn't already exist, the newly loaded {@link Font}. 
	 */
	public Font getFont(String filePath, float...fontSizes) {
		return fontMap.computeIfAbsent(filePath, k -> new Font(k, fontSizes));
	}
	
	/**
	 * 
	 */
	public void cleanup() {
		fontMap.values().forEach(Font::cleanup);
	}
	
}
