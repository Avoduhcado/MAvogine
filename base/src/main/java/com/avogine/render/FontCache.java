package com.avogine.render;

import java.util.*;
import com.avogine.render.data.font.Font;
import com.avogine.render.data.font.FontIdentifier;

/**
 * A simple cache of {@link Font}s.
 * </p>
 * This cache is keyed by {@link FontIdentifier} which allows you to specify an array of permitted
 * font sizes, but makes no effort in merging existing cached values if there's a mismatch of sizes
 * and will instead insert a new entry if you specify a different set of permitted sizes.
 */
public class FontCache {

	private final Map<FontIdentifier, Font> fontMap;
	
	/**
	 * 
	 */
	public FontCache() {
		fontMap = new HashMap<>();
	}
	
	/**
	 * @param identifier the {@link FontIdentifier} representing the {@link Font} to retrieve.
	 * @return the cached {@link Font} for the given path, or if it didn't already exist, the newly loaded {@link Font}. 
	 */
	public Font getFont(FontIdentifier identifier) {
		return fontMap.computeIfAbsent(identifier, fontIdentifier -> new Font(fontIdentifier.fontResource(), fontIdentifier.sizes()));
	}
	
	/**
	 * @param filePath The file path to the resource file containing the Font data to load.
	 * @param fontSizes 
	 * @return the cached {@link Font} for the given path, or if it didn't already exist, the newly loaded {@link Font}. 
	 */
	public Font getFont(String resourcePath, float...sizes) {
		return getFont(new FontIdentifier(resourcePath, sizes));
	}
	
	/**
	 * 
	 */
	public void cleanup() {
		fontMap.values().forEach(Font::cleanup);
	}
	
}
