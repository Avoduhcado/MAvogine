package com.avogine.render.shader.uniform;

public class UniformFloatArray extends Uniform {

	private UniformFloat[] floatUniforms;
	
	public UniformFloatArray(String name, int size) {
		super(name);
		floatUniforms = new UniformFloat[size];
		for (int i = 0; i < size; i++){
			floatUniforms[i] = new UniformFloat(name + "[" + i + "]");
		}
	}
	
	@Override
	public void storeUniformLocation(int programID) {
		for (UniformFloat floatUniform : floatUniforms) {
			floatUniform.storeUniformLocation(programID);
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
