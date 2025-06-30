package com.avogine.render.shader.uniform;

import java.nio.FloatBuffer;

import org.joml.Matrix4f;
import org.lwjgl.opengl.GL20;
import org.lwjgl.system.MemoryStack;

public class UniformMat4Array extends Uniform {

	public void loadMatrixArray(Matrix4f[] matrices) {
		try (MemoryStack stack = MemoryStack.stackPush()) {
			int length = matrices != null ? matrices.length : 0;
			FloatBuffer matrixBuffer = stack.mallocFloat(16 * length);
			for (int i = 0; i < length; i++) {
				matrices[i].get(16 * i, matrixBuffer);
			}
			GL20.glUniformMatrix4fv(super.getLocation(), false, matrixBuffer);
		}
	}
	
}
