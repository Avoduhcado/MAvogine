package com.avogine.render.font;

/**
 * @param ascent 
 * @param descent 
 * @param lineGap 
 *
 */
public record FontMetrics(int ascent, int descent, int lineGap) {

	/**
	 * @return the un-scaled height of a single line.
	 */
	public int getLineHeight() {
		return ascent - descent + lineGap;
	}
	
	/**
	 * @return the un-scaled distance from the ascent to the baseline.
	 */
	public int getBaseline() {
		return ascent + lineGap;
	}
	
}
