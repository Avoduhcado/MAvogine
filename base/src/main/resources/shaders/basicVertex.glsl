#version 330 core

layout (location=0) in vec3 position;
layout (location=1) in vec3 normals;
layout (location=2) in vec3 tangent;
layout (location=3) in vec3 bitangent;
layout (location=4) in vec2 textureCoordinates;

uniform mat4 projection;
uniform mat4 view;
uniform mat4 model;
uniform mat3 uvTransform;

out vec2 vertTextureCoordinates;

void main() {

	gl_Position = projection * view * model * vec4(position, 1.0);
	vertTextureCoordinates = vec3(uvTransform * vec3(textureCoordinates, 0.0)).xy;

}