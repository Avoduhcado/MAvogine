package com.avogine.render.shader.uniform;

public class UniformSamplerArray extends Uniform {

	private UniformSampler[] samplerUniforms;
	
	public UniformSamplerArray(int size) {
		samplerUniforms = new UniformSampler[size];
		for (int i = 0; i < size; i++) {
			samplerUniforms[i] = new UniformSampler();
		}
	}
	
	@Override
	public void storeUniformLocation(int programID, String name) {
		for (int i = 0; i < samplerUniforms.length; i++) {
			samplerUniforms[i].storeUniformLocation(programID, name + "[" + i + "]");
		}
	}
	
	public void loadTexUnitArray(int...units) {
		for (int i = 0; i < units.length; i++) {
			samplerUniforms[i].loadTexUnit(units[i]);
		}
	}
	
	public void loadTexUnit(int texUnit, int index) {
		samplerUniforms[index].loadTexUnit(texUnit);
	}
}
