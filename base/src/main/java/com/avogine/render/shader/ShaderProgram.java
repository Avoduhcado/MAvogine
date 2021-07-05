package com.avogine.render.shader;

import java.lang.invoke.*;
import java.lang.reflect.*;
import java.util.*;
import java.util.function.*;
import java.util.stream.*;

import static org.lwjgl.opengl.GL33.*;
import org.slf4j.*;

import com.avogine.render.shader.uniform.Uniform;
import com.avogine.util.resource.ResourceConstants;
import com.avogine.util.resource.ResourceFileReader;

/**
 * TODO The System.exit(-1) calls should likely just be throwing exceptions instead
 */
public abstract class ShaderProgram {

	private static final Logger log = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass().getSimpleName());
	
	private final int programId;
	
	private int vertexShaderId;
	private int fragmentShaderId;
	
	/**
	 * @param vertexShaderFile
	 * @param fragmentShaderFile
	 */
	protected ShaderProgram(String vertexShaderFile, String fragmentShaderFile) {
		programId = glCreateProgram();
		vertexShaderId = createShader(vertexShaderFile, GL_VERTEX_SHADER);
		fragmentShaderId = createShader(fragmentShaderFile, GL_FRAGMENT_SHADER);
		link();
	}
	
	/**
	 * 
	 */
	public void bind() {
		glUseProgram(programId);
	}
	
	/**
	 * 
	 */
	public void unbind() {
		glUseProgram(0);
	}

	protected void storeAllUniformLocations(Uniform... uniforms) {
		// Via reflection, collect all of the public fields of the shader program and map them to instance and field
		// so that the uniform variables can link up directly with the name supplied to the variable.
		Map<Object, Field> reflectionMap = Stream.of(getClass().getFields())
				.collect(Collectors.toMap(field -> {
					try {
						return field.get(this);
					} catch (IllegalArgumentException | IllegalAccessException e) {
						log.error("Failed to reflectively access class variable!", e);
					}
					return null;
				}, Function.identity()));
		
		for (Uniform uniform : uniforms) {
			uniform.storeUniformLocation(programId, reflectionMap.get(uniform).getName());
		}
		glValidateProgram(programId);
	}
	
	/**
	 * An alternate to using the {@code location layout} format of linking vertex attributes.
	 * @param inVariables
	 */
	@SuppressWarnings("unused")
	private void bindAttributes(String[] inVariables){
		for (int i = 0; i < inVariables.length; i++) {
			glBindAttribLocation(programId, i, inVariables[i]);
		}
	}
	
	/**
	 * Read in the shader file provided into a {@code CharSequence} so it can be compiled into GLSL shader code.
	 * @param shaderFile The file name of the shader file to be created, including file extension
	 * @param shaderType The type of shader being loaded
	 * @return An integer address to the shader in memory
	 * @throws Exception If there are any errors reading, creating, or compiling the shader
	 */
	protected int createShader(String shaderFile, int shaderType) {
		CharSequence shaderCode = ResourceFileReader.readTextFile(ResourceConstants.SHADER_PATH + shaderFile);
		
		int shaderId = glCreateShader(shaderType);
		if (shaderId == 0) {
			log.error("Error creating shader. Type: {}", shaderType);
			System.exit(-1);
		}
		
		glShaderSource(shaderId, shaderCode);
		glCompileShader(shaderId);
		if (glGetShaderi(shaderId, GL_COMPILE_STATUS) == 0) {
			if (log.isErrorEnabled()) {
				log.error("Error compiling shader: {}\n{}", shaderFile, glGetShaderInfoLog(shaderId, 500));
			}
			System.exit(-1);
		}
		
		glAttachShader(programId, shaderId);
		
		return shaderId;
	}
	
	protected void link() {
		glLinkProgram(programId);
		if (glGetProgrami(programId, GL_LINK_STATUS) == 0) {
			if (log.isErrorEnabled()) {
				log.error("Error linking shader code: {}", glGetProgramInfoLog(programId));
			}
			System.exit(-1);
		}
		
		glValidateProgram(programId);
		if (glGetProgrami(programId, GL_VALIDATE_STATUS) == 0 && log.isWarnEnabled()) {
			log.warn("Warning validating shader code: {}", glGetProgramInfoLog(programId));
		}
		
		// Detaching shaders will delete the source code of the shader itself, which may make debugging difficult for minimal performance gain
//		if (vertexShaderId != 0) {
//			GL20.glDetachShader(programId, vertexShaderId);
//		}
//		if (fragmentShaderId != 0) {
//			GL20.glDetachShader(programId, fragmentShaderId);
//		}
		glDeleteShader(vertexShaderId);
		glDeleteShader(fragmentShaderId);
	}
	
	/**
	 * Unbind and delete this program from memory.
	 */
	public void cleanup() {
		unbind();
		glDeleteProgram(programId);
	}
	
}
