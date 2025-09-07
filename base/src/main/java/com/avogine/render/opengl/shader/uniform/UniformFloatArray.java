package com.avogine.render.opengl.shader.uniform;

public class UniformFloatArray extends Uniform {

	private UniformFloat[] floatUniforms;
	
	public UniformFloatArray(int size) {
		floatUniforms = new UniformFloat[size];
		for (int i = 0; i < size; i++) {
			floatUniforms[i] = new UniformFloat();
		}
	}
	
	@Override
	public void storeUniformLocation(int programID, String name) {
		for (int i = 0; i < floatUniforms.length; i++) {
			floatUniforms[i].storeUniformLocation(programID, name + "[" + i + "]");
		}
	}

	public void loadFloatArray(float[] floats) {
		for (int i = 0; i < floats.length; i++) {
			floatUniforms[i].loadFloat(floats[i]);
		}
	}
	
	public void loadFloat(float value, int index) {
		floatUniforms[index].loadFloat(value);
	}
}
