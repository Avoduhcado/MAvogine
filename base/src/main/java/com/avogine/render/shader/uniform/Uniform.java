package com.avogine.render.shader.uniform;

import org.lwjgl.opengl.GL20;

import com.avogine.logging.AvoLog;

/**
 * Base class for uniform variables to be passed along to OpenGL shaders.
 */
public abstract class Uniform {

	private int location;
	
	/**
	 * @param programID
	 * @param name
	 */
	public void storeUniformLocation(int programID, String name) {
		location = GL20.glGetUniformLocation(programID, name);
		if (location < 0) {
			AvoLog.log().warn("No uniform variable called {} found!", name);
		}
	}
	
	protected int getLocation() {
		return location;
	}
	
}
