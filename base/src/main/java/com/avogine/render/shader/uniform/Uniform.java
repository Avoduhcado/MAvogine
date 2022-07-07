package com.avogine.render.shader.uniform;

import org.lwjgl.opengl.*;
import org.slf4j.*;

import com.avogine.logging.*;

/**
 * Base class for uniform variables to be passed along to OpenGL shaders.
 */
public abstract class Uniform {
	private static final Logger logger = LogUtil.requestLogger();

	private int location;
	
	/**
	 * @param programID
	 * @param name
	 */
	public void storeUniformLocation(int programID, String name) {
		location = GL20.glGetUniformLocation(programID, name);
		if (location < 0) {
			logger.warn("No uniform variable called {} found!", name);
		}
	}
	
	protected int getLocation() {
		return location;
	}
	
}
