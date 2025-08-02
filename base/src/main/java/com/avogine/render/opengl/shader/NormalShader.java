package com.avogine.render.opengl.shader;

import static com.avogine.util.resource.ResourceConstants.SHADERS;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL32.GL_GEOMETRY_SHADER;

import com.avogine.render.opengl.shader.uniform.UniformMat4;

/**
 *
 */
public class NormalShader extends ShaderProgram {

	public final UniformMat4 projection = new UniformMat4();
	public final UniformMat4 view = new UniformMat4();
	public final UniformMat4 model = new UniformMat4();
	
	/**
	 * 
	 */
	public NormalShader() {
		super(
				new ShaderModuleData(SHADERS.with("normalVertex.glsl"), GL_VERTEX_SHADER),
				new ShaderModuleData(SHADERS.with("normalGeometry.glsl"), GL_GEOMETRY_SHADER),
				new ShaderModuleData(SHADERS.with("normalFragment.glsl"), GL_FRAGMENT_SHADER));
		storeAllUniformLocations(projection, view, model);
	}
	
}
