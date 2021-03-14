package com.avogine.render.shader.uniform;

import org.lwjgl.opengl.GL20;

public abstract class Uniform {

	private int location;
	
	public void storeUniformLocation(int programID, String name) {
		location = GL20.glGetUniformLocation(programID, name);
		if (location < 0) {
			System.err.println("No uniform variable called " + name + " found!");
		}
	}
	
	protected int getLocation() {
		return location;
	}
	
}
