#version 330 core

in vec2 UV;
in vec4 vertColor;

uniform sampler2D particleSampler;

out vec4 fragColor;

void main() {
//	fragColor = texture(particleSampler, UV) * vertColor;
	fragColor = vertColor;
}