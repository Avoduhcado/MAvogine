package com.avogine.render.shader;

import org.lwjgl.opengl.*;

import com.avogine.render.shader.*;
import com.avogine.render.shader.uniform.UniformMat4;

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
				new ShaderFileType("normalVertex.glsl", GL20.GL_VERTEX_SHADER),
				new ShaderFileType("normalGeometry.glsl", GL32.GL_GEOMETRY_SHADER),
				new ShaderFileType("normalFragment.glsl", GL20.GL_FRAGMENT_SHADER));
		storeAllUniformLocations(projection, view, model);
	}
	
}
