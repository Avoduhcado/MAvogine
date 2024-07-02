package com.avogine.render.data;

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

}
