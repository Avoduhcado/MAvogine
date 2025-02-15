package com.avogine.render.shader;

import static org.lwjgl.opengl.GL20.*;

import java.lang.reflect.Field;
import java.util.*;
import java.util.function.Function;
import java.util.stream.*;

import org.lwjgl.opengl.*;

import com.avogine.logging.AvoLog;
import com.avogine.render.shader.uniform.Uniform;
import com.avogine.util.ResourceUtils;
import com.avogine.util.resource.ResourceConstants;

/**
 * Wrapper class for an OpenGL shader program object.
 */
public abstract class ShaderProgram {
	
	private final int programId;
	
	/**
	 * @param vertexShaderFile
	 * @param fragmentShaderFile
	 */
	protected ShaderProgram(String vertexShaderFile, String fragmentShaderFile) {
		this(new ShaderModuleData(vertexShaderFile, GL_VERTEX_SHADER), new ShaderModuleData(fragmentShaderFile, GL_FRAGMENT_SHADER));
	}
	
	/**
	 * Create a ShaderProgram from an arbitrary set of shader files.
	 * <p>
	 * <b>Note:</b> This makes no assertion that the supplied files constitute a complete shader. 
	 * @param shaders A list of {@link ShaderModuleData}s to construct the ShaderProgram from.
	 */
	protected ShaderProgram(ShaderModuleData...shaders) {
		programId = glCreateProgram();
		List<Integer> shaderIds = new ArrayList<>();
		for (ShaderModuleData shader : shaders) {
			shaderIds.add(createShader(shader.fileName, shader.type));
		}
		link(shaderIds);
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
	
	/**
	 * @return the programId
	 */
	public int getProgramId() {
		return programId;
	}

	protected void storeAllUniformLocations(Uniform... uniforms) {
		// Via reflection, collect all of the public fields of the shader program and map them to instance and field
		// so that the uniform variables can link up directly with the name supplied to the variable.
		Map<Object, Field> reflectionMap = Stream.of(getClass().getFields())
				.collect(Collectors.toMap(field -> {
					try {
						return field.get(this);
					} catch (IllegalArgumentException | IllegalAccessException e) {
						AvoLog.log().error("Failed to reflectively access class variable!", e);
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
	 * Read in the shader file contents provided into a String so it can be compiled into GLSL shader code.
	 * @param shaderFile The file name of the shader file to be created, including file extension
	 * @param shaderType The type of shader being loaded
	 * @return An integer address to the shader in memory
	 * @throws Exception If there are any errors reading, creating, or compiling the shader
	 */
	protected int createShader(String shaderFile, int shaderType) {
		String shaderCode = ResourceUtils.readResource(ResourceConstants.SHADERS.with(shaderFile));
		
		int shaderId = glCreateShader(shaderType);
		if (shaderId == 0) {
			AvoLog.log().error("Error creating shader. Type: {}", shaderType);
			throw new IllegalStateException();
		}
		
		glShaderSource(shaderId, shaderCode);
		glCompileShader(shaderId);
		if (glGetShaderi(shaderId, GL_COMPILE_STATUS) == 0) {
			String shaderErrorMessage = glGetShaderInfoLog(shaderId, 1024);
			AvoLog.log().error("Error compiling shader: {}\n{}", shaderFile, shaderErrorMessage);
			throw new IllegalStateException();
		}
		
		glAttachShader(programId, shaderId);
		
		return shaderId;
	}
	
	protected void link(List<Integer> shaderIds) {
		glLinkProgram(programId);
		if (glGetProgrami(programId, GL_LINK_STATUS) == 0) {
			String shaderErrorMessage = glGetProgramInfoLog(programId, 1024);
			AvoLog.log().error("Error linking shader code: {}", shaderErrorMessage);
			throw new IllegalStateException();
		}
		
		shaderIds.forEach(shader -> glDetachShader(programId, shader));
		shaderIds.forEach(GL20::glDeleteShader);
	}
	
	/**
	 * Validate the shader program.
	 * </p>
	 * This method should be reserved for debugging purposes as it's dependent on the current OpenGL state
	 * which may not be fully complete when actually running the full application.
	 */
	public void validate() {
		glValidateProgram(programId);
		if (glGetProgrami(programId, GL_VALIDATE_STATUS) == 0) {
			String shaderErrorMessage = glGetProgramInfoLog(programId, 1024);
			AvoLog.log().warn("Warning validating shader code: {}", shaderErrorMessage);
		}
	}
	
	/**
	 * Un-bind and delete this program from memory.
	 */
	public void cleanup() {
		unbind();
		glDeleteProgram(programId);
	}
	
	/**
	 * A tuple of a shader file name and its shader type.
	 * @param fileName The name of the file to load the shader code from.
	 * @param type The type of the shader. One of:<br>
	 * <table><tr>
	 * <td>{@link GL20C#GL_VERTEX_SHADER VERTEX_SHADER}</td>
	 * <td>{@link GL20C#GL_FRAGMENT_SHADER FRAGMENT_SHADER}</td>
	 * <td>{@link GL32#GL_GEOMETRY_SHADER GEOMETRY_SHADER}</td>
	 * <td>{@link GL40#GL_TESS_CONTROL_SHADER TESS_CONTROL_SHADER}</td>
	 * </tr><tr>
	 * <td>{@link GL40#GL_TESS_EVALUATION_SHADER TESS_EVALUATION_SHADER}</td>
	 * </tr></table>
	 */
	public record ShaderModuleData(String fileName, int type) {
		
	}
	
}
