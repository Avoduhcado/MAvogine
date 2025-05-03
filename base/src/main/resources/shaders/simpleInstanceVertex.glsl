#version 330 core

layout (location=0) in vec3 position;
layout (location=1) in vec3 normal;
layout (location=4) in vec2 textureCoordinates;
layout (location=5) in mat4 instanceMatrix;
layout (location=9) in mat4 instanceNormal;

uniform mat4 projectionMatrix;
uniform mat4 viewMatrix;

out vec3 vertPosition;
out vec3 vertNormal;
out vec2 vertTextureCoordinates;

void main() {
	
	gl_Position = projectionMatrix * viewMatrix * instanceMatrix * vec4(position, 1.0);
	
	// Transform outs to view space
	vertPosition = vec3(viewMatrix * instanceMatrix * vec4(position, 1.0));
	vertNormal = mat3(instanceNormal) * normal;
	vertTextureCoordinates = textureCoordinates;
	
}