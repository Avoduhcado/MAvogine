package com.avogine.render.data.font;

/**
 * @param family
 * @param style
 */
public record FontNameInfo(String family, int style) {
	
	public static final int PLAIN = 0b00;
	public static final int BOLD = 0b01;
	public static final int ITALIC = 0b10;
	
}
