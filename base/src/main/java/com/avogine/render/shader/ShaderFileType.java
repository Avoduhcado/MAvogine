package com.avogine.render.shader;

import org.lwjgl.opengl.*;

/**
 * A tuple of Shader file name and Shader file type.
 * @param fileName The name of the file to load the shader code from.
 * @param shaderType The type of the shader. One of:<br><table><tr><td>{@link GL20C#GL_VERTEX_SHADER VERTEX_SHADER}</td><td>{@link GL20C#GL_FRAGMENT_SHADER FRAGMENT_SHADER}</td><td>{@link GL32#GL_GEOMETRY_SHADER GEOMETRY_SHADER}</td><td>{@link GL40#GL_TESS_CONTROL_SHADER TESS_CONTROL_SHADER}</td></tr><tr><td>{@link GL40#GL_TESS_EVALUATION_SHADER TESS_EVALUATION_SHADER}</td></tr></table>
 */
public record ShaderFileType(String fileName, int shaderType) {

}
