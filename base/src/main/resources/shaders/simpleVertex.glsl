#version 330 core

layout (location=0) in vec3 position;
layout (location=1) in vec3 normal;
layout (location=4) in vec2 textureCoordinates;

uniform mat4 projection;
uniform mat4 view;
uniform mat4 model;
uniform mat4 normalMatrix;

out vec3 vertPosition;
out vec3 vertNormal;
out vec2 vertTextureCoordinates;

void main() {
	
	gl_Position = projection * view * model * vec4(position, 1.0);
	
	vec4 vertPos4 = view * model * vec4(position, 1.0);
	vertPosition = vec3(vertPos4) / vertPos4.w;
	vertNormal = mat3(transpose(inverse(view * model))) * normal;
	vertTextureCoordinates = textureCoordinates;
	
}