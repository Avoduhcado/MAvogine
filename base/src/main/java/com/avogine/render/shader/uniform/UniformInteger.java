package com.avogine.render.shader.uniform;

import org.lwjgl.opengl.GL20;

public class UniformInteger extends Uniform {

	private int currentValue;
	private boolean used = false;
	
	public void loadInteger(int value) {
		if (!used || currentValue != value){
			GL20.glUniform1i(super.getLocation(), value);
			used = true;
			currentValue = value;
		}
	}
}
