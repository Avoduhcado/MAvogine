package com.avogine.core.scene.shader.uniform;

import org.lwjgl.opengl.GL20;

public abstract class Uniform {

	private int location;
	private String name;
	
	protected Uniform(String name){
		this.name = name;
	}
	
	public void storeUniformLocation(int programID) {
		location = GL20.glGetUniformLocation(programID, name);
		if(location < 0){
			System.err.println("No uniform variable called " + name + " found!");
		}
	}
	
	protected int getLocation() {
		return location;
	}
	
	@Override
	public String toString() {
		return name;
	}
	
}
