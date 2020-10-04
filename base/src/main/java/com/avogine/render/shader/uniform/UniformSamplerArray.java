package com.avogine.render.shader.uniform;

public class UniformSamplerArray extends Uniform {

	private UniformSampler[] samplerUniforms;
	
	public UniformSamplerArray(String name, int size) {
		super(name);
		samplerUniforms = new UniformSampler[size];
		for (int i = 0; i < size; i++){
			samplerUniforms[i] = new UniformSampler(name + "[" + i + "]");
		}
	}
	
	@Override
	public void storeUniformLocation(int programID) {
		for(UniformSampler samplerUniform : samplerUniforms){
			samplerUniform.storeUniformLocation(programID);
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
