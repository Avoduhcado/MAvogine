package com.avogine.render.util;

import java.util.*;

import com.avogine.render.data.font.Font;

/**
 * A simple cache of {@link Font}s.
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
	 * @param resourcePath The file path to the resource file containing the Font data to load.
	 * @param sizes an optional array of sizes to pack into the {@link Font}.
	 * @return the cached {@link Font} for the given path, or if it didn't already exist, the newly loaded {@link Font}. 
	 */
	public Font getFont(String resourcePath, int...sizes) {
		if (fontMap.containsKey(resourcePath) && !fontMap.get(resourcePath).containsAllSizes(sizes)) {
			return fontMap.get(resourcePath).addRenderData(sizes);
		} else {
			return fontMap.computeIfAbsent(resourcePath, k -> new Font(k, sizes));
		}
	}
	
	/**
	 * 
	 */
	public void cleanup() {
		fontMap.values().forEach(Font::cleanup);
	}
	
}
