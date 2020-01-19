package com.avogine.core.scene.shader.uniform;

import org.joml.Matrix4f;

public class UniformMat4Array extends Uniform {

	private UniformMat4[] matrixUniforms;
	
	public UniformMat4Array(String name, int size) {
		super(name);
		matrixUniforms = new UniformMat4[size];
		for (int i = 0; i < size; i++){
			matrixUniforms[i] = new UniformMat4(name + "[" + i + "]");
		}
	}
	
	@Override
	public void storeUniformLocation(int programID) {
		for(UniformMat4 matrixUniform : matrixUniforms){
			matrixUniform.storeUniformLocation(programID);
		}
	}

	public void loadMatrixArray(Matrix4f[] matrices){
		for (int i = 0; i < matrices.length; i++){
			matrixUniforms[i].loadMatrix(matrices[i]);
		}
	}
	
	public void loadMatrix(Matrix4f matrix, int index) {
		matrixUniforms[index].loadMatrix(matrix);
	}
	
}
