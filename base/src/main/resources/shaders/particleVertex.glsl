#version 330 core

layout (location=0) in vec3 vertex;
layout (location=1) in vec4 position;
layout (location=2) in vec4 color;

uniform mat4 projection;
uniform mat4 view;
uniform vec3 cameraRight_worldspace;
uniform vec3 cameraUp_worldspace;

out vec2 UV;
out vec4 vertColor;

void main() {
	float particleSize = position.w;
	vec3 particleCenter_worldspace = position.xyz;
	
	vec3 vertexPosition_worldspace =
		particleCenter_worldspace
		+ cameraRight_worldspace * vertex.x * particleSize
		+ cameraUp_worldspace * vertex.y * particleSize;
	
	gl_Position = projection * view * vec4(vertexPosition_worldspace, 1.0);
	
	UV = vertex.xy + vec2(0.5, 0.5);
	vertColor = color;
}