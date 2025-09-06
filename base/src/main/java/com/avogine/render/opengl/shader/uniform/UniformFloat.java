package com.avogine.render.opengl.shader.uniform;

import org.lwjgl.opengl.GL20;

public class UniformFloat extends Uniform {

	private float currentValue;
	private boolean used = false;
	
	public void loadFloat(float value) {
		if (!used || currentValue!=value) {
			GL20.glUniform1f(super.getLocation(), value);
			used = true;
			currentValue = value;
		}
	}

}
