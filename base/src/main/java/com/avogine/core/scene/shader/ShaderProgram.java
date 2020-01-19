package com.avogine.core.scene.shader;

import org.lwjgl.opengl.GL20;

import com.avogine.core.resource.util.ResourceConstants;
import com.avogine.core.resource.util.ResourceFileReader;
import com.avogine.core.scene.shader.uniform.Uniform;

public abstract class ShaderProgram {

	private final int programId;
	
	private int vertexShaderId;
	private int fragmentShaderId;
	
	public ShaderProgram(String vertexShaderFile, String fragmentShaderFile) {
		programId = GL20.glCreateProgram();
		vertexShaderId = createShader(vertexShaderFile, GL20.GL_VERTEX_SHADER);
		fragmentShaderId = createShader(fragmentShaderFile, GL20.GL_FRAGMENT_SHADER);
		link();
	}
	
	public void bind() {
		GL20.glUseProgram(programId);
	}
	
	public void unbind() {
		GL20.glUseProgram(0);
	}

	protected void storeAllUniformLocations(Uniform... uniforms) {
		for (Uniform uniform : uniforms) {
			uniform.storeUniformLocation(programId);
		}
		GL20.glValidateProgram(programId);
	}
	
	/**
	 * An alternate to using the <code>location layout</code> format of linking vertex attributes.
	 * @param inVariables
	 */
	@SuppressWarnings("unused")
	private void bindAttributes(String[] inVariables){
		for (int i = 0; i < inVariables.length; i++) {
			GL20.glBindAttribLocation(programId, i, inVariables[i]);
		}
	}
	
	/**
	 * Read in the shader file provided into a {@link CharSequence} so it can be compiled into GLSL shader code.
	 * @param shaderFile The file name of the shader file to be created, including file extension
	 * @param shaderType The type of shader being loaded
	 * @return An int address to the shader in memory
	 * @throws Exception If there are any errors reading, creating, or compiling the shader
	 */
	protected int createShader(String shaderFile, int shaderType) {
		CharSequence shaderCode = ResourceFileReader.readTextFile(ResourceConstants.SHADER_PATH + shaderFile);
		
		int shaderId = GL20.glCreateShader(shaderType);
		if (shaderId == 0) {
			System.err.println("Error creating shader. Type: " + shaderType);
			System.exit(-1);
		}
		
		GL20.glShaderSource(shaderId, shaderCode);
		GL20.glCompileShader(shaderId);
		if (GL20.glGetShaderi(shaderId, GL20.GL_COMPILE_STATUS) == 0) {
			System.err.println("Error compiling shader: " + shaderFile + "\n" + GL20.glGetShaderInfoLog(shaderId, 500));
			System.exit(-1);
		}
		
		GL20.glAttachShader(programId, shaderId);
		
		return shaderId;
	}
	
	protected void link() {
		GL20.glLinkProgram(programId);
		if (GL20.glGetProgrami(programId, GL20.GL_LINK_STATUS) == 0) {
			System.err.println("Error linking shader code: " + GL20.glGetProgramInfoLog(programId));
			System.exit(-1);
		}
		
		if (vertexShaderId != 0) {
			GL20.glDetachShader(programId, vertexShaderId);
		}
		if (fragmentShaderId != 0) {
			GL20.glDetachShader(programId, fragmentShaderId);
		}
		GL20.glDeleteShader(vertexShaderId);
		GL20.glDeleteShader(fragmentShaderId);
		
		GL20.glValidateProgram(programId);
		if (GL20.glGetProgrami(programId, GL20.GL_VALIDATE_STATUS) == 0) {
			System.err.println("Warning validating shader code: " + GL20.glGetProgramInfoLog(programId));
		}
	}
	
	public void cleanup() {
		unbind();
		if (programId != 0) {
			GL20.glDeleteProgram(programId);
		}
	}
	
}
