package com.avogine.render.data;

import org.lwjgl.opengl.GL11;
import org.lwjgl.stb.*;

/**
 * @param fontSize 
 * @param descent 
 * @param scale 
 * @param info 
 * @param cdata 
 * @param textureID 
 */
public record FontDetails(int fontSize, float descent, float scale, STBTTFontinfo info, STBTTPackedchar.Buffer cdata, int textureID) {

	/**
	 * Free up any memory in use by this {@link FontDetails}.
	 */
	public void cleanup() {
		GL11.glDeleteTextures(textureID);
		info.free();
		cdata.free();
	}
	
}
