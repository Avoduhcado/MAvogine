#version 330 core

layout (location=0) in vec3 position;
layout (location=1) in vec3 normal;

uniform mat4 projectionMatrix;
uniform mat4 viewMatrix;
uniform mat4 modelMatrix;
uniform mat3 normalMatrix;

out vec3 vertPosition;
out vec3 vertNormal;
out vec2 vertTextureCoords;

void main() {
	
	gl_Position = projectionMatrix * viewMatrix * modelMatrix * vec4(position, 1.0);
	
	// Transform outs to view space
	vertPosition = vec3(viewMatrix * modelMatrix * vec4(position, 1.0));
	vertNormal = normalMatrix * normal;
	
}