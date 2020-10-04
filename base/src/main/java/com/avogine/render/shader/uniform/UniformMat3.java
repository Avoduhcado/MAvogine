package com.avogine.render.shader.uniform;

import java.nio.FloatBuffer;

import org.joml.Matrix3f;
import org.lwjgl.opengl.GL20;
import org.lwjgl.system.MemoryStack;

/**
 *
 */
public class UniformMat3 extends Uniform {

	public UniformMat3(String name) {
		super(name);
	}
	
	public void loadMatrix(Matrix3f matrix) {
		try(MemoryStack stack = MemoryStack.stackPush()) {
			FloatBuffer matrixBuffer = stack.mallocFloat(12);
			matrix.get(matrixBuffer);
			GL20.glUniformMatrix3fv(super.getLocation(), false, matrixBuffer);
		}
	}
	
}
