#version 330 core

layout (location=0) in vec3 position;
layout (location=1) in vec3 normal;
layout (location=2) in vec2 textureCoordinates;

uniform mat4 projectionMatrix;
uniform mat4 viewMatrix;
uniform mat4 modelMatrix;
uniform mat3 normalMatrix;
uniform mat3 uvMatrix;

out vec3 vertPosition;
out vec3 vertNormal;
out vec2 vertTextureCoords;

void main() {
	
	gl_Position = projectionMatrix * viewMatrix * modelMatrix * vec4(position, 1.0);
	
	// Normals
//	mat4 modelViewMatrix = viewMatrix * modelMatrix;
//	vec3 surfaceNormal = (modelViewMatrix * vec4(normal, 0.0)).xyz;
//	vec3 norm = normalize(surfaceNormal);
//	vec3 tang = normalize((modelViewMatrix * vec4(tangent, 0.0)).xyz);
//	vec3 bitang = normalize(cross(norm, tang));
//	
//	mat3 toTangentSpace = mat3(
//		tang.x, bitang.x, norm.x,
//		tang.y, bitang.y, norm.y,
//		tang.z, bitang.z, norm.z
//	);
	
	// Transform outs to view space
//	vertPosition = vec3(viewMatrix * modelMatrix * vec4(position, 1.0));
	vertPosition = vec3(modelMatrix * vec4(position, 1.0));
	vertNormal = (normalMatrix * normal).xyz;
//	vertNormal = normalize((modelMatrix * vec4(normal, 0.0)).xyz);
//	vertNormal = normal;
	vertTextureCoords = vec3(uvMatrix * vec3(textureCoordinates, 0.0)).xy;
	
}