package com.avogine.render.loader.font;

import java.util.*;

import com.avogine.render.data.FontDetails;

/**
 *
 */
public class FontCache {

	private final Map<String, FontDetails> fontMap;
	
	/**
	 * 
	 */
	public FontCache() {
		fontMap = new HashMap<>();
	}
	
	/**
	 * @param filePath The file path to the resource file containing the Font data to load.
	 * @return
	 */
	public FontDetails getFont(String filePath) {
		return fontMap.computeIfAbsent(filePath, FontDetails::new);
	}
	
	/**
	 * 
	 */
	public void cleanup() {
		fontMap.values().forEach(FontDetails::cleanup);
	}
}
