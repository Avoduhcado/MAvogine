package com.avogine.render.shader.uniform;

import java.util.*;

import org.joml.Matrix4f;

import com.avogine.render.shader.uniform.light.*;

public class UniformMat4Array extends Uniform {

	private UniformMat4[] matrixUniforms;
	
	public UniformMat4Array(int size) {
		matrixUniforms = new UniformMat4[size];
		for (int i = 0; i < size; i++) {
			matrixUniforms[i] = new UniformMat4();
		}
	}
	
	@Override
	public void storeUniformLocation(int programID, String name) {
		for (int i = 0; i < matrixUniforms.length; i++) {
			matrixUniforms[i].storeUniformLocation(programID, name + "[" + i + "]");
		}
	}

	public void loadMatrixArray(Matrix4f[] matrices) {
		for (int i = 0; i < matrices.length; i++) {
			matrixUniforms[i].loadMatrix(matrices[i]);
		}
	}
	
	public void loadMatrix(Matrix4f matrix, int index) {
		matrixUniforms[index].loadMatrix(matrix);
	}
	
}
