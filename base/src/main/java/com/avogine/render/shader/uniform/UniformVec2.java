package com.avogine.render.shader.uniform;

import org.joml.Vector2f;
import org.lwjgl.opengl.GL20;

public class UniformVec2 extends Uniform {

	private float currentX;
	private float currentY;
	private boolean used = false;

	public void loadVec2(Vector2f vector) {
		loadVec2(vector.x, vector.y);
	}

	public void loadVec2(float x, float y) {
		if (!used || x != currentX || y != currentY) {
			this.currentX = x;
			this.currentY = y;
			used = true;
			GL20.glUniform2f(super.getLocation(), x, y);
		}
	}
}
